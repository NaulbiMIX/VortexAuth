package ru.TheHelpix.protect;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class apiCmd implements CommandExecutor {
    Main plugin;
    public apiCmd(Main plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[NeedPanel] &2Использование: /"+cmd+" register пароль."));
            return true;
        }
        if (args.length <= 0) {
            return true;
        }
        if (args[0].equalsIgnoreCase("register")) {
            if (args.length > 1) {
                System.out.println(args[1]);
                plugin.addUser(sender.getName(), args[1]);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[NeedPanel] &2Вы создали учётную запись с паролем &3"+args[1]));
                return true;
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[NeedPanel] &2Использование: /"+cmd+" register пароль."));
            }
            return true;
        }
        return true;
    }
}
