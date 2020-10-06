package com.emiv.awesomechallenges;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class onJoin implements Listener {

	Main plugin;
	public onJoin(Main instance) {
		plugin = instance;
	}
	
	void Save() {
		try {
			plugin.getPYaml().save(plugin.getPFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (!plugin.getPYaml().contains(e.getPlayer().getName())) {
			Player p = e.getPlayer();
			for (String s : plugin.getCYaml().getKeys(false)) {
				String type = plugin.getCYaml().getString(s + ".Type");
				plugin.getPYaml().set(p.getName() + "." + type + "." + s + ".Amount", 0);
				plugin.getPYaml().set(p.getName() + "." + type + "." + s + ".Tier", 1);
			}
			Save();
		}
	}
	
}
