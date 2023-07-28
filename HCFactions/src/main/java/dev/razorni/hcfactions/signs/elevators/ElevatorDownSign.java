package dev.razorni.hcfactions.signs.elevators;

import dev.razorni.hcfactions.signs.CustomSign;
import dev.razorni.hcfactions.signs.CustomSignManager;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.Set;

@Getter
public class ElevatorDownSign extends CustomSign {
    private final int elevatorIndex;
    private final int downIndex;

    public ElevatorDownSign(CustomSignManager manager) {
        super(manager, manager.getConfig().getStringList("SIGNS_CONFIG.DOWN_SIGN.LINES"));
        this.elevatorIndex = this.getIndex("elevator");
        this.downIndex = this.getIndex("down");
    }

    @Override
    public void onClick(Player player, Sign sign) {
        Location signLocation = sign.getLocation();
        Location playerLocation = player.getLocation();
        Block signBlock = signLocation.getBlock();
        Block playerBlock = player.getTargetBlock((Set) null, 10);
        if (this.getInstance().getGlitchListener().getHitCooldown().hasCooldown(player)) {
            return;
        }
        if (playerBlock != null && !playerBlock.getType().name().contains("SIGN")) {
            return;
        }
        if (signBlock.getRelative(BlockFace.DOWN).getType() == Material.AIR && signBlock.getRelative(BlockFace.DOWN, 2).getType() == Material.AIR) {
            player.sendMessage(this.getLanguageConfig().getString("CUSTOM_SIGNS.ELEVATOR_SIGNS.INVALID_SIGN"));
            return;
        }
        for (int i = signLocation.getWorld().getMaxHeight(); i >= 0; --i) {
            if (i == 0) {
                player.sendMessage(this.getLanguageConfig().getString("CUSTOM_SIGNS.ELEVATOR_SIGNS.CANNOT_FIND_LOCATION"));
                break;
            }
            Block blockAt = signLocation.getWorld().getBlockAt(signLocation.getBlockX(), i, signLocation.getBlockZ());
            Block relative = blockAt.getRelative(BlockFace.DOWN);
            if (blockAt.getType() == Material.AIR && relative.getType() == Material.AIR) {
                Location location = blockAt.getLocation().add(0.5, 0.0, 0.5);
                location.setYaw(playerLocation.getYaw());
                location.setPitch(playerLocation.getPitch());
                player.teleport(location);
                break;
            }
        }
    }

}