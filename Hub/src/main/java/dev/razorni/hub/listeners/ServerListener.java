package dev.razorni.hub.listeners;

import dev.razorni.core.Core;
import dev.razorni.core.extras.rank.Rank;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.grant.event.GrantAppliedEvent;
import dev.razorni.core.profile.grant.event.GrantExpireEvent;
import dev.razorni.hub.Hub;
import dev.razorni.hub.menu.SelectorMenu;
import dev.razorni.hub.utils.shits.CC;
import dev.razorni.hub.utils.shits.ItemBuilder;
import dev.razorni.hub.utils.shits.ItemBuilder3;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class ServerListener implements Listener {

    ItemStack ENDER_BUTT = new ItemBuilder(Material.ENDER_PEARL, 1).displayName(CC.translate(Hub.getInstance().getSettingsConfig().getConfig().getString("ENDERBUTT.NAME"))).build();
    ItemStack SELECTOR = new ItemBuilder(Material.NETHER_STAR, 1).displayName(CC.translate(Hub.getInstance().getSettingsConfig().getConfig().getString("SELECTOR.NAME"))).build();

    public ServerListener() {
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(null);
        String name = Core.getInstance().getCoreAPI().getRankColor(player) + player.getName();
        if (Profile.getByUuid(player.getUniqueId()).getActiveBlacklist() != null) {
            IChatBaseComponent CT = ChatSerializer.a("{\"text\":\"" + "§4§lBLACKLISTED" + "\"}");
            IChatBaseComponent CS = ChatSerializer.a("{\"text\": \"§cYou cannot appeal this punishment\"}");
            PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, CT);
            PacketPlayOutTitle subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, CS);
            PacketPlayOutTitle length = new PacketPlayOutTitle(10, 30, 10);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitle);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
        } else if (Profile.getByUuid(player.getUniqueId()).getActiveBan() != null) {
            IChatBaseComponent CT = ChatSerializer.a("{\"text\":\"" + "§4§lBANNED" + "\"}");
            IChatBaseComponent CS = ChatSerializer.a("{\"text\": \"§cAppeal at discord.pinkmc.cc\"}");
            PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, CT);
            PacketPlayOutTitle subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, CS);
            PacketPlayOutTitle length = new PacketPlayOutTitle(10, 30, 10);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitle);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
        } else {
            IChatBaseComponent CT1 = ChatSerializer.a("{\"text\":\"" + "§d§lWelcome to PinkMC Network" + "\"}");
            IChatBaseComponent CS1 = ChatSerializer.a("{\"text\": \"§f§ostore.pinkmc.cc\"}");
            PacketPlayOutTitle title1 = new PacketPlayOutTitle(EnumTitleAction.TITLE, CT1);
            PacketPlayOutTitle subtitle1 = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, CS1);
            PacketPlayOutTitle length1 = new PacketPlayOutTitle(10, 30, 10);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title1);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitle1);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length1);
        }
        for (String s : Hub.getInstance().getSettingsConfig().getConfig().getStringList("JOIN-MESSAGE")) {
            event.getPlayer().sendMessage(CC.translate(s).replaceAll("%player%", event.getPlayer().getName()).replaceAll("%colorrank%", String.valueOf(Core.getInstance().getCoreAPI().getRankColor(player))).replaceAll("%rank%", Core.getInstance().getCoreAPI().getRankName(player)));
        }
        event.getPlayer().getInventory().setArmorContents(null);
        event.getPlayer().getInventory().clear();
        player.setWalkSpeed(0.2F);
        player.setFlySpeed(0.1F);
        player.setHealth(20.0D);
        player.setSaturation(20.0F);
        player.setFallDistance(0.0F);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setMaximumNoDamageTicks(20);
        player.setExp(0.0F);
        player.setLevel(0);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().setContents(new ItemStack[36]);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.getInventory().setHeldItemSlot(4);
        ItemStack SHOP = new ItemBuilder3(Material.SKULL_ITEM, 3)
                .setDisplayName(CC.translate("&dNetwork Shop"))
                .setSkullOwner(player.getName())
                .build();
        player.getInventory().setItem(4, SHOP);
        player.getInventory().setItem(Hub.getInstance().getSettingsConfig().getConfig().getInt("ENDERBUTT.SLOT"), ENDER_BUTT);
        player.getInventory().setItem(Hub.getInstance().getSettingsConfig().getConfig().getInt("SELECTOR.SLOT"), SELECTOR);
        giveArmor(event.getPlayer());
        player.updateInventory();
        player.teleport(new Location(Bukkit.getWorld("world"), -276.450, 74.000, 511.428));
        player.setAllowFlight(true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
    }

    private void giveArmor(Player player) {
        Rank rank = Profile.getByUuid(player.getUniqueId()).getActiveRank();
        ItemStack chest = new ItemBuilder(Material.LEATHER_CHESTPLATE).enchant(Enchantment.DURABILITY, 1).setLeatherArmorColor(colorByRank(rank)).build();
        ItemStack legs = new ItemBuilder(Material.LEATHER_LEGGINGS).enchant(Enchantment.DURABILITY, 1).setLeatherArmorColor(colorByRank(rank)).build();
        ItemStack boots = new ItemBuilder(Material.LEATHER_BOOTS).enchant(Enchantment.DURABILITY, 1).setLeatherArmorColor(colorByRank(rank)).build();
        player.getInventory().setChestplate(chest);
        player.getInventory().setLeggings(legs);
        player.getInventory().setBoots(boots);
        player.updateInventory();
    }

    private Color colorByRank(Rank rank) {
        if (rank.getColor() == ChatColor.RED) {
            return Color.RED;
        } else if (rank.getColor() == ChatColor.DARK_RED) {
            return Color.MAROON;
        } else if (rank.getColor() == ChatColor.BLUE) {
            return Color.BLUE;
        } else if (rank.getColor() == ChatColor.DARK_BLUE) {
            return Color.NAVY;
        } else if (rank.getColor() == ChatColor.GRAY) {
            return Color.SILVER;
        } else if (rank.getColor() == ChatColor.DARK_GRAY) {
            return Color.GRAY;
        } else if (rank.getColor() == ChatColor.WHITE) {
            return Color.WHITE;
        } else if (rank.getColor() == ChatColor.YELLOW) {
            return Color.YELLOW;
        } else if (rank.getColor() == ChatColor.GREEN) {
            return Color.LIME;
        } else if (rank.getColor() == ChatColor.DARK_GREEN) {
            return Color.GREEN;
        } else if (rank.getColor() == ChatColor.GOLD) {
            return Color.ORANGE;
        } else if (rank.getColor() == ChatColor.LIGHT_PURPLE) {
            return Color.FUCHSIA;
        } else if (rank.getColor() == ChatColor.DARK_PURPLE) {
            return Color.PURPLE;
        }
        return Color.TEAL;
    }

    @EventHandler
    public void onMobspawn(EntitySpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onMobTarget(EntityTargetEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler
    public void onGrantApply(GrantAppliedEvent event) {
        giveArmor(event.getPlayer());
    }
    @EventHandler
    public void onExpire(GrantExpireEvent event) {
        giveArmor(event.getPlayer());
    }

    @EventHandler
    public void onFoodLevel(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        event.setCancelled(true);

    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!event.getPlayer().hasMetadata("buildmode")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!event.getPlayer().hasMetadata("buildmode")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Material item = event.getPlayer().getItemInHand().getType();

        switch (item) {
            case ENDER_PEARL:
                if (event.getAction() != Action.RIGHT_CLICK_AIR) {
                    return;
                }
                player.setVelocity(player.getLocation().getDirection().normalize().multiply(3.4));
                player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1000, 1000);
                event.setUseItemInHand(Event.Result.DENY);
                event.setUseInteractedBlock(Event.Result.DENY);

                player.updateInventory();
                break;
            case NETHER_STAR:
                if (Profile.getByUuid(player.getUniqueId()).getActiveBan() != null || Profile.getByUuid(player.getUniqueId()).getActiveBlacklist() != null) {
                    player.sendMessage(dev.razorni.core.util.CC.RED + "You cannot join server while you have active punishment.");
                    return;
                }
                new SelectorMenu().openMenu(player);
                break;
            case SKULL_ITEM:
                player.chat("/buy");
        }
    }

    @SneakyThrows
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile instanceof EnderPearl) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event){
        if (!event.getWhoClicked().hasMetadata("buildmode")) {
            event.getResult();
            event.setResult(Event.Result.DENY);
        }
    }

}
