package me.iangry.trollingfreedom.commands;

import me.iangry.trollingfreedom.main.Core;
import me.iangry.trollingfreedom.other.SkullCreator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * The classic "Herobrine shrine" troll.
 * Reworked to use a plain ArmorStand with a custom player head so the
 * Citizens NPC dependency (and its console save/reload commands) is gone.
 */
public class Herobrine implements Listener {

    private static final String HEROCRINE_SKULL_BASE64 =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGY0YzY3MzZjYjFjNjg4MGQ4MGEyMDYyZDdhYTRhYWIxZWEwYzU1YmYxNDJhZDMwZmQ1MmM1NzUxNWYwYzJkMSJ9fX0=";

    public static void Herobrine(final Player p) {
        World world = p.getWorld();
        final Location loc2 = p.getLocation().add(p.getLocation().getDirection().setY(0).normalize().multiply(5));
        final Block block1 = world.getBlockAt(loc2);
        final Block block2 = world.getBlockAt(loc2.clone().add(0.0D, 1.0D, 0.0D));
        final Block block3 = world.getBlockAt(loc2.clone().add(0.0D, 2.0D, 0.0D));
        final Block block4 = world.getBlockAt(p.getLocation().add(0.0D, 4.0D, 0.0D));
        block1.setType(Material.GOLD_BLOCK);
        block2.setType(Material.NETHERRACK);
        block3.setType(Material.FIRE);
        block4.setType(Material.BARRIER);

        final Location standLoc = loc2.clone().add(0.5D, 1.0D, 0.5D);
        Location facePlayer = standLoc.clone();
        facePlayer.setDirection(p.getLocation().add(0.0D, 1.5D, 0.0D).toVector().subtract(standLoc.toVector()));

        final ArmorStand herobrine = world.spawn(standLoc, ArmorStand.class, stand -> {
            stand.setInvisible(true);
            stand.setGravity(false);
            stand.setMarker(true);
            stand.setSmall(true);
            stand.setBasePlate(false);
            stand.setArms(true);
            stand.setCustomNameVisible(false);
            stand.setSilent(true);
            ItemStack head = SkullCreator.itemFromBase64(HEROCRINE_SKULL_BASE64);
            stand.setHelmet(head);
        });
        herobrine.setRotation(facePlayer.getYaw(), facePlayer.getPitch());

        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 1));
        p.setPlayerTime(15000L, true);
        p.playSound(p.getLocation(), Sound.ENTITY_GHAST_AMBIENT, 50.0F, 1.0F);
        p.playSound(p.getLocation(), Sound.AMBIENT_CAVE, 100.0F, 1.0F);
        p.playSound(p.getLocation(), Sound.AMBIENT_CAVE, 100.0F, 1.0F);
        p.playSound(p.getLocation(), Sound.AMBIENT_CAVE, 100.0F, 1.0F);
        levo(p);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Core.instance, () -> {
            herobrine.remove();
            p.playSound(p.getLocation(), Sound.ENTITY_GHAST_AMBIENT, 50.0F, 1.0F);
            p.getWorld().strikeLightning(loc2);
            p.getWorld().strikeLightning(loc2);
            p.getWorld().strikeLightning(loc2);
            p.getWorld().strikeLightning(loc2);
            p.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc2, 300);
            block1.setType(Material.REDSTONE_WALL_TORCH);
            block2.setType(Material.AIR);
            block3.setType(Material.AIR);
            block4.setType(Material.AIR);
            p.resetPlayerTime();
        }, 200L);
    }

    private static void levo(Player p) {
        if (Core.instance.getConfig().getBoolean("troll-config.herobrine-levitation")) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 200, 1));
        }
    }
}
