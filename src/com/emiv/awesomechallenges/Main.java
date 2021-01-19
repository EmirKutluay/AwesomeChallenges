package com.emiv.awesomechallenges;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin{
	
	//Challenges
	private File cFile;
	private YamlConfiguration cYaml;
	
	//Progress
	private File pFile;
	private YamlConfiguration pYaml;
	
	@Override
	public void onEnable() {		
		
		this.saveDefaultConfig();
		
		getCommand("awesomechallenges").setExecutor(new AcCommand(this));
		getCommand("challenge").setExecutor(new ChallengeCommand(this));
		
		Bukkit.getPluginManager().registerEvents(new cMenuListener(this), this);
		Bukkit.getPluginManager().registerEvents(new onJoin(this), this);
		Bukkit.getPluginManager().registerEvents(new onMine(this), this);
		Bukkit.getPluginManager().registerEvents(new onCraft(this), this);
		Bukkit.getPluginManager().registerEvents(new onSmelt(this), this);
		Bukkit.getPluginManager().registerEvents(new onKill(this), this);
		
		try {
			initiateFiles();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setChallenges();
		Save();
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("AwesomeChallenges"), new Runnable() {
		    @Override
		    public void run() {
		    	for (String s : cYaml.getKeys(false)) {
		    		String type = cYaml.getString(s + ".Type");
		    		if (type.equals("Playtime")) {
		    			for (Player p : Bukkit.getOnlinePlayers()) {
			    			pYaml.set(p.getName() + "." + type + "." + s + ".Amount", pYaml.getInt(p.getName() + "." + type + "." + s + ".Amount") + 1);
					        int tier = pYaml.getInt(p.getName() + ".Playtime." + s + ".Tier");
					        if (pYaml.getInt(p.getName() + "." + type + "." + s + ".Amount") >= cYaml.getInt(s + ".Tier" + String.valueOf(tier) + ".Amount")) {
								pYaml.set(p.getName() + ".Playtime." + s + ".Amount", pYaml.getInt(p.getName() + ".Playtime." + s + ".Amount") - cYaml.getInt(s + ".Tier" + String.valueOf(tier) + ".Amount"));
								pYaml.set(p.getName() + ".Playtime." + s + ".Tier", tier + 1);
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cYaml.getString(s + ".Tier" + String.valueOf(tier) + ".Command").replace("%player%", p.getName()));
								if (tier == cYaml.getInt(s + ".TierNumber")) {
									sendMsgWithPrefix(getConfig().getString("ChallengeComplete").replace("%challenge%", s), p);
								} else {
									sendMsgWithPrefix(getConfig().getString("TierUp").replace("%tier%", String.valueOf(tier)).replace("%challenge%", s), p);
								}
					        }
		    			}
		    		}
		    	}
		        try {
					pYaml.save(pFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		}, 20L, 20L);
	}
	
	public void Save() {
		try {
			pYaml.save(pFile);
			cYaml.save(cFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public YamlConfiguration getCYaml() { return cYaml; }
	public File getCFile() { return cFile; }
	public YamlConfiguration getPYaml() { return pYaml; }
	public File getPFile() { return pFile; }
	
	public void sendMsgWithPrefix(String s, Player p) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("ServerPrefix") + " " + s));
	}
	
	public void initiateFiles() throws IOException {
		cFile = new File(Bukkit.getServer().getPluginManager().getPlugin("AwesomeChallenges").getDataFolder(), "challenges.yml");
		if (!cFile.exists()) {
			cFile.createNewFile();
		}
		
		cYaml = YamlConfiguration.loadConfiguration(cFile);
		
		pFile = new File(Bukkit.getServer().getPluginManager().getPlugin("AwesomeChallenges").getDataFolder(), "playerprogress.yml");
		if (!pFile.exists()) {
			pFile.createNewFile();
		}
		
		pYaml = YamlConfiguration.loadConfiguration(pFile);
	}
	
	
	public void setChallenges() {
		if (!cYaml.contains("Coal")) {
			int[] Amounts = {250, 1000, 5000, 20000};
			String[] Rewards = {"5x Diamond", "16x Diamond", "64x Diamond", "64x Diamond Block"};
			String[] Commands = {"give %player% diamond 5", "give %player% diamond 16", "give %player% diamond 64", "give %player% minecraft:diamond_block 64"};
			challengeHook("Coal", "Mine", "COAL_ORE", 4, Amounts, "COAL", "Mine some Coal", "Mine %amount% Coal (%collected%/%amount%)", Rewards, Commands);
		}
		if (!cYaml.contains("Diamond")) {
			int[] Amounts = {10, 30, 100};
			String[] Rewards = {"2x Netherite", "8x Netherite", "32x Netherite"};
			String[] Commands = {"give %player% netherite_ingot 2", "give %player% netherite_ingot 8", "give %player% netherite_ingot 24"};
			challengeHook("Diamond", "Mine", "DIAMOND_ORE", 3, Amounts, "DIAMOND", "Mine some Diamonds", "Mine %amount% Diamonds (%collected%/%amount%)", Rewards, Commands);
		}
		if (!cYaml.contains("Furnace")) {
			int[] Amounts = {1, 3, 10};
			String[] Rewards = {"2x Coal Block", "8x Coal Block", "32x Coal Block"};
			String[] Commands = {"give %player% coal_block 2", "give %player% coal_block 8", "give %player% coal_block 32"};
			challengeHook("Furnace", "Craft", "FURNACE", 3, Amounts, "FURNACE", "Craft some Furnace", "Craft %amount% Furnace (%collected%/%amount%)", Rewards, Commands);
		}
		if (!cYaml.contains("Enchanting Table")) {
			int[] Amounts = {1};
			String[] Rewards = {"15x Lapis Lazuli"};
			String[] Commands = {"give %player% lapis_lazuli 15"};
			challengeHook("Enchanting Table", "Craft", "ENCHANTING_TABLE", 1, Amounts, "ENCHANTING_TABLE", "Craft an Enchanting Table", "Craft %amount% Enchanting Table (%collected%/%amount%)", Rewards, Commands);
		}
		if (!cYaml.contains("Chicken")) {
			int[] Amounts = {16, 32, 64};
			String[] Rewards = {"1x Diamond", "2x Diamond", "5x Diamond"};
			String[] Commands = {"give %player% diamond 1", "give %player% diamond 2", "give %player% diamond 5"};
			challengeHook("Chicken", "Smelt", "COOKED_CHICKEN", 3, Amounts, "COOKED_CHICKEN", "Cook some Chicken", "Cook %amount% Chicken (%collected%/%amount%)", Rewards, Commands);
		}
		if (!cYaml.contains("Cow"))
		{
			int[] Amounts = {16, 32, 64};
			String[] Rewards = {"1x Diamond", "3x Diamond", "10x Diamond"};
			String[] Commands = {"give %player% diamond 1", "give %player% diamond 3", "give %player% diamond 10"};
			challengeHook("Cow", "Kill", "COW", 3, Amounts, "COW_SPAWN_EGG", "Kill some Cows", "Kill %amount% Cows (%collected%/%amount%)", Rewards, Commands);
		}
		if (!cYaml.contains("Spend Time")) {
			int[] Amounts = {3600, 10800, 36000};
			String[] Rewards = {"3x Diamond", "10x Diamond", "32x Diamond"};
			String[] Commands = {"give %player% diamond 3", "give %player% diamond 10", "give %player% diamond 32"};
			challengeHook("Spend Time", "Playtime", "Seconds", 3, Amounts, "CLOCK", "Spend some time", "Achieve %amount% seconds of playtime (%collected%/%amount%)", Rewards, Commands);
		}
	}
	
	void challengeHook(String name, String type, String object, int tierNumber, int[] amounts, String icon, String desc, String task, String[] rewards, String[] commands) {
		cYaml.set(name + ".Type", type);
		cYaml.set(name + ".Object", object);
		cYaml.set(name + ".TierNumber", tierNumber);
		for (int i = 0; i < tierNumber; i++) {
			cYaml.set(name + ".Tier" + String.valueOf(i + 1) + ".Amount", amounts[i]);
		}
		cYaml.set(name + ".Icon", icon);
		cYaml.set(name + ".Description", desc);
		cYaml.set(name + ".Task", task);
		for (int i = 0; i < tierNumber; i++) {
			cYaml.set(name + ".Tier" + String.valueOf(i + 1) + ".Reward", rewards[i]);
		}
		for (int i = 0; i < tierNumber; i++) {
			cYaml.set(name + ".Tier" + String.valueOf(i + 1) + ".Command", commands[i]);
		}
		
	}
}
