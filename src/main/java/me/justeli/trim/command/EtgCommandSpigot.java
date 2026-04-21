package me.justeli.trim.command;

import me.justeli.trim.ExplosionsTrimGrass;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eli
 * @since April 20, 2026
 */
public final class EtgCommandSpigot extends EtgCommandLogic implements CommandExecutor, TabCompleter {
    public EtgCommandSpigot(ExplosionsTrimGrass plugin) {
        super(plugin);
        checkAndPrintLatestVersion(plugin.getDescription().getVersion(), plugin.getDescription().getWebsite());

        var command = plugin.getCommand("etg");
        if (command == null) {
            return;
        }

        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage("§4You do not have access to that command.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> sendReload(sender);
            case "version" -> sendVersion(sender, plugin.getDescription().getVersion());
            default -> sendHelp(sender);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        if (!sender.hasPermission(PERMISSION)) {
            return List.of();
        }

        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            String remaining = args[0].toLowerCase();
            if ("help".startsWith(remaining)) {
                list.add("help");
            }
            if ("reload".startsWith(remaining)) {
                list.add("reload");
            }
            if ("version".startsWith(remaining)) {
                list.add("version");
            }
        }

        return list;
    }
}
