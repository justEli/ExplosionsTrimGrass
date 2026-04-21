package me.justeli.trim.command;

import com.google.gson.JsonParser;
import me.justeli.trim.ExplosionsTrimGrass;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Eli
 * @since August 09, 2020 (me.justeli.trim)
 */
public abstract class EtgCommandLogic {
    protected final ExplosionsTrimGrass plugin;
    public EtgCommandLogic(ExplosionsTrimGrass plugin) {
        this.plugin = plugin;
    }

    protected void sendHelp(CommandSender sender) {
        sender.sendMessage("§e/etg help §f- show this page");
        sender.sendMessage("§e/etg reload §f- reload config settings");
        sender.sendMessage("§e/etg version §f- check for updates");
    }

    protected void sendReload(CommandSender sender) {
        sender.sendMessage(
            "§eConfig has been reloaded in §a" + plugin.getConfigCache().reload() + "ms§e."
        );
    }

    protected void sendVersion(CommandSender sender, String version) {
        sender.sendMessage("§eInstalled version: §f" + version);
        sender.sendMessage("§eLatest version: §f" + latestVersion.get());
    }

    protected static final String PERMISSION = "etg.admin";

    private static final ExecutorService ASYNC_EXECUTOR = Executors.newSingleThreadExecutor();
    private static final String UNKNOWN = "Unknown";

    private final AtomicReference<String> latestVersion = new AtomicReference<>(UNKNOWN);

    protected void checkAndPrintLatestVersion(String version, String website) {
        ASYNC_EXECUTOR.submit(() -> {
            try {
                var request = URI.create(
                    "https://api.github.com/repos/justEli/ExplosionsTrimGrass/releases/latest"
                ).toURL().openConnection();

                request.connect();
                try (var reader = new InputStreamReader((InputStream) request.getContent())){
                    latestVersion.set(JsonParser.parseReader(reader).getAsJsonObject().get("tag_name").getAsString());
                }
            }
            catch (IOException ignored) {}

            if (UNKNOWN.equals(latestVersion.get()) || version.equals(latestVersion.get())) {
                return;
            }

            plugin.getLogger().warning(" ------------------------------------------------------------------");
            plugin.getLogger().warning("  Detected an outdated version of ExplosionsTrimGrass.");
            plugin.getLogger().warning("  Version " + version + " is installed, while " + latestVersion + " was released.");
            plugin.getLogger().warning("  Download: " + website);
            plugin.getLogger().warning(" ------------------------------------------------------------------");
        });
    }
}
