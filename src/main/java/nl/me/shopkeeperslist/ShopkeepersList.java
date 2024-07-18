package nl.me.shopkeeperslist;

import nl.me.shopkeeperslist.commands.FindShopsCommand;
import nl.me.shopkeeperslist.commands.PluginCommand;
import nl.me.shopkeeperslist.listeners.InventoryClickListener;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ShopkeepersList extends JavaPlugin implements CommandExecutor, TabCompleter {

    private static ShopkeepersList plugin;

    @Override
    public void onEnable() {
        plugin = this;
        Bukkit.getLogger().info("ShopkeepersList has been enabled.");

        saveDefaultConfig();

        setCommandExecutor("findshops", new FindShopsCommand());
        setCommandExecutor("shopkeeperslist", new PluginCommand());
        registerEvent(new InventoryClickListener());
    }

    private void setCommandExecutor(String command, CommandExecutor executor) {
        org.bukkit.command.PluginCommand pluginCommand = plugin.getCommand(command);
        if (pluginCommand != null) pluginCommand.setExecutor(executor);
        else Bukkit.getLogger().warning("Could not set command executor for " + command + "!");
    }

    private void registerEvent(Listener listener) {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(listener, this);
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("ShopkeepersList has been disabled.");
    }

    public static ShopkeepersList getPlugin() {
        return plugin;
    }
}
