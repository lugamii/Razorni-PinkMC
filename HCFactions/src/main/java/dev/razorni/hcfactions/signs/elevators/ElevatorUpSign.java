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

@Getter
public class ElevatorUpSign extends CustomSign {
    private final int elevatorIndex;
    private final int upIndex;

    public ElevatorUpSign(CustomSignManager manager) {
        super(manager, manager.getConfig().getStringList("SIGNS_CONFIG.UP_SIGN.LINES"));
        this.elevatorIndex = this.getIndex("elevator");
        this.upIndex = this.getIndex("up");
    }

    @Override
    public void onClick(Player player, Sign sign) {
        Location signLocation = sign.getLocation();
        Location playerLocation = player.getLocation();
        Block signBlock = signLocation.getBlock();
        if (this.getInstance().getGlitchListener().getHitCooldown().hasCooldown(player) && !player.getEyeLocation().getBlock().getType().name().contains("SIGN")) {
            return;
        }
        if (signBlock.getRelative(BlockFace.UP).getType() == Material.AIR && signBlock.getRelative(BlockFace.UP, 2).getType() == Material.AIR) {
            player.sendMessage(this.getLanguageConfig().getString("CUSTOM_SIGNS.ELEVATOR_SIGNS.INVALID_SIGN"));
            return;
        }
        for (int i = signLocation.getBlockY(); i <= signLocation.getWorld().getMaxHeight(); ++i) {
            if (i == signLocation.getWorld().getMaxHeight()) {
                player.sendMessage(this.getLanguageConfig().getString("CUSTOM_SIGNS.ELEVATOR_SIGNS.CANNOT_FIND_LOCATION"));
                break;
            }
            Block block = signLocation.getWorld().getBlockAt(signLocation.getBlockX(), i, signLocation.getBlockZ());
            Block relative = block.getRelative(BlockFace.UP);
            if (block.getType() == Material.AIR && relative.getType() == Material.AIR) {
                Location location = block.getLocation().add(0.5, 0.0, 0.5);
                location.setYaw(playerLocation.getYaw());
                location.setPitch(playerLocation.getPitch());
                player.teleport(location);
                break;
            }
        }
    }
}