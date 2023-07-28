package dev.razorni.hcfactions.teams.commands.team.args.co_leader;

import cc.invictusgames.ilib.utils.CC;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.claims.Claim;
import dev.razorni.hcfactions.teams.claims.ClaimManager;
import dev.razorni.hcfactions.teams.player.Role;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.teams.type.WarzoneTeam;
import dev.razorni.hcfactions.teams.type.WildernessTeam;
import dev.razorni.hcfactions.utils.ItemBuilder;
import dev.razorni.hcfactions.utils.ItemUtils;
import dev.razorni.hcfactions.utils.Tasks;
import dev.razorni.hcfactions.utils.Utils;
import dev.razorni.hcfactions.utils.extra.Cooldown;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TeamClaimArg extends Argument {
    private final ItemStack claimWand;
    private final Cooldown cooldown;
    private final Map<UUID, PlayerClaim> claimMap;
    private final List<BlockFace> faces;

    public TeamClaimArg(CommandManager manager) {
        super(manager, Collections.singletonList("claim"));
        this.claimMap = new HashMap<>();
        this.cooldown = new Cooldown(manager);
        this.faces = Arrays.asList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);
        this.claimWand = new ItemBuilder(ItemUtils.getMat(manager.getTeamConfig().getString("CLAIMING.CLAIM_WAND.TYPE"))).setName(manager.getTeamConfig().getString("CLAIMING.CLAIM_WAND.NAME")).setLore(manager.getTeamConfig().getStringList("CLAIMING.CLAIM_WAND.LORE")).toItemStack();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.getDrops().remove(this.claimWand);
        this.clearSelection(player);
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
        if (this.getInstance().getTeamManager().getByPlayer(player.getUniqueId()) == null) {
            return;
        }
        if (!player.hasPermission("azurite.claim.bypass") && player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            this.sendMessage(player, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CLAIM.WRONG_WORLD"));
            return;
        }
        Block block = event.getClickedBlock();
        PlayerTeam playerTeam = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        PlayerClaim playerClaim = this.claimMap.get(player.getUniqueId());
        Location pos1 = playerClaim.getLocation1();
        Location pos2 = playerClaim.getLocation2();
        Action action = event.getAction();
        event.setCancelled(true);
        if (action == Action.RIGHT_CLICK_AIR) {
            this.clearSelection(player);
            this.sendMessage(player, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CLAIM.CANCELLED_SELECTION"));
            this.getManager().setItemInHand(player, new ItemStack(Material.AIR));
            return;
        }
        if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && player.isSneaking()) {
            if (pos1 == null || pos2 == null) {
                this.sendMessage(player, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CLAIM.INSUFFICIENT_SELECTIONS"));
                return;
            }
            Claim claim = new Claim(playerTeam.getUniqueID(), pos1, pos2);
            boolean bypassMoney = player.hasPermission("azurite.claim.nomoney");
            int price = this.getInstance().getTeamManager().getClaimManager().getPrice(claim, false);
            if (this.cannotClaim(player, pos1.getBlock()) || this.cannotClaim(player, pos2.getBlock())) {
                return;
            }
            if (this.cannotClaim(player, claim, false)) {
                return;
            }
            if (!playerTeam.getClaims().isEmpty() && this.cannotResize(player, pos1.getBlock()) && this.cannotResize(player, pos2.getBlock())) {
                this.sendMessage(player, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CLAIM.CLAIMS_TOUCHING"));
                return;
            }
            for (Block b : claim.getWalls(0, 0)) {
                if (this.cannotClaim(player, b)) {
                    return;
                }
            }
            if (playerTeam.getBalance() < price && !bypassMoney) {
                this.sendMessage(player, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CLAIM.INSUFFICIENT_BALANCE"));
                return;
            }
            if (!bypassMoney) {
                playerTeam.setBalance(playerTeam.getBalance() - price);
            }
            playerTeam.getClaims().add(claim);
            for (Claim c : playerTeam.getClaims()) {
                this.getInstance().getTeamManager().getClaimManager().saveClaim(c);
            }
            this.clearSelection(player);
            this.getManager().setItemInHand(player, new ItemStack(Material.AIR));
            this.sendMessage(player, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CLAIM.PURCHASED_CLAIM").replaceAll("%balance%", String.valueOf(playerTeam.getBalance())).replaceAll("%price%", String.valueOf(price)));
        } else {
            if (action != Action.RIGHT_CLICK_BLOCK) {
                if (action == Action.LEFT_CLICK_BLOCK) {
                    if (block == null) {
                        return;
                    }
                    if (this.cooldown.hasCooldown(player)) {
                        return;
                    }
                    Location loc = block.getLocation();
                    Team locTeam = this.getInstance().getTeamManager().getClaimManager().getTeam(loc);
                    if (!(locTeam instanceof WildernessTeam)) {
                        this.sendMessage(player, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CLAIM.CANNOT_CLAIM_HERE"));
                        return;
                    }
                    if (this.cannotClaim(player, block)) {
                        return;
                    }
                    if (pos2 != null && pos2.getBlockX() == loc.getBlockX() && pos2.getBlockZ() == loc.getBlockZ()) {
                        return;
                    }
                    if (!playerTeam.getClaims().isEmpty() && pos1 == null && this.cannotResize(player, block)) {
                        this.sendMessage(player, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CLAIM.CLAIMS_TOUCHING"));
                        return;
                    }
                    if (pos1 != null) {
                        Claim alreadyClaim = new Claim(playerTeam.getUniqueID(), pos1, loc);
                        if (this.cannotClaim(player, alreadyClaim, true)) {
                            return;
                        }
                    }
                    this.getInstance().getWallManager().clearPillar(player, playerClaim.getLocation2());
                    Tasks.execute(this.getManager(), () -> this.getInstance().getWallManager().sendPillar(player, loc));
                    this.cooldown.applyCooldownTicks(player, 25);
                    playerClaim.setLocation2(loc);
                }
                return;
            }
            if (block == null) {
                return;
            }
            if (this.cooldown.hasCooldown(player)) {
                return;
            }
            Location blockLocation = block.getLocation();
            Team blockTeam = this.getInstance().getTeamManager().getClaimManager().getTeam(blockLocation);
            if (!(blockTeam instanceof WildernessTeam)) {
                this.sendMessage(player, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CLAIM.CANNOT_CLAIM_HERE"));
                return;
            }
            if (this.cannotClaim(player, block)) {
                return;
            }
            if (pos1 != null && pos1.getBlockX() == blockLocation.getBlockX() && pos1.getBlockZ() == blockLocation.getBlockZ()) {
                return;
            }
            if (!playerTeam.getClaims().isEmpty() && pos2 == null && this.cannotResize(player, block)) {
                this.sendMessage(player, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CLAIM.CLAIMS_TOUCHING"));
                return;
            }
            if (pos2 != null) {
                Claim claim2 = new Claim(playerTeam.getUniqueID(), pos2, blockLocation);
                if (this.cannotClaim(player, claim2, true)) {
                    return;
                }
            }
            this.getInstance().getWallManager().clearPillar(player, playerClaim.getLocation1());
            Tasks.execute(this.getManager(), () -> this.getInstance().getWallManager().sendPillar(player, blockLocation));
            this.cooldown.applyCooldownTicks(player, 25);
            playerClaim.setLocation1(blockLocation);
        }
    }

    private boolean cannotResize(Player player, Block block) {
        PlayerTeam playerTeam = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        ClaimManager manager = this.getInstance().getTeamManager().getClaimManager();
        for (BlockFace face : this.faces) {
            Team team = manager.getTeam(block.getRelative(face).getLocation());
            if (team.getUniqueID() != playerTeam.getUniqueID()) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.claimMap.containsKey(player.getUniqueId())) {
            this.claimMap.remove(player.getUniqueId());
            player.getInventory().remove(this.claimWand);
        }
    }

    private boolean cannotClaim(Player player, Claim claim, boolean message) {
        int min = this.getTeamConfig().getInt("CLAIMING.MIN_SIZE");
        int max = this.getTeamConfig().getInt("CLAIMING.MAX_SIZE");
        if (claim.getLength() < min || claim.getWidth() < min) {
            this.sendMessage(player, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CLAIM.SIZE_SMALL").replaceAll("%size%", min + "x" + min));
            return true;
        }
        if (claim.getLength() > max || claim.getWidth() > max) {
            this.sendMessage(player, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CLAIM.SIZE_LARGE").replaceAll("%size%", max + "x" + max));
            return true;
        }
        if (message) {
            this.sendMessage(player, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CLAIM.SET_LOCATIONS").replaceAll("%price%", String.valueOf(this.getInstance().getTeamManager().getClaimManager().getPrice(claim, false))).replaceAll("%length%", String.valueOf(claim.getLength())).replaceAll("%width%", String.valueOf(claim.getWidth())).replaceAll("%blocks%", String.valueOf(claim.getArea())));
        }
        return false;
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

    @EventHandler
    public void onItem(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        if (event.getItem().isSimilar(this.claimWand)) {
            this.clearSelection(player);
            this.manager.setItemInHand(player, new ItemStack(Material.AIR));
        }
    }

    private void clearSelection(Player player) {
        if (this.claimMap.containsKey(player.getUniqueId())) {
            PlayerClaim claim = this.claimMap.get(player.getUniqueId());
            this.getInstance().getWallManager().clearPillar(player, claim.getLocation1());
            this.getInstance().getWallManager().clearPillar(player, claim.getLocation2());
            this.claimMap.remove(player.getUniqueId());
        }
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player) sender;
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        if (HCF.getPlugin().getEotwHandler().isEndOfTheWorld()) {
            player.sendMessage(CC.RED + "You cannot claim while EOTW is active.");
            return;
        }
        if (team == null) {
            this.sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }
        if (!team.checkRole(player, Role.CO_LEADER)) {
            this.sendMessage(sender, Config.INSUFFICIENT_ROLE.replaceAll("%role%", Role.CO_LEADER.getName()));
            return;
        }
        if (team.getClaims().size() == this.getTeamConfig().getInt("TEAMS.MAX_CLAIMS")) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CLAIM.MAX_CLAIMS"));
            return;
        }
        if (player.getInventory().firstEmpty() == -1) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CLAIM.INVENTORY_FULL"));
            return;
        }
        this.claimMap.put(player.getUniqueId(), new PlayerClaim());
        Utils.giveClaimingWand(this.getManager(), player, this.claimWand);
    }

    private boolean cannotClaim(Player player, Block block) {
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        Location location = block.getLocation();
        int separator = this.getTeamConfig().getInt("CLAIMING.CLAIM_SEPARATOR");
        int x = location.getBlockX();
        int z = location.getBlockZ();
        boolean claimed = false;
        for (int i = x - separator; i <= x + separator; ++i) {
            for (int f = z - separator; f <= z + separator; ++f) {
                Team tTeam = this.getInstance().getTeamManager().getClaimManager().getTeam(location.getWorld(), i, f);
                if (!(tTeam instanceof WildernessTeam)) {
                    if (!(tTeam instanceof WarzoneTeam)) {
                        if (tTeam != team) {
                            claimed = true;
                            break;
                        }
                    }
                }
            }
        }
        if (claimed) {
            this.sendMessage(player, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CLAIM.TOO_CLOSE").replaceAll("%amount%", String.valueOf(separator)));
        }
        return claimed;
    }

    @Override
    public String usage() {
        return null;
    }

    @Getter
    @Setter
    private static class PlayerClaim {
        private Location location1;
        private Location location2;

        public PlayerClaim() {
            this.location1 = null;
            this.location2 = null;
        }
    }
}