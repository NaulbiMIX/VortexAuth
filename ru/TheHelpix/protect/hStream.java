package ru.TheHelpix.protect;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class hStream implements CommandExecutor {
    Main plugin;
    public hStream(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        if(!(sender instanceof Player)) {
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("hstream.console")));
          return true;
        }

        if (sender.hasPermission(plugin.getConfig().getString("hstream.perm"))) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("hstream.help")));
                return true;
            }

            if (args.length <= 0) {
                return true;
            }

            if (args[0].equalsIgnoreCase("add")) {
                if (args.length > 1) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',  "&6[Стримы] " + "&c" + sender.getName() + "&e снимает стрим. Заходим к нему на стрим :D\n&6[&lСсылка&6] &6" + args[1]));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("hstream.sended")));
                    return true;
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("hstream.help").replace("%сmd%",  ""+cmd)));
                }
                return true;
            }
            return true;
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("hstream.not_perm").replace("%сmd%", ""+cmd)));
        }
        return true;
    }
}
