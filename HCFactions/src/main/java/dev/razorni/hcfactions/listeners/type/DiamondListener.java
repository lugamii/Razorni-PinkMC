package dev.razorni.hcfactions.listeners.type;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.listeners.ListenerManager;
import dev.razorni.hcfactions.pvpclass.type.miner.MinerClass;
import dev.razorni.hcfactions.users.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.List;

public class DiamondListener extends Module<ListenerManager> {
    private final List<BlockFace> faces;

    public DiamondListener(ListenerManager manager) {
        super(manager);
        this.faces = Arrays.asList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST, BlockFace.UP, BlockFace.DOWN);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (block.getType() != Material.DIAMOND_ORE) {
            return;
        }
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        user.setDiamonds(user.getDiamonds() + 1);
        user.save();
        MinerClass minerClass = this.getInstance().getClassManager().getMinerClass();
        if (minerClass.getPlayers().contains(player.getUniqueId())) {
            minerClass.addEffects(player);
        }
        if (block.hasMetadata("exception")) {
            block.removeMetadata("exception", this.getInstance());
            return;
        }
        block.setMetadata("exception", new FixedMetadataValue(this.getInstance(), true));
        String s = this.getLanguageConfig().getString("DIAMOND_LISTENER.FD_MESSAGE").replaceAll("%player%", event.getPlayer().getName()).replaceAll("%amount%", String.valueOf(this.count(block)));
        for (Player online : Bukkit.getOnlinePlayers()) {
            User userOnline = this.getInstance().getUserManager().getByUUID(online.getUniqueId());
            if (userOnline.isFoundDiamondAlerts()) {
                online.sendMessage(s);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (block.getType() == Material.DIAMOND_ORE) {
            block.setMetadata("exception", new FixedMetadataValue(this.getInstance(), true));
        }
    }

    private int count(Block block) {
        int i = 1;
        for (BlockFace face : this.faces) {
            Block relative = block.getRelative(face);
            if (relative.hasMetadata("exception")) {
                continue;
            }
            if (relative.getType() != Material.DIAMOND_ORE) {
                continue;
            }
            relative.setMetadata("exception", new FixedMetadataValue(this.getInstance(), true));
            i += this.count(relative);
        }
        return i;
    }
}
