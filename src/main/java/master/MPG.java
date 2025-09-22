package master;

import dev.iseal.powergems.api.ApiManager;
import dev.iseal.sealLib.Updater.UpdateChecker;
import master.gems.*;
import master.listeners.DoubleDropsListener;
import master.listeners.PlayerLeaveEvent;
import master.listeners.TradeListener;
import master.listeners.WitherDamageListener;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class MPG extends JavaPlugin {

    @Override
    public void onEnable() {
        Logger logger = getLogger();
        ApiManager api = ApiManager.getInstance();
        UpdateChecker updater = new UpdateChecker( //NOPMD - Its being used the IDE just doesn't see it
                "XqguI8fH",
                this,
                "powergems.admin",
                864000, // 12 hours
                ex -> getLogger().warning("Update check failed: " + ex.getMessage()),
                (newVersion, sender) -> {
                    String oldVersion = getDescription().getVersion();
                    getLogger().info(
                            ChatColor.GREEN + "New version available: " + oldVersion + " -> " + newVersion
                    );
                }
        );

        api.registerGemClass(AffluenceGem.class, this);
        api.registerGemClass(PoisonGem.class, this);
        api.registerGemClass(RuinGem.class, this);
        api.registerGemClass(ShulkerGem.class, this);
        api.registerGemClass(WitherGem.class, this);
        logger.info("Registered MorePowerGems gems");

        // Listeners
        getServer().getPluginManager().registerEvents(new TradeListener(), this);
        getServer().getPluginManager().registerEvents(new DoubleDropsListener(), this);
        getServer().getPluginManager().registerEvents(new WitherDamageListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveEvent(), this);
        logger.info("Registered MorePowerGems listeners");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
