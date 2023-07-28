package dev.razorni.hcfactions.utils.versions.type;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.loggers.Logger;
import dev.razorni.hcfactions.utils.ReflectionUtils;
import dev.razorni.hcfactions.utils.versions.Version;
import dev.razorni.hcfactions.utils.versions.VersionManager;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("unused")
public class Version1_7_R4 extends Module<VersionManager> implements Version {
    private static final Field INFO_FIELD;
    private static final Field CHANNEL_FIELD;
    private static final Field PROFILE_FIELD;
    private static final Field ACTION_FIELD;

    static {
        CHANNEL_FIELD = ReflectionUtils.accessField(NetworkManager.class, "m");
        INFO_FIELD = ReflectionUtils.accessField(PacketPlayOutPlayerInfo.class, "player");
        ACTION_FIELD = ReflectionUtils.accessField(PacketPlayOutPlayerInfo.class, "action");
        PROFILE_FIELD = ReflectionUtils.accessField(PacketPlayOutNamedEntitySpawn.class, "b");
    }

    public Version1_7_R4(VersionManager manager) {
        super(manager);
    }

    @Override
    public CommandMap getCommandMap() {
        try {
            CraftServer sender = (CraftServer) Bukkit.getServer();
            Method method = sender.getClass().getMethod("getCommandMap");
            return (CommandMap) method.invoke(sender, new Object[0]);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void playEffect(Location location, String inputEffect, Object obj) {
        try {
            Effect effect = Effect.valueOf(inputEffect);
            location.getWorld().playEffect(location, effect, obj);
        } catch (IllegalArgumentException ignored) {
            throw new IllegalArgumentException("Particle " + inputEffect + " does not exist.");
        }
    }

    @Override
    public void showArmor(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        for (int i = 1; i < 5; ++i) {
            net.minecraft.server.v1_7_R4.ItemStack stack = CraftItemStack.asNMSCopy(player.getInventory().getArmorContents()[i - 1]);
            PacketPlayOutEntityEquipment equipment = new PacketPlayOutEntityEquipment(player.getEntityId(), i, stack);
            entityPlayer.getWorld().getWorld().getHandle().getTracker().a(entityPlayer, equipment);
        }
    }

    @Override
    public void hideArmor(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        for (int i = 1; i < 5; ++i) {
            PacketPlayOutEntityEquipment equipment = new PacketPlayOutEntityEquipment(player.getEntityId(), i, null);
            entityPlayer.getWorld().getWorld().getHandle().getTracker().a(entityPlayer, equipment);
        }
    }

    @Override
    public void handleLoggerDeath(Logger logger) {
        EntityPlayer entityPlayer = ((CraftPlayer) logger.getPlayer()).getHandle();
        entityPlayer.getBukkitEntity().getInventory().clear();
        entityPlayer.getBukkitEntity().getInventory().setArmorContents(null);
        entityPlayer.getBukkitEntity().setExp(0.0f);
        entityPlayer.setHealth(0.0f);
        entityPlayer.getBukkitEntity().saveData();
    }

    @Override
    public String getTPSColored() {
        double tps = MinecraftServer.getServer().recentTps[0];
        String color = tps > 18.0 ? "§a" : (tps > 16.0 ? "§e" : "§c");
        String str = tps > 20.0 ? "*" : "";
        return color + str + Math.min((double) Math.round(tps * 100.0) / 100.0, 20.0);
    }

    @Override
    public int getPing(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        return craftPlayer.getHandle().ping;
    }

    @Override
    public void setItemInHand(Player player, ItemStack stack) {
        player.setItemInHand(stack);
    }

    @Override
    public boolean isNotGapple(ItemStack stack) {
        return stack.getType() != Material.GOLDEN_APPLE || stack.getDurability() != 1;
    }

    @Override
    public ItemStack getItemInHand(Player player) {
        return player.getItemInHand();
    }
}