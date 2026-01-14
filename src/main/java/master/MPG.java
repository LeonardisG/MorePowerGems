package master;

import dev.iseal.powergems.api.ApiManager;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.sealLib.Updater.UpdateChecker;
import master.gems.*;
import master.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;


public final class MPG extends JavaPlugin {
    public static boolean PassiveLoreEnabled = true;
private TradeListener tradeListener;
    @Override
    public void onEnable() {
        SingletonManager sm = SingletonManager.getInstance();
        GeneralConfigManager configManager = sm.configManager.getRegisteredConfigInstance(GeneralConfigManager.class);
        PassiveLoreEnabled = configManager.giveGemPermanentEffectOnLvlX();
        tradeListener = new TradeListener();

        Logger logger = getLogger();
        Keys.init(this);
        ApiManager api = ApiManager.getInstance();
        UpdateChecker updater = new UpdateChecker( //NOPMD - Its being used the IDE just doesn't see it
                "XqguI8fH",
                this,
                "powergems.admin",
                864000, // 12 hours
                ex -> getLogger().warning("Update check failed: " + ex.getMessage()),
                (newVersion, sender) -> {
                }
        );

        api.registerGemClass(AffluenceGem.class, this);
        api.registerGemClass(AmethystGem.class, this);
        api.registerGemClass(BrezzeGem.class, this);
        api.registerGemClass(EnderGem.class, this);
        api.registerGemClass(MagicGem.class, this);
        api.registerGemClass(MechGem.class, this);
        api.registerGemClass(PoisonGem.class, this);
        api.registerGemClass(RuinGem.class, this);
        api.registerGemClass(ShulkerGem.class, this);
        api.registerGemClass(WitherGem.class, this);
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
    public static int versionChecker() {
        String mcVersion= Bukkit.getBukkitVersion();
        String versionOnly = mcVersion.split("-")[0];
        String[] parts = versionOnly.split("\\.");
        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        int patch = Integer.parseInt(parts[2]);
        return major * 1000 + minor * 10 + patch;
}
}