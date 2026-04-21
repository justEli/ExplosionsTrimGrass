package me.justeli.trim.config;

import me.justeli.trim.ExplosionsTrimGrass;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eli
 * @since August 09, 2020 (me.justeli.trim)
 */
public final class ConfigCache {
    private final ExplosionsTrimGrass plugin;
    public ConfigCache(ExplosionsTrimGrass plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        load();
    }

    private final AtomicBoolean disableDamageToNonMobs = new AtomicBoolean();

    public boolean disableDamageToNonMobs() {
        return disableDamageToNonMobs.get();
    }

    private final AtomicBoolean onlyEnableForCreepers = new AtomicBoolean();

    public boolean isOnlyEnabledForCreepers() {
        return onlyEnableForCreepers.get();
    }

    private final Map<Material, ConfiguredBlock> configuredBlocks = new HashMap<>();
    private final Map<String, Set<Material>> definitions = new HashMap<>();

    public ConfiguredBlock getConfiguredBlock(Material material) {
        return configuredBlocks.computeIfAbsent(material, empty -> null);
    }

    private @Nullable Material parseMaterial(String name) {
        var key = NamespacedKey.fromString(name);
        if (key == null) {
            return null;
        }

        return Registry.MATERIAL.get(key);
    }

    private @Nullable Set<Material> getBlocksFromName(String name) {
        var defined = definitions.get(name);
        if (defined != null) {
            return defined;
        }

        var material = parseMaterial(name);
        if (material == null) {
            return null;
        }

        var materials = new HashSet<>(Collections.singleton(material));
        definitions.put(name, materials);
        return materials;
    }

    public long reload() {
        long current = System.currentTimeMillis();

        plugin.reloadConfig();
        configuredBlocks.clear();
        definitions.clear();
        load();

        return System.currentTimeMillis() - current;
    }

    private void load() {
        FileConfiguration config = plugin.getConfig();

        disableDamageToNonMobs.set(config.getBoolean("disable-damage-to-non-mobs", true));
        onlyEnableForCreepers.set(config.getBoolean("only-enable-for-creepers", true));

        setDefinitions(config.getConfigurationSection("definitions"));
        setTransformBlocks(config);
    }

    private void setDefinitions(ConfigurationSection section) {
        if (section == null) {
            return;
        }

        for (String key : section.getKeys(false)) {
            List<String> materialList = section.getStringList(key);
            Set<Material> converted = new HashSet<>();

            for (String material : materialList) {
                var matched = parseMaterial(material);
                if (matched == null) {
                    plugin.getLogger().warning(String.format(
                        "Found invalid material type '%s' in the definition list of '%s'. Skipped.",
                        material, key
                    ));
                    continue;
                }

                converted.add(matched);
            }

            definitions.put(key, converted);
        }
    }

    private final AtomicInteger totalTransformers = new AtomicInteger();

    public int getTotalTransformers() {
        return totalTransformers.get();
    }

    private void setTransformBlocks(FileConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection("transform-blocks");
        if (section == null) {
            return;
        }

        int maximumYLevel = config.getInt("default-values.maximum-y-level", 0);
        boolean disableInClaims = config.getBoolean("default-values.disable-in-claims", false);
        boolean disableInRegions = config.getBoolean("default-values.disable-in-regions", true);

        Set<String> keys = section.getKeys(false);
        totalTransformers.set(keys.size());

        for (String key : keys) {
            Set<Material> materials = getBlocksFromName(key);
            if (materials == null) {
                plugin.getLogger().warning(
                    "Found undefined block list '%s' in 'transform-blocks'. Skipped.".formatted(key)
                );
                continue;
            }

            ConfigurationSection part = section.getConfigurationSection(key);
            if (part == null) {
                continue;
            }

            ConfigurationSection conversion = part.getConfigurationSection("conversion");
            if (conversion == null) {
                continue;
            }

            // parsing materials from 'conversion'
            Map<Material, Object> conversions = new HashMap<>();
            for (var entry : conversion.getValues(false).entrySet()) {
                var material = parseMaterial(entry.getKey());
                if (material == null) {
                    plugin.getLogger().warning(String.format(
                        "Found invalid material type '%s' in 'transform-blocks.%s.conversion'. Skipped.",
                        entry.getKey(), key
                    ));
                    continue;
                }
                conversions.put(material, entry.getValue());
            }

            var configuredBlock = new ConfiguredBlock(
                part.getInt("maximum-y-level", maximumYLevel),
                part.getBoolean("disable-in-claims", disableInClaims),
                part.getBoolean("disable-in-regions", disableInRegions),
                conversions
            );

            for (Material material : materials) {
                configuredBlocks.put(material, configuredBlock);
            }
        }
    }
}
