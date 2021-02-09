package com.emiv.awesomechallenges;

import java.io.IOException;
import java.util.List;

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
						List<String> commandList = plugin.getCYaml().getStringList(s + ".Tier" + String.valueOf(tier) + ".Commands");
						for (String c: commandList) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c.replace("%player%", e.getPlayer().getName()));
						}
						if (tier == plugin.getCYaml().getInt(s + ".TierNumber")) {
							plugin.sendMsgWithPrefix(plugin.getConfig().getString("ChallengeComplete").replace("%challenge%", s), e.getPlayer());
						} else {
							plugin.sendMsgWithPrefix(plugin.getConfig().getString("TierUp").replace("%tier%", String.valueOf(tier)).replace("%challenge%", s), e.getPlayer());
						}
					}
				}
			}
		}
		if (e.getPlayer().hasPermission("awesomechallenges.premium")) {
			if (plugin.getConfig().getString("PremiumChallenges").equals("true")) {
				for (String s : plugin.getVYaml().getKeys(false)) {
					if (plugin.getVYaml().getString(s + ".Type").equals("Mine")) {
						if (Material.getMaterial(plugin.getVYaml().getString(s + ".Object")) == e.getBlock().getType()) {
							plugin.getPYaml().set(e.getPlayer().getName() + ".Mine." + s + ".Amount", plugin.getPYaml().getInt(e.getPlayer().getName() + ".Mine." + s + ".Amount") + 1);
							int tier = plugin.getPYaml().getInt(e.getPlayer().getName() + ".Mine." + s + ".Tier");
							if (plugin.getPYaml().getInt(e.getPlayer().getName() + ".Mine." + s + ".Amount") == plugin.getVYaml().getInt(s + ".Tier" + String.valueOf(tier) + ".Amount")) {
								plugin.getPYaml().set(e.getPlayer().getName() + ".Mine." + s + ".Amount", 0);
								plugin.getPYaml().set(e.getPlayer().getName() + ".Mine." + s + ".Tier", tier + 1);
								List<String> commandList = plugin.getVYaml().getStringList(s + ".Tier" + String.valueOf(tier) + ".Commands");
								for (String c: commandList) {
									Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c.replace("%player%", e.getPlayer().getName()));
								}
								if (tier == plugin.getVYaml().getInt(s + ".TierNumber")) {
									plugin.sendMsgWithPrefix(plugin.getConfig().getString("ChallengeComplete").replace("%challenge%", s), e.getPlayer());
								} else {
									plugin.sendMsgWithPrefix(plugin.getConfig().getString("TierUp").replace("%tier%", String.valueOf(tier)).replace("%challenge%", s), e.getPlayer());
								}
							}
						}
					}
				}	
			}
		}
		save();
	}
	
}
