package dev.razorni.hcfactions.listeners.type;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.listeners.ListenerManager;
import dev.razorni.hcfactions.users.PlaySession;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.utils.CC;
import dev.razorni.hcfactions.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class MainListener extends Module<ListenerManager> {
    @Getter
    @Setter
    private static PlaySession playSession;
    private final ItemStack bookItem;
    private final List<ItemStack> joinItems;
    private String[] repairlines = new String[] { dev.razorni.hcfactions.utils.menuapi.CC.translate(" "), dev.razorni.hcfactions.utils.menuapi.CC.translate("&6Repair"), dev.razorni.hcfactions.utils.menuapi.CC.translate("&6&l┃ &fItem in Hand"), dev.razorni.hcfactions.utils.menuapi.CC.translate(" ")};


    public static PlaySession getSession() {
        return playSession;
    }

    public MainListener(ListenerManager manager) {
        super(manager);
        this.joinItems = new ArrayList<>();
        this.bookItem = this.loadBook();
        this.load();
    }

    public static void updateActionBar() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(HCF.getPlugin(), new Runnable() {

            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (HCF.getPlugin().getStaffManager().isStaffEnabled(p)) {
                        HCF.getPlugin().getStaffManager().sendActionBar(p);
                    }
                }
            }
        }, 0L, 20L);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) && block.getState() instanceof Sign) {
            Sign sign = (Sign)block.getState();
            for (int i = 0; i < this.repairlines.length; i++) {
                if (!sign.getLine(i).equals(this.repairlines[i]))
                    return;
            }
            if (HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId()).getBalance() < 500) {
                player.sendMessage(dev.razorni.hcfactions.utils.menuapi.CC.RED + "You dont have enough money.");
                return;
            }
            if (player.getItemInHand().getType() == Material.AIR) {
                player.sendMessage(ChatColor.RED + "You dont have any item in hand.");
                return;
            }
            if (player.getItemInHand().getDurability() > 0) {
                player.sendMessage(dev.razorni.hcfactions.utils.menuapi.CC.RED + "Item doesnt need to be repaired.");
                return;
            }
            ItemStack s = player.getItemInHand();
            s.setDurability((short) 0);
            player.setItemInHand(s);
            player.sendMessage(dev.razorni.hcfactions.utils.menuapi.CC.GREEN + "Successfully repaired item in your hand.");
            HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId()).setBalance(HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId()).getBalance() - 500);
            HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId()).save();
        }
    }

    private ItemStack loadBook() {
        ItemStack stack = new ItemBuilder(Material.WRITTEN_BOOK).toItemStack();
        BookMeta meta = (BookMeta) stack.getItemMeta();
        meta.setTitle(this.getConfig().getString("JOIN_ITEMS.BOOK_ITEM.TITLE"));
        meta.setPages(this.getConfig().getStringList("JOIN_ITEMS.BOOK_ITEM.PAGES"));
        meta.setAuthor(this.getConfig().getString("JOIN_ITEMS.BOOK_ITEM.AUTHOR"));
        stack.setItemMeta(meta);
        this.joinItems.add(stack);
        return stack;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        playSession = new PlaySession();
        String name = HCF.getPlugin().getRankManager().getRankColor(player) + player.getName();
        IChatBaseComponent CT1 = ChatSerializer.a("{\"text\": \"§d§lWelcome on PinkMC §7(1.0)\"}");
        IChatBaseComponent CS1 = ChatSerializer.a("{\"text\": \"§f§ostore.pinkmc.cc\"}");
        PacketPlayOutTitle title1 = new PacketPlayOutTitle(EnumTitleAction.TITLE, CT1);
        PacketPlayOutTitle subtitle1 = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, CS1);
        PacketPlayOutTitle length1 = new PacketPlayOutTitle(10, 30, 10);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title1);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitle1);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length1);
        event.setJoinMessage(null);
        if (!player.hasPlayedBefore() && this.getConfig().getBoolean("JOIN_ITEMS.ENABLED")) {
            for (ItemStack stack : this.joinItems) {
                player.getInventory().addItem(stack);
            }
            player.teleport(new Location(Bukkit.getWorld("world"), 0.491, 70, 0.643));
        }
        User targetUser = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        if (!targetUser.hasDailyTime() || targetUser.getCoinsleft() < 0L) {
            player.sendMessage(CC.translate(" "));
            player.sendMessage(CC.translate("&7➥ &b&nHey " + player.getName() + "!&r"));
            player.sendMessage(CC.translate("&eYou have pending prizes, redeem them by using &f/prizes"));
            player.sendMessage(CC.translate(" "));
        }
        if (this.getConfig().getBoolean("TEAM_INFO_JOIN")) {
            player.chat("/t info");
        }
    }

    @EventHandler
    public void EOTWJoin(AsyncPlayerPreLoginEvent event) {
        User profile = HCF.getPlugin().getUserManager().getByUUID(event.getUniqueId());
        if (profile.isEOTWKilled()) {
            event.setKickMessage(CC.chat("&cYou cannot join server till next map because you are killed on EOTW."));
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
        }
    }

    private void load() {
        updateActionBar();
        for (String s : this.getConfig().getStringList("JOIN_ITEMS.NORMAL_ITEMS")) {
            String[] items = s.split(", ");
            ItemBuilder builder = new ItemBuilder(Material.valueOf(items[0]), Integer.parseInt(items[1]));
            if (!items[2].equals("NONE")) {
                String[] enchantments = items[2].split(":");
                Enchantment enchantment = Enchantment.getByName(enchantments[0]);
                builder.addEnchant(enchantment, Integer.parseInt(enchantments[1]));
            }
            this.joinItems.add(builder.toItemStack());
        }
        new BukkitRunnable() {
            public void run() {
                List<String> lines = MainListener.this.getConfig().getStringList("ONLINE_DONOR.MESSAGE");
                List<String> toSend = new ArrayList<>();
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (!online.hasPermission("azurite.donor")) {
                        continue;
                    }
                    if (online.isOp()) {
                        continue;
                    }
                    toSend.add(online.getName());
                }
                String text = toSend.isEmpty() ? "None" : StringUtils.join(toSend, ", ");
                lines.replaceAll(s -> s.replaceAll("%members%", text));
                for (String s : lines) {
                    Bukkit.broadcastMessage(s);
                }
            }
        }.runTaskTimerAsynchronously(this.getInstance(), 400L, 20L * this.getConfig().getInt("ONLINE_DONOR.INTERVAL"));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        User profile = HCF.getPlugin().getUserManager().getByUUID(event.getPlayer().getUniqueId());
        playSession.setEndTime(System.currentTimeMillis());
        profile.setPlaytime(profile.getPlaytime() + playSession.getTime());
        profile.save();
        event.setQuitMessage(null);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();
        if (item.getItemStack().isSimilar(this.bookItem)) {
            item.remove();
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof EnderDragon || entity instanceof Wither) {
            event.setCancelled(true);
        }
    }
}
