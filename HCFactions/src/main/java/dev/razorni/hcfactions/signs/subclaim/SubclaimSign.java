package dev.razorni.hcfactions.signs.subclaim;

import dev.razorni.hcfactions.signs.CustomSign;
import dev.razorni.hcfactions.signs.CustomSignManager;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.player.Role;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.Arrays;
import java.util.List;

@Getter
public class SubclaimSign extends CustomSign {
    private final int memberIndex;
    private final List<BlockFace> around;
    private final int subclaimIndex;

    public SubclaimSign(CustomSignManager manager) {
        super(manager, manager.getConfig().getStringList("SIGNS_CONFIG.SUBCLAIM_SIGN.LINES"));
        this.subclaimIndex = this.getIndex("subclaim");
        this.memberIndex = this.getIndex("%member%");
        this.around = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);
    }

    private Sign getSubclaim(Block block) {
        Sign sign = this.checkSign(block);
        if (sign != null) {
            return sign;
        }
        for (BlockFace face : this.around) {
            Block relative = block.getRelative(face);
            if (relative.getType() == block.getType()) {
                Sign signChecked = this.checkSign(relative);
                if (signChecked != null) {
                    return signChecked;
                }
            }
        }
        return null;
    }

    @EventHandler
    public void onSign(SignChangeEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        PlayerTeam playerTeam = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        Team claimTeam = this.getInstance().getTeamManager().getClaimManager().getTeam(player.getLocation());
        if (!event.getLine(this.subclaimIndex).toLowerCase().contains("[subclaim]") || event.getLine(this.memberIndex).isEmpty()) {
            return;
        }
        if (playerTeam == null || playerTeam != claimTeam) {
            block.breakNaturally();
            player.sendMessage(this.getLanguageConfig().getString("CUSTOM_SIGNS.SUBCLAIM_SIGNS.NOT_CREATABLE"));
            return;
        }
        org.bukkit.material.Sign sign = (org.bukkit.material.Sign) block.getState().getData();
        Block relative = block.getRelative(sign.getAttachedFace());
        if (!relative.getType().name().contains("CHEST")) {
            block.breakNaturally();
            player.sendMessage(this.getLanguageConfig().getString("CUSTOM_SIGNS.SUBCLAIM_SIGNS.NOT_CHEST"));
            return;
        }
        if (this.getSubclaim(relative) != null) {
            block.breakNaturally();
            player.sendMessage(this.getLanguageConfig().getString("CUSTOM_SIGNS.SUBCLAIM_SIGNS.ALREADY_SUBCLAIMED"));
            return;
        }
        event.setLine(this.subclaimIndex, this.lines.get(this.subclaimIndex));
        player.sendMessage(this.getLanguageConfig().getString("CUSTOM_SIGNS.SUBCLAIM_SIGNS.CREATED_SUBCLAIM"));
    }

    @EventHandler
    public void onMove(InventoryMoveItemEvent event) {
        if (event.getSource().getType() != InventoryType.CHEST) {
            return;
        }
        if (event.getDestination().getType() != InventoryType.HOPPER) {
            return;
        }
        InventoryHolder holder = event.getSource().getHolder();
        Location location = (holder instanceof DoubleChest) ? ((DoubleChest) holder).getLocation() : ((holder instanceof BlockState) ? ((BlockState) holder).getLocation() : null);
        if (location != null && this.getSubclaim(location.getBlock()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!block.getType().name().contains("CHEST")) {
            return;
        }
        Sign sign = this.getSubclaim(block);
        if (sign != null && this.cannotUse(player, sign)) {
            event.setCancelled(true);
            player.sendMessage(this.getLanguageConfig().getString("CUSTOM_SIGNS.SUBCLAIM_SIGNS.DENIED_OPEN"));
        }
    }

    @Override
    public void onClick(Player player, Sign sign) {
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (block.getType().name().contains("CHEST") || block.getType().name().contains("SIGN")) {
            Sign sign = this.getSubclaim(block);
            if (sign != null && this.cannotUse(player, sign)) {
                event.setCancelled(true);
                player.sendMessage(this.getLanguageConfig().getString("CUSTOM_SIGNS.SUBCLAIM_SIGNS.DENIED_BREAK"));
            }
        }
    }

    private Sign checkSign(Block block) {
        if (block.getType().name().contains("SIGN")) {
            return (Sign) block.getState();
        }
        for (BlockFace face : this.around) {
            Block relative = block.getRelative(face);
            if (relative.getType().name().contains("SIGN")) {
                Sign sign = (Sign) relative.getState();
                org.bukkit.material.Sign data = (org.bukkit.material.Sign) sign.getData();
                Block relativeBlock = relative.getRelative(data.getAttachedFace());
                if (relativeBlock.getLocation().equals(block.getLocation()) && sign.getLine(this.subclaimIndex).equals(this.lines.get(this.subclaimIndex))) {
                    return sign;
                }
            }
        }
        return null;
    }

    public boolean cannotUse(Player player, Sign sign) {
        Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(sign.getLocation());
        if (!(team instanceof PlayerTeam)) {
            return false;
        }
        if (sign.getLine(this.memberIndex).equals(player.getName())) {
            return false;
        }
        PlayerTeam targetTeam = (PlayerTeam) team;
        return !targetTeam.isRaidable() && !targetTeam.checkRole(player, Role.CO_LEADER);
    }
}
