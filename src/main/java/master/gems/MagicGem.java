package master.gems;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import master.MPG;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vex;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;

import static dev.iseal.sealLib.SealLib.getPlugin;
import static master.Keys.EVOKER_OWNER;

public class MagicGem extends Gem {
    public MagicGem() {
        super("Magic");
    }

    public static String Fly_Metadata_Key = "MAGIC_FLY";

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    /** Summons evoker fangs that damage nearby entities. */
    @Override
    protected void rightClick(Player player, int level) {
        Location spawnLocation = player.getLocation();
        Vector direction = spawnLocation.getDirection().normalize();
        int numFangs =5 + level;
        for (int i = 0; i < numFangs; i++) {
            Location loc = spawnLocation.clone().add(direction.clone().multiply(i * 0.8));

            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                player.getWorld().spawn(loc, EvokerFangs.class, evokerFangs -> evokerFangs.setOwner(player));
            }, i * 2L);

        }

        player.getWorld().spawn(spawnLocation, EvokerFangs.class, evokerFangs -> evokerFangs.setOwner(player));
    }

    /** Grants temporary flight ability. */
    @Override
    protected void leftClick(Player player, int level) {
        int durationTicks = 20 * (10 + Math.min(level, 3) * 2); // 10s base, +2s per level, max 16s at level 3

        player.setMetadata(Fly_Metadata_Key, new FixedMetadataValue(getPlugin(), true));
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.LEVITATION,
                durationTicks,
                0,
                false,
                false
        ));

        Bukkit.getScheduler().runTaskLater(
                getPlugin(),
                () -> player.removeMetadata(Fly_Metadata_Key, getPlugin()),
                durationTicks
        );
    }

    /** Spawns vex mobs to fight for you. */
    @Override
    protected void shiftClick(Player player, int level) {
        int spawnNum = 5 + level;
        for(int a = 0; a < spawnNum; a++) {
            Location spawnLocation = player.getLocation().add(0, 1, 0);
            player.getWorld().spawn(spawnLocation, Vex.class, vex -> {
                vex.getPersistentDataContainer().set(EVOKER_OWNER, PersistentDataType.STRING ,player.getUniqueId().toString());
                var nearby = player.getNearbyEntities(15, 15, 15);
                for (var entity : nearby) {
                    if (entity instanceof Monster ||
                        (entity instanceof Player p && p != player
                            && vex.getPersistentDataContainer().get(EVOKER_OWNER, PersistentDataType.STRING )!= p.getUniqueId().toString()
                        )) {
                        vex.setTarget((LivingEntity) entity);
                        break;
                    }
                }
            });
        }
    }

    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.RED + "Level %level%");
        lore.add(ChatColor.RED + "Abilities");
        lore.add(ChatColor.WHITE + "Right click: Summon Evoker Fangs");
        lore.add(ChatColor.WHITE + "Shift click: Spawn loyal Vexes");
        lore.add(ChatColor.WHITE + "Left click: Temporary flight");
        if(MPG.PassiveLoreEnabled) {lore.add(ChatColor.AQUA + "Passive: Haste");}
        return lore;
    }


    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.HASTE;
    }

    @Override
    public int getDefaultEffectLevel() {
        return 1;
    }

    @Override
    public Particle getDefaultParticle() {
        return Particle.ENCHANT;
    }

    @Override
    public BlockData getParticleBlockData() {
        return null;
    }
}
