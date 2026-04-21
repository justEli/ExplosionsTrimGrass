package me.justeli.trim.config;

import org.bukkit.Material;

import java.util.Map;
import java.util.NavigableMap;
import java.util.SplittableRandom;
import java.util.TreeMap;

/**
 * @author Eli
 * @since April 29, 2021 (me.justeli.trim.config)
 */
public final class ConfiguredBlock {
    private final int maximumYLevel;
    private final boolean disabledInClaims;
    private final boolean disabledInRegions;
    private final NavigableMap<Double, Material> transformations = new TreeMap<>();

    private static final SplittableRandom RANDOM = new SplittableRandom();

    public ConfiguredBlock(int maximumYLevel, boolean disabledInClaims, boolean disabledInRegions, Map<Material, Object> map) {
        this.maximumYLevel = maximumYLevel;
        this.disabledInClaims = disabledInClaims;
        this.disabledInRegions = disabledInRegions;

        double adding = 0;
        for (var entry : map.entrySet()) {
            transformations.put(adding, entry.getKey());
            adding += (double) entry.getValue();
        }

        transformations.put(adding, null);
    }

    public int getMaximumYLevel() {
        return maximumYLevel;
    }

    public boolean isDisabledInClaims() {
        return disabledInClaims;
    }

    public boolean isDisabledInRegions() {
        return disabledInRegions;
    }

    public Material getRandomTransform() {
        var map = transformations.floorEntry(RANDOM.nextDouble());
        return map == null? null : map.getValue();
    }
}
