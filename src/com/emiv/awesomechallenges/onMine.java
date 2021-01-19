package com.emiv.awesomechallenges;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class onMine implements Listener {

	Main plugin;
	public onMine(Main instance) {
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
	public void onPlayerMine(BlockBreakEvent e) {
		for (String s : plugin.getCYaml().getKeys(false)) {
			if (plugin.getCYaml().getString(s + ".Type").equals("Mine")) {
				if (Material.getMaterial(plugin.getCYaml().getString(s + ".Object")) == e.getBlock().getType()) {
					plugin.getPYaml().set(e.getPlayer().getName() + ".Mine." + s + ".Amount", plugin.getPYaml().getInt(e.getPlayer().getName() + ".Mine." + s + ".Amount") + 1);
					int tier = plugin.getPYaml().getInt(e.getPlayer().getName() + ".Mine." + s + ".Tier");
					if (plugin.getPYaml().getInt(e.getPlayer().getName() + ".Mine." + s + ".Amount") == plugin.getCYaml().getInt(s + ".Tier" + String.valueOf(tier) + ".Amount")) {
						plugin.getPYaml().set(e.getPlayer().getName() + ".Mine." + s + ".Amount", 0);
						plugin.getPYaml().set(e.getPlayer().getName() + ".Mine." + s + ".Tier", tier + 1);
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.getCYaml().getString(s + ".Tier" + String.valueOf(tier) + ".Command").replace("%player%", e.getPlayer().getName()));
						if (tier == plugin.getCYaml().getInt(s + ".TierNumber")) {
							plugin.sendMsgWithPrefix(plugin.getConfig().getString("ChallengeComplete").replace("%challenge%", s), e.getPlayer());
						} else {
							plugin.sendMsgWithPrefix(plugin.getConfig().getString("TierUp").replace("%tier%", String.valueOf(tier)).replace("%challenge%", s), e.getPlayer());
						}
					}
				}
			}

		}
		save();
	}
	
}
