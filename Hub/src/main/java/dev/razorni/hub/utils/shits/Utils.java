package dev.razorni.hub.utils.shits;

import dev.razorni.hub.Hub;
import dev.razorni.hub.framework.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import protocolsupport.api.ProtocolSupportAPI;
import us.myles.ViaVersion.api.Via;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Utils {
    public static List<PotionEffectType> DEBUFFS;
    public static Pattern ALPHA_NUMERIC;

    static {
        DEBUFFS = Arrays.asList(PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION, PotionEffectType.HARM, PotionEffectType.HUNGER, PotionEffectType.POISON, PotionEffectType.SATURATION, PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING, PotionEffectType.WEAKNESS, PotionEffectType.WITHER);
        ALPHA_NUMERIC = Pattern.compile("[^a-zA-Z0-9]");
    }

    public static boolean isNotAlphanumeric(String input) {
        return Utils.ALPHA_NUMERIC.matcher(input).find();
    }

    public static int getAmountItems(Manager manager, Player player, ItemStack stack) {
        ItemStack[] contents = player.getInventory().getContents();
        int i = 0;
        for (ItemStack type : contents) {
            if (type == null || type.getType() != stack.getType() || manager.getData(type) != manager.getData(stack))
                continue;
            i += type.getAmount();
        }
        return i;
    }

    public static boolean isDebuff(ThrownPotion potion) {
        for (PotionEffect effect : potion.getEffects()) {
            if (!Utils.DEBUFFS.contains(effect.getType())) {
                continue;
            }
            return true;
        }
        return false;
    }

    public static Block getActualHighestBlock(Block block) {
        for (block = block.getWorld().getHighestBlockAt(block.getLocation()); block.getType() == Material.AIR && block.getY() > 0; block = block.getRelative(BlockFace.DOWN)) {
        }
        return block;
    }

    public static void giveClaimingWand(Manager manager, Player player, ItemStack stack) {
        if (player.getInventory().contains(stack)) {
            player.getInventory().remove(stack);
        }
        if (manager.getItemInHand(player) == null) {
            manager.setItemInHand(player, stack);
        } else {
            player.getInventory().addItem(stack);
        }
    }

    public static void takeItems(Manager manager, Player player, ItemStack type, int amount) {
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < player.getInventory().getContents().length; ++i) {
            ItemStack stack = contents[i];
            if (stack != null) {
                if (stack.getType() == type.getType()) {
                    if (manager.getData(stack) == manager.getData(type)) {
                        if (amount < stack.getAmount()) {
                            stack.setAmount(stack.getAmount() - amount);
                            break;
                        }
                        amount -= stack.getAmount();
                        player.getInventory().setItem(i, null);
                        player.updateInventory();
                    }
                }
            }
        }
    }

    public static String getNMSVer() {
        String bukkit = Bukkit.getServer().getClass().getPackage().getName();
        return bukkit.split("\\.")[3].replaceAll("v", "");
    }

    public static <E> List<E> createList(Object object, Class<E> clazz) {
        List<E> list = new ArrayList<E>();
        if (!(object instanceof List)) {
            return list;
        }
        for (Object o : (List) object) {
            if (o == null) {
                continue;
            }
            if (o.getClass() == null) {
                continue;
            }
            if (!clazz.isAssignableFrom(o.getClass())) {
                String s = clazz.getSimpleName();
                throw new AssertionError("Cannot cast to list! Key " + o + " is not a " + s);
            }
            E e = clazz.cast(o);
            list.add(e);
        }
        return list;
    }

    public static boolean isntNumber(String input) {
        try {
            Integer.parseInt(input);
            return false;
        } catch (NumberFormatException ignored) {
            return true;
        }
    }

    public static boolean isVer16(Player player) {
        return getProtocolVersion(player) > 107;
    }

    public static String formatLocation(Location location) {
        return location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
    }

    public static int getProtocolVersion(Player player) {
        PluginManager manager = Bukkit.getPluginManager();
        if (manager.getPlugin("ViaVersion") != null) {
            return Via.getAPI().getPlayerVersion(player.getUniqueId());
        }
        if (manager.getPlugin("ProtocolSupport") != null) {
            return ProtocolSupportAPI.getProtocolVersion(player).getId();
        }
        return 100;
    }

    public static String fastReplace(String s1, String s2, String s3) {
        int lenght = s2.length();
        int index = s1.indexOf(s2);
        if (lenght == 0) {
            return s1;
        }
        if (index == -1) {
            return s1;
        }
        StringBuilder builder = new StringBuilder((lenght > s3.length()) ? s1.length() : (s1.length() * 2));
        int i = 0;
        do {
            builder.append(s1, i, index);
            builder.append(s3);
            i = index + lenght;
            index = s1.indexOf(s2, i);
        } while (index > 0);
        builder.append(s1, i, s1.length());
        return String.valueOf(builder);
    }

    public static Player getDamager(Entity entity) {
        if (entity instanceof Player) {
            return (Player) entity;
        }
        if (entity instanceof Projectile) {
            Projectile projectile = (Projectile) entity;
            if (projectile.getShooter() instanceof Player) {
                return (Player) projectile.getShooter();
            }
        }
        return null;
    }

    public static boolean verifyPlugin(String pluginName, Hub plugin) {
        PluginManager manager = plugin.getServer().getPluginManager();
        return manager.getPlugin(pluginName) != null;
    }
}
