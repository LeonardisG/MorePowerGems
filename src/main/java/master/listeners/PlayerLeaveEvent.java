package master.listeners;

import master.Keys;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveEvent implements Listener {

    /**
     * Cleans player PDC data when they leave the server,
     * so active ability flags don't persist on rejoin.
     */
    @EventHandler
    public void onPlayerLeaveEvent(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        player.closeInventory();

        player.getPersistentDataContainer().remove(Keys.AFFLUENCE_DISCOUNT);
        player.getPersistentDataContainer().remove(Keys.AFFLUENCE_DOUBLE_DROPS);
        player.getPersistentDataContainer().remove(Keys.WITHER_DAMAGE_REDUCTION);
        player.getPersistentDataContainer().remove(Keys.AMETHYST_TRAP);
        player.getPersistentDataContainer().remove(Keys.MAGIC_FLY);
    }
}
