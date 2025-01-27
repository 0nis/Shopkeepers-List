package nl.me.shopkeeperslist.utils;

import com.nisovin.shopkeepers.api.shopkeeper.ShopType;
import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import com.nisovin.shopkeepers.api.shopkeeper.TradingRecipe;
import com.nisovin.shopkeepers.api.util.UnmodifiableItemStack;
import nl.me.shopkeeperslist.cache.OfflinePlayerCache;
import nl.me.shopkeeperslist.enums.ShopGUIType;
import nl.me.shopkeeperslist.inventoryHolders.ShopInventoryHolder;
import nl.me.shopkeeperslist.ShopkeepersList;
import org.bukkit.*;
import org.bukkit.block.Skull;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ShopDisplayUtils {

    private static final NamespacedKey IDENTIFIER_KEY = new NamespacedKey(ShopkeepersList.getPlugin(), "identifier");

    /**
     * Populates the GUI with all shops
     * @param commandSender The command sender to display the GUI to.
     * @param page The page of the GUI to display.
     */
    public static void displayAllShopkeepersForPage(CommandSender commandSender, int page) {
        List<Shopkeeper> shops = ShopFindingUtils.findAllShops();
        shops = removeDuplicateShopkeepers(shops);
        if (shops.isEmpty()) {
            MessageUtils.onNoShopsFound(commandSender);
            return;
        }
        Inventory gui = Bukkit.createInventory(
                new ShopInventoryHolder(ShopGUIType.ALL, null, page),
                36,
                "All shops"
        );
        gui = pupulateGUI(gui, shops, page);
        ((Player) commandSender).openInventory(gui);
    }

    /**
     * Displays the shopkeepers selling the given item in a GUI.
     * @param commandSender The command sender to display the GUI to.
     * @param item The item to find shops selling.
     * @param page The page of the GUI to display.
     */
    public static void displayShopkeepersForSellingItemForPage(CommandSender commandSender, String item, int page) {
        List<Shopkeeper> shops = ShopFindingUtils.findShopsSelling(item);
        shops = removeDuplicateShopkeepers(shops);
        if (shops.isEmpty()) {
            MessageUtils.onNoShopsSellingItem(commandSender, item);
            return;
        }
        Inventory gui = Bukkit.createInventory(
                new ShopInventoryHolder(ShopGUIType.SELLING_ITEM, item, page),
                36,
                "Shops selling " + item
        );
        gui = pupulateGUI(gui, shops, page);
        ((Player) commandSender).openInventory(gui);
    }

    /**
     * Displays the shopkeepers buying the given item in a GUI.
     * @param commandSender The command sender to display the GUI to.
     * @param item The item to find shops buying.
     * @param page The page of the GUI to display.
     */
    public static void displayShopkeepersForBuyingItemForPage(CommandSender commandSender, String item, int page) {
        List<Shopkeeper> shops = ShopFindingUtils.findShopsBuying(item);
        shops = removeDuplicateShopkeepers(shops);
        if (shops.isEmpty()) {
            MessageUtils.onNoShopsBuyingItem(commandSender, item);
            return;
        }
        Inventory gui = Bukkit.createInventory(
                new ShopInventoryHolder(ShopGUIType.BUYING_ITEM, item, page),
                36,
                "Shops buying " + item
        );
        gui = pupulateGUI(gui, shops, page);
        ((Player) commandSender).openInventory(gui);
    }

    /**
     * Displays the shopkeepers for the given player in a GUI.
     * @param commandSender The command sender to display the GUI to.
     * @param player The player's username to find shops for.
     * @param page The page of the GUI to display.
     */
    public static void displayShopkeepersForPlayerForPage(CommandSender commandSender, String player, int page) {
        List<Shopkeeper> shops = ShopFindingUtils.findShopsForPlayer(player);
        shops = removeDuplicateShopkeepers(shops);
        if (shops.isEmpty()) {
            MessageUtils.onNoShopsForPlayer(commandSender, player);
            return;
        }
        Inventory gui = Bukkit.createInventory(
                new ShopInventoryHolder(ShopGUIType.PLAYER, player, page),
                36,
                "Shops for " + player
        );
        gui = pupulateGUI(gui, shops, page);
        ((Player) commandSender).openInventory(gui);
    }

    /**
     * Removes duplicate shopkeepers from the given list.
     * @param shopkeepers The list of shopkeepers to remove duplicates from.
     * @return The list of shopkeepers without duplicates.
     */
    public static List<Shopkeeper> removeDuplicateShopkeepers(List<Shopkeeper> shopkeepers) {
        List<Shopkeeper> uniqueShopkeepers = new ArrayList<>();
        for (Shopkeeper shopkeeper : shopkeepers) {
            if (!uniqueShopkeepers.contains(shopkeeper)) {
                uniqueShopkeepers.add(shopkeeper);
            }
        }
        return uniqueShopkeepers;
    }

    /**
     * Populates the given GUI with the shopkeepers' heads and page navigation items.
     * @param gui The GUI to populate: {@link Inventory} with {@link ShopInventoryHolder}.
     * @param shops The list of shopkeepers to display.
     * @param page The page of the GUI to display.
     * @return The GUI with the shopkeepers' heads and page navigation items.
     */
    public static Inventory pupulateGUI(Inventory gui, List<Shopkeeper> shops, int page) {
        int pageSize = 27; // Number of shopkeepers per page. Magic number, but sue me.
        int totalPages = (int) Math.ceil((double) shops.size() / pageSize);

        // Calculate the starting and ending index of the shopkeepers to display on the current page
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, shops.size());

        // Populate the GUI with shopkeepers' heads
        for (int i = startIndex; i < endIndex; i++) {
            Shopkeeper shopkeeper = shops.get(i);
            ItemStack headItem = ShopDisplayUtils.createHeadItem(shopkeeper);
            gui.addItem(headItem);
        }

        // Add the "Next" item to navigate to the next page, if there are more pages
        if (page < totalPages) {
            ItemStack nextPageItem = PageNavigatorUtils.createNextPageItem();
            gui.setItem(35, nextPageItem);
        }

        // Add the "Previous" item to navigate to the previous page, if there are previous pages
        if (page > 1) {
            ItemStack previousPageItem = PageNavigatorUtils.createPreviousPageItem();
            gui.setItem(27, previousPageItem);
        }

        return gui;
    }

    /**
     * Gets the head of the player who owns the shopkeeper.
     * @param shopkeeper The shopkeeper to get the head of.
     * @return The head of the shopkeeper.
     */
    public static ItemStack getHeadItem(Shopkeeper shopkeeper) {
        UUID uuid = ShopFindingUtils.findShopOwnerUUID(shopkeeper);
        if (uuid == null) return new ItemStack(Material.PLAYER_HEAD, 1);
        OfflinePlayer offlinePlayer = OfflinePlayerCache.getOfflinePlayerByUUID(uuid);
        ItemStack headItem = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) headItem.getItemMeta();
        if (offlinePlayer.hasPlayedBefore()) skullMeta.setOwningPlayer(offlinePlayer);
        headItem.setItemMeta(skullMeta);
        return headItem;
    }

    /**
     * Creates a head item for the given shopkeeper, with the shopkeeper's name as title and coordinates and trading recipes as lore.
     * @param shopkeeper The shopkeeper to create the head item for.
     * @return The head item of the shopkeeper.
     */
    public static ItemStack createHeadItem(Shopkeeper shopkeeper) {
        ItemStack headItem = getHeadItem(shopkeeper);
        ItemMeta itemMeta = headItem.getItemMeta();
        FileConfiguration config = ShopkeepersList.getPlugin().getConfig();

        NamespacedKey idKey = new NamespacedKey(ShopkeepersList.getPlugin(), "shopkeeper_id");
        assert itemMeta != null;
        itemMeta.getPersistentDataContainer().set(IDENTIFIER_KEY, PersistentDataType.STRING, "SHOPKEEPERSLIST_SHOPKEEPER");
        itemMeta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, shopkeeper.getUniqueId().toString());

        String name = shopkeeper.getName();
        String displayNameTemplate = config.getString("shopkeeper-display.name.custom-name", "&e&l{name}");
        String displayName = name.isEmpty() ? config.getString("shopkeeper-display.name.default-name", "&e&lShopkeeper") : displayNameTemplate.replace("{name}", name);
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));

        List<String> lore = buildLore(shopkeeper, config);
        itemMeta.setLore(lore);

        headItem.setItemMeta(itemMeta);
        return headItem;
    }

    /**
     * Builds the lore for the given shopkeeper using the given configuration.
     * @param shopkeeper The shopkeeper to build the lore for.
     * @param config The configuration to use for building the lore.
     * @return The lore for the given shopkeeper.
     */
    private static List<String> buildLore(Shopkeeper shopkeeper, FileConfiguration config) {
        List<String> loreLines = config.getStringList("shopkeeper-display.lore-lines");
        List<String> lore = new ArrayList<>();

        for (String line : loreLines) {
            line = line.replace("{owner}", ShopFindingUtils.findShopOwnerName(shopkeeper))
                    .replace("{world}", shopkeeper.getWorldName())
                    .replace("{X}", String.valueOf(shopkeeper.getX()))
                    .replace("{Y}", String.valueOf(shopkeeper.getY()))
                    .replace("{Z}", String.valueOf(shopkeeper.getZ()));

            if (line.contains("{trades}")) {
                List<String> trades = formatTrades((List<TradingRecipe>) shopkeeper.getTradingRecipes(null), config);
                for (String trade : trades) {
                    lore.add(trade);
                }
                continue;
            }

            lore.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        return lore;
    }

    /**
     * Formats the given trading recipes into a list of strings using the given configuration.
     * @param tradingRecipes The trading recipes to format.
     * @param config The configuration to use for formatting the trading recipes.
     * @return The list of formatted trading recipes.
     */
    public static List<String> formatTrades(List<TradingRecipe> tradingRecipes, FileConfiguration config) {
        List<String> trades = new ArrayList<>();

        if (tradingRecipes.isEmpty()) {
            return Collections.singletonList(ChatColor.translateAlternateColorCodes('&', config.getString("shopkeeper-display.trades.no-trades", "&7No trades found.")));
        }

        for (TradingRecipe tradingRecipe : tradingRecipes) {
            String loreLine = formatSingleTrade(tradingRecipe, config);
            trades.add(ChatColor.translateAlternateColorCodes('&', loreLine));
        }

        return trades;
    }

    /**
     * Formats a single trading recipe into a string using the given configuration.
     * @param tradingRecipe The trading recipe to format.
     * @param config The configuration to use for formatting the trading recipe.
     * @return The formatted trading recipe.
     */
    private static String formatSingleTrade(TradingRecipe tradingRecipe, FileConfiguration config) {
        String outOfStockPrefix = tradingRecipe.isOutOfStock() ? config.getString("shopkeeper-display.trades.trade-format.out-of-stock-prefix", "&c&lX&r ") : "";
        String costDivider = config.getString("shopkeeper-display.trades.trade-format.cost-divider", " &7+&r ");
        String divider = config.getString("shopkeeper-display.trades.trade-format.divider", " &7&l->&e ");
        String costFormat = config.getString("shopkeeper-display.trades.trade-format.cost-1", "&a{amount}x {item}&r");
        String resultFormat = config.getString("shopkeeper-display.trades.trade-format.result", "&e{amount}x {item}&r");

        // Format the cost and result items using appropriate formats
        String cost1 = formatTradeItem(tradingRecipe.getItem1(), costFormat);
        String cost2 = tradingRecipe.getItem2() != null ? formatTradeItem(tradingRecipe.getItem2(), costFormat) : "";
        String result = formatTradeItem(tradingRecipe.getResultItem(), resultFormat);

        // Construct the complete trade string
        String trade = outOfStockPrefix +
                cost1 +
                (tradingRecipe.getItem2() != null ? costDivider + cost2 : "") +
                divider +
                result;
        return trade;
    }

    /**
     * Formats the given item using the given item format.
     * @param item The item to format.
     * @param itemFormat The format to use for formatting the item.
     * @return The formatted item.
     */
    private static String formatTradeItem(UnmodifiableItemStack item, String itemFormat) {
        if (item == null) return "";
        return itemFormat.replace("{amount}", Integer.toString(item.getAmount()))
                .replace("{item}", getItemName(item));
    }

    /**
     * Gets the name of the given item. If the item has a display name, that is returned. Otherwise, the item's type
     * @param item The item to get the name of.
     * @return The name of the given item.
     */
    public static String getItemName(UnmodifiableItemStack item) {
        if (item == null) {
            return null;
        }

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null && itemMeta.hasDisplayName()) {
            return itemMeta.getDisplayName();
        }

        // If the item is an enchanted book, display the enchantments
        if (item.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) itemMeta;
            if (enchantmentStorageMeta.hasStoredEnchants()) {
                StringBuilder enchantmentString = new StringBuilder();
                Map<Enchantment, Integer> enchantments = enchantmentStorageMeta.getStoredEnchants();
                for (Enchantment key : enchantments.keySet()) {
                    enchantmentString.append(key.getKey().getKey()).append(" ").append(enchantments.get(key)).append(", ");
                }
                enchantmentString.delete(enchantmentString.length() - 2, enchantmentString.length());
                return enchantmentString.toString();
            }
        }

        // If the item has enchantments, display the number of enchantments
        Map<Enchantment, Integer> enchantments = item.getEnchantments();
        if (!enchantments.isEmpty()) {
            StringBuilder itemString = new StringBuilder();
            itemString.append(item.getType().name().toLowerCase()).append(" &o(+").append(enchantments.size()).append(" enchantments)");
            return itemString.toString();
        }

        return item.getType().name().toLowerCase();
    }

    /**
     * Validates if the given item is a shopkeeper item by checking if it has the correct identifier.
     * @param item The item to validate.
     * @return true if the item is a shopkeeper item, false if the item is not a shopkeeper item.
     */
    public static boolean isShopkeeperItem(ItemStack item) {
        if (item == null) return false;
        if (item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();

            if (itemMeta.getPersistentDataContainer().has(IDENTIFIER_KEY, PersistentDataType.STRING)) {
                String identifier = itemMeta.getPersistentDataContainer().get(IDENTIFIER_KEY, PersistentDataType.STRING);

                if (identifier.equals("SHOPKEEPERSLIST_SHOPKEEPER")) {
                    return true;
                }
            }
        }
        return false;
    }
}
