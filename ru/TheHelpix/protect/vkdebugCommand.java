package ru.TheHelpix.protect;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class vkdebugCommand implements CommandExecutor {
    Main plugin;
    public vkdebugCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        if ((sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("vk.player")));
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[hAuth] &6Использование: \n&c/vk_debug on &3- Включение дебага.&c \n/vk_debug off &3- Выключение дебага"));
        }
        if (args.length <= 0) {
            return true;
        }
        if (args[0].equalsIgnoreCase("on")){
            plugin.getConfig().set("vk.debug", true);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[hAuth] &2Дебаг включен!"));
        }

        if (args[0].equalsIgnoreCase("off")) {
            plugin.getConfig().set("vk.debug", false);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[hAuth] &2Дебаг выключен!"));
        }
        return true;
    }
}
