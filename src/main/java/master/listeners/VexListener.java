package master.listeners;

import org.bukkit.entity.Player;
import org.bukkit.entity.Vex;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static master.Keys.EVOKER_OWNER;

public class VexListener implements Listener {

    @EventHandler
    public void onVex(EntityDamageByEntityEvent e) {

        if(e.getDamager() instanceof Vex vex && e.getEntity() instanceof Player victim) {
            PersistentDataContainer pdc = vex.getPersistentDataContainer();
            if(pdc.has(EVOKER_OWNER, PersistentDataType.STRING)) {
                String ownerUUID = pdc.get(EVOKER_OWNER, PersistentDataType.STRING);
                if(victim.getUniqueId().toString().equals(ownerUUID)) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
