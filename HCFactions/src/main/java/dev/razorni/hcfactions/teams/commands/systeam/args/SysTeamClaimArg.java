package dev.razorni.hcfactions.teams.commands.systeam.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.claims.Claim;
import dev.razorni.hcfactions.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class SysTeamClaimArg extends Argument {
    private final ItemStack claimWand;
    private final Map<UUID, SystemClaim> claimMap;

    public SysTeamClaimArg(CommandManager manager) {
        super(manager, Collections.singletonList("claim"));
        this.claimMap = new HashMap<>();
        this.claimWand = new ItemBuilder(ItemUtils.getMat(manager.getTeamConfig().getString("CLAIMING.CLAIM_WAND.TYPE"))).setName(manager.getTeamConfig().getString("CLAIMING.CLAIM_WAND.NAME") + " &7(System Claim)").setLore(manager.getTeamConfig().getStringList("CLAIMING.CLAIM_WAND.LORE")).toItemStack();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String other = args[args.length - 1];
            return this.getInstance().getTeamManager().getSystemTeams().values().stream().map(Team::getName).filter(s -> s.regionMatches(true, 0, other, 0, other.length())).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        Player player = (Player) sender;
        Team team = this.getInstance().getTeamManager().getTeam(args[0]);
        if (team == null) {
            this.sendMessage(sender, Config.TEAM_NOT_FOUND.replaceAll("%team%", args[0]));
            return;
        }
        if (player.getInventory().firstEmpty() == -1) {
            this.sendMessage(sender, this.getLanguageConfig().getString("SYSTEM_TEAM_COMMAND.SYSTEM_TEAM_CLAIM.INVENTORY_FULL"));
            return;
        }
        this.claimMap.put(player.getUniqueId(), new SystemClaim(team));
        Utils.giveClaimingWand(this.getManager(), player, this.claimWand);
    }

    @EventHandler
    public void onItem(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        if (event.getItem().isSimilar(this.claimWand)) {
            this.clearSelection(player);
            this.manager.setItemInHand(player, new ItemStack(Material.AIR));
        }
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("SYSTEM_TEAM_COMMAND.SYSTEM_TEAM_CLAIM.USAGE");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!this.claimMap.containsKey(player.getUniqueId())) {
            return;
        }
        if (event.getItem() == null) {
            return;
        }
        if (!event.getItem().isSimilar(this.claimWand)) {
            return;
        }
        SystemClaim claim = this.claimMap.get(player.getUniqueId());
        Team team = claim.getSystemTeam();
        Location pos1 = claim.getLocation1();
        Location pos2 = claim.getLocation2();
        event.setCancelled(true);
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            this.sendMessage(player, this.getLanguageConfig().getString("SYSTEM_TEAM_COMMAND.SYSTEM_TEAM_CLAIM.CANCELLED_SELECTION"));
            this.clearSelection(player);
            this.getManager().setItemInHand(player, new ItemStack(Material.AIR));
            return;
        }
        if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && player.isSneaking()) {
            if (pos1 == null || pos2 == null) {
                this.sendMessage(player, this.getLanguageConfig().getString("SYSTEM_TEAM_COMMAND.SYSTEM_TEAM_CLAIM.INSUFFICIENT_SELECTIONS"));
                return;
            }
            Claim tClaim = new Claim(team.getUniqueID(), pos1, pos2);
            this.getInstance().getTeamManager().getClaimManager().saveClaim(tClaim);
            team.getClaims().add(tClaim);
            team.save();
            this.sendMessage(player, this.getLanguageConfig().getString("SYSTEM_TEAM_COMMAND.SYSTEM_TEAM_CLAIM.CLAIMED_SUCCESSFUL"));
            this.clearSelection(player);
            this.getManager().setItemInHand(player, new ItemStack(Material.AIR));
        } else {
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    if (event.getClickedBlock() == null) {
                        return;
                    }
                    Location location = event.getClickedBlock().getLocation();
                    player.sendMessage(CC.translate("&eYou set &a#1 &elocation."));
                    this.getInstance().getWallManager().clearPillar(player, claim.getLocation2());
                    Tasks.executeAsync(this.getManager(), () -> this.getInstance().getWallManager().sendPillar(player, location));
                    claim.setLocation2(location);
                }
                return;
            }
            if (event.getClickedBlock() == null) {
                return;
            }
            Location loc2 = event.getClickedBlock().getLocation();
            player.sendMessage(CC.translate("&eYou set &a#2 &elocation."));
            this.getInstance().getWallManager().clearPillar(player, claim.getLocation1());
            Tasks.executeAsync(this.getManager(), () -> this.getInstance().getWallManager().sendPillar(player, loc2));
            claim.setLocation1(loc2);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.claimMap.containsKey(player.getUniqueId())) {
            this.claimMap.remove(player.getUniqueId());
            player.getInventory().remove(this.claimWand);
        }
    }

    private void clearSelection(Player player) {
        if (this.claimMap.containsKey(player.getUniqueId())) {
            SystemClaim system = this.claimMap.get(player.getUniqueId());
            this.getInstance().getWallManager().clearPillar(player, system.getLocation1());
            this.getInstance().getWallManager().clearPillar(player, system.getLocation2());
            this.claimMap.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.getDrops().remove(this.claimWand);
        this.clearSelection(player);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItemDrop();
        if (item.getItemStack().isSimilar(this.claimWand)) {
            item.remove();
            this.clearSelection(player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().contains(this.claimWand)) {
            player.getInventory().remove(this.claimWand);
        }
    }

    @Getter
    @Setter
    private static class SystemClaim {
        private Team systemTeam;
        private Location location1;
        private Location location2;

        public SystemClaim(Team systemTeam) {
            this.systemTeam = systemTeam;
            this.location1 = null;
            this.location2 = null;
        }
    }
}
