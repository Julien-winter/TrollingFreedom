package me.iangry.trollingfreedom.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class NickWithoutEss implements Listener {

    public void NickName(final Player p) {
        if (Bukkit.getServer().getPluginManager().getPlugin("Essentials") == null) {
            p.sendMessage(me.iangry.trollingfreedom.main.Core.tcc(
                    me.iangry.trollingfreedom.main.Core.instance.getConfig().getString("prefix"))
                    + "§cThe nick troll needs the Essentials plugin.");
            return;
        }
        new Nick().NickName(p);
    }

    public void UnNick(Player p) {
        if (Bukkit.getServer().getPluginManager().getPlugin("Essentials") == null) {
            return;
        }
        new Nick().UnNick(p);
    }
}