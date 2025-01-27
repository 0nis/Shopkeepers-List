package nl.me.shopkeeperslist.utils;

import com.nisovin.shopkeepers.api.ShopkeepersAPI;
import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import com.nisovin.shopkeepers.api.shopkeeper.admin.AdminShopkeeper;
import com.nisovin.shopkeepers.api.shopkeeper.player.PlayerShopkeeper;
import com.nisovin.shopkeepers.api.util.UnmodifiableItemStack;
import nl.me.shopkeeperslist.cache.OfflinePlayerCache;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShopFindingUtils {

    /**
     * Finds all shops on the server.
     * @return A list of all shops on the server.
     */
    public static List<Shopkeeper> findAllShops() {
        return new ArrayList<>(ShopkeepersAPI.getShopkeeperRegistry().getAllShopkeepers());
    }

    /**
     * Finds all shops selling the given item. Meaning the item is the result of the trading recipe.
     * @param item The item to find shops selling.
     * @return A list of shops selling the given item.
     */
    public static List<Shopkeeper> findShopsSelling(String item) {
        List<Shopkeeper> shops = new ArrayList<>();
        ShopkeepersAPI.getShopkeeperRegistry().getAllShopkeepers().forEach(shopkeeper -> {
            shopkeeper.getTradingRecipes(null).forEach(tradingRecipe -> {
                if (matchesItemOrEnchant(tradingRecipe.getResultItem(), item)) {
                    shops.add(shopkeeper);
                }
            });
        });
        return shops;
    }

    /**
     * Finds all shops buying the given item. Meaning the item is one of the ingredients of the trading recipe.
     * @param item The item to find shops buying.
     * @return A list of shops buying the given item.
     */
    public static List<Shopkeeper> findShopsBuying(String item) {
        List<Shopkeeper> shops = new ArrayList<>();
        ShopkeepersAPI.getShopkeeperRegistry().getAllShopkeepers().forEach(shopkeeper -> {
            shopkeeper.getTradingRecipes(null).forEach(tradingRecipe -> {
                if (matchesItemOrEnchant(tradingRecipe.getItem1(), item) || matchesItemOrEnchant(tradingRecipe.getItem2(), item)) {
                    shops.add(shopkeeper);
                }
            });
        });
        return shops;
    }

    /**
     * Finds all shops owned by the given player.
     * @param player The player to find shops for.
     * @return A list of shops owned by the given player.
     */
    public static List<Shopkeeper> findShopsForPlayer(String player) {
        List<Shopkeeper> shops = new ArrayList<>();
        OfflinePlayer playerObj = OfflinePlayerCache.getOfflinePlayer(player);
        if (playerObj == null || !playerObj.hasPlayedBefore()) return shops;
        ShopkeepersAPI.getShopkeeperRegistry().getPlayerShopkeepersByOwner(playerObj.getUniqueId()).forEach(shopkeeper -> {
            shops.add(shopkeeper);
        });
        return shops;
    }

    /**
     * Finds the owner of the given shop.
     * @param shopkeeper The shop to find the owner for.
     * @return The UUID of the owner of the given shop.
     */
    public static UUID findShopOwnerUUID(Shopkeeper shopkeeper) {
        if (!(shopkeeper instanceof PlayerShopkeeper)) return null;
        PlayerShopkeeper playerShopkeeper = (PlayerShopkeeper) shopkeeper;
        return playerShopkeeper.getOwnerUUID();
    }

    /**
     * Finds the name of the owner of the given shop.
     * @param shopkeeper The shop to find the owner for.
     * @return The name of the owner of the given shop.
     */
    public static String findShopOwnerName(Shopkeeper shopkeeper) {
        if (shopkeeper instanceof AdminShopkeeper) return "Admin";
        PlayerShopkeeper playerShopkeeper = (PlayerShopkeeper) shopkeeper;
        return playerShopkeeper.getOwnerName();
    }

    /**
     * Whether the name of the item or enchantment matches the given item or enchantment.
     * @param itemStack The item to check.
     * @param itemOrEnchant The item or enchantment to check.
     * @return True if the name of the item or enchantment matches the given item or enchantment.
     */
    public static boolean matchesItemOrEnchant(UnmodifiableItemStack itemStack, String itemOrEnchant) {
        if (itemStack == null) return false;
        if (itemStack.getType().name().equalsIgnoreCase(itemOrEnchant)) return true;
        if (itemStack.getItemMeta() instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) itemStack.getItemMeta();
            for (Enchantment enchantment : enchantmentStorageMeta.getStoredEnchants().keySet()) {
                if (enchantment.getKey().getKey().toLowerCase().contains(itemOrEnchant.toLowerCase())) return true;
            }
        }
        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            if (enchantment.getKey().getKey().toLowerCase().contains(itemOrEnchant.toLowerCase())) return true;
        }
        return false;
    }

}
