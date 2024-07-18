package nl.me.shopkeeperslist.commands;

import nl.me.shopkeeperslist.ShopkeepersList;
import nl.me.shopkeeperslist.utils.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PluginCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 0) {
            MessageUtils.onHelpMessage(commandSender);
            return true;
        } else if (args[0].equalsIgnoreCase("reload")) {
            if (commandSender.hasPermission("shopkeeperslist.reload") || commandSender.isOp()) {
                ShopkeepersList.getPlugin().reloadConfig();
                MessageUtils.onConfigReloaded(commandSender);
            } else {
                MessageUtils.onNoPermission(commandSender, "shopkeeperslist.reload");
            }
            return true;
        } else if (args[0].equalsIgnoreCase("help")) {
            MessageUtils.onHelpMessage(commandSender);
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> completions = new ArrayList<>();
        if (commandSender.hasPermission("shopkeeperslist.reload")) {
            completions.add("reload");
        }
        completions.add("help");
        return completions;
    }
}
