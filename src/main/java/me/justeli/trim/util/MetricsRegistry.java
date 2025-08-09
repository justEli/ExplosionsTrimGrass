package me.justeli.trim.util;

import me.justeli.trim.ExplosionsTrimGrass;
import me.justeli.trim.integration.Integration;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Eli
 * @since April 29, 2021 (me.justeli.trim.api)
 */
public final class MetricsRegistry {
    private final Metrics metrics;

    private static final ExecutorService ASYNC_EXECUTOR = Executors.newSingleThreadExecutor();

    public MetricsRegistry(ExplosionsTrimGrass plugin) {
        this.metrics = new Metrics(plugin, 11185);

        ASYNC_EXECUTOR.submit(() -> {
            add("totalBlockTransformers", plugin.getConfigCache().getTotalTransformers());
            add("disableDamageToNonMobs", plugin.getConfigCache().disableDamageToNonMobs());
            add("installedWorldGuard", Integration.isWorldGuardLoaded());
            add("installedGriefPrevention", Integration.isWorldGuardLoaded());
        });
    }

    private void add(String key, Object value) {
        metrics.addCustomChart(new SimplePie(key, value::toString));
    }
}
