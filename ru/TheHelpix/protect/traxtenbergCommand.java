package ru.TheHelpix.protect;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class traxtenbergCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        Main plugin;
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[Август Трахтенберг] &4Нельзя получить бонус в консоли!"));
            return true;
        }
        final Player p = (Player)sender;
        if(Main.gived.contains((p.getName()))) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[Август Трахтенберг] &4Ты уже получил Бонус!"));
            return true;
        }
        return true;
    }
}
