package nl.me.shopkeeperslist.commands;

import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import nl.me.shopkeeperslist.utils.MessageUtils;
import nl.me.shopkeeperslist.utils.PageNavigatorUtils;
import nl.me.shopkeeperslist.utils.ShopDisplayUtils;
import nl.me.shopkeeperslist.utils.ShopFindingUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FindShopsCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            MessageUtils.onOnlyPlayers(commandSender);
            return true;
        }
        if (!commandSender.hasPermission("shopkeeperslist.findshops")) {
            MessageUtils.onNoPermission(commandSender, "shopkeeperslist.findshops");
            return true;
        }
        if (args.length == 0) {
            ShopDisplayUtils.displayAllShopkeepersForPage(commandSender, 1);
            return true;
        }
        if (args.length < 2) {
            MessageUtils.onWrongUsage(commandSender, "/findshops <selling/buying/player> <item/player>");
            return true;
        }
        if (args[0].equalsIgnoreCase("selling")) {
            ShopDisplayUtils.displayShopkeepersForSellingItemForPage(commandSender, args[1], 1);
        } else if (args[0].equalsIgnoreCase("buying")) {
            ShopDisplayUtils.displayShopkeepersForBuyingItemForPage(commandSender, args[1], 1);
        } else if (args[0].equalsIgnoreCase("player")) {
            ShopDisplayUtils.displayShopkeepersForPlayerForPage(commandSender, args[1], 1);
        } else {
            MessageUtils.onWrongUsage(commandSender, "/findshops <selling/buying/player> <item/player>");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1) {
            String[] options = {"selling", "buying", "player"};
            String filter = args[0].toLowerCase();
            List<String> optionsList = new ArrayList<>();
            for (String option : options) {
                if (option.toLowerCase().startsWith(filter)) {
                    optionsList.add(option);
                }
            }
            return optionsList;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("player")) {
            String filter = args[1].toLowerCase();
            List<String> playerNames = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getName().toLowerCase().startsWith(filter)) {
                    playerNames.add(onlinePlayer.getName());
                }
            }
            return playerNames;
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("selling") || args[0].equalsIgnoreCase("buying"))) {
            List<String> itemNames = new ArrayList<>();
            String filter = args[1].toLowerCase();
            for (Material material : Material.values()) {
                String itemName = material.name();
                if (itemName.toLowerCase().startsWith(filter)) {
                    itemNames.add(itemName.toLowerCase());
                }
            }
            for (Enchantment enchantment : Enchantment.values()) {
                String itemName = enchantment.getKey().getKey();
                if (itemName.toLowerCase().startsWith(filter)) {
                    itemNames.add(itemName.toLowerCase());
                }
            }
            return itemNames;
        } else {
            return Collections.emptyList();
        }
    }
}
