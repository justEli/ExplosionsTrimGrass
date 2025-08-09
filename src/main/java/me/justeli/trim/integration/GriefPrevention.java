package me.justeli.trim.integration;

import org.bukkit.Location;

/**
 * @author Eli
 * @since April 29, 2021 (me.justeli.trim.integration)
 */
public final class GriefPrevention {
    public static boolean isClaimAt(Location location) {
        if (!Integration.isGriefPreventionLoaded()) {
            return false;
        }

        return me.ryanhamshire.GriefPrevention.GriefPrevention.instance.dataStore.getClaimAt(location, true, null) != null;
    }
}
