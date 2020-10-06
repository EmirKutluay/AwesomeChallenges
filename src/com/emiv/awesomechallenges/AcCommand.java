package com.emiv.awesomechallenges;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.md_5.bungee.api.ChatColor;

public class AcCommand implements CommandExecutor {

	Main plugin;
	public AcCommand(Main instance) {
		plugin = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String correctUse = "&8----" + plugin.getConfig().getString("ServerPrefix") + "&8----" + "\n" + "&d/ac reload &8- &eReload all configurations";
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (args.length == 1) {
				if (args[0].equals("reload")) {
					if (p.hasPermission("awesomechallenges.reload")) {
						reloadCommand();
						plugin.sendMsgWithPrefix(plugin.getConfig().getString("ConfigsReloaded"), p);
					} else {
						plugin.sendMsgWithPrefix(plugin.getConfig().getString("NoPermission"), p);
					}
				} else {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', correctUse));
				}
			} else {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', correctUse));
			}
		} else {
			if (args[0].equals("reload")) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("ServerPrefix") + " " + plugin.getConfig().getString("ConfigsReloaded")));
				reloadCommand();
			}
		}
		return false;
	}

	
	public void reloadCommand() {
		Plugin pl = Bukkit.getServer().getPluginManager().getPlugin("AwesomeChallenges");
		plugin.reloadConfig();
		pl.getPluginLoader().disablePlugin(pl);
		pl.getPluginLoader().enablePlugin(pl);
	}

}
