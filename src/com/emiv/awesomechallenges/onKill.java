package com.emiv.awesomechallenges;

import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class onKill implements Listener {

	Main plugin;
	public onKill(Main instance) {
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
	public void onKillEvent(EntityDeathEvent e) {
		if (e.getEntity().getKiller() instanceof Player) {
			Player p = e.getEntity().getKiller();
			for (String s : plugin.getCYaml().getKeys(false)) {
				if (plugin.getCYaml().getString(s + ".Type").equals("Kill")) {
					if (e.getEntityType() == getEntityByName(plugin.getCYaml().getString(s + ".Object"))) {
						plugin.getPYaml().set(p.getName() + ".Kill." + s + ".Amount", plugin.getPYaml().getInt(p.getName() + ".Kill." + s + ".Amount") + 1);
						int tier = plugin.getPYaml().getInt(p.getName() + ".Kill." + s + ".Tier");
						if (plugin.getPYaml().getInt(p.getName() + ".Kill." + s + ".Amount") >= plugin.getCYaml().getInt(s + ".Tier" + String.valueOf(tier) + ".Amount")) {
							plugin.getPYaml().set(p.getName() + ".Kill." + s + ".Amount", plugin.getPYaml().getInt(p.getName() + ".Kill." + s + ".Amount") - plugin.getCYaml().getInt(s + ".Tier" + String.valueOf(tier) + ".Amount"));
							plugin.getPYaml().set(p.getName() + ".Kill." + s + ".Tier", tier + 1);
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
						if (plugin.getVYaml().getString(s + ".Type").equals("Kill")) {
							if (e.getEntityType() == getEntityByName(plugin.getVYaml().getString(s + ".Object"))) {
								plugin.getPYaml().set(p.getName() + ".Kill." + s + ".Amount", plugin.getPYaml().getInt(p.getName() + ".Kill." + s + ".Amount") + 1);
								int tier = plugin.getPYaml().getInt(p.getName() + ".Kill." + s + ".Tier");
								if (plugin.getPYaml().getInt(p.getName() + ".Kill." + s + ".Amount") >= plugin.getVYaml().getInt(s + ".Tier" + String.valueOf(tier) + ".Amount")) {
									plugin.getPYaml().set(p.getName() + ".Kill." + s + ".Amount", plugin.getPYaml().getInt(p.getName() + ".Kill." + s + ".Amount") - plugin.getVYaml().getInt(s + ".Tier" + String.valueOf(tier) + ".Amount"));
									plugin.getPYaml().set(p.getName() + ".Kill." + s + ".Tier", tier + 1);
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
	
	public EntityType getEntityByName(String name) {
        for (EntityType type : EntityType.values()) {
            if(type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
