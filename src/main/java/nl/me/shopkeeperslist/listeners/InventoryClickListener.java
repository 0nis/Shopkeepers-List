package nl.me.shopkeeperslist.listeners;

import com.nisovin.shopkeepers.api.ShopkeepersAPI;
import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import nl.me.shopkeeperslist.ShopkeepersList;
import nl.me.shopkeeperslist.enums.ShopGUIType;
import nl.me.shopkeeperslist.inventoryHolders.ShopInventoryHolder;
import nl.me.shopkeeperslist.utils.MessageUtils;
import nl.me.shopkeeperslist.utils.PageNavigatorUtils;
import nl.me.shopkeeperslist.utils.ShopDisplayUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();

        // If the player clicked in an inventory that is not a shopkeepers list inventory, return
        if (!(inventory.getHolder() instanceof ShopInventoryHolder)) {
            return;
        }

        event.setCancelled(true);
        player.updateInventory();

        ShopInventoryHolder holder = (ShopInventoryHolder) inventory.getHolder();

        // If the player clicked on the next page item, display the next page
        if (PageNavigatorUtils.isNextPageItem(event.getCurrentItem())) {
            int nextPage = holder.getPage() + 1;
            navigateToNewPage(player, holder, nextPage);
        }

        // If the player clicked on the previous page item, display the previous page
        if (PageNavigatorUtils.isPreviousPageItem(event.getCurrentItem())) {
            int previousPage = holder.getPage() - 1;
            navigateToNewPage(player, holder, previousPage);
        }

        // If the player clicked on a shopkeeper item:
        // - If the player right-clicked, teleport the player to the shopkeeper locations
        // - If the player left-clicked, open the shopkeeper inventory
        if (ShopDisplayUtils.isShopkeeperItem(event.getCurrentItem())) {
            if (event.isRightClick()) {
                if (!player.hasPermission("shopkeeperslist.teleport") && !player.isOp()) {
                    player.closeInventory();
                    MessageUtils.onNoPermission(player, "shopkeeperslist.teleport");
                } else {
                    teleportToShopkeeper(player, event.getCurrentItem());
                }
            } else {
                if (!player.hasPermission("shopkeeperslist.openremotely") && !player.isOp()) {
                    player.closeInventory();
                    MessageUtils.onNoPermission(player, "shopkeeperslist.openremotely");
                } else {
                    openShopkeeperInventory(player, event.getCurrentItem());
                }
            }
        }
    }

    /**
     * Navigates to a new page in the shopkeepers list inventory
     * @param player The player to navigate for
     * @param holder The inventory holder
     * @param page The page to navigate to
     */
    private static void navigateToNewPage(Player player, ShopInventoryHolder holder, int page) {
        ShopGUIType type = holder.getTYPE();
        String value = holder.getVALUE();

        switch (type) {
            case SELLING_ITEM:
                ShopDisplayUtils.displayShopkeepersForSellingItemForPage(player, value, page);
                break;
            case BUYING_ITEM:
                ShopDisplayUtils.displayShopkeepersForBuyingItemForPage(player, value, page);
                break;
            case PLAYER:
                ShopDisplayUtils.displayShopkeepersForPlayerForPage(player, value, page);
                break;
            case ALL:
                ShopDisplayUtils.displayAllShopkeepersForPage(player, page);
                break;
        }
    }

    /**
     * Teleports the player to the shopkeeper location
     * @param player The player to teleport
     * @param item The shopkeeper item that was clicked
     */
    private static void teleportToShopkeeper(Player player, ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey shopkeeperKey = new NamespacedKey(ShopkeepersList.getPlugin(), "shopkeeper_id");

        if (container.has(shopkeeperKey, PersistentDataType.STRING)) {
            String shopkeeperId = container.get(shopkeeperKey, PersistentDataType.STRING);
            Shopkeeper shopkeeper = ShopkeepersAPI.getShopkeeperRegistry().getShopkeeperByUniqueId(UUID.fromString(shopkeeperId));
            if (shopkeeper == null) {
                MessageUtils.onUnknownError(player);
                Bukkit.getLogger().warning("Could not find shopkeeper with id " + shopkeeperId + " for shopkeeper teleportation.");
                return;
            }
            Location location = shopkeeper.getLocation();
            player.teleport(location);
            MessageUtils.onTeleport(player);
        } else {
            MessageUtils.onUnknownError(player);
            Bukkit.getLogger().warning("Could not find shopkeeper id for shopkeeper teleportation.");
        }
    }

    /**
     * Opens the shopkeeper inventory for the player
     * @param player The player to open the inventory for
     * @param item The shopkeeper item that was clicked
     */
    private static void openShopkeeperInventory(Player player, ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey shopkeeperKey = new NamespacedKey(ShopkeepersList.getPlugin(), "shopkeeper_id");

        if (container.has(shopkeeperKey, PersistentDataType.STRING)) {
            String shopkeeperId = container.get(shopkeeperKey, PersistentDataType.STRING);
            if (shopkeeperId == null) {
                MessageUtils.onUnknownError(player);
                Bukkit.getLogger().warning("Could not find shopkeeper id for shopkeeper inventory opening.");
                return;
            }
            Shopkeeper shopkeeper = ShopkeepersAPI.getShopkeeperRegistry().getShopkeeperByUniqueId(UUID.fromString(shopkeeperId));

            if (shopkeeper == null) {
                MessageUtils.onUnknownError(player);
                Bukkit.getLogger().warning("Could not find shopkeeper with id " + shopkeeperId + " for shopkeeper inventory opening.");
                return;
            }

            shopkeeper.openTradingWindow(player);
        } else {
            MessageUtils.onUnknownError(player);
            Bukkit.getLogger().warning("Could not find shopkeeper id for shopkeeper inventory opening.");
        }
    }

}
