package nl.me.shopkeeperslist.cache;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class OfflinePlayerCache {

    private static final Logger log = Logger.getLogger("OfflinePlayerCache");

    private static OfflinePlayer[] offlinePlayers;
    private static long expirationTimeMillis;

    private static OfflinePlayer[] getOfflinePlayers() {
        if (offlinePlayers == null || expirationTimeMillis < System.currentTimeMillis()) {
            offlinePlayers = Bukkit.getOfflinePlayers();

            // Set cache time of 10 minutes
            expirationTimeMillis = System.currentTimeMillis() + (600 * 1000);
        }

        return offlinePlayers;
    }

    public static OfflinePlayer getOfflinePlayer(String name) {
        OfflinePlayer[] offlinePlayers = getOfflinePlayers();

        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            if (Objects.requireNonNull(offlinePlayer.getName()).equalsIgnoreCase(name)) {
                return offlinePlayer;
            }
        }

        // If a player cant be found in cache request single player
        return Bukkit.getOfflinePlayer(name);
    }

    public static OfflinePlayer getOfflinePlayerByUUID(UUID uuid) {
        OfflinePlayer[] offlinePlayers = getOfflinePlayers();

        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            if (Objects.requireNonNull(offlinePlayer.getUniqueId()).equals(uuid)) {
                return offlinePlayer;
            }
        }

        // If a player cant be found in cache request single player
        return Bukkit.getOfflinePlayer(uuid);
    }
}
