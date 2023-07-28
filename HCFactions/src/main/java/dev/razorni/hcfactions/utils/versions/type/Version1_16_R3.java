package dev.razorni.hcfactions.utils.versions.type;

import com.mojang.datafixers.util.Pair;
import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.loggers.Logger;
import dev.razorni.hcfactions.utils.ReflectionUtils;
import dev.razorni.hcfactions.utils.versions.Version;
import dev.razorni.hcfactions.utils.versions.VersionManager;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class Version1_16_R3 extends Module<VersionManager> implements Version {
    private static final Method method;
    private static final Field ACTION_FIELD;
    private static final Field UUID_FIELD;
    private static final Field INFO_FIELD;

    static {
        INFO_FIELD = ReflectionUtils.accessField(PacketPlayOutPlayerInfo.class, "b");
        ACTION_FIELD = ReflectionUtils.accessField(PacketPlayOutPlayerInfo.class, "a");
        UUID_FIELD = ReflectionUtils.accessField(PacketPlayOutNamedEntitySpawn.class, "b");
        method = ReflectionUtils.accessMethod(CraftWorld.class, "spawnParticle", Particle.class, Location.class, Integer.TYPE, Object.class);
    }

    public Version1_16_R3(VersionManager manager) {
        super(manager);
    }

    @Override
    public void playEffect(Location location, String inputParticle, Object object) {
        try {
            Particle particle = Particle.valueOf(inputParticle);
            method.invoke(location.getWorld(), particle, location, 1, object);
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException ignored) {
            throw new IllegalArgumentException("Particle " + inputParticle + " does not exist.");
        }
    }

    @Override
    public CommandMap getCommandMap() {
        try {
            CraftServer server = (CraftServer) Bukkit.getServer();
            Method method = server.getClass().getMethod("getCommandMap");
            return (CommandMap) method.invoke(server, new Object[0]);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void hideArmor(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        if (entityPlayer.playerConnection != null) {
            ArrayList<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> pairs = new ArrayList<>();
            pairs.add(new Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(null)));
            pairs.add(new Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(null)));
            pairs.add(new Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(null)));
            pairs.add(new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(null)));
            entityPlayer.playerConnection.sendPacket(new PacketPlayOutEntityEquipment(player.getEntityId(), pairs));
        }
    }

    @Override
    public ItemStack getItemInHand(Player player) {
        try {
            Method method = player.getInventory().getClass().getMethod("getItemInMainHand");
            return (ItemStack) method.invoke(player.getInventory(), new Object[0]);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isNotGapple(ItemStack stack) {
        return !stack.getType().name().contains("ENCHANTED_GOLDEN_APPLE");
    }

    @Override
    public void setItemInHand(Player player, ItemStack stack) {
        try {
            Method method = player.getInventory().getClass().getMethod("setItemInMainHand", ItemStack.class);
            method.invoke(player.getInventory(), stack);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showArmor(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        if (entityPlayer.playerConnection != null) {
            PlayerInventory inventory = player.getInventory();
            ArrayList<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> list = new ArrayList<>();
            list.add(new Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(inventory.getItem(8))));
            list.add(new Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(inventory.getItem(7))));
            list.add(new Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(inventory.getItem(6))));
            list.add(new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(inventory.getItem(5))));
            entityPlayer.playerConnection.sendPacket(new PacketPlayOutEntityEquipment(player.getEntityId(), list));
        }
    }

    @Override
    public int getPing(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        return craftPlayer.getHandle().ping;
    }

    @Override
    public String getTPSColored() {
        double tps = MinecraftServer.getServer().recentTps[0];
        String color = tps > 18.0 ? "§a" : (tps > 16.0 ? "§e" : "§c");
        String text = tps > 20.0 ? "*" : "";
        return color + text + Math.min((double) Math.round(tps * 100.0) / 100.0, 20.0);
    }

    @Override
    public void handleLoggerDeath(Logger logger) {
        EntityPlayer entityPlayer = ((CraftPlayer) logger.getPlayer()).getHandle();
        entityPlayer.getBukkitEntity().getInventory().clear();
        entityPlayer.getBukkitEntity().getInventory().setArmorContents(null);
        entityPlayer.setHealth(0.0f);
        entityPlayer.getBukkitEntity().saveData();
    }
}
