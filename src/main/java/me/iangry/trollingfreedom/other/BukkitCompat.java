package me.iangry.trollingfreedom.other;

import org.bukkit.Bukkit;

/**
 * Small helpers so optional plugin integrations fail gracefully
 * instead of throwing NoClassDefFoundError.
 */
public final class BukkitCompat {

    private BukkitCompat() {
    }

    public static boolean isPluginEnabled(String name) {
        return Bukkit.getPluginManager().isPluginEnabled(name);
    }

    public static boolean isProtocolLibEnabled() {
        return isPluginEnabled("ProtocolLib");
    }

    public static boolean isEssentialsEnabled() {
        return isPluginEnabled("Essentials");
    }
}
