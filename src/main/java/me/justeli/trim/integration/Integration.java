package me.justeli.trim.integration;

import me.justeli.trim.ExplosionsTrimGrass;
import org.bukkit.event.server.PluginEnableEvent;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Eli
 * @since August 09, 2020 (me.justeli.trim)
 */
public final class Integration {
    public Integration(ExplosionsTrimGrass plugin) {
        plugin.registerEvent(PluginEnableEvent.class, event -> {
            switch (event.getPlugin().getName()) {
                case "WorldGuard":
                    WORLD_GUARD_LOADED.set(true);
                    return;
                case "GriefPrevention":
                    GRIEF_PREVENTION_LOADED.set(true);
            }
        });
    }

    private static final AtomicBoolean WORLD_GUARD_LOADED = new AtomicBoolean(false);
    private static final AtomicBoolean GRIEF_PREVENTION_LOADED = new AtomicBoolean(false);

    public static boolean isGriefPreventionLoaded() {
        return GRIEF_PREVENTION_LOADED.get();
    }

    public static boolean isWorldGuardLoaded() {
        return WORLD_GUARD_LOADED.get();
    }
}
