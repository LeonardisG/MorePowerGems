package master.listeners;

import master.gems.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import static dev.iseal.sealLib.SealLib.getPlugin;

public class PlayerLeaveEvent implements Listener {

    Plugin plugin = getPlugin();
    /**
     * Cleans player data when they leave the server.
     * This is meant so that metadata doesn't break things when they rejoin.
     */
    @EventHandler
    public void onPlayerLeaveEvent(PlayerQuitEvent e) {
        e.getPlayer().removeMetadata(AffluenceGem.DISCOUNT_METADATA_KEY, plugin);
        e.getPlayer().removeMetadata(AffluenceGem.DOUBLE_DROPS_METADATA_KEY, plugin);
        e.getPlayer().removeMetadata(WitherGem.WITHER_DAMAGE_REDUCTION_KEY, plugin);
        e.getPlayer().removeMetadata(WitherGem.WITHER_SKULL_KEY, plugin);
        e.getPlayer().removeMetadata(WitherGem.WITHER_SKULL_LEVEL_KEY, plugin);
    }
}
