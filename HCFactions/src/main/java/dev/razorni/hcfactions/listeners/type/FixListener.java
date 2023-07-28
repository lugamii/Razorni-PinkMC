package dev.razorni.hcfactions.listeners.type;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.listeners.ListenerManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.potion.PotionEffectType;

import java.util.Iterator;
import java.util.Random;

public class FixListener extends Module<ListenerManager> {
    private final Random foodRandom;

    public FixListener(ListenerManager manager) {
        super(manager);
        this.foodRandom = new Random();
        this.load();
    }

    @EventHandler
    public void onBed(PlayerBedEnterEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWorldChanged(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World from = event.getFrom();
        World world = player.getWorld();
        if (from == world) {
            return;
        }
        if (!player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            return;
        }
        if (world.getEnvironment() == World.Environment.THE_END) {
            player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (event.getFoodLevel() < player.getFoodLevel() && this.foodRandom.nextInt(101) > 4) {
            event.setCancelled(true);
        }
    }

    private void load() {
        if (!this.getConfig().getBoolean("FIXES.ENDERCHEST_CRAFTING.ENABLED")) {
            Iterator<Recipe> recipes = Bukkit.recipeIterator();
            while (recipes.hasNext()) {
                if (recipes.next().getResult().getType() != Material.ENDER_CHEST) {
                    continue;
                }
                recipes.remove();
            }
        }
    }

    @EventHandler
    public void onCreeper(EntityTargetEvent event) {
        if (!(event.getEntity() instanceof Creeper)) {
            return;
        }
        event.setTarget(null);
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlace(VehicleCreateEvent event) {
        Vehicle vehicle = event.getVehicle();
        if (!(vehicle instanceof Boat)) {
            return;
        }
        if (!this.getConfig().getBoolean("FIXES.BOATS.ENABLED")) {
            vehicle.remove();
            return;
        }
        if (!vehicle.getLocation().subtract(0.0, 1.0, 0.0).getBlock().isLiquid()) {
            vehicle.remove();
        }
    }
}
