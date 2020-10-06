package com.emiv.awesomechallenges;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceExtractEvent;

public class onSmelt implements Listener {

	Main plugin;
	public onSmelt(Main instance) {
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
	public void onPlayerSmelt(FurnaceExtractEvent e) {
		Player p = e.getPlayer();
		for (String s : plugin.getCYaml().getKeys(false)) {
			if (plugin.getCYaml().getString(s + ".Type").equals("Smelt")) {
				if (e.getItemType() == Material.getMaterial(plugin.getCYaml().getString(s + ".Object"))) {
					plugin.getPYaml().set(p.getName() + ".Smelt." + s + ".Amount", plugin.getPYaml().getInt(p.getName() + ".Smelt." + s + ".Amount") + 	e.getItemAmount());
					int tier = plugin.getPYaml().getInt(p.getName() + ".Smelt." + s + ".Tier");
					if (plugin.getPYaml().getInt(p.getName() + ".Smelt." + s + ".Amount") >= plugin.getCYaml().getInt(s + ".Tier" + String.valueOf(tier) + ".Amount")) {
						plugin.getPYaml().set(p.getName() + ".Smelt." + s + ".Amount", plugin.getPYaml().getInt(p.getName() + ".Smelt." + s + ".Amount") - plugin.getCYaml().getInt(s + ".Tier" + String.valueOf(tier) + ".Amount"));
						plugin.getPYaml().set(p.getName() + ".Smelt." + s + ".Tier", tier + 1);
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.getCYaml().getString(s + ".Tier" + String.valueOf(tier) + ".Command").replace("%player%", p.getName()));
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
