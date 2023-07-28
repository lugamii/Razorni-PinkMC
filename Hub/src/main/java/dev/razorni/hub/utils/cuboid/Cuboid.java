package dev.razorni.hub.utils.cuboid;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;


@SuppressWarnings({"unused", "CopyConstructorMissesField"})
@Getter
@Setter
public class Cuboid implements Iterable<Block>, Cloneable {
    protected int z2;
    protected int y2;
    protected String worldName;
    protected int x1;
    protected int y1;
    protected int z1;
    protected int x2;

    public Cuboid(Cuboid cuboid) {
        this(cuboid.getWorld().getName(), cuboid.x1, cuboid.y1, cuboid.z1, cuboid.x2, cuboid.y2, cuboid.z2);
    }

    public Cuboid(Location pos1, Location pos2) {
        Preconditions.checkNotNull(pos1, "Location 1 cannot be null");
        Preconditions.checkNotNull(pos2, "Location 2 cannot be null");
        Preconditions.checkArgument(pos1.getWorld().equals(pos2.getWorld()), "Locations must be on the same world");
        this.worldName = pos1.getWorld().getName();
        this.x1 = Math.min(pos1.getBlockX(), pos2.getBlockX());
        this.y1 = Math.min(pos1.getBlockY(), pos2.getBlockY());
        this.z1 = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        this.x2 = Math.max(pos1.getBlockX(), pos2.getBlockX());
        this.y2 = Math.max(pos1.getBlockY(), pos2.getBlockY());
        this.z2 = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
    }

    public Cuboid(String worldName, int x1, int y1, int z1, int x2, int y2, int z2) {
        Preconditions.checkNotNull(worldName, "World name cannot be null");
        this.worldName = worldName;
        this.x1 = Math.min(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.x2 = Math.max(x1, x2);
        this.y2 = Math.max(y1, y2);
        this.z2 = Math.max(z1, z2);
    }

    public boolean containsOnly(Material material) {
        for (Block block : this) {
            if (block.getType() != material) {
                return false;
            }
        }
        return true;
    }

    public boolean contains(int x, int y, int z) {
        return x >= this.x1 && x <= this.x2 && y >= this.y1 && y <= this.y2 && z >= this.z1 && z <= this.z2;
    }

    public Set<Player> getPlayers() {
        Set<Player> toReturn = new HashSet<>();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (this.contains(online)) {
                toReturn.add(online);
            }
        }
        return toReturn;
    }

    public Location getMinimumPoint() {
        return new Location(this.getWorld(), Math.min(this.x1, this.x2), Math.min(this.y1, this.y2), Math.min(this.z1, this.z2));
    }

    public Location getCenter() {
        int x = this.x2 + 1;
        int y = this.y2 + 1;
        int z = this.z2 + 1;
        return new Location(this.getWorld(), this.x1 + (x - this.x1) / 2.0, this.y1 + (y - this.y1) / 2.0, this.z1 + (z - this.z1) / 2.0);
    }

    public int getLength() {
        return this.getMaximumPoint().getBlockZ() - this.getMinimumPoint().getBlockZ();
    }

    public byte getAverageLightLevel() {
        long l = 0L;
        int i = 0;
        for (Block block : this) {
            if (block.isEmpty()) {
                l += block.getLightLevel();
                ++i;
            }
        }
        return (byte) ((i > 0) ? ((byte) (l / i)) : 0);
    }

    public boolean contains(Player player) {
        return this.contains(player.getLocation());
    }

    public Block getRelativeBlock(int x, int y, int z) {
        return this.getWorld().getBlockAt(this.x1 + x, this.y1 + y, this.z1 + z);
    }

    public Cuboid clone() {
        try {
            return (Cuboid) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("This could never happen", e);
        }
    }

    public Cuboid getFace(CuboidDirection direction) {
        switch (direction) {
            case DOWN: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y1, this.z2);
            }
            case UP: {
                return new Cuboid(this.worldName, this.x1, this.y2, this.z1, this.x2, this.y2, this.z2);
            }
            case NORTH: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x1, this.y2, this.z2);
            }
            case SOUTH: {
                return new Cuboid(this.worldName, this.x2, this.y1, this.z1, this.x2, this.y2, this.z2);
            }
            case EAST: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z1);
            }
            case WEST: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z2, this.x2, this.y2, this.z2);
            }
            default: {
                throw new IllegalArgumentException("Invalid direction " + direction);
            }
        }
    }

    @Override
    public String toString() {
        return "Cuboid: " + this.worldName + ',' + this.x1 + ',' + this.y1 + ',' + this.z1 + "=>" + this.x2 + ',' + this.y2 + ',' + this.z2;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Cuboid)) {
            return false;
        }
        Cuboid cuboid = (Cuboid) object;
        return this.x1 == cuboid.x1 && this.y1 == cuboid.y1 && this.z1 == cuboid.z1 && this.x2 == cuboid.x2 && this.y2 == cuboid.y2 && this.z2 == cuboid.z2 && this.worldName.equals(cuboid.worldName);
    }

    public List<Block> getWalls(int max) {
        List<Block> blocks = new ArrayList<>();
        for (int i = this.x1; i <= this.x2; ++i) {
            for (int f = this.y1; f <= max; ++f) {
                blocks.add(this.getWorld().getBlockAt(i, f, this.z1));
                blocks.add(this.getWorld().getBlockAt(i, f, this.z2));
            }
        }
        for (int i = this.y1; i <= max; ++i) {
            for (int f = this.z1; f <= this.z2; ++f) {
                blocks.add(this.getWorld().getBlockAt(this.x1, i, f));
                blocks.add(this.getWorld().getBlockAt(this.x2, i, f));
            }
        }
        return blocks;
    }

    public int getMaximumY() {
        return Math.max(this.y1, this.y2);
    }

    public int getHeight() {
        return this.getMaximumPoint().getBlockY() - this.getMinimumPoint().getBlockY();
    }

    public Location getUpperSW() {
        return new Location(this.getWorld(), this.x2, this.y2, this.z2);
    }

    public Cuboid shift(CuboidDirection direction, int size) throws IllegalArgumentException {
        return this.expand(direction, size).expand(direction.opposite(), -size);
    }

    public int getSizeX() {
        return this.x2 - this.x1 + 1;
    }

    public Location[] getCornerLocations() {
        Location[] locations = new Location[8];
        Block[] blocks = this.getCornerBlocks();
        for (int i = 0; i < blocks.length; ++i) {
            locations[i] = blocks[i].getLocation();
        }
        return locations;
    }

    public int getMaximumZ() {
        return Math.max(this.z1, this.z2);
    }

    public int getWidth() {
        return this.getMaximumPoint().getBlockX() - this.getMinimumPoint().getBlockX();
    }

    public int getMaximumX() {
        return Math.max(this.x1, this.x2);
    }

    public boolean contains(Block block) {
        return this.contains(block.getLocation());
    }

    public boolean hasBothPositionsSet() {
        return this.getMinimumPoint() != null && this.getMaximumPoint() != null;
    }

    public Cuboid outset(CuboidDirection direction, int size) throws IllegalArgumentException {
        switch (direction) {
            case HORIZONTAL: {
                return this.expand(CuboidDirection.NORTH, size).expand(CuboidDirection.SOUTH, size).expand(CuboidDirection.EAST, size).expand(CuboidDirection.WEST, size);
            }
            case VERTICAL: {
                return this.expand(CuboidDirection.DOWN, size).expand(CuboidDirection.UP, size);
            }
            case BOTH: {
                return this.outset(CuboidDirection.HORIZONTAL, size).outset(CuboidDirection.VERTICAL, size);
            }
            default: {
                throw new IllegalArgumentException("Invalid direction " + direction);
            }
        }
    }

    public boolean contains(Cuboid cuboid) {
        return this.contains(cuboid.getMinimumPoint()) || this.contains(cuboid.getMaximumPoint());
    }

    public int getMinimumZ() {
        return Math.min(this.z1, this.z2);
    }

    public Block getRelativeBlock(World world, int x, int y, int z) {
        return world.getBlockAt(this.x1 + x, this.y1 + y, this.z1 + z);
    }

    public Cuboid contract(CuboidDirection direction) {
        Cuboid cuboid = this.getFace(direction.opposite());
        switch (direction) {
            case DOWN: {
                while (cuboid.containsOnly(Material.AIR) && cuboid.y1 > this.y1) {
                    cuboid = cuboid.shift(CuboidDirection.DOWN, 1);
                }
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, cuboid.y2, this.z2);
            }
            case UP: {
                while (cuboid.containsOnly(Material.AIR) && cuboid.y2 < this.y2) {
                    cuboid = cuboid.shift(CuboidDirection.UP, 1);
                }
                return new Cuboid(this.worldName, this.x1, cuboid.y1, this.z1, this.x2, this.y2, this.z2);
            }
            case NORTH: {
                while (cuboid.containsOnly(Material.AIR) && cuboid.x1 > this.x1) {
                    cuboid = cuboid.shift(CuboidDirection.NORTH, 1);
                }
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, cuboid.x2, this.y2, this.z2);
            }
            case SOUTH: {
                while (cuboid.containsOnly(Material.AIR) && cuboid.x2 < this.x2) {
                    cuboid = cuboid.shift(CuboidDirection.SOUTH, 1);
                }
                return new Cuboid(this.worldName, cuboid.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
            }
            case EAST: {
                while (cuboid.containsOnly(Material.AIR) && cuboid.z1 > this.z1) {
                    cuboid = cuboid.shift(CuboidDirection.EAST, 1);
                }
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, cuboid.z2);
            }
            case WEST: {
                while (cuboid.containsOnly(Material.AIR) && cuboid.z2 < this.z2) {
                    cuboid = cuboid.shift(CuboidDirection.WEST, 1);
                }
                return new Cuboid(this.worldName, this.x1, this.y1, cuboid.z1, this.x2, this.y2, this.z2);
            }
            default: {
                throw new IllegalArgumentException("Invalid direction " + direction);
            }
        }
    }

    public List<Chunk> getChunks() {
        World world = this.getWorld();
        int x1 = this.x1 & 0xFFFFFFF0;
        int x2 = this.x2 & 0xFFFFFFF0;
        int z1 = this.z1 & 0xFFFFFFF0;
        int z2 = this.z2 & 0xFFFFFFF0;
        List<Chunk> chunks = new ArrayList<>(x2 - x1 + 16 + (z2 - z1) * 16);
        for (int x = x1; x <= x2; x += 16) {
            for (int z = z1; z <= z2; z += 16) {
                chunks.add(world.getChunkAt(x >> 4, z >> 4));
            }
        }
        return chunks;
    }

    public Location getMaximumPoint() {
        return new Location(this.getWorld(), Math.max(this.x1, this.x2), Math.max(this.y1, this.y2), Math.max(this.z1, this.z2));
    }

    public boolean contains(Location location) {
        if (location == null || this.worldName == null) {
            return false;
        }
        World world = location.getWorld();
        return world != null && this.worldName.equals(location.getWorld().getName()) && this.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public Iterator<Block> iterator() {
        return new CuboidBlockIterator(this.getWorld(), this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
    }

    public Iterator<Location> locationIterator() {
        return new CuboidLocationIterator(this.getWorld(), this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
    }

    public Cuboid inset(CuboidDirection direction, int size) throws IllegalArgumentException {
        return this.outset(direction, -size);
    }

    public int getMinimumY() {
        return Math.min(this.y1, this.y2);
    }

    public int getSizeZ() {
        return this.z2 - this.z1 + 1;
    }

    public Block[] getCornerBlocks() {
        Block[] blocks = new Block[8];
        World world = this.getWorld();
        blocks[0] = world.getBlockAt(this.x1, this.y1, this.z1);
        blocks[1] = world.getBlockAt(this.x1, this.y1, this.z2);
        blocks[2] = world.getBlockAt(this.x1, this.y2, this.z1);
        blocks[3] = world.getBlockAt(this.x1, this.y2, this.z2);
        blocks[4] = world.getBlockAt(this.x2, this.y1, this.z1);
        blocks[5] = world.getBlockAt(this.x2, this.y1, this.z2);
        blocks[6] = world.getBlockAt(this.x2, this.y2, this.z1);
        blocks[7] = world.getBlockAt(this.x2, this.y2, this.z2);
        return blocks;
    }

    public int getArea() {
        Location min = this.getMinimumPoint();
        Location max = this.getMaximumPoint();
        return (max.getBlockX() - min.getBlockX() + 1) * (max.getBlockZ() - min.getBlockZ() + 1);
    }

    public World getWorld() {
        return Bukkit.getWorld(this.worldName);
    }

    public Cuboid contract() {
        return this.contract(CuboidDirection.DOWN).contract(CuboidDirection.SOUTH).contract(CuboidDirection.EAST).contract(CuboidDirection.UP).contract(CuboidDirection.NORTH).contract(CuboidDirection.WEST);
    }

    public int getMinimumX() {
        return Math.min(this.x1, this.x2);
    }

    public int getSizeY() {
        return this.y2 - this.y1 + 1;
    }

    public int getVolume() {
        return this.getSizeX() * this.getSizeY() * this.getSizeZ();
    }

    public Cuboid getBoundingCuboid(Cuboid cuboid) {
        if (cuboid == null) {
            return this;
        }
        int x1 = Math.min(this.x1, cuboid.x1);
        int y1 = Math.min(this.y1, cuboid.y1);
        int z1 = Math.min(this.z1, cuboid.z1);
        int x2 = Math.max(this.x2, cuboid.x2);
        int y2 = Math.max(this.y2, cuboid.y2);
        int z2 = Math.max(this.z2, cuboid.z2);
        return new Cuboid(this.worldName, x1, y1, z1, x2, y2, z2);
    }

    public boolean contains(World world, int x, int z) {
        return (world == null || this.getWorld().equals(world)) && x >= this.x1 && x <= this.x2 && z >= this.z1 && z <= this.z2;
    }

    public Location getLowerNE() {
        return new Location(this.getWorld(), this.x1, this.y1, this.z1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
    }

    public int getZ1() {
        return this.z1;
    }

    public Cuboid expand(CuboidDirection direction, int size) throws IllegalArgumentException {
        switch (direction) {
            case NORTH: {
                return new Cuboid(this.worldName, this.x1 - size, this.y1, this.z1, this.x2, this.y2, this.z2);
            }
            case SOUTH: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2 + size, this.y2, this.z2);
            }
            case EAST: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1 - size, this.x2, this.y2, this.z2);
            }
            case WEST: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z2 + size);
            }
            case DOWN: {
                return new Cuboid(this.worldName, this.x1, this.y1 - size, this.z1, this.x2, this.y2, this.z2);
            }
            case UP: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2 + size, this.z2);
            }
            default: {
                throw new IllegalArgumentException("Invalid direction " + direction);
            }
        }
    }
}
