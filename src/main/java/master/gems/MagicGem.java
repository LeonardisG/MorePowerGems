package master.gems;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vex;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

import static dev.iseal.sealLib.SealLib.getPlugin;
import static master.Keys.EVOKER_OWNER;

public class MagicGem extends Gem {
    public MagicGem() {
        super("Magic");
    }

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    /** Summons evoker fangs that damage nearby entities. */
    @Override
    protected void rightClick(Player player, int level) {
        Location spawnLocation = player.getLocation();
        player.getWorld().spawn(spawnLocation, EvokerFangs.class, evokerFangs -> evokerFangs.setOwner(player));
    }

    /** Grants temporary flight ability. */
    @Override
    protected void leftClick(Player player, int level) {
        boolean couldFly = player.getAllowFlight();

        player.setFlying(true);
        player.setAllowFlight(true);

        Bukkit.getScheduler().runTaskLater(
                getPlugin(),
                () -> {
                    if (player.getGameMode() != org.bukkit.GameMode.CREATIVE &&
                        player.getGameMode() != org.bukkit.GameMode.SPECTATOR) {
                        player.setFlying(false);
                        player.setAllowFlight(couldFly);
                    }
                },
                20L * (5 + level)
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
                    if (entity instanceof org.bukkit.entity.Monster ||
                        (entity instanceof Player p && p != player)) {
                        vex.setTarget((org.bukkit.entity.LivingEntity) entity);
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
        return lore;
    }


    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.INVISIBILITY;
    }

    @Override
    public int getDefaultEffectLevel() {
        return 0;
    }

    @Override
    public Particle getDefaultParticle() {
        return null;
    }

    @Override
    public BlockData getParticleBlockData() {
        return null;
    }
}
