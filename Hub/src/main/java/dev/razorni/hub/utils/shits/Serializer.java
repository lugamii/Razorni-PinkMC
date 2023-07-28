package dev.razorni.hub.utils.shits;

import dev.razorni.hub.framework.Manager;
import dev.razorni.hub.utils.cuboid.Cuboid;
import dev.razorni.hub.utils.extra.Pair;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.stream.Collectors;

public class Serializer {

    public static String serializeCuboid(Cuboid cuboid) {
        return cuboid.getWorldName() + ", " + cuboid.getX1() + ", " + cuboid.getY1() + ", " + cuboid.getZ1() + ", " + cuboid.getX2() + ", " + cuboid.getY2() + ", " + cuboid.getZ2();
    }

    public static Location deserializeLoc(String input) {
        String[] array = input.split(", ");
        return new Location(Bukkit.getWorld(array[0]), parseDouble(array[1]), parseDouble(array[2]), parseDouble(array[3]), parseFloat(array[4]), parseFloat(array[5]));
    }

    public static Cuboid deserializeCuboid(String input) {
        String[] array = input.split(", ");
        return new Cuboid(new Location(Bukkit.getWorld(array[0]), (double) parseInt(array[1]), (double) parseInt(array[2]), (double) parseInt(array[3])), new Location(Bukkit.getWorld(array[0]), (double) parseInt(array[4]), (double) parseInt(array[5]), (double) parseInt(array[6])));
    }

    public static String serializeMountainBlock(Location location, Material material) {
        return serializeLoc(location) + ";" + material.name();
    }

    public static Pair<Location, Material> deserializeMountainBlock(String input) {
        String[] array = input.split(";");
        return new Pair<>(deserializeLoc(array[0]), ItemUtils.getMat(array[1]));
    }

    public static PotionEffect getEffect(String input) {
        String[] array = input.split(", ");
        int max = array[1].equals("MAX_VALUE") ? Integer.MAX_VALUE : (20 * parseInt(array[1].replaceAll("s", "")));
        return new PotionEffect(PotionEffectType.getByName(array[0]), max, parseInt(array[2]) - 1);
    }

    private static Integer parseInt(String input) {
        return Integer.parseInt(input);
    }

    private static Float parseFloat(String input) {
        return Float.parseFloat(input);
    }

    private static Double parseDouble(String input) {
        return Double.parseDouble(input);
    }

    public static String serializeItem(Manager manager, ItemStack stack) {
        if (stack == null) {
            return "null";
        }
        String s = stack.getType().name() + ", " + stack.getAmount() + ", " + manager.getData(stack);
        if (stack.getItemMeta() != null) {
            s = s + ", true";
            ItemMeta meta = stack.getItemMeta();
            if (meta.getDisplayName() != null) {
                s = s + ", " + meta.getDisplayName();
            } else {
                s = s + ", none";
            }
            if (meta.getLore() != null) {
                s = s + ", " + StringUtils.join(meta.getLore(), ";");
            } else {
                s = s + ", none";
            }
            if (meta.hasEnchants()) {
                s = s + ", " + StringUtils.join(meta.getEnchants().keySet().stream().map(g -> g.getName() + ":" + meta.getEnchants().get(g)).collect(Collectors.toList()), ";");
            } else {
                s = s + ", none";
            }
        } else {
            s = s + ", none";
        }
        return s;
    }

    public static String serializeLoc(Location input) {
        return input.getWorld().getName() + ", " + input.getBlockX() + ", " + input.getBlockY() + ", " + input.getBlockZ() + ", " + input.getYaw() + ", " + input.getPitch();
    }

}
