package me.iangry.trollingfreedom.commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import me.iangry.trollingfreedom.main.Core;
import me.iangry.trollingfreedom.other.BukkitCompat;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


/**
 * Shows the "end of the credits" screen to a player.
 * Modernized: the packet layout is written directly
 * (float reason, float value) instead of reflecting into obfuscated NMS fields.
 */
public class Credits implements Listener {

    private static final float REASON_SHOW_CREDITS = 4.0F;

    public void Credits(Player player) {
        if (!BukkitCompat.isProtocolLibEnabled()) {
            Core.instance.getLogger().warning("Credits troll needs ProtocolLib.");
            return;
        }
        final PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.GAME_STATE_CHANGE);
        packet.getFloat().write(0, REASON_SHOW_CREDITS);
        packet.getFloat().write(1, 1.0F);
        Credits.sendPacket(player, packet);
    }

    public static boolean sendPacket(final Player player, final PacketContainer packet) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            Core.instance.getLogger().severe("Failed to show credits screen to player:");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
