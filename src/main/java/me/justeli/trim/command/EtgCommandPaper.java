package me.justeli.trim.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.justeli.trim.ExplosionsTrimGrass;

/**
 * @author Eli
 * @since August 09, 2020 (me.justeli.trim)
 */
public final class EtgCommandPaper extends EtgCommandLogic {
    public EtgCommandPaper(ExplosionsTrimGrass plugin) {
        super(plugin);
        checkAndPrintLatestVersion(plugin.getPluginMeta().getVersion(), plugin.getPluginMeta().getWebsite());

        registerCommand(
            Commands.literal("etg")
                .requires(source -> source.getSender().hasPermission(PERMISSION))
                .executes(context -> {
                    sendHelp(context.getSource().getSender());
                    return Command.SINGLE_SUCCESS;
                })
                .then(
                    Commands.literal("help")
                        .executes(context -> {
                            sendHelp(context.getSource().getSender());
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(
                    Commands.literal("reload")
                        .executes(context -> {
                            sendReload(context.getSource().getSender());
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(
                    Commands.literal("version")
                        .executes(context -> {
                            sendVersion(context.getSource().getSender(), plugin.getPluginMeta().getVersion());
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build()
        );
    }

    private void registerCommand(LiteralCommandNode<CommandSourceStack> node) {
        plugin.getLifecycleManager().registerEventHandler(
            LifecycleEvents.COMMANDS, event -> event.registrar().register(node)
        );
    }
}
