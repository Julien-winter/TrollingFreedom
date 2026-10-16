package me.iangry.trollingfreedom.commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import me.iangry.trollingfreedom.main.Core;
import me.iangry.trollingfreedom.other.BukkitCompat;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


/**
 * Opens the "demo account" screen on the victim's client.
 * Modernized: written directly via float reason/value instead of obfuscated NMS fields.
 */
public class Demo implements Listener {

    private static final float REASON_SHOW_DEMO = 5.0F;

    public void DemoMenu(Player player) {
        if (!BukkitCompat.isProtocolLibEnabled()) {
            Core.instance.getLogger().warning("Demo troll needs ProtocolLib.");
            return;
        }
        final PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.GAME_STATE_CHANGE);
        packet.getFloat().write(0, REASON_SHOW_DEMO);
        packet.getFloat().write(1, 0.0F);
        Demo.sendPacket(player, packet);
    }

    public static boolean sendPacket(final Player player, final PacketContainer packet) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            Core.instance.getLogger().severe("Failed to show demo screen to player:");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
