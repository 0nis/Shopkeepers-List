package nl.me.shopkeeperslist.utils;

import nl.me.shopkeeperslist.ShopkeepersList;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class PageNavigatorUtils {

    private static final NamespacedKey IDENTIFIER_KEY = new NamespacedKey(ShopkeepersList.getPlugin(), "identifier");

    /**
     * Creates a next page item. This item is used to navigate to the next page in a shopkeepers list.
     * @return The next page item.
     */
    public static ItemStack createNextPageItem() {
        ItemStack nextPageItem = new ItemStack(Material.ARROW, 1);
        ItemMeta itemMeta = nextPageItem.getItemMeta();
        itemMeta.setDisplayName("Next Page");
        itemMeta.getPersistentDataContainer().set(IDENTIFIER_KEY, PersistentDataType.STRING, "SHOPKEEPERSLIST_NEXTPAGE");
        nextPageItem.setItemMeta(itemMeta);
        return nextPageItem;
    }

    /**
     * Creates a previous page item. This item is used to navigate to the previous page in a shopkeepers list.
     * @return The previous page item.
     */
    public static ItemStack createPreviousPageItem() {
        ItemStack previousPageItem = new ItemStack(Material.ARROW, 1);
        ItemMeta itemMeta = previousPageItem.getItemMeta();
        itemMeta.setDisplayName("Previous Page");
        itemMeta.getPersistentDataContainer().set(IDENTIFIER_KEY, PersistentDataType.STRING, "SHOPKEEPERSLIST_PREVIOUSPAGE");
        previousPageItem.setItemMeta(itemMeta);
        return previousPageItem;
    }

    /**
     * Validates if the given item is a next page item by checking if it has the correct identifier.
     * @param item The item to validate.
     * @return true if the item is a next page item, false if the item is not a next page item.
     */
    public static boolean isNextPageItem(ItemStack item) {
        if (item == null) return false;
        if (item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();

            if (itemMeta.getPersistentDataContainer().has(IDENTIFIER_KEY, PersistentDataType.STRING)) {
                String identifier = itemMeta.getPersistentDataContainer().get(IDENTIFIER_KEY, PersistentDataType.STRING);

                if (identifier.equals("SHOPKEEPERSLIST_NEXTPAGE")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Validates if the given item is a previous page item by checking if it has the correct identifier.
     * @param item The item to validate.
     * @return true if the item is a previous page item, false if the item is not a previous page item.
     */
    public static boolean isPreviousPageItem(ItemStack item) {
        if (item == null) return false;
        if (item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();

            if (itemMeta.getPersistentDataContainer().has(IDENTIFIER_KEY, PersistentDataType.STRING)) {
                String identifier = itemMeta.getPersistentDataContainer().get(IDENTIFIER_KEY, PersistentDataType.STRING);

                if (identifier.equals("SHOPKEEPERSLIST_PREVIOUSPAGE")) {
                    return true;
                }
            }
        }
        return false;
    }

}
