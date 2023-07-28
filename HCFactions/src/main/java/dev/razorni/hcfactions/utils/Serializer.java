package dev.razorni.hcfactions.utils;

import dev.razorni.hcfactions.deathban.Deathban;
import dev.razorni.hcfactions.deathban.DeathbanManager;
import dev.razorni.hcfactions.extras.framework.Manager;
import dev.razorni.hcfactions.teams.claims.Claim;
import dev.razorni.hcfactions.teams.player.Member;
import dev.razorni.hcfactions.teams.player.Role;
import dev.razorni.hcfactions.utils.cuboid.Cuboid;
import dev.razorni.hcfactions.utils.extra.Pair;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

public class Serializer {
    public static String serializeClaim(Claim claim) {
        return claim.getTeam().toString() + ", " + claim.getWorldName() + ", " + claim.getX1() + ", " + claim.getY1() + ", " + claim.getZ1() + ", " + claim.getX2() + ", " + claim.getY2() + ", " + claim.getZ2();
    }

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

    public static String serializeMember(Member member) {
        return member.getUniqueID().toString() + ", " + member.getRole().toString();
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

    public static ItemStack deserializeItem(Manager manager, String input) {
        if (input.equals("null")) {
            return null;
        }
        String[] array = input.split(", ");
        ItemBuilder builder = new ItemBuilder(ItemUtils.getMat(array[0]), Serializer.parseInt(array[1])).data(manager, Short.parseShort(array[2]));
        if (!array[3].equals("none")) {
            if (!array[4].equals("none")) {
                builder.setName(array[4]);
            }
            if (!array[5].equals("none")) {
                for (String s : array[5].split(";")) {
                    builder.addLoreLine(s);
                }
            }
            if (!array[6].equals("none")) {
                for (String s : array[6].split(";")) {
                    String[] enchantments = s.split(":");
                    builder.addUnsafeEnchantment(Enchantment.getByName(enchantments[0]), Serializer.parseInt(enchantments[1]));
                }
            }
        }
        return builder.toItemStack();
    }

    public static Member deserializeMember(String input) {
        String[] array = input.split(", ");
        return new Member(UUID.fromString(array[0]), Role.valueOf(array[1]));
    }

    public static Claim deserializeClaim(String input) {
        String[] array = input.split(", ");
        return new Claim(UUID.fromString(array[0]), new Location(Bukkit.getWorld(array[1]), (double) parseInt(array[2]), (double) parseInt(array[3]), (double) parseInt(array[4])), new Location(Bukkit.getWorld(array[1]), (double) parseInt(array[5]), (double) parseInt(array[6]), (double) parseInt(array[7])));
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

    public static String serializeDeathban(Deathban input) {
        return input.getUniqueID().toString() + ";" + input.getTime() + ";" + input.getReason() + ";" + serializeLoc(input.getLocation()) + ";" + input.getDate().getTime();
    }

    public static Deathban deserializeDeathban(DeathbanManager manager, String input) {
        String[] array = input.split(";");
        Deathban deathban = new Deathban(manager, UUID.fromString(array[0]), Long.parseLong(array[1]), array[2], deserializeLoc(array[3]));
        deathban.setDate(new Date(Long.parseLong(array[4])));
        return deathban;
    }
}
