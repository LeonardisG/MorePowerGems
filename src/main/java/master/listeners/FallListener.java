package master.listeners;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.NamespacedKeyManager;
import dev.iseal.powergems.managers.SingletonManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class FallListener implements Listener {

    private static final String BREZZE_GEM_POWER = "Brezze";

    private final GemManager gm = SingletonManager.getInstance().gemManager;
    private final NamespacedKeyManager nkm = SingletonManager.getInstance().namespacedKeyManager;

    @EventHandler
    public void onFall(EntityDamageEvent e) {
        if (e.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        if (!(e.getEntity() instanceof Player player)) {
            return;
        }

        if (hasBrezzePowerGem(player)) {
            e.setCancelled(true);
        }
    }

    private boolean hasBrezzePowerGem(Player player) {
        for (ItemStack item : gm.getPlayerGems(player)) {
            if (item == null || !item.hasItemMeta()) {
                continue;
            }

            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                continue;
            }

            String gemPower = meta.getPersistentDataContainer()
                    .get(nkm.getKey("gem_power"), PersistentDataType.STRING);

            if (BREZZE_GEM_POWER.equals(gemPower)) {
                return true;
            }
        }
        return false;
    }
}