package me.justeli.trim.util;

/**
 * @author Eli
 * @since April 20, 2026
 */
public final class SoftwareUtil {
    public enum Platform {
        BUKKIT, SPIGOT, PAPER, FOLIA
    }

    private static final Platform PLATFORM = findPlatform();

    public static boolean isPlatformAtLeast(Platform platform) {
        return PLATFORM.ordinal() >= platform.ordinal();
    }

    public static Platform getPlatform() {
        return PLATFORM;
    }

    private static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        }
        catch (ClassNotFoundException exception) {
            return false;
        }
    }

    private static Platform findPlatform() {
        if (hasClass("io.papermc.paper.threadedregions.RegionizedServer")) {
            return Platform.FOLIA;
        }
        else if (hasClass("com.destroystokyo.paper.PaperConfig") || hasClass("io.papermc.paper.configuration.Configuration")) {
            return Platform.PAPER;
        }
        else if (hasClass("org.spigotmc.SpigotConfig")) {
            return Platform.SPIGOT;
        }
        else {
            return Platform.BUKKIT;
        }
    }
}
