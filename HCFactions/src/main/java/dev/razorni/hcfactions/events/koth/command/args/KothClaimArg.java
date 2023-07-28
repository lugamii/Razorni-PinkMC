package dev.razorni.hcfactions.events.koth.command.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.events.koth.Koth;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.utils.ItemBuilder;
import dev.razorni.hcfactions.utils.ItemUtils;
import dev.razorni.hcfactions.utils.Utils;
import dev.razorni.hcfactions.utils.cuboid.Cuboid;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class KothClaimArg extends Argument {
    private final ItemStack claimWand;
    private final Map<UUID, ZoneClaim> claimCache;

    public KothClaimArg(CommandManager manager) {
        super(manager, Arrays.asList("claimzone", "setzone", "claim"));
        this.setPermissible("azurite.koth.claim");
        this.claimCache = new HashMap<>();
        this.claimWand = new ItemBuilder(ItemUtils.getMat(manager.getTeamConfig().getString("CLAIMING.CLAIM_WAND.TYPE"))).setName(String.valueOf(new StringBuilder().append(manager.getTeamConfig().getString("CLAIMING.CLAIM_WAND.NAME")).append(" &7(Capture Zone)"))).setLore(manager.getTeamConfig().getStringList("CLAIMING.CLAIM_WAND.LORE")).toItemStack();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!this.claimCache.containsKey(player.getUniqueId())) {
            return;
        }
        if (event.getItem() == null) {
            return;
        }
        if (!event.getItem().isSimilar(this.claimWand)) {
            return;
        }
        ZoneClaim claim = this.claimCache.get(player.getUniqueId());
        Location pos1 = claim.getLocation1();
        Location pos2 = claim.getLocation2();
        Koth koth = claim.getKoth();
        Block block = event.getClickedBlock();
        event.setCancelled(true);
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            this.claimCache.remove(player.getUniqueId());
            this.getManager().setItemInHand(player, new ItemStack(Material.AIR));
            return;
        }
        if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && player.isSneaking()) {
            if (pos1 == null || pos2 == null) {
                this.sendMessage(player, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_CLAIMZONE.NEED_BOTH_LOCS"));
                return;
            }
            Cuboid cuboid = new Cuboid(pos1, pos2);
            koth.checkZone(true);
            koth.setCaptureZone(cuboid);
            koth.save();
            this.claimCache.remove(player.getUniqueId());
            this.getManager().setItemInHand(player, new ItemStack(Material.AIR));
            this.sendMessage(player, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_CLAIMZONE.SET_ZONE"));
        } else {
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    if (block == null) {
                        return;
                    }
                    Location location = block.getLocation();
                    if (pos2 == location) {
                        return;
                    }
                    claim.setLocation2(location);
                    this.sendMessage(player, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_CLAIMZONE.UPDATED_LOC2").replaceAll("%loc%", Utils.formatLocation(location)));
                }
                return;
            }
            if (block == null) {
                return;
            }
            Location location = block.getLocation();
            if (pos1 == location) {
                return;
            }
            claim.setLocation1(location);
            this.sendMessage(player, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_CLAIMZONE.UPDATED_LOC1").replaceAll("%loc%", Utils.formatLocation(location)));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return this.getInstance().getKothManager().getKoths().values().stream().map(Koth::getName).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_CLAIMZONE.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        Player player = (Player) sender;
        Koth koth = this.getInstance().getKothManager().getKoth(args[0]);
        if (koth == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_NOT_FOUND").replaceAll("%koth%", args[0]));
            return;
        }
        this.claimCache.put(player.getUniqueId(), new ZoneClaim(koth));
        player.getInventory().addItem(this.claimWand);
        this.sendMessage(sender, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_CLAIMZONE.CLAIMING_STARTED").replaceAll("%koth%", koth.getName()));
    }

    @Getter
    @Setter
    private static class ZoneClaim {
        private Location location1;
        private Koth koth;
        private Location location2;


        public ZoneClaim(Koth koth) {
            this.koth = koth;
            this.location1 = null;
            this.location2 = null;
        }
    }
}
