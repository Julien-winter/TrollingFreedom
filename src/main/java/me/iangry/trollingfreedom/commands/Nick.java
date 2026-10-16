package me.iangry.trollingfreedom.commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import me.iangry.trollingfreedom.main.Core;
import me.iangry.trollingfreedom.other.BukkitCompat;
import me.iangry.trollingfreedom.other.Mode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Nick troll based on Essentials. Guards against a missing Essentials plugin
 * (no more NPE/ClassCastException) and actually stops the nickname task on
 * untroll and on quit - previously the repeating task ran forever and was
 * unreachable because every call created a new Nick instance.
 */
public class Nick implements Listener {

    public static ArrayList<String> Nick1 = new ArrayList();
    private static final Map<UUID, Integer> TASKS = new HashMap<>();

    private static Essentials getEssentials() {
        if (!BukkitCompat.isEssentialsEnabled()) {
            return null;
        }
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Essentials");
        if (plugin instanceof Essentials) {
            return (Essentials) plugin;
        }
        return null;
    }

    private static String prefix() {
        return Core.tcc(Core.instance.getConfig().getString("prefix"));
    }

    public void NickName(final Player p) {
        Essentials ess = getEssentials();
        if (ess == null) {
            p.sendMessage(prefix() + "§cThe nick troll needs the Essentials plugin.");
            return;
        }
        User user = ess.getUserMap().getUser(p.getName());
        if (user == null) {
            p.sendMessage(prefix() + "§cEssentials could not resolve that player.");
            return;
        }
        if (!Nick1.contains(p.getName())) {
            Nick1.add(p.getName());
        }
        stopTask(p.getUniqueId());
        TASKS.put(p.getUniqueId(), Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.instance, () -> {
            if (!p.isOnline()) {
                return;
            }
            user.setNickname(Mode.getString(8, Mode.ALPHANUMERIC));
        }, 10L, 10L));
    }

    public void UnNick(Player p) {
        if (!Nick1.contains(p.getName())) {
            return;
        }
        stopTask(p.getUniqueId());
        Nick1.remove(p.getName());
        if (getEssentials() != null) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "nick " + p.getName() + " off");
        }
    }

    private static void stopTask(UUID id) {
        Integer task = TASKS.remove(id);
        if (task != null) {
            Bukkit.getScheduler().cancelTask(task);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (Nick1.contains(event.getPlayer().getName())) {
            UnNick(event.getPlayer());
        }
    }
}
