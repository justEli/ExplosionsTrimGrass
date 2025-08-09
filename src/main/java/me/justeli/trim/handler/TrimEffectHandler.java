package me.justeli.trim.handler;

import me.justeli.trim.ExplosionsTrimGrass;
import me.justeli.trim.config.ConfiguredBlock;
import me.justeli.trim.event.ExplosionTrimEvent;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Explosive;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eli
 * @since January 4, 2017 (me.justeli.trim)
 */
public final class TrimEffectHandler {
    public TrimEffectHandler(ExplosionsTrimGrass plugin) {
        plugin.registerEvent(EntityExplodeEvent.class, EventPriority.LOWEST, event -> {
            if (!(event.getEntity() instanceof Explosive) && !(event.getEntity() instanceof Creeper)) {
                return;
            }

            if (plugin.getConfigCache().isOnlyEnabledForCreepers() && !(event.getEntity() instanceof Creeper)) {
                return;
            }

            plugin.getServer().getPluginManager().callEvent(new ExplosionTrimEvent(event));
        });

        plugin.registerEvent(ExplosionTrimEvent.class, event -> {
            var world = event.getLocation().getWorld();
            if (world == null) {
                return;
            }

            var blockList = new ArrayList<>(event.getBlockList());
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                for (Block block : blockList) {
                    ConfiguredBlock configuredBlock = plugin.getConfigCache().getConfiguredBlock(block.getType());
                    if (configuredBlock == null) {
                        continue;
                    }

                    if (configuredBlock.isDisabledInClaims() && event.isInsideClaim()) {
                        continue;
                    }

                    if (configuredBlock.isDisabledInRegions() && event.isInRegion()) {
                        continue;
                    }

                    if (event.getLocation().getY() > configuredBlock.getMaximumYLevel()) {
                        continue;
                    }

                    Material setTo = configuredBlock.getRandomTransform();
                    if (setTo == null || setTo == block.getType()) {
                        continue;
                    }

                    if (setTo == Material.AIR) {
                        world.playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
                        block.breakNaturally();
                    }
                    else if (block.getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.AIR) {
                        Location location = block.getLocation().clone().add(0.5, 1.05, 0.5);
                        world.spawnParticle(Particle.BLOCK, location, 30, 0.5, 0, 0.5, block.getType().createBlockData());
                        block.setType(setTo);
                    }
                }
            }, 1);

            event.getBlockList().clear();
        });
    }
}
