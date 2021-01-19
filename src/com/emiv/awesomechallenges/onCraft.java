package com.emiv.awesomechallenges;

import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class onCraft implements Listener {
	Main plugin;
	public onCraft(Main instance) {
		plugin = instance;
	}
	
	void save() {
		try {
			plugin.getCYaml().save(plugin.getCFile());
			plugin.getPYaml().save(plugin.getPFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onPlayerCraft(CraftItemEvent e) {
		Player p = (Player) e.getWhoClicked();
		for (String s: plugin.getCYaml().getKeys(false)) {
			if (plugin.getCYaml().getString(s + ".Type").equals("Craft")) {
				if (e.getRecipe().getResult().getType() == Material.getMaterial(plugin.getCYaml().getString(s + ".Object"))) {
					plugin.getPYaml().set(p.getName() + ".Craft." + s + ".Amount", plugin.getPYaml().getInt(p.getName() + ".Craft." + s + ".Amount") + e.getRecipe().getResult().getAmount());
					int tier = plugin.getPYaml().getInt(p.getName() + ".Craft." + s + ".Tier");
					if (plugin.getPYaml().getInt(p.getName() + ".Craft." + s + ".Amount") >= plugin.getCYaml().getInt(s + ".Tier" + String.valueOf(tier) + ".Amount")) {
						plugin.getPYaml().set(p.getName() + ".Craft." + s + ".Amount", plugin.getPYaml().getInt(p.getName() + ".Craft." + s + ".Amount") - plugin.getCYaml().getInt(s + ".Tier" + String.valueOf(tier) + ".Amount"));
						plugin.getPYaml().set(p.getName() + ".Craft." + s + ".Tier", tier + 1);
						List<String> commandList = plugin.getCYaml().getStringList(s + ".Tier" + String.valueOf(tier) + ".Commands");
						for (String c: commandList) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c.replace("%player%", p.getName()));
						}
						if (tier == plugin.getCYaml().getInt(s + ".TierNumber")) {
							plugin.sendMsgWithPrefix(plugin.getConfig().getString("ChallengeComplete").replace("%challenge%", s), p);
						} else {
							plugin.sendMsgWithPrefix(plugin.getConfig().getString("TierUp").replace("%tier%", String.valueOf(tier)).replace("%challenge%", s), p);
						}
					}
				}
			}

		}
		save();
	}
}
