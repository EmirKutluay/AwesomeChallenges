package com.emiv.awesomechallenges;

import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;

public class onMythicKill implements Listener {

	Main plugin;
	public onMythicKill(Main instance) {
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
	public void onKillEvent(MythicMobDeathEvent e) {
		if (e.getKiller() instanceof Player) {
			Player p = (Player) e.getKiller();
			for (String s : plugin.getCYaml().getKeys(false)) {
				if (plugin.getCYaml().getString(s + ".Type").equals("MythicKill")) {
					if (e.getMobType().toString().equals("MythicMob{" + plugin.getCYaml().getString(s + ".Object") + "}")) {
						plugin.getPYaml().set(p.getName() + ".MythicKill." + s + ".Amount", plugin.getPYaml().getInt(p.getName() + ".MythicKill." + s + ".Amount") + 1);
						int tier = plugin.getPYaml().getInt(p.getName() + ".MythicKill." + s + ".Tier");
						if (plugin.getPYaml().getInt(p.getName() + ".MythicKill." + s + ".Amount") >= plugin.getCYaml().getInt(s + ".Tier" + String.valueOf(tier) + ".Amount")) {
							plugin.getPYaml().set(p.getName() + ".MythicKill." + s + ".Amount", plugin.getPYaml().getInt(p.getName() + ".MythicKill." + s + ".Amount") - plugin.getCYaml().getInt(s + ".Tier" + String.valueOf(tier) + ".Amount"));
							plugin.getPYaml().set(p.getName() + ".MythicKill." + s + ".Tier", tier + 1);
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
			if (p.hasPermission("awesomechallenges.premium")) {
				if (plugin.getConfig().getString("PremiumChallenges").equals("true")) {
					for (String s : plugin.getVYaml().getKeys(false)) {
						if (plugin.getVYaml().getString(s + ".Type").equals("MythicKill")) {
							if (e.getMobType().toString().equals("MythicMob{" + plugin.getVYaml().getString(s + ".Object") + "}")) {
								plugin.getPYaml().set(p.getName() + ".MythicKill." + s + ".Amount", plugin.getPYaml().getInt(p.getName() + ".MythicKill." + s + ".Amount") + 1);
								int tier = plugin.getPYaml().getInt(p.getName() + ".MythicKill." + s + ".Tier");
								if (plugin.getPYaml().getInt(p.getName() + ".MythicKill." + s + ".Amount") >= plugin.getVYaml().getInt(s + ".Tier" + String.valueOf(tier) + ".Amount")) {
									plugin.getPYaml().set(p.getName() + ".MythicKill." + s + ".Amount", plugin.getPYaml().getInt(p.getName() + ".MythicKill." + s + ".Amount") - plugin.getVYaml().getInt(s + ".Tier" + String.valueOf(tier) + ".Amount"));
									plugin.getPYaml().set(p.getName() + ".MythicKill." + s + ".Tier", tier + 1);
									List<String> commandList = plugin.getVYaml().getStringList(s + ".Tier" + String.valueOf(tier) + ".Commands");
									for (String c: commandList) {
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c.replace("%player%", p.getName()));
									}
									if (tier == plugin.getVYaml().getInt(s + ".TierNumber")) {
										plugin.sendMsgWithPrefix(plugin.getConfig().getString("ChallengeComplete").replace("%challenge%", s), p);
									} else {
										plugin.sendMsgWithPrefix(plugin.getConfig().getString("TierUp").replace("%tier%", String.valueOf(tier)).replace("%challenge%", s), p);
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
	
}
