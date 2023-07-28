package dev.razorni.hcfactions.staff;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.framework.Manager;
import dev.razorni.hcfactions.staff.extra.StaffItem;
import dev.razorni.hcfactions.staff.extra.StaffItemAction;
import dev.razorni.hcfactions.staff.listener.StaffListener;
import dev.razorni.hcfactions.utils.ItemBuilder;
import dev.razorni.hcfactions.utils.ItemUtils;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

@Getter
public class StaffManager extends Manager {
    private final Set<UUID> frozen;
    private final Set<UUID> staffBuild;
    private final Set<UUID> vanished;
    private final Map<UUID, Staff> staffMembers;
    private final Map<ItemStack, StaffItem> staffItems;
    private PacketPlayOutChat packet;

    public StaffManager(HCF plugin) {
        super(plugin);
        this.staffMembers = new HashMap<>();
        this.staffItems = new HashMap<>();
        this.vanished = new HashSet<>();
        this.frozen = new HashSet<>();
        this.staffBuild = new HashSet<>();
        new StaffListener(this);
        this.load();
    }

    public boolean isStaffBuild(Player player) {
        return this.staffBuild.contains(player.getUniqueId());
    }

    @Override
    public void disable() {
        Iterator<UUID> members = this.staffMembers.keySet().iterator();
        while (members.hasNext()) {
            UUID member = members.next();
            Staff staff = this.staffMembers.get(member);
            Player player = Bukkit.getPlayer(member);
            PlayerInventory inventory = player.getInventory();
            for (PotionEffect effect : staff.getEffects()) {
                player.addPotionEffect(effect);
            }
            inventory.setContents(staff.getContents());
            inventory.setArmorContents(staff.getArmorContents());
            player.updateInventory();
            player.setGameMode(staff.getGameMode());
            members.remove();
        }
    }

    public boolean isStaffEnabled(Player player) {
        return this.staffMembers.containsKey(player.getUniqueId());
    }

    public void enableVanish(Player player) {
        this.vanished.add(player.getUniqueId());
        player.spigot().setCollidesWithEntities(false);
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.hasPermission("azurite.vanish")) {
                continue;
            }
            online.hidePlayer(player);
        }
    }

    public void openStaffList(Player player) {
        player.chat("/liststaff");
    }

    public void freezePlayer(Player player) {
        player.setWalkSpeed(0.0f);
        player.setFoodLevel(0);
        player.setSprinting(false);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128));
        this.frozen.add(player.getUniqueId());
    }

    public void disableVanish(Player player) {
        this.vanished.remove(player.getUniqueId());
        player.spigot().setCollidesWithEntities(true);
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.showPlayer(player);
        }
    }

    private void load() {
        for (String s : this.getConfig().getConfigurationSection("STAFF_MODE.STAFF_ITEMS").getKeys(false)) {
            String path = "STAFF_MODE.STAFF_ITEMS." + s + ".";
            String action = this.getConfig().getString(path + "ACTION");
            String replace = this.getConfig().getString(path + "REPLACE");
            ItemStack stack = new ItemBuilder(ItemUtils.getMat(this.getConfig().getString(path + "MATERIAL"))).setName(this.getConfig().getString(path + "NAME")).setLore(this.getConfig().getStringList(path + "LORE")).data(this, (short) this.getConfig().getInt(path + "DATA")).toItemStack();
            this.staffItems.put(stack, new StaffItem(action.isEmpty() ? null : StaffItemAction.valueOf(action), replace.isEmpty() ? null : replace, stack, this.getConfig().getInt(path + "SLOT")));
        }
    }

    public boolean isVanished(Player player) {
        return this.vanished.contains(player.getUniqueId());
    }

    public void unfreezePlayer(Player player) {
        player.setWalkSpeed(0.2f);
        player.setFoodLevel(20);
        player.setSprinting(false);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.removePotionEffect(PotionEffectType.JUMP);
        this.frozen.remove(player.getUniqueId());
    }

    public void sendActionBar(Player player) {
        String vanished = "";
        if (HCF.getPlugin().getStaffManager().isVanished(player)) {
            vanished = ChatColor.GREEN + "✔";
        } else {
            vanished = ChatColor.RED + "✘";
        }
        String staffchat = this.getInstance().getVersionManager().getVersion().getTPSColored();
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§dVanished: " + vanished + " §7┃ §dTPS: " + staffchat + " §7┃ §dOnline: " + ChatColor.GREEN + Bukkit.getOnlinePlayers().size() + "" + "\"}"), (byte) 2);
        this.packet = packet;
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public void enableStaff(Player player) {
        IChatBaseComponent CT = IChatBaseComponent.ChatSerializer.a("{\"text\": \"§d§lSTAFF\"}");
        IChatBaseComponent CS = IChatBaseComponent.ChatSerializer.a("{\"text\": \"§fEnabled\"}");
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, CT);
        PacketPlayOutTitle subtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, CS);
        PacketPlayOutTitle length = new PacketPlayOutTitle(10, 30, 10);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitle);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
        PlayerInventory inventory = player.getInventory();
        Staff staff = new Staff(inventory, player.getGameMode());
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1);
        skull.setDurability((short) 3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwner(player.getName());
        skullMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Staff List");
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.WHITE + "Right click to see all staff members online.");
        skullMeta.setLore(lore);
        skull.setItemMeta(skullMeta);
        inventory.clear();
        inventory.setArmorContents(null);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
            staff.getEffects().add(effect);
        }
        for (StaffItem item : this.staffItems.values()) {
            if (item.getAction() == StaffItemAction.VANISH_ON) {
                continue;
            }
            player.getInventory().setItem(item.getSlot() - 1, item.getItem());
        }
        player.getInventory().setItem(4, skull);
        player.updateInventory();
        player.setGameMode(GameMode.CREATIVE);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
        this.enableVanish(player);
        this.staffMembers.put(player.getUniqueId(), staff);
        this.getInstance().getNametagManager().update();
    }

    public boolean isFrozen(Player player) {
        return this.frozen.contains(player.getUniqueId());
    }

    public void disableStaff(Player player) {
        IChatBaseComponent CT = IChatBaseComponent.ChatSerializer.a("{\"text\": \"§d§lSTAFF\"}");
        IChatBaseComponent CS = IChatBaseComponent.ChatSerializer.a("{\"text\": \"§fDisabled\"}");
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, CT);
        PacketPlayOutTitle subtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, CS);
        PacketPlayOutTitle length = new PacketPlayOutTitle(10, 30, 10);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitle);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
        Staff staff = this.staffMembers.get(player.getUniqueId());
        if (staff != null) {
            PlayerInventory inventory = player.getInventory();
            inventory.setContents(staff.getContents());
            inventory.setArmorContents(staff.getArmorContents());
            for (PotionEffect effect : staff.getEffects()) {
                player.addPotionEffect(effect);
            }
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.updateInventory();
            player.setGameMode(staff.getGameMode());
            this.disableVanish(player);
            this.staffMembers.remove(player.getUniqueId());
            this.staffBuild.remove(player.getUniqueId());
            this.getInstance().getNametagManager().update();
        }
    }
}