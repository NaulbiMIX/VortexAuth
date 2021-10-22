package ru.TheHelpix.protect;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoader;

public class hConsoleCmd implements CommandExecutor {
    Main plugin;

    public hConsoleCmd(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        if(!(sender instanceof Player)) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.console.help")));
            }
            if (args.length <= 0) {
                return true;
            }
            if (args[0].equalsIgnoreCase("add")) {
                if (args.length > 1) {
                    if (plugin.getad(args[1]) > 1) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[hAuth] &сТакой Администратор уже существует!"));
                    } else {
                        plugin.addAdmin(args[1], Integer.parseInt(args[2]));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[hAuth] &2Вы добавили Администратора &c" + args[1] + "&2, его ВК: &9" + "https://vk.com/id" + args[2]));
                    }
                    return true;
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[hAuth] &2Использование: /hprotect add ник &9idvk&2."));
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                if (args.length > 1) {
                    if (plugin.getad(args[1]) < 1) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[hAuth] &сТакого Администратора не существует!"));
                    } else {
                        plugin.removeAdmin(args[1]);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[hAuth] &2Вы удалили Администратора &c" + args[1]));
                    }
                    return true;
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[hAuth] &2Использование: /hprotect remove ник."));
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                if (args.length > 1) {
                    plugin.reloadConfig();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[hAuth] &2Плагин перезагружен!"));
                    return true;
                }
                return true;
            }
            return true;
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.console.isPlayer")));
        sendMessage((Player) sender);
        return true;
    }
    public void sendMessage(Player p)
    {
        TextComponent VK = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&9&lМой ВК"));
        //VK.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://vk.com/sayhe"));
        //VK.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/gamemode 1"));
        VK.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, "C:/"));
        VK.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, "C:/"));
        p.spigot().sendMessage( VK );
    }
}
