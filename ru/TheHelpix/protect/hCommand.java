package ru.TheHelpix.protect;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class hCommand implements CommandExecutor {
    String message = null;
    Main plugin;

    public hCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[hBroadcast] &4Нельзя выполнять в консоли."));
            return true;
        }
        if (sender.getName().equals(plugin.getConfig().getString("cbc.presedent"))) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.cmd")));
            }
            if (args.length <= 0) {
                return true;
            }
            message = args[0];
            for (int ii = 1; ii != args.length; ++ii) {
                message = String.valueOf(message) + " " + args[ii];
            }
            if (args.length > 0) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("cbc.broadcast") + message));
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.not_perm")));
        }
        return true;
    }
}

