package me.justeli.trim;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.justeli.trim.util.MetricsRegistry;
import me.justeli.trim.command.EtgCommand;
import me.justeli.trim.config.ConfigCache;
import me.justeli.trim.handler.EntityDamageHandler;
import me.justeli.trim.integration.Integration;
import me.justeli.trim.handler.TrimEffectHandler;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author Eli
 * @since December 9, 2019 (me.justeli.trim)
 */
public final class ExplosionsTrimGrass extends JavaPlugin {
    // todo:
    //  - choose y-level instead of sea level
    //  - blacklist for biomes

    private ConfigCache configCache;

    @Override
    public void onEnable() {
        this.configCache = new ConfigCache(this);

        new EtgCommand(this);
        new TrimEffectHandler(this);
        new EntityDamageHandler(this);
        new Integration(this);
        new MetricsRegistry(this);
    }

    public ConfigCache getConfigCache() {
        return configCache;
    }

    public <T extends Event> void registerEvent(Class<T> eventType, EventPriority priority, Consumer<T> event) {
        getServer().getPluginManager().registerEvent(
            eventType, new Listener() {}, priority, ((ignored, e) -> {
                if (eventType.isInstance(e)) {
                    event.accept(eventType.cast(e));
                }
            }), this
        );
    }

    public <T extends Event> void registerEvent(Class<T> eventType, Consumer<T> event) {
        registerEvent(eventType, EventPriority.NORMAL, event);
    }

    public void registerCommand(LiteralCommandNode<CommandSourceStack> node, Collection<String> aliases) {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->
            event.registrar().register(node, aliases)
        );
    }
}
