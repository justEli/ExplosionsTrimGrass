package me.justeli.trim.config;

import me.justeli.trim.ExplosionsTrimGrass;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

    private final HashMap<Material, ConfiguredBlock> configuredBlocks = new HashMap<>();
    private final HashMap<String, Set<Material>> definitions = new HashMap<>();

    public ConfiguredBlock getConfiguredBlock(Material material) {
        return configuredBlocks.computeIfAbsent(material, empty -> null);
    }

    private Set<Material> getBlockFromName(String name) {
        try {
            return definitions.computeIfAbsent(
                name.toUpperCase(),
                empty -> new HashSet<>(Collections.singleton(Material.matchMaterial(name.toUpperCase())))
            );
        }
        catch (EnumConstantNotPresentException exception) {
            plugin.getLogger().warning(String.format(
                "Found '%s' in 'transform-blocks', but it is not a defined block. Skipped.",
                name.toUpperCase()
            ));
            return null;
        }
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
                try {
                    var matched = Material.matchMaterial(material.toUpperCase());
                    if (matched == null) {
                        throw new IllegalArgumentException();
                    }

                    converted.add(matched);
                }
                catch (EnumConstantNotPresentException | IllegalArgumentException exception) {
                    plugin.getLogger().warning(String.format(
                        "Found '%s' in the definition list of '%s', but it is not a material. Skipped.",
                        material.toUpperCase(),
                        key.toUpperCase()
                    ));
                }
            }

            this.definitions.put(key.toUpperCase(), converted);
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
            Set<Material> materials = getBlockFromName(key.toUpperCase());
            ConfigurationSection part = section.getConfigurationSection(key);
            if (materials == null || part == null) {
                continue;
            }

            ConfigurationSection conversion = part.getConfigurationSection("conversion");
            if (conversion == null) {
                continue;
            }

            for (Material material : materials) {
                configuredBlocks.put(material, new ConfiguredBlock(
                    part.getInt("maximum-y-level", maximumYLevel),
                    part.getBoolean("disable-in-claims", disableInClaims),
                    part.getBoolean("disable-in-regions", disableInRegions),
                    conversion.getValues(false)
                ));
            }
        }
    }
}
