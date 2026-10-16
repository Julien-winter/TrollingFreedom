package me.iangry.trollingfreedom.commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import me.iangry.trollingfreedom.main.Core;
import me.iangry.trollingfreedom.other.BukkitCompat;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


/**
 * Triggers the elder guardian "curse" effect on a player.
 * Modernized: written directly via float reason/value instead of obfuscated NMS fields.
 */
public class Guardian implements Listener {

    private static final float REASON_ELDER_GUARDIAN = 10.0F;

    public void Guardian(Player player) {
        if (!BukkitCompat.isProtocolLibEnabled()) {
            Core.instance.getLogger().warning("Guardian troll needs ProtocolLib.");
            return;
        }
        final PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.GAME_STATE_CHANGE);
        packet.getFloat().write(0, REASON_ELDER_GUARDIAN);
        packet.getFloat().write(1, 0.0F);
        Guardian.sendPacket(player, packet);
    }

    public static boolean sendPacket(final Player player, final PacketContainer packet) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 100, 1);
            player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_DEATH, 100, 1);
            player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 100, 1);
            player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 100, 1);
        } catch (Exception e) {
            Core.instance.getLogger().severe("Failed to show guardian effect to player:");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
