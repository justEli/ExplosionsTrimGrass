package me.justeli.trim.util;

import me.justeli.trim.ExplosionsTrimGrass;
import org.bukkit.Location;

/**
 * @author Eli
 * @since April 20, 2026
 */
public final class ScheduleUtil {
    private final ExplosionsTrimGrass plugin;
    public ScheduleUtil(ExplosionsTrimGrass plugin) {
        this.plugin = plugin;
    }

    public void runLocationTaskLater(Location location, long delay, Runnable runnable) {
        if (SoftwareUtil.getPlatform() == SoftwareUtil.Platform.FOLIA) {
            plugin.getServer().getRegionScheduler().runDelayed(plugin, location, task -> runnable.run(), delay);
        }
        else {
            plugin.getServer().getScheduler().runTaskLater(plugin, runnable, delay);
        }
    }
}
