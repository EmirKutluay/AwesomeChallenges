package com.emiv.awesomechallenges;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin{
	
	//Challenges
	private File cFile;
	private YamlConfiguration cYaml;
	
	//Premium Challenges
	private File vFile;
	private YamlConfiguration vYaml;
	
	//Progress
	private File pFile;
	private YamlConfiguration pYaml;
	
	
	
	
	boolean challengesSet = false;
	boolean premiumSet = false;
	
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
		
		if (cYaml.getKeys(false).size() == 0) {
			setChallenges();
		}
		if (vYaml.getKeys(false).size() == 0) {
			setPremium();
		}
		if (Bukkit.getServer().getOnlinePlayers().size() > 0) {
			setPlayerData();
		}
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
								List<String> commandList = cYaml.getStringList(s + ".Tier" + String.valueOf(tier) + ".Commands");
								for (String c: commandList) {
									Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c.replace("%player%", p.getName()));
								}
								if (tier == cYaml.getInt(s + ".TierNumber")) {
									sendMsgWithPrefix(getConfig().getString("ChallengeComplete").replace("%challenge%", s), p);
								} else {
									sendMsgWithPrefix(getConfig().getString("TierUp").replace("%tier%", String.valueOf(tier)).replace("%challenge%", s), p);
								}
					        }
		    			}
		    		}
		    	}
		    	for (String s : vYaml.getKeys(false)) {
		    		String type = vYaml.getString(s + ".Type");
		    		if (type.equals("Playtime")) {
		    			for (Player p : Bukkit.getOnlinePlayers()) {
			    			if (p.hasPermission("awesomechallenges.premium")) {
			    				pYaml.set(p.getName() + "." + type + "." + s + ".Amount", pYaml.getInt(p.getName() + "." + type + "." + s + ".Amount") + 1);
						        int tier = pYaml.getInt(p.getName() + ".Playtime." + s + ".Tier");
						        if (pYaml.getInt(p.getName() + "." + type + "." + s + ".Amount") >= vYaml.getInt(s + ".Tier" + String.valueOf(tier) + ".Amount")) {
									pYaml.set(p.getName() + ".Playtime." + s + ".Amount", pYaml.getInt(p.getName() + ".Playtime." + s + ".Amount") - vYaml.getInt(s + ".Tier" + String.valueOf(tier) + ".Amount"));
									pYaml.set(p.getName() + ".Playtime." + s + ".Tier", tier + 1);
									List<String> commandList = vYaml.getStringList(s + ".Tier" + String.valueOf(tier) + ".Commands");
									for (String c: commandList) {
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c.replace("%player%", p.getName()));
									}
									if (tier == vYaml.getInt(s + ".TierNumber")) {
										sendMsgWithPrefix(getConfig().getString("ChallengeComplete").replace("%challenge%", s), p);
									} else {
										sendMsgWithPrefix(getConfig().getString("TierUp").replace("%tier%", String.valueOf(tier)).replace("%challenge%", s), p);
									}
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
			vYaml.save(vFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setPlayerData() {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (!pYaml.contains(p.getName())) {
				for (String s : cYaml.getKeys(false)) {
					String type = cYaml.getString(s + ".Type");
					pYaml.set(p.getName() + "." + type + "." + s + ".Amount", 0);
					pYaml.set(p.getName() + "." + type + "." + s + ".Tier", 1);
				}
				if (p.hasPermission("awesomechallenges.premium")) {
					if (this.getConfig().getString("PremiumChallenges").equals("true")) {
						for (String s : vYaml.getKeys(false)) {
							String type = vYaml.getString(s + ".Type");
							pYaml.set(p.getName() + "." + type + "." + s + ".Amount", 0);
							pYaml.set(p.getName() + "." + type + "." + s + ".Tier", 1);
						}
					}			
				}
			} else {
				for (String s : cYaml.getKeys(false)) {
					String type = cYaml.getString(s + ".Type");
					if (!pYaml.contains(p.getName() + "." + type + "." + s + ".Amount")) {
						pYaml.set(p.getName() + "." + type + "." + s + ".Amount", 0);
						pYaml.set(p.getName() + "." + type + "." + s + ".Tier", 1);
					}
				}
				if (p.hasPermission("awesomechallenges.premium")) {
					if (this.getConfig().getString("PremiumChallenges").equals("true")) {
						for (String s : vYaml.getKeys(false)) {
							String type = vYaml.getString(s + ".Type");
							if (!pYaml.contains(p.getName() + "." + type + "." + s + ".Amount")) {
								pYaml.set(p.getName() + "." + type + "." + s + ".Amount", 0);
								pYaml.set(p.getName() + "." + type + "." + s + ".Tier", 1);
							}
						}
					}
				}
			}
			Save();
		}
	}
	
	public YamlConfiguration getCYaml() { return cYaml; }
	public File getCFile() { return cFile; }
	public YamlConfiguration getPYaml() { return pYaml; }
	public File getPFile() { return pFile; }
	public YamlConfiguration getVYaml() { return vYaml; }
	public File getVFile() { return vFile; }
	
	public void sendMsgWithPrefix(String s, Player p) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("ServerPrefix") + " " + s));
	}
	
	public void initiateFiles() throws IOException {
		cFile = new File(Bukkit.getServer().getPluginManager().getPlugin("AwesomeChallenges").getDataFolder(), "challenges.yml");
		if (!cFile.exists()) {
			cFile.createNewFile();
		} else {
			challengesSet = true;
		}
		
		cYaml = YamlConfiguration.loadConfiguration(cFile);
		
		pFile = new File(Bukkit.getServer().getPluginManager().getPlugin("AwesomeChallenges").getDataFolder(), "playerprogress.yml");
		if (!pFile.exists()) {
			pFile.createNewFile();
		}
		
		pYaml = YamlConfiguration.loadConfiguration(pFile);
		
		vFile = new File(Bukkit.getServer().getPluginManager().getPlugin("AwesomeChallenges").getDataFolder(), "premiumchallenges.yml");
		if (!vFile.exists()) {
			vFile.createNewFile();
		} else {
			premiumSet = true;
		}
		
		vYaml = YamlConfiguration.loadConfiguration(vFile);
	}
	
	
	public void setChallenges() {
		if (!cYaml.contains("Coal")) {
			int[] Amounts = {250, 1000, 5000, 20000};
			String[] Rewards = {"5x Diamond", "16x Diamond", "64x Diamond", "64x Diamond Block"};
			String[] tierOne = {"give %player% diamond 5"};
			String[] tierTwo = {"give %player% diamond 16"};
			String[] tierThree = {"give %player% diamond 64"};
			String[] tierFour = {"give %player% minecraft:diamond_block 64"};
			String[][] Commands = {tierOne, tierTwo, tierThree, tierFour};
			challengeHook("Coal", "Mine", "COAL_ORE", 4, Amounts, "COAL", "Mine some Coal", "Mine %amount% Coal (%collected%/%amount%)", Rewards, Commands);
		}
		if (!cYaml.contains("Diamond")) {
			int[] Amounts = {10, 30, 100};
			String[] Rewards = {"2x Netherite", "8x Netherite", "32x Netherite"};
			String[] tierOne = {"give %player% netherite_ingot 2"};
			String[] tierTwo = {"give %player% netherite_ingot 8"};
			String[] tierThree = {"give %player% netherite_ingot 24"};
			String[][] Commands = {tierOne, tierTwo, tierThree};
			challengeHook("Diamond", "Mine", "DIAMOND_ORE", 3, Amounts, "DIAMOND", "Mine some Diamonds", "Mine %amount% Diamonds (%collected%/%amount%)", Rewards, Commands);
		}
		if (!cYaml.contains("Furnace")) {
			int[] Amounts = {1, 3, 10};
			String[] Rewards = {"2x Coal Block & 1x Diamond & 1x Levels", "8x Coal Block & 3x Diamond & 3x Levels", "32x Coal Block & 10x Diamond & 10x Levels"};
			String[] tierOne = {"give %player% coal_block 2", "give %player% diamond 1", "xp add %player% 1 levels"};
			String[] tierTwo = {"give %player% coal_block 8", "give %player% diamond 3", "xp add %player% 3 levels"};
			String[] tierThree = {"give %player% coal_block 32", "give %player% diamond 10", "xp add %player% 10 levels"};
			String[][] Commands = {tierOne, tierTwo, tierThree};
			challengeHook("Furnace", "Craft", "FURNACE", 3, Amounts, "FURNACE", "Craft some Furnace", "Craft %amount% Furnace (%collected%/%amount%)", Rewards, Commands);
		}
		if (!cYaml.contains("Enchanting Table")) {
			int[] Amounts = {1};
			String[] Rewards = {"15x Lapis Lazuli & 5x Levels"};
			String[] tierOne = {"give %player% lapis_lazuli 15", "xp add %player% 5 levels"};
			String[][] Commands = {tierOne};
			challengeHook("Enchanting Table", "Craft", "ENCHANTING_TABLE", 1, Amounts, "ENCHANTING_TABLE", "Craft an Enchanting Table", "Craft %amount% Enchanting Table (%collected%/%amount%)", Rewards, Commands);
		}
		if (!cYaml.contains("Chicken")) {
			int[] Amounts = {16, 32, 64};
			String[] Rewards = {"1x Diamond", "2x Diamond", "5x Diamond"};
			String[] tierOne = {"give %player% diamond 1"};
			String[] tierTwo = {"give %player% diamond 2"};
			String[] tierThree = {"give %player% diamond 5"};
			String[][] Commands = {tierOne, tierTwo, tierThree};
			challengeHook("Chicken", "Smelt", "COOKED_CHICKEN", 3, Amounts, "COOKED_CHICKEN", "Cook some Chicken", "Cook %amount% Chicken (%collected%/%amount%)", Rewards, Commands);
		}
		if (!cYaml.contains("Cow"))
		{
			int[] Amounts = {16, 32, 64};
			String[] Rewards = {"1x Diamond", "3x Diamond", "10x Diamond"};
			String[] tierOne = {"give %player% diamond 1"};
			String[] tierTwo = {"give %player% diamond 3"};
			String[] tierThree = {"give %player% diamond 10"};
			String[][] Commands = {tierOne, tierTwo, tierThree};
			challengeHook("Cow", "Kill", "COW", 3, Amounts, "COW_SPAWN_EGG", "Kill some Cows", "Kill %amount% Cows (%collected%/%amount%)", Rewards, Commands);
		}
		if (!cYaml.contains("Spend Time")) {
			int[] Amounts = {3600, 10800, 36000};
			String[] Rewards = {"3x Diamond & 10x Coal", "10x Diamond & 32x Coal", "32x Diamond & 128x Coal"};
			String[] tierOne = {"give %player% diamond 3", "give %player% coal 10"};
			String[] tierTwo = {"give %player% diamond 10", "give %player% coal 32"};
			String[] tierThree = {"give %player% diamond 32", "give %player% coal 128"};
			String[][] Commands = {tierOne, tierTwo, tierThree};
			challengeHook("Spend Time", "Playtime", "Seconds", 3, Amounts, "CLOCK", "Spend some time", "Achieve %amount% seconds of playtime (%collected%/%amount%)", Rewards, Commands);
		}
	}
	
	void challengeHook(String name, String type, String object, int tierNumber, int[] amounts, String icon, String desc, String task, String[] rewards, String[][] commands) {
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
			cYaml.set(name + ".Tier" + String.valueOf(i + 1) + ".Commands", Arrays.asList(commands[i])); 
		}
		
	}
	
	public void setPremium() {
		if (!vYaml.contains("Oak Log")) {
			int[] Amounts = {32, 96, 256, 1024};
			String[] Rewards = {"Stone Axe", "Iron Axe", "Diamond Axe", "Netherite Axe"};
			String[] tierOne = {"give %player% minecraft:stone_axe 1"};
			String[] tierTwo = {"give %player% minecraft:iron_axe 1"};
			String[] tierThree = {"give %player% minecraft:diamond_axe 1"};
			String[] tierFour = {"give %player% minecraft:netherite_axe 1"};
			String[][] Commands = {tierOne, tierTwo, tierThree, tierFour};
			premiumHook("Oak Log", "Mine", "OAK_LOG", 4, Amounts, "OAK_LOG", "Cut some Oak Trees", "Collect %amount% Oak Log (%collected%/%amount%)", Rewards, Commands);
		}
		if (!vYaml.contains("Bookshelf")) {
			int[] Amounts = {8, 16};
			String[] Rewards = {"Sharpness II Book", "Protection II Book"};
			String[] tierOne = {"give %player% enchanted_book{StoredEnchantments:[{id:sharpness,lvl:2}]} 1"};
			String[] tierTwo = {"give %player% enchanted_book{StoredEnchantments:[{id:protection,lvl:2}]} 1"};
			String[][] Commands = {tierOne, tierTwo};
			premiumHook("Bookshelf", "Craft", "BOOKSHELF", 2, Amounts, "BOOKSHELF", "Craft some Bookshelves", "Craft %amount% Bookshelves (%collected%/%amount%)", Rewards, Commands);
		}
		if (!vYaml.contains("Zombie")) {
			int[] Amounts = {5, 10, 20};
			String[] Rewards = {"Stone Sword", "Iron Sword", "Diamond Sword"};
			String[] tierOne = {"give %player% minecraft:stone_sword 1"};
			String[] tierTwo = {"give %player% minecraft:iron_sword 1"};
			String[] tierThree = {"give %player% minecraft:diamond_sword 1"};
			String[][] Commands = {tierOne, tierTwo, tierThree};
			premiumHook("Zombie", "Kill", "ZOMBIE", 3, Amounts, "ZOMBIE_HEAD", "Kill some Zombies", "Kill %amount% Zombies (%collected%/%amount%)", Rewards, Commands);
		}
	}
	
	void premiumHook(String name, String type, String object, int tierNumber, int[] amounts, String icon, String desc, String task, String[] rewards, String[][] commands) {
		vYaml.set(name + ".Type", type);
		vYaml.set(name + ".Object", object);
		vYaml.set(name + ".TierNumber", tierNumber);
		for (int i = 0; i < tierNumber; i++) {
			vYaml.set(name + ".Tier" + String.valueOf(i + 1) + ".Amount", amounts[i]);
		}
		vYaml.set(name + ".Icon", icon);
		vYaml.set(name + ".Description", desc);
		vYaml.set(name + ".Task", task);
		for (int i = 0; i < tierNumber; i++) {
			vYaml.set(name + ".Tier" + String.valueOf(i + 1) + ".Reward", rewards[i]);
		}
		for (int i = 0; i < tierNumber; i++) {
			vYaml.set(name + ".Tier" + String.valueOf(i + 1) + ".Commands", Arrays.asList(commands[i])); 
		}
		
	}
}
