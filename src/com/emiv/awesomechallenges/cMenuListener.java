package com.emiv.awesomechallenges;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import net.md_5.bungee.api.ChatColor;

public class cMenuListener implements Listener {

	Main plugin;
	public cMenuListener(Main instance) {
		plugin = instance;
	}
	
	// e.getCurrentItem().getItemMeta().getDisplayName()
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (ChatColor.translateAlternateColorCodes('&', e.getView().getTitle()).equals(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("ChallengeMenuTitle")))) {
			if (e.getCurrentItem() != null) {
				e.setCancelled(true);
			}
		}
	}
	
}
