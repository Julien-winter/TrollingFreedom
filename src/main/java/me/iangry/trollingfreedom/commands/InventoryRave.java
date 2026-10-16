package me.iangry.trollingfreedom.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import me.iangry.trollingfreedom.main.Core;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class InventoryRave implements Listener {
    public static ArrayList<String> Rave1 = new ArrayList<>();
    private static final Map<String, List<BukkitTask>> TASKS = new HashMap<>();
    private static final String[] GLASS_COLORS = {
            "WHITE", "ORANGE", "MAGENTA", "LIGHT_BLUE", "YELLOW", "LIME", "PINK",
            "GRAY", "LIGHT_GRAY", "CYAN", "PURPLE", "BLUE", "BROWN", "GREEN", "RED", "BLACK"
    };
    private final Random random = new Random();
    private int r = 255, g = 0, b = 0;

    private int posRed, negRed;
    private int posGreen, negGreen;
    private int posBlue, negBlue;

    public void saveInventory(Player p) throws IOException {
        File f = new File(Core.instance.getDataFolder().getAbsolutePath(), p.getName() + ".yml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        c.set("inventory.armor", p.getInventory().getArmorContents());
        c.set("inventory.content", p.getInventory().getContents());
        c.save(f);
    }

    @SuppressWarnings("unchecked")
    public void restoreInventory(Player p) throws IOException {
        File f = new File(Core.instance.getDataFolder().getAbsolutePath(), p.getName() + ".yml");
        if (!f.exists()) {
            return;
        }
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        if (c.isList("inventory.armor")) {
            ItemStack[] armor = ((List<ItemStack>) c.get("inventory.armor")).toArray(new ItemStack[0]);
            p.getInventory().setArmorContents(armor);
        }
        if (c.isList("inventory.content")) {
            ItemStack[] content = ((List<ItemStack>) c.get("inventory.content")).toArray(new ItemStack[0]);
            p.getInventory().setContents(content);
        }
    }

    public void deleteInventoryFile(Player p) throws IOException {
        File f = new File(Core.instance.getDataFolder().getAbsolutePath(), p.getName() + ".yml");
        //noinspection ResultOfMethodCallIgnored
        f.delete();
    }

    public void InvRave(Player p) throws IOException {
        if (Rave1.contains(p.getName())) {
            return;
        } else {
            Rave1.add(p.getName());
            saveInventory(p);
            Rave(p);
            Rave2(p);
        }
    }

    public void UnInvRave(Player p) throws IOException {
        if (Rave1.contains(p.getName())) {
            Rave1.remove(p.getName());
            cancelTasks(p.getName());
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
            restoreInventory(p);
            deleteInventoryFile(p);
        }
    }

    private void cancelTasks(String playerName) {
        List<BukkitTask> tasks = TASKS.remove(playerName);
        if (tasks != null) {
            for (BukkitTask task : tasks) {
                task.cancel();
            }
        }
    }

    private void addTask(String playerName, BukkitTask task) {
        TASKS.computeIfAbsent(playerName, k -> new ArrayList<>()).add(task);
    }

    @EventHandler
    public void onQuit(org.bukkit.event.player.PlayerQuitEvent event) {
        String name = event.getPlayer().getName();
        Rave1.remove(name);
        cancelTasks(name);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (Rave1.contains(event.getPlayer().getName()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (Rave1.contains(event.getPlayer().getName()))
            event.setCancelled(true);
    }

    public void Rave(final Player p) {
        BukkitTask task = (new BukkitRunnable() {
            public void run() {
                p.getInventory().clear();
                for (int counter = 1; counter <= 36; counter++)
                    p.getInventory().setItem(p.getInventory().firstEmpty(), randomPane());
            }
        }).runTaskTimer((Plugin) Core.instance, 1L, 5L);
        addTask(p.getName(), task);
    }

    public void Rave2(final Player p) {
        p.getInventory().setArmorContents(null);
        BukkitTask task = (new BukkitRunnable() {
            public void run() {
                if (!Rave1.contains(p.getName())) {
                    cancel();
                    return;
                }
                Color color = nextRGB();

                p.getInventory().setHelmet(randomGlass());
                p.getInventory().setChestplate(getItemStack(Material.LEATHER_CHESTPLATE, color));
                p.getInventory().setLeggings(getItemStack(Material.LEATHER_LEGGINGS, color));
                p.getInventory().setBoots(getItemStack(Material.LEATHER_BOOTS, color));
                p.getInventory().setItemInOffHand(randomPane());
            }
        }).runTaskTimer((Plugin) Core.instance, 1L, 1L);
        addTask(p.getName(), task);
    }

    private ItemStack randomPane() {
        Material m = Material.matchMaterial(GLASS_COLORS[random.nextInt(GLASS_COLORS.length)] + "_STAINED_GLASS_PANE");
        if (m == null) m = Material.WHITE_STAINED_GLASS_PANE;
        return new ItemStack(m);
    }

    private ItemStack randomGlass() {
        Material m = Material.matchMaterial(GLASS_COLORS[random.nextInt(GLASS_COLORS.length)] + "_STAINED_GLASS");
        if (m == null) m = Material.WHITE_STAINED_GLASS;
        return new ItemStack(m);
    }

    public ItemStack getItemStack(Material material, Color color) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) itemMeta).setColor(color);
            itemMeta.setDisplayName("§4R§ca§6v§ee§a!");
            itemMeta.addItemFlags(ItemFlag.values());
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    public ItemStack getItemStack(Material material) {
        return getItemStack(material, Color.fromRGB(r, g, b));
    }

    private Color nextRGB() {
        int increment = 15;
        int max = 255 / increment;

        if (posGreen <= max) {
            ++this.posGreen;
            this.g = (posGreen - 1) * increment;
        } else if (negRed <= max) {
            this.negRed++;
            this.r = 255 - increment * (negRed - 1);
        } else if (posBlue <= max) {
            ++this.posBlue;
            this.b = (posBlue - 1) * increment;
        } else if (negGreen <= max) {
            ++this.negGreen;
            this.g = 255 - increment * (negGreen - 1);
        } else if (posRed <= max) {
            ++this.posRed;
            this.r = (posRed - 1) * increment;
        } else if (negBlue <= max) {
            ++this.negBlue;
            this.b = 255 - increment * (negBlue - 1);
        } else {
            this.posRed = 0;
            this.negRed = 0;
            this.posGreen = 0;
            this.negGreen = 0;
            this.posBlue = 0;
            this.negBlue = 0;
        }

        return Color.fromRGB(r, g, b);
    }
}
