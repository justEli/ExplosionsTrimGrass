package me.justeli.trim.handler;

import me.justeli.trim.ExplosionsTrimGrass;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * @author Eli
 * @since April 29, 2021 (me.justeli.trim.handlers)
 */
public final class EntityDamageHandler {
    public EntityDamageHandler(ExplosionsTrimGrass plugin) {
        plugin.registerEvent(EntityDamageByEntityEvent.class, event -> {
            if (!plugin.getConfigCache().disableDamageToNonMobs()) {
                return;
            }

            if (event.getEntity() instanceof Player || event.getEntity() instanceof Mob) {
                return;
            }

            if (!(event.getDamager() instanceof Explosive) && !(event.getDamager() instanceof Creeper)) {
                return;
            }

            if (plugin.getConfigCache().isOnlyEnabledForCreepers() && !(event.getDamager() instanceof Creeper)) {
                return;
            }

            event.setCancelled(true);
        });
    }
}
