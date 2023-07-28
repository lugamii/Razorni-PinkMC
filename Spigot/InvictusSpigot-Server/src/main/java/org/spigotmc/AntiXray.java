package org.spigotmc;

import gnu.trove.set.TByteSet;
import gnu.trove.set.hash.TByteHashSet;
import net.minecraft.server.*;
import net.techcable.tacospigot.utils.BlockHelper;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;

import java.util.ArrayList;
// TacoSpigot end

public class AntiXray
{

    // Used to keep track of which blocks to obfuscate
    private final boolean[] obfuscateBlocks = new boolean[ Short.MAX_VALUE ];
    // Used to select a random replacement ore
    private final byte[] replacementOres;
    // PaperSpigot start
    public boolean queueUpdates = true;
    public final ArrayList<BlockPosition> pendingUpdates = new ArrayList<>();
    // PaperSpigot end

    public AntiXray(SpigotWorldConfig config)
    {
        // Set all listed blocks as true to be obfuscated
        for ( int id : ( config.engineMode == 1 ) ? config.hiddenBlocks : config.replaceBlocks )
        {
            obfuscateBlocks[id] = true;
        }

        // For every block
        TByteSet blocks = new TByteHashSet();
        for ( Integer i : config.hiddenBlocks )
        {
            Block block = Block.getById( i );
            // Check it exists and is not a tile entity
            if ( block != null && !block.isTileEntity() )
            {
                // Add it to the set of replacement blocks
                blocks.add( (byte) (int) i );
            }
        }
        // Bake it to a flat array of replacements
        replacementOres = blocks.toArray();
    }

    /**
     * PaperSpigot - Flush queued block updates for world.
     */
    public void flushUpdates(World world)
    {
        if ( world.spigotConfig.antiXray && !pendingUpdates.isEmpty() )
        {
            queueUpdates = false;

            for ( BlockPosition position : pendingUpdates )
            {
                updateNearbyBlocks( world, position );
            }

            pendingUpdates.clear();
            queueUpdates = true;
        }
    }

    /**
     * Starts the timings handler, then updates all blocks within the set radius
     * of the given coordinate, revealing them if they are hidden ores.
     */
    public void updateNearbyBlocks(World world, BlockPosition position)
    {
        if ( world.spigotConfig.antiXray )
        {
            // PaperSpigot start
            if ( queueUpdates )
            {
                pendingUpdates.add( position );
                return;
            }
            // PaperSpigot end
            updateNearbyBlocks( world, position.getX(), position.getY(), position.getZ(), 2, false ); // 2 is the radius, we shouldn't change it as that would make it exponentially slower
        }
    }

    /**
     * Starts the timings handler, and then removes all non exposed ores from
     * the chunk buffer.
     */
    public void obfuscateSync(int chunkX, int chunkY, int bitmask, byte[] buffer, World world)
    {
        if ( world.spigotConfig.antiXray )
        {
            obfuscate( chunkX, chunkY, bitmask, buffer, world );
        }
    }

    /**
     * Removes all non exposed ores from the chunk buffer.
     */
    public void obfuscate(int chunkX, int chunkY, int bitmask, byte[] buffer, World world)
    {
        // If the world is marked as obfuscated
        if ( world.spigotConfig.antiXray )
        {
            // Initial radius to search around for air
            int initialRadius = 1;
            // Which block in the buffer we are looking at, anywhere from 0 to 16^4
            int index = 0;
            // The iterator marking which random ore we should use next
            int randomOre = 0;

            // Chunk corner X and Z blocks
            int startX = chunkX << 4;
            int startZ = chunkY << 4;

            byte replaceWithTypeId;
            switch ( world.getWorld().getEnvironment() )
            {
                case NETHER:
                    replaceWithTypeId = (byte) CraftMagicNumbers.getId(Blocks.NETHERRACK);
                    break;
                case THE_END:
                    replaceWithTypeId = (byte) CraftMagicNumbers.getId(Blocks.END_STONE);
                    break;
                default:
                    replaceWithTypeId = (byte) CraftMagicNumbers.getId(Blocks.STONE);
                    break;
            }

            BlockPosition.MutableBlockPosition pos = new BlockPosition.MutableBlockPosition(); // TacoSpigot - preallocate MutableBlockPosition
            // Chunks can have up to 16 sections
            for ( int i = 0; i < 16; i++ )
            {
                // If the bitmask indicates this chunk is sent...
                if ( ( bitmask & 1 << i ) != 0 )
                {
                    // Work through all blocks in the chunk, y,z,x
                    for ( int y = 0; y < 16; y++ )
                    {
                        for ( int z = 0; z < 16; z++ )
                        {
                            for ( int x = 0; x < 16; x++ )
                            {
                                // For some reason we can get too far ahead of ourselves (concurrent modification on bulk chunks?) so if we do, just abort and move on
                                if ( index >= buffer.length )
                                {
                                    index++;
                                    continue;
                                }
                                // Grab the block ID in the buffer.
                                // TODO: extended IDs are not yet supported
                                int blockId = (buffer[index << 1] & 0xFF)
                                        | ((buffer[(index << 1) + 1] & 0xFF) << 8);
                                blockId >>>= 4;
                                // Check if the block should be obfuscated
                                if ( obfuscateBlocks[blockId] )
                                {
                                    // The world isn't loaded, bail out
                                    // TacoSpigot start
                                    pos.setValues(startX + x, ( i << 4 ) + y, startZ + z);
                                    if ( !isLoaded( world, /* new BlockPosition( startX + x, ( i << 4 ) + y, startZ + z ) */ pos, initialRadius ) )
                                    {
                                        // TacoSpigot end
                                        index++;
                                        continue;
                                    }
                                    // On the otherhand, if radius is 0, or the nearby blocks are all non air, we can obfuscate
                                    if ( !hasTransparentBlockAdjacent( world, /* new BlockPosition( startX + x, ( i << 4 ) + y, startZ + z ) */ pos, initialRadius ) ) // TacoSpigot - use prexisting MutableBlockPosition
                                    {
                                        int newId = blockId;
                                        switch ( world.spigotConfig.engineMode )
                                        {
                                            case 1:
                                                // Replace with replacement material
                                                newId = replaceWithTypeId & 0xFF;
                                                break;
                                            case 2:
                                                // Replace with random ore.
                                                if ( randomOre >= replacementOres.length )
                                                {
                                                    randomOre = 0;
                                                }
                                                newId = replacementOres[randomOre++] & 0xFF;
                                                break;
                                        }
                                        newId <<= 4;
                                        buffer[index << 1] = (byte) (newId & 0xFF);
                                        buffer[(index << 1) + 1] = (byte) ((newId >> 8) & 0xFF);
                                    }
                                }

                                index++;
                            }
                        }
                    }
                }
            }
        }
    }

    // TacoSpigot start
    private void updateNearbyBlocks(World world, final int x, final int y, final int z, int radius, boolean updateSelf) {
        int startX = x - radius;
        int endX = x + radius;
        int startY = Math.max(0, y - radius);
        int endY = Math.min(255, y + radius);
        int startZ = z - radius;
        int endZ = z + radius;
        for (int x1 = startX; x1 <= endX; x1++) {
            for (int y1 = startY; y1 <= endY; y1++) {
                for (int z1 = startZ; z1 <= endZ; z1++) {
                    if (!updateSelf && x1 == x & y1 == y & z1 == z)
                        continue;
                    if (world.isLoaded(x1, y1, z1))
                        updateBlock(world, x1, y1, z1);
                }
            }
        }
    }

    private void updateBlock(World world, int x, int y, int z)
    {
        // If the block in question is loaded

        // See if it needs update
        if ( obfuscateBlocks[Block.getId( getType(world, x, y, z) )] ) // TacoSpigot - always update
        {
            // Send the update
            world.notify( new BlockPosition(x, y, z) );
        }

    }

    private static boolean isLoaded(World world, BlockPosition position, int radius)
    {
        // TacoSpigot start
        return BlockHelper.isAllAdjacentBlocksLoaded(world, position, radius);
        // TacoSpigot end
    }

    // TacoSpigot start
    private static boolean hasTransparentBlockAdjacent(World w, BlockPosition startPos, int radius)
    {
        // !(solid blocks all around)
        return !BlockHelper.isAllAdjacentBlocksFillPredicate(w, startPos, radius, (world, position) -> isSolidBlock(getType(world, position.getX(), position.getY(), position.getZ()))); /* isSolidBlock */
        // TacoSpigot end
    }

    private static boolean isSolidBlock(Block block) {
        // Mob spawners are treated as solid blocks as far as the
        // game is concerned for lighting and other tasks but for
        // rendering they can be seen through therefor we special
        // case them so that the antixray doesn't show the fake
        // blocks around them.
        return block.isOccluding() && block != Blocks.MOB_SPAWNER && block != Blocks.BARRIER;
    }

    // TacoSpigot start
    public static Block getType(World world, int x, int y, int z) {
        Chunk chunk = world.getChunkIfLoaded(x >> 4, z >> 4);
        if (chunk == null) return Blocks.AIR;
        int sectionId = y >> 4;
        if (sectionId < 0 || sectionId > 15) return Blocks.AIR;
        ChunkSection section = chunk.getSections()[sectionId];
        if (section == null) return Blocks.AIR; // Handle empty chunks
        return BlockHelper.getBlock(section.getIdArray()[((y & 0xF) << 8) | ((z & 0xF) << 4) | (x & 0xF)] >> 4);
    }
    // TacoSpigot end
}
