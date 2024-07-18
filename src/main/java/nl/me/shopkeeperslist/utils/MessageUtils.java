package nl.me.shopkeeperslist.utils;

import nl.me.shopkeeperslist.ShopkeepersList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class MessageUtils {

    /**
     * Formats a message with color codes
     * @param commandSender The command sender to replace the placeholders for (null if console)
     * @param message The message to format
     * @return The formatted message
     */
    public static String formatMessage(CommandSender commandSender, String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void onHelpMessage(CommandSender commandSender) {
        FileConfiguration config = ShopkeepersList.getPlugin().getConfig();
        List<String> helpCommandLines = config.getStringList("help-command-lines");
        for (String line : helpCommandLines) {
            line  = line
                    .replace("{name}", ShopkeepersList.getPlugin().getDescription().getName())
                    .replace("{version}", ShopkeepersList.getPlugin().getDescription().getVersion())
                    .replace("{author}", ShopkeepersList.getPlugin().getDescription().getAuthors().get(0));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
        }
    }

    public static void onNoPermission(CommandSender sender, String permission) {
        sender.sendMessage(formatMessage(sender,
                ShopkeepersList.getPlugin().getConfig().getString("no-permission")
                        .replace("{permission}", permission)
        ));
    }

    public static void onWrongUsage(CommandSender sender, String usage) {
        sender.sendMessage(formatMessage(sender,
                ShopkeepersList.getPlugin().getConfig().getString("wrong-usage")
                        .replace("{usage}", usage)
        ));
    }

    public static void onOnlyPlayers(CommandSender sender) {
        sender.sendMessage(formatMessage(sender,
                ShopkeepersList.getPlugin().getConfig().getString("only-players")
        ));
    }

    public static void onConfigReloaded(CommandSender sender) {
        sender.sendMessage(formatMessage(sender,
                ShopkeepersList.getPlugin().getConfig().getString("config-reloaded")
        ));
    }

    public static void onTeleport(CommandSender sender) {
        sender.sendMessage(formatMessage(sender,
                ShopkeepersList.getPlugin().getConfig().getString("on-teleport")
        ));
    }

    public static void onNoShopsFound(CommandSender sender) {
        sender.sendMessage(formatMessage(sender,
                ShopkeepersList.getPlugin().getConfig().getString("no-shops-found")
        ));
    }

    public static void onNoShopsSellingItem(CommandSender sender, String item) {
        sender.sendMessage(formatMessage(sender,
                ShopkeepersList.getPlugin().getConfig().getString("no-shops-selling")
                        .replace("{item}", item)
        ));
    }

    public static void onNoShopsBuyingItem(CommandSender sender, String item) {
        sender.sendMessage(formatMessage(sender,
                ShopkeepersList.getPlugin().getConfig().getString("no-shops-buying")
                        .replace("{item}", item)
        ));
    }

    public static void onNoShopsForPlayer(CommandSender sender, String player) {
        sender.sendMessage(formatMessage(sender,
                ShopkeepersList.getPlugin().getConfig().getString("no-shops-player")
                        .replace("{player}", player)
        ));
    }

    public static void onUnknownError(CommandSender sender) {
        sender.sendMessage(formatMessage(sender,
                ShopkeepersList.getPlugin().getConfig().getString("unknown-error")
        ));
    }

}
