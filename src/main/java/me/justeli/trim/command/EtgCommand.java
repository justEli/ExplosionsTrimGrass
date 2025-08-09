package me.justeli.trim.command;

import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.Commands;
import me.justeli.trim.ExplosionsTrimGrass;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Eli
 * @since August 09, 2020 (me.justeli.trim)
 */
public final class EtgCommand {
    private static final ExecutorService ASYNC_EXECUTOR = Executors.newSingleThreadExecutor();

    private static final String UNKNOWN = "Unknown";
    private final AtomicReference<String> latestVersion = new AtomicReference<>(UNKNOWN);

    public EtgCommand(ExplosionsTrimGrass plugin) {
        plugin.registerCommand(
            Commands.literal("etg")
                .requires(source -> source.getSender().hasPermission("etg.admin"))
                .then(
                    Commands.literal("help")
                        .executes(context -> {
                            context.getSource().getSender().sendRichMessage("<#FFFF55>/ctg help <#FFFFFF>- show this page");
                            context.getSource().getSender().sendRichMessage("<#FFFF55>/ctg reload <#FFFFFF>- reload config settings");
                            context.getSource().getSender().sendRichMessage("<#FFFF55>/ctg version <#FFFFFF>- check for updates");
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(
                    Commands.literal("reload")
                        .executes(context -> {
                            context.getSource().getSender().sendRichMessage("<#FFFF55>Config has been reloaded in <#55FF55>" + plugin.getConfigCache().reload() + "ms<#FFFF55>.");
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(
                    Commands.literal("version")
                        .executes(context -> {
                            context.getSource().getSender().sendRichMessage("<#FFFF55>Installed version: <#FFFFFF>" + plugin.getPluginMeta().getVersion());
                            context.getSource().getSender().sendRichMessage("<#FFFF55>Latest version: <#FFFFFF>" + latestVersion.get());
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build(),
            List.of("ctg")
        );

        ASYNC_EXECUTOR.submit(() -> {
            try {
                var request = URI.create(
                    "https://api.github.com/repos/justEli/ExplosionsTrimGrass/releases/latest"
                ).toURL().openConnection();

                request.connect();
                try (var content = (InputStream) request.getContent(); var reader = new InputStreamReader(content)){
                    this.latestVersion.set(JsonParser.parseReader(reader).getAsJsonObject().get("tag_name").getAsString());
                }
            }
            catch (IOException ignored) {}

            if (UNKNOWN.equals(latestVersion.get())) {
                return;
            }

            var version = plugin.getPluginMeta().getVersion();
            if (version.equals(latestVersion.get())) {
                return;
            }

            plugin.getLogger().warning(" ------------------------------------------------------------------");
            plugin.getLogger().warning("  You're running an outdated version of ExplosionsTrimGrass.");
            plugin.getLogger().warning("  Version " + version + " is installed, while " + latestVersion + " is out.");
            plugin.getLogger().warning("  https://hangar.papermc.io/Eli/ExplosionsTrimGrass");
            plugin.getLogger().warning(" ------------------------------------------------------------------");
        });
    }
}
