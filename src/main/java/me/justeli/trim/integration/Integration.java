package me.justeli.trim.integration;

import me.justeli.trim.ExplosionsTrimGrass;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Eli
 * @since August 09, 2020 (me.justeli.trim)
 */
public final class Integration implements Listener {
    // todo improve
    public Integration(ExplosionsTrimGrass plugin) {
        plugin.parseEventHandlers(this);

        if (plugin.getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
            WORLD_GUARD_LOADED.set(true);
        }

        if (plugin.getServer().getPluginManager().isPluginEnabled("GriefPrevention")) {
            GRIEF_PREVENTION_LOADED.set(true);
        }
    }

    @EventHandler
    void onPluginEnableEvent(PluginEnableEvent event) {
        switch (event.getPlugin().getName()) {
            case "WorldGuard" -> WORLD_GUARD_LOADED.set(true);
            case "GriefPrevention" -> GRIEF_PREVENTION_LOADED.set(true);
        }
    }

    private static final AtomicBoolean WORLD_GUARD_LOADED = new AtomicBoolean(false);
    private static final AtomicBoolean GRIEF_PREVENTION_LOADED = new AtomicBoolean(false);

    public static boolean isWorldGuardLoaded() {
        return WORLD_GUARD_LOADED.get();
    }

    public static boolean isGriefPreventionLoaded() {
        return GRIEF_PREVENTION_LOADED.get();
    }
}
