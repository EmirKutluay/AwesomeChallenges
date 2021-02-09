package com.emiv.awesomechallenges;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import net.md_5.bungee.api.ChatColor;

public class cMenuListener implements Listener {

	Main plugin;
	public cMenuListener(Main instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (ChatColor.translateAlternateColorCodes('&', e.getView().getTitle()).equals(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("ChallengeMenuTitle")))) {
			if (e.getCurrentItem() != null) {
				e.setCancelled(true);
				if (e.getCurrentItem().getType() == Material.IRON_INGOT) {
					if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&eFree Challenges"))) {
						e.getWhoClicked().closeInventory();
						Player p = (Player) e.getWhoClicked();
						ChallengeCommand.chCmd.freeUI(p);
					}
				} else if (e.getCurrentItem().getType() == Material.DIAMOND) {
					if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&aPremium Challenges"))) {
						e.getWhoClicked().closeInventory();
						Player p = (Player) e.getWhoClicked();
						if (p.hasPermission("awesomechallenges.premium")) {
							ChallengeCommand.chCmd.preUI(p);
						} else {
							plugin.sendMsgWithPrefix(plugin.getConfig().getString("NoPermissionForPremium"), p);
						}
					}
				}
			}
		}
	}
	
}
