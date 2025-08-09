package me.justeli.trim.integration;

import org.bukkit.Location;

/**
 * @author Eli
 * @since April 29, 2021 (me.justeli.trim.integration)
 */
public final class WorldGuard {
    private static final com.sk89q.worldguard.protection.regions.RegionQuery QUERY =
        com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();

    public static boolean isRegionAt(Location location) {
        if (Integration.isGriefPreventionLoaded()) {
            var claim = me.ryanhamshire.GriefPrevention.GriefPrevention.instance.dataStore.getClaimAt(location, true, null);
            if (claim != null && claim.isAdminClaim()) {
                return true;
            }
        }

        if (!Integration.isWorldGuardLoaded()) {
            return false;
        }

        if (location.getWorld() == null) {
            return false;
        }

        return QUERY.getApplicableRegions(
            com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(location)
        ).size() > 0;
    }
}
