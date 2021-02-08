package com.emiv.awesomechallenges;

import java.util.ArrayList;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class ChallengeCommand implements CommandExecutor {

	Main plugin;
	public ChallengeCommand(Main instance) {
		plugin = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (args.length == 0) {
				applyUI(p);
			} else {
				plugin.sendMsgWithPrefix("&eCorrect Use: &d/challenge", p);
			}
		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis command is not available on console"));			
		}
		return false;
	}
	

	private void applyUI(Player p) {
		ArrayList<String> playerCh = new ArrayList<>();
		int challengeAmount = plugin.getCYaml().getKeys(false).size();
		
		for (int i = 0; i < challengeAmount; i++) {
			Set<String> challenges = plugin.getCYaml().getKeys(false);
			String[] arrayOfChallenges = challenges.toArray(new String[challenges.size()]);
			String name = arrayOfChallenges[i];
			if (plugin.getPYaml().getInt(p.getName() + "." + plugin.getCYaml().getString(name + ".Type") + "." + name + ".Tier") <= plugin.getCYaml().getInt(name + ".TierNumber")) {
				playerCh.add(name);
			} else {
				if (playerCh.contains(name)) {
					playerCh.remove(name);
				}
			}

		}
		if (playerCh.size() == 0) {
			plugin.sendMsgWithPrefix(plugin.getConfig().getString("NoActiveChallenge"), p);
		} else if (playerCh.size() > 0 && playerCh.size() <= 28) {
			if (playerCh.size() > 0 && playerCh.size() <= 7) {
				createPage(p, playerCh.size(), 27, playerCh);
			} else if (playerCh.size() > 7 && playerCh.size() <= 14) {
				createPage(p, playerCh.size(), 36, playerCh);
			} else if (playerCh.size() > 14 && playerCh.size() <= 21) {
				createPage(p, playerCh.size(), 45, playerCh);
			} else if (playerCh.size() > 21 && playerCh.size() <= 28) {
				createPage(p, playerCh.size(), 54, playerCh);
			}
		} else {
			plugin.sendMsgWithPrefix("&cNumber of challenges is more than the plugin supports, please contact an administrator", p);
		}
		
	}
	
	private void createPage(Player p, int chAmo, int invSize, ArrayList<String> playerCh) {
		Inventory gui = Bukkit.createInventory(null, invSize, ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("ChallengeMenuTitle")));
		//Glass Panes
		int[] gpLoc27 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
		int[] gpLoc36 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};
		int[] gpLoc45 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
		int[] gpLoc54 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
		int[] gpLoc = null;
		if (invSize == 27) {
			gpLoc = gpLoc27;
		} else if (invSize == 36) {
			gpLoc = gpLoc36;
		} else if (invSize == 45) {
			gpLoc = gpLoc45;
		} else {
			gpLoc = gpLoc54;
		}
		for (int i : gpLoc) {
			ItemStack grayPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
			ItemMeta grayPaneMeta = grayPane.getItemMeta();
			grayPaneMeta.setDisplayName(" ");
			grayPane.setItemMeta(grayPaneMeta);
			gui.setItem(i, grayPane);
		}
		//Challenges
		int[] cLoc = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
		for (int i = 0; i < playerCh.size(); i ++) {
			String name = playerCh.get(i);
			String icon = plugin.getCYaml().getString(name + ".Icon");
			ItemStack challenge = new ItemStack(Material.getMaterial(icon));
			ItemMeta chMeta = challenge.getItemMeta();
			int tier = plugin.getPYaml().getInt(p.getName() + "." + plugin.getCYaml().getString(name + ".Type") + "." + name + ".Tier");
			chMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&9&l" + name + " (Tier " + String.valueOf(tier) + ")"));
			ArrayList<String> chLore = new ArrayList<>();
			chLore.add(ChatColor.translateAlternateColorCodes('&', "&7" + plugin.getCYaml().getString(name + ".Description")));
			chLore.add("");
			chLore.add(ChatColor.translateAlternateColorCodes('&', "&a&lTasks:"));
			int collected = plugin.getPYaml().getInt(p.getName() + "." + plugin.getCYaml().getString(name + ".Type") + "." + name + ".Amount");
			int amount = plugin.getCYaml().getInt(name + ".Tier" + String.valueOf(tier) + ".Amount");
			chLore.add(ChatColor.translateAlternateColorCodes('&', "&7 - " + plugin.getCYaml().getString(name + ".Task").replace("%amount%", String.valueOf(amount)).replace("%collected%", String.valueOf(collected))));
			chLore.add("");
			chLore.add(ChatColor.translateAlternateColorCodes('&', "&a&lRewards:"));
			String reward = plugin.getCYaml().getString(name + ".Tier" + String.valueOf(tier) + ".Reward");
			chLore.add(ChatColor.translateAlternateColorCodes('&', "&7 - " + reward));
			chMeta.setLore(chLore);
			challenge.setItemMeta(chMeta);
			gui.setItem(cLoc[i], challenge);
		}
		
		p.openInventory(gui);
	}

}
