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

	public static ChallengeCommand chCmd;
	
	Main plugin;
	public ChallengeCommand(Main instance) {
		chCmd = this;
		plugin = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (args.length == 0) {
				if (plugin.getConfig().getString("PremiumChallenges").equals("true")) {
					mainInv(p);
				} else {
					freeUI(p);
				}
			} else {
				plugin.sendMsgWithPrefix("&eCorrect Use: &d/challenge", p);
			}
		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis command is not available on console"));			
		}
		return false;
	}
	
	
	private void mainInv(Player p) {
		Inventory gui = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("ChallengeMenuTitle")));
		//Free
		ItemStack freeChallenges = new ItemStack(Material.IRON_INGOT);
		ItemMeta freeMeta = freeChallenges.getItemMeta();
		freeMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eFree Challenges"));
		freeChallenges.setItemMeta(freeMeta);
		gui.setItem(12, freeChallenges);
		//Premium
		ItemStack premiumChallenges = new ItemStack(Material.DIAMOND);
		ItemMeta premiumMeta = premiumChallenges.getItemMeta();
		premiumMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aPremium Challenges"));
		premiumChallenges.setItemMeta(premiumMeta);
		gui.setItem(14, premiumChallenges);
		
		p.openInventory(gui);
	}

	public void freeUI(Player p) {
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
				freePage(p, playerCh.size(), 27, playerCh);
			} else if (playerCh.size() > 7 && playerCh.size() <= 14) {
				freePage(p, playerCh.size(), 36, playerCh);
			} else if (playerCh.size() > 14 && playerCh.size() <= 21) {
				freePage(p, playerCh.size(), 45, playerCh);
			} else if (playerCh.size() > 21 && playerCh.size() <= 28) {
				freePage(p, playerCh.size(), 54, playerCh);
			}
		} else {
			plugin.sendMsgWithPrefix("&cNumber of challenges is more than the plugin supports, please contact an administrator", p);
		}
		
	}
	
	private void freePage(Player p, int chAmo, int invSize, ArrayList<String> playerCh) {
		Inventory gui = Bukkit.createInventory(null, invSize, ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("UserChallengesTitle")));
		
		//Challenges
		int[] cLoc = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
		for (int i = 0; i < playerCh.size(); i ++) {
			String name = playerCh.get(i);
			String icon = plugin.getCYaml().getString(name + ".Icon");
			ItemStack challenge = new ItemStack(Material.getMaterial(icon));
			ItemMeta chMeta = challenge.getItemMeta();
			int tier = plugin.getPYaml().getInt(p.getName() + "." + plugin.getCYaml().getString(name + ".Type") + "." + name + ".Tier");
			chMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("ChallengeTags").replace("%challengeName%", name).replace("%challengeTier%", String.valueOf(tier))));
			ArrayList<String> chLore = new ArrayList<>();
			chLore.add(ChatColor.translateAlternateColorCodes('&', "&7" + plugin.getCYaml().getString(name + ".Description")));
			chLore.add("");
			chLore.add(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("ChallengeTaskTitle")));
			int collected = plugin.getPYaml().getInt(p.getName() + "." + plugin.getCYaml().getString(name + ".Type") + "." + name + ".Amount");
			int amount = plugin.getCYaml().getInt(name + ".Tier" + String.valueOf(tier) + ".Amount");
			chLore.add(ChatColor.translateAlternateColorCodes('&', "&7 - " + plugin.getCYaml().getString(name + ".Task").replace("%amount%", String.valueOf(amount)).replace("%collected%", String.valueOf(collected))));
			chLore.add("");
			chLore.add(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("ChallengeRewardTitle")));
			String reward = plugin.getCYaml().getString(name + ".Tier" + String.valueOf(tier) + ".Reward");
			chLore.add(ChatColor.translateAlternateColorCodes('&', "&7 - " + reward));
			chMeta.setLore(chLore);
			challenge.setItemMeta(chMeta);
			gui.setItem(cLoc[i], challenge);
		}
		
		p.openInventory(gui);
	}
	
	public void preUI(Player p) {
		ArrayList<String> playerCh = new ArrayList<>();
		int challengeAmount = plugin.getVYaml().getKeys(false).size();
		
		for (int i = 0; i < challengeAmount; i++) {
			Set<String> challenges = plugin.getVYaml().getKeys(false);
			String[] arrayOfChallenges = challenges.toArray(new String[challenges.size()]);
			String name = arrayOfChallenges[i];
			if (plugin.getPYaml().getInt(p.getName() + "." + plugin.getVYaml().getString(name + ".Type") + "." + name + ".Tier") <= plugin.getVYaml().getInt(name + ".TierNumber")) {
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
				prePage(p, playerCh.size(), 27, playerCh);
			} else if (playerCh.size() > 7 && playerCh.size() <= 14) {
				prePage(p, playerCh.size(), 36, playerCh);
			} else if (playerCh.size() > 14 && playerCh.size() <= 21) {
				prePage(p, playerCh.size(), 45, playerCh);
			} else if (playerCh.size() > 21 && playerCh.size() <= 28) {
				prePage(p, playerCh.size(), 54, playerCh);
			}
		} else {
			plugin.sendMsgWithPrefix("&cNumber of challenges is more than the plugin supports, please contact an administrator", p);
		}
		
	}
	
	private void prePage(Player p, int chAmo, int invSize, ArrayList<String> playerCh) {
		Inventory gui = Bukkit.createInventory(null, invSize, ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("PremiumChallengesTitle")));
		
		//Challenges
		int[] cLoc = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
		for (int i = 0; i < playerCh.size(); i ++) {
			String name = playerCh.get(i);
			String icon = plugin.getVYaml().getString(name + ".Icon");
			ItemStack challenge = new ItemStack(Material.getMaterial(icon));
			ItemMeta chMeta = challenge.getItemMeta();
			int tier = plugin.getPYaml().getInt(p.getName() + "." + plugin.getVYaml().getString(name + ".Type") + "." + name + ".Tier");
			chMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("ChallengeTags").replace("%challengeName%", name).replace("%challengeTier%", String.valueOf(tier))));
			ArrayList<String> chLore = new ArrayList<>();
			chLore.add(ChatColor.translateAlternateColorCodes('&', "&7" + plugin.getVYaml().getString(name + ".Description")));
			chLore.add("");
			chLore.add(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("ChallengeTaskTitle")));
			int collected = plugin.getPYaml().getInt(p.getName() + "." + plugin.getVYaml().getString(name + ".Type") + "." + name + ".Amount");
			int amount = plugin.getVYaml().getInt(name + ".Tier" + String.valueOf(tier) + ".Amount");
			chLore.add(ChatColor.translateAlternateColorCodes('&', "&7 - " + plugin.getVYaml().getString(name + ".Task").replace("%amount%", String.valueOf(amount)).replace("%collected%", String.valueOf(collected))));
			chLore.add("");
			chLore.add(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("ChallengeRewardTitle")));
			String reward = plugin.getVYaml().getString(name + ".Tier" + String.valueOf(tier) + ".Reward");
			chLore.add(ChatColor.translateAlternateColorCodes('&', "&7 - " + reward));
			chMeta.setLore(chLore);
			challenge.setItemMeta(chMeta);
			gui.setItem(cLoc[i], challenge);
		}
		
		p.openInventory(gui);
	}

}
