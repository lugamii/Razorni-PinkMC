package dev.razorni.hcfactions.teams.claims;

import com.google.common.base.Preconditions;
import dev.razorni.hcfactions.utils.cuboid.CuboidBlockIterator;
import dev.razorni.hcfactions.utils.cuboid.CuboidDirection;
import dev.razorni.hcfactions.utils.cuboid.CuboidLocationIterator;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

@SuppressWarnings("unused")
public class Claim implements Iterable<Block>, Cloneable {
    protected int x1;
    protected int z2;
    protected int y2;
    protected int y1;
    protected UUID team;
    protected boolean locked;
    protected String worldName;
    protected int x2;
    protected int z1;

    private Claim(String worldName, int x1, int i1, int z1, int x2, int i2, int z2) {
        Preconditions.checkNotNull(worldName, "World name cannot be null");
        this.worldName = worldName;
        this.x1 = Math.min(x1, x2);
        this.y1 = 0;
        this.z1 = Math.min(z1, z2);
        this.x2 = Math.max(x1, x2);
        this.y2 = 256;
        this.z2 = Math.max(z1, z2);
        this.locked = false;
    }

    public Claim(UUID uuid, Location pos1, Location pos2) {
        Preconditions.checkNotNull(pos1, "Location 1 cannot be null");
        Preconditions.checkNotNull(pos2, "Location 2 cannot be null");
        Preconditions.checkArgument(pos1.getWorld().equals(pos2.getWorld()), "Locations must be on the same world");
        this.worldName = pos1.getWorld().getName();
        this.team = uuid;
        this.x1 = Math.min(pos1.getBlockX(), pos2.getBlockX());
        this.y1 = 0;
        this.z1 = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        this.x2 = Math.max(pos1.getBlockX(), pos2.getBlockX());
        this.y2 = 256;
        this.z2 = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        this.locked = false;
    }

    public boolean contains(Claim claim) {
        return this.contains(claim.getMinimumPoint()) || this.contains(claim.getMaximumPoint());
    }

    public int getSizeY() {
        return this.y2 - this.y1 + 1;
    }

    public boolean contains(Location location) {
        if (location == null || this.worldName == null) {
            return false;
        }
        World world = location.getWorld();
        return world != null && this.worldName.equals(location.getWorld().getName()) && this.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public Claim getBoundingCuboid(Claim claim) {
        if (claim == null) {
            return this;
        }
        int x1 = Math.min(this.x1, claim.x1);
        int y1 = Math.min(this.y1, claim.y1);
        int z1 = Math.min(this.z1, claim.z1);
        int x2 = Math.max(this.x2, claim.x2);
        int y2 = Math.max(this.y2, claim.y2);
        int z2 = Math.max(this.z2, claim.z2);
        return new Claim(this.worldName, x1, y1, z1, x2, y2, z2);
    }

    @Override
    public String toString() {
        return "Cuboid: " + this.worldName + ',' + this.x1 + ',' + this.y1 + ',' + this.z1 + "=>" + this.x2 + ',' + this.y2 + ',' + this.z2;
    }

    public Location[] getCornerLocations() {
        Location[] locations = new Location[8];
        Block[] corner = this.getCornerBlocks();
        for (int i = 0; i < corner.length; ++i) {
            locations[i] = corner[i].getLocation();
        }
        return locations;
    }

    public Claim getFace(CuboidDirection direction) {
        switch (direction) {
            case DOWN: {
                return new Claim(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y1, this.z2);
            }
            case UP: {
                return new Claim(this.worldName, this.x1, this.y2, this.z1, this.x2, this.y2, this.z2);
            }
            case NORTH: {
                return new Claim(this.worldName, this.x1, this.y1, this.z1, this.x1, this.y2, this.z2);
            }
            case SOUTH: {
                return new Claim(this.worldName, this.x2, this.y1, this.z1, this.x2, this.y2, this.z2);
            }
            case EAST: {
                return new Claim(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z1);
            }
            case WEST: {
                return new Claim(this.worldName, this.x1, this.y1, this.z2, this.x2, this.y2, this.z2);
            }
            default: {
                throw new IllegalArgumentException("Invalid direction " + direction);
            }
        }
    }

    public Iterator<Location> locationIterator() {
        return new CuboidLocationIterator(this.getWorld(), this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
    }

    public boolean containsOnly(Material material) {
        for (Block block : this) {
            if (block.getType() != material) {
                return false;
            }
        }
        return true;
    }

    public Location getLowerNE() {
        return new Location(this.getWorld(), this.x1, this.y1, this.z1);
    }

    public Location getUpperSW() {
        return new Location(this.getWorld(), this.x2, this.y2, this.z2);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Claim)) {
            return false;
        }
        Claim claim = (Claim) object;
        return this.x1 == claim.x1 && this.y1 == claim.y1 && this.z1 == claim.z1 && this.x2 == claim.x2 && this.y2 == claim.y2 && this.z2 == claim.z2 && this.worldName.equals(claim.worldName);
    }

    public Location getCenter() {
        int x = this.x2 + 1;
        int y = this.y2 + 1;
        int z = this.z2 + 1;
        return new Location(this.getWorld(), this.x1 + (x - this.x1) / 2.0, this.y1 + (y - this.y1) / 2.0, this.z1 + (z - this.z1) / 2.0);
    }

    public int getHeight() {
        return this.getMaximumPoint().getBlockY() - this.getMinimumPoint().getBlockY();
    }

    public List<Block> getWalls(int x, int z) {
        List<Block> blocks = new ArrayList<>();
        World world = this.getWorld();
        for (int i = this.x1; i <= this.x2; ++i) {
            for (int f = x; f <= z; ++f) {
                blocks.add(world.getBlockAt(i, f, this.z1));
                blocks.add(world.getBlockAt(i, f, this.z2));
            }
        }
        for (int i = x; i <= z; ++i) {
            for (int f = this.z1; f <= this.z2; ++f) {
                blocks.add(world.getBlockAt(this.x1, i, f));
                blocks.add(world.getBlockAt(this.x2, i, f));
            }
        }
        return blocks;
    }

    public int getMinimumX() {
        return Math.min(this.x1, this.x2);
    }

    public int getY2() {
        return this.y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public int getWidth() {
        return this.getMaximumPoint().getBlockX() - this.getMinimumPoint().getBlockX();
    }

    public int getX1() {
        return this.x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
    }

    public Claim outset(CuboidDirection direction, int pos) throws IllegalArgumentException {
        switch (direction) {
            case HORIZONTAL: {
                return this.expand(CuboidDirection.NORTH, pos).expand(CuboidDirection.SOUTH, pos).expand(CuboidDirection.EAST, pos).expand(CuboidDirection.WEST, pos);
            }
            case VERTICAL: {
                return this.expand(CuboidDirection.DOWN, pos).expand(CuboidDirection.UP, pos);
            }
            case BOTH: {
                return this.outset(CuboidDirection.HORIZONTAL, pos).outset(CuboidDirection.VERTICAL, pos);
            }
            default: {
                throw new IllegalArgumentException("Invalid direction " + direction);
            }
        }
    }

    public Claim clone() {
        try {
            return (Claim) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("This could never happen", e);
        }
    }

    public World getWorld() {
        return Bukkit.getWorld(this.worldName);
    }

    public int getSizeX() {
        return this.x2 - this.x1 + 1;
    }

    public int getLength() {
        return this.getMaximumPoint().getBlockZ() - this.getMinimumPoint().getBlockZ();
    }

    public byte getAverageLightLevel() {
        long l1 = 0L;
        int i = 0;
        for (Block block : this) {
            if (block.isEmpty()) {
                l1 += block.getLightLevel();
                ++i;
            }
        }
        return (byte) ((i > 0) ? ((byte) (l1 / i)) : 0);
    }

    public int getSizeZ() {
        return this.z2 - this.z1 + 1;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getMinimumZ() {
        return Math.min(this.z1, this.z2);
    }

    public String getWorldName() {
        return this.worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public int getZ2() {
        return this.z2;
    }

    public void setZ2(int z2) {
        this.z2 = z2;
    }

    public int getMaximumX() {
        return Math.max(this.x1, this.x2);
    }

    public int getArea() {
        Location min = this.getMinimumPoint();
        Location max = this.getMaximumPoint();
        return (max.getBlockX() - min.getBlockX() + 1) * (max.getBlockZ() - min.getBlockZ() + 1);
    }

    public Set<Player> getPlayers() {
        Set<Player> players = new HashSet<>();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (this.contains(online)) {
                players.add(online);
            }
        }
        return players;
    }

    public Claim contract(CuboidDirection direction) {
        Claim clam = this.getFace(direction.opposite());
        switch (direction) {
            case DOWN: {
                while (clam.containsOnly(Material.AIR) && clam.y1 > this.y1) {
                    clam = clam.shift(CuboidDirection.DOWN, 1);
                }
                return new Claim(this.worldName, this.x1, this.y1, this.z1, this.x2, clam.y2, this.z2);
            }
            case UP: {
                while (clam.containsOnly(Material.AIR) && clam.y2 < this.y2) {
                    clam = clam.shift(CuboidDirection.UP, 1);
                }
                return new Claim(this.worldName, this.x1, clam.y1, this.z1, this.x2, this.y2, this.z2);
            }
            case NORTH: {
                while (clam.containsOnly(Material.AIR) && clam.x1 > this.x1) {
                    clam = clam.shift(CuboidDirection.NORTH, 1);
                }
                return new Claim(this.worldName, this.x1, this.y1, this.z1, clam.x2, this.y2, this.z2);
            }
            case SOUTH: {
                while (clam.containsOnly(Material.AIR) && clam.x2 < this.x2) {
                    clam = clam.shift(CuboidDirection.SOUTH, 1);
                }
                return new Claim(this.worldName, clam.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
            }
            case EAST: {
                while (clam.containsOnly(Material.AIR) && clam.z1 > this.z1) {
                    clam = clam.shift(CuboidDirection.EAST, 1);
                }
                return new Claim(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, clam.z2);
            }
            case WEST: {
                while (clam.containsOnly(Material.AIR) && clam.z2 < this.z2) {
                    clam = clam.shift(CuboidDirection.WEST, 1);
                }
                return new Claim(this.worldName, this.x1, this.y1, clam.z1, this.x2, this.y2, this.z2);
            }
            default: {
                throw new IllegalArgumentException("Invalid direction " + direction);
            }
        }
    }

    @Override
    public Iterator<Block> iterator() {
        return new CuboidBlockIterator(this.getWorld(), this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
    }

    public boolean contains(World world, int x, int z) {
        return (world == null || this.getWorld().equals(world)) && x >= this.x1 && x <= this.x2 && z >= this.z1 && z <= this.z2;
    }

    public Claim expand(CuboidDirection drection, int x) throws IllegalArgumentException {
        switch (drection) {
            case NORTH: {
                return new Claim(this.worldName, this.x1 - x, this.y1, this.z1, this.x2, this.y2, this.z2);
            }
            case SOUTH: {
                return new Claim(this.worldName, this.x1, this.y1, this.z1, this.x2 + x, this.y2, this.z2);
            }
            case EAST: {
                return new Claim(this.worldName, this.x1, this.y1, this.z1 - x, this.x2, this.y2, this.z2);
            }
            case WEST: {
                return new Claim(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z2 + x);
            }
            case DOWN: {
                return new Claim(this.worldName, this.x1, this.y1 - x, this.z1, this.x2, this.y2, this.z2);
            }
            case UP: {
                return new Claim(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2 + x, this.z2);
            }
            default: {
                throw new IllegalArgumentException("Invalid direction " + drection);
            }
        }
    }

    public Claim contract() {
        return this.contract(CuboidDirection.DOWN).contract(CuboidDirection.SOUTH).contract(CuboidDirection.EAST).contract(CuboidDirection.UP).contract(CuboidDirection.NORTH).contract(CuboidDirection.WEST);
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

    public boolean contains(Player player) {
        return this.contains(player.getLocation());
    }

    public Location getMaximumPoint() {
        return new Location(this.getWorld(), Math.max(this.x1, this.x2), Math.max(this.y1, this.y2), Math.max(this.z1, this.z2));
    }

    public boolean hasBothPositionsSet() {
        return this.getMinimumPoint() != null && this.getMaximumPoint() != null;
    }

    public boolean contains(int x, int y, int z) {
        return x >= this.x1 && x <= this.x2 && y >= this.y1 && y <= this.y2 && z >= this.z1 && z <= this.z2;
    }

    public int getMaximumZ() {
        return Math.max(this.z1, this.z2);
    }

    public UUID getTeam() {
        return this.team;
    }

    public void setTeam(UUID team) {
        this.team = team;
    }

    public boolean contains(Block block) {
        return this.contains(block.getLocation());
    }

    public Claim inset(CuboidDirection direction, int i) throws IllegalArgumentException {
        return this.outset(direction, -i);
    }

    public int getY1() {
        return this.y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getVolume() {
        return this.getSizeX() * this.getSizeY() * this.getSizeZ();
    }

    public int getX2() {
        return this.x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public Claim shift(CuboidDirection direction, int i) throws IllegalArgumentException {
        return this.expand(direction, i).expand(direction.opposite(), -i);
    }

    public Block getRelativeBlock(int x, int y, int z) {
        return this.getWorld().getBlockAt(this.x1 + x, this.y1 + y, this.z1 + z);
    }

    public int getZ1() {
        return this.z1;
    }

    public void setZ1(int z1) {
        this.z1 = z1;
    }

    public List<Chunk> getChunks() {
        World world = this.getWorld();
        int x1 = this.x1 & 0xFFFFFFF0;
        int x2 = this.x2 & 0xFFFFFFF0;
        int z1 = this.z1 & 0xFFFFFFF0;
        int z2 = this.z2 & 0xFFFFFFF0;
        List<Chunk> chunks = new ArrayList<>(x2 - x1 + 16 + (z2 - z1) * 16);
        for (int i = x1; i <= x2; i += 16) {
            for (int f = z1; f <= z2; f += 16) {
                chunks.add(world.getChunkAt(i >> 4, f >> 4));
            }
        }
        return chunks;
    }

    public Location getMinimumPoint() {
        return new Location(this.getWorld(), Math.min(this.x1, this.x2), Math.min(this.y1, this.y2), Math.min(this.z1, this.z2));
    }

    public Block getRelativeBlock(World world, int x, int y, int z) {
        return world.getBlockAt(this.x1 + x, this.y1 + y, this.z1 + z);
    }
}
