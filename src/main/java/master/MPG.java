package master;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import dev.iseal.powergems.api.ApiManager;
import dev.iseal.sealLib.Updater.UpdateChecker;
import master.gems.AffluenceGem;
import master.gems.AmethystGem;
import master.gems.BrezzeGem;
import master.gems.EnderGem;
import master.gems.MagicGem;
import master.gems.MechGem;
import master.gems.PoisonGem;
import master.gems.RuinGem;
import master.gems.ShulkerGem;
import master.gems.WitherGem;
import master.listeners.AmethystThrowListener;
import master.listeners.DoubleDropsListener;
import master.listeners.FallListener;
import master.listeners.PlayerLeaveEvent;
import master.listeners.TradeListener;
import master.listeners.VexListener;
import master.listeners.WitherDamageListener;


public final class MPG extends JavaPlugin {
    public static boolean PassiveLoreEnabled;
    public static double AmethystShardBaseDamage;
    public static double AmethystShardDamagePerLevel;
    public static double AmethystShardMaxDamage;
    private TradeListener tradeListener;


    @Override
    public void onEnable() {
        saveDefaultConfig();

        PassiveLoreEnabled = getConfig().getBoolean("passive-lore-enabled", true);
        AmethystShardBaseDamage = getConfig().getDouble("amethyst.shard-damage.base", 6.0);
        AmethystShardDamagePerLevel = getConfig().getDouble("amethyst.shard-damage.per-level", 2.0);
        AmethystShardMaxDamage = getConfig().getDouble("amethyst.shard-damage.max", 20000.0);
        tradeListener = new TradeListener();

        Logger logger = getLogger();
        Keys.init(this);
        ApiManager api = ApiManager.getInstance();
        UpdateChecker updater = new UpdateChecker( //NOPMD - Its being used the IDE just doesn't see it
                "XqguI8fH",
                this,
                "powergems.admin",
                43200000, // 12 hours
                ex -> getLogger().warning("Update check failed: " + ex.getMessage()),
                (newVersion, sender) -> {
                }
        );

        api.registerGemClass(AffluenceGem.class, this);
        api.registerGemClass(AmethystGem.class,this);
        api.registerGemClass(BrezzeGem.class,this);
        api.registerGemClass(EnderGem.class,this);
        api.registerGemClass(MagicGem.class,this);
        api.registerGemClass(MechGem.class,this);
        api.registerGemClass(PoisonGem.class,this);
        api.registerGemClass(RuinGem.class,this);
        api.registerGemClass(ShulkerGem.class,this);
        api.registerGemClass(WitherGem.class,this);
        logger.info("Registered MorePowerGems gems");

        // Listeners
        getServer().getPluginManager().registerEvents(tradeListener, this);
        getServer().getPluginManager().registerEvents(new DoubleDropsListener(), this);
        getServer().getPluginManager().registerEvents(new WitherDamageListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveEvent(), this);
        getServer().getPluginManager().registerEvents(new AmethystThrowListener(), this);
        getServer().getPluginManager().registerEvents(new VexListener(), this);
        getServer().getPluginManager().registerEvents(new FallListener(), this);
        logger.info("Registered MorePowerGems listeners");

    }

    @Override
    public void onDisable() {
    if(tradeListener != null) {
        tradeListener.cleanup();
    }
}


}