package me.justeli.trim;

import me.justeli.trim.command.EtgCommandSpigot;
import me.justeli.trim.util.MetricsRegistry;
import me.justeli.trim.command.EtgCommandPaper;
import me.justeli.trim.config.ConfigCache;
import me.justeli.trim.handler.EntityDamageHandler;
import me.justeli.trim.integration.Integration;
import me.justeli.trim.handler.TrimEffectHandler;
import me.justeli.trim.util.ScheduleUtil;
import me.justeli.trim.util.SoftwareUtil;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Eli
 * @since December 9, 2019 (me.justeli.trim)
 */
public final class ExplosionsTrimGrass extends JavaPlugin {
    // todo:
    //  - blacklist for biomes

    private ConfigCache configCache;
    private ScheduleUtil scheduleUtil;

    @Override
    public void onEnable() {
        if (SoftwareUtil.getPlatform() == SoftwareUtil.Platform.BUKKIT) {
            getLogger().severe("Bukkit is not supported. Plugin will be disabled.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.configCache = new ConfigCache(this);
        this.scheduleUtil = new ScheduleUtil(this);

        if (SoftwareUtil.isPlatformAtLeast(SoftwareUtil.Platform.PAPER)) {
            if (SoftwareUtil.getMinecraftVersion() > 20) {
                new EtgCommandPaper(this);
            }
            else {
                getLogger().warning("Could not register commands for this Minecraft version.");
            }
        }
        else {
            new EtgCommandSpigot(this);
        }

        new TrimEffectHandler(this);
        new EntityDamageHandler(this);
        new Integration(this);
        new MetricsRegistry(this);
    }

    public void parseEventHandlers(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public ConfigCache getConfigCache() {
        return configCache;
    }

    public ScheduleUtil getScheduler() {
        return scheduleUtil;
    }
}
