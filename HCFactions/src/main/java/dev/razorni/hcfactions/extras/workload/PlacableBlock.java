package dev.razorni.hcfactions.extras.workload;

import lombok.RequiredArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;

import java.util.UUID;

/**
 * An arbitrary implementation for Workload that changes
 * a single Block to a given Material.
 */
@RequiredArgsConstructor
public class PlacableBlock implements Workload {

    private final UUID worldID;
    private final int blockX;
    private final int blockY;
    private final int blockZ;
    private final Material material;
    private final byte data;
    private boolean sound = true;

    public PlacableBlock(Location block, Material material, byte data, boolean sound) {
        this(block.getWorld().getUID(), block.getBlockX(), block.getBlockY(), block.getBlockZ(), material, data);
        this.sound = sound;
    }

    @Override
    public void compute() {
        World world = Bukkit.getWorld(this.worldID);
        Block block = world.getBlockAt(this.blockX, this.blockY, this.blockZ);

        if (sound){
            if (material != Material.AIR) {
                world.playEffect(new Location(world, blockX, blockY, blockZ), Effect.STEP_SOUND, material.getId());
            } else {
                world.playEffect(new Location(world, blockX, blockY, blockZ), Effect.STEP_SOUND, block.getType().getId());
            }
        }

        block.setTypeIdAndData(material.getId(), data,
                material != Material.WATER && material != Material.STATIONARY_WATER
                        && material != Material.LAVA && material != Material.STATIONARY_LAVA);
    }
}