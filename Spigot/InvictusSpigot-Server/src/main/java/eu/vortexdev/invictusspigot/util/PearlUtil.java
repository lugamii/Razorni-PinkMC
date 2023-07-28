package eu.vortexdev.invictusspigot.util;

import eu.vortexdev.invictusspigot.config.PearlConfig;
import net.jafama.FastMath;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Gate;
import org.bukkit.material.TrapDoor;

public class PearlUtil {

    public static final BlockFace[] FACES = {BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST,
            BlockFace.NORTH_WEST, BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST};

    public static boolean isGood(Block block) {
        return !PearlUtil.isFullBlock(block) && !block.getType().toString().contains("FENCE_GATE");
    }

    public static BlockFace direction(Location l) {
        return FACES[FastMath.round(l.getYaw() / 45f) & 0x7];
    }

    public static Pair<BlockFace, BlockFace> getPair(BlockFace face) {
        switch (face) {
            case NORTH_WEST:
                return Pair.of(BlockFace.NORTH, BlockFace.WEST);
            case SOUTH_WEST:
                return Pair.of(BlockFace.SOUTH, BlockFace.WEST);
            case NORTH_EAST:
                return Pair.of(BlockFace.NORTH, BlockFace.EAST);
            case SOUTH_EAST:
                return Pair.of(BlockFace.SOUTH, BlockFace.EAST);
            default:
                return null;
        }
    }

    public static boolean isNotVisible(BlockFace stairFace, BlockFace direction) {
        switch (direction) {
            case EAST:
                return stairFace != BlockFace.SOUTH && stairFace != BlockFace.NORTH && stairFace != BlockFace.WEST;
            case WEST:
                return stairFace != BlockFace.SOUTH && stairFace != BlockFace.NORTH && stairFace != BlockFace.EAST;
            case NORTH:
                return stairFace != BlockFace.EAST && stairFace != BlockFace.WEST && stairFace != BlockFace.SOUTH;
            case SOUTH_EAST:
                return stairFace != BlockFace.EAST && stairFace != BlockFace.WEST && stairFace != BlockFace.NORTH;
            default:
                return false;
        }
    }

    public static boolean isFacingNormal(BlockFace direction, BlockFace blockFacing) {
        switch (direction) {
            case EAST:
            case WEST:
                return blockFacing != BlockFace.WEST && blockFacing != BlockFace.EAST;
            case NORTH:
            case SOUTH:
                return blockFacing != BlockFace.NORTH && blockFacing != BlockFace.SOUTH;
            default:
                return true;
        }
    }

    public static boolean isDiagonal(BlockFace face) {
        return !(face == BlockFace.EAST || face == BlockFace.WEST || face == BlockFace.SOUTH
                || face == BlockFace.NORTH);
    }

    public static boolean isAble(Block b, final Location l) {
        if (distance(b, l, 1.25))
            return PearlUtil.thruEnabled(b.getType());
        return false;
    }

    public static void setToCenter(Location l) {
        l.setX(l.getBlockX() + 0.5);
        l.setY(l.getBlockY());
        l.setZ(l.getBlockZ() + 0.5);
    }

    public static boolean isNeeded(String to) {
        return to.contains("FENCE") || to.contains("WALL") || to.equals("IRON_TRAPDOOR") || to.equals("TRAP_DOOR");
    }

    public static boolean containsW(BlockFace blockFace) {
        return blockFace == BlockFace.WEST || blockFace == BlockFace.NORTH_WEST || blockFace == BlockFace.SOUTH_WEST;
    }

    public static boolean containsE(BlockFace blockFace) {
        return blockFace == BlockFace.EAST || blockFace == BlockFace.NORTH_EAST || blockFace == BlockFace.SOUTH_EAST;
    }

    public static boolean containsS(BlockFace blockFace) {
        return blockFace == BlockFace.SOUTH || blockFace == BlockFace.SOUTH_EAST || blockFace == BlockFace.SOUTH_WEST;
    }

    public static boolean containsN(BlockFace blockFace) {
        return blockFace == BlockFace.NORTH || blockFace == BlockFace.NORTH_EAST || blockFace == BlockFace.NORTH_WEST;
    }

    public static Location addToLocation(BlockFace direction, Location location, double value) {
        switch (direction) {
            case EAST:
                location.add(value, 0, 0);
                return location;
            case WEST:
                location.add(-value, 0, 0);
                return location;
            case NORTH:
                location.add(0, 0, -value);
                return location;
            case SOUTH:
                location.add(0, 0, value);
                return location;
            case SOUTH_EAST:
                location.add(value, 0, value);
                return location;
            case NORTH_EAST:
                location.add(value, 0, -value);
                return location;
            case NORTH_WEST:
                location.add(-value, 0, -value);
                return location;
            case SOUTH_WEST:
                location.add(-value, 0, value);
                return location;
            default:
                return location;
        }
    }

    public static void copyLocation(Location paste, Location copy) {
        paste.setX(copy.getX());
        paste.setY(copy.getY());
        paste.setZ(copy.getZ());
        paste.setPitch(copy.getPitch());
        paste.setYaw(copy.getYaw());
    }

    public static boolean distance(Block b, Location l, double d) {
        final Location loc = b.getLocation().clone();
        loc.setX(loc.getBlockX() + 0.5);
        loc.setZ(loc.getBlockZ() + 0.5);
        return loc.distance(l) <= d;
    }

    public static boolean isPlant(String blockName) {
        return blockName.equals("Grass") || blockName.equals("Plant") || blockName.equals("Flower")
                || blockName.contains("Sapling");
    }

    public static boolean isPlantBukkit(Block block) {
        Material type = block.getType();
        String blockName = type.toString();
        return type == Material.LONG_GRASS || blockName.contains("MUSHROOM") || blockName.equals("RED_ROSE")
                || blockName.equals("DOUBLE_PLANT") || blockName.equals("RED_FLOWER")
                || blockName.equals("YELLOW_FLOWER") || blockName.contains("SAPLING");
    }

    public static boolean slab(Material m) {
        final String a = m.toString();
        return (a.contains("STEP") || a.equals("STONE_SLAB2")) && !a.contains("DOUBLE");
    }

    public static boolean stairs(Material m) {
        return m.name().contains("STAIRS");
    }

    public static boolean critblock(Material mm) {
        final PearlConfig m = getSettingByItemStack(mm);
        return m != null && m.isCritblock();
    }

    public static boolean crossPearlAndEnabled(Material mm) {
        final PearlConfig m = getSettingByItemStack(mm);
        return m != null && m.isEnabled() && m.isCrosspearl();
    }

    public static boolean diagonalPearl(Material mm) {
        final PearlConfig m = getSettingByItemStack(mm);
        return m != null && m.isDiagonal();
    }

    public static boolean thruEnabled(Material mm) {
        final PearlConfig m = getSettingByItemStack(mm);
        return m != null && m.isEnabled();
    }

    public static boolean thruDisabledAndThruable(Material mm) {
        final PearlConfig m = getSettingByItemStack(mm);
        if (m != null) {
            return !m.isEnabled();
        }
        return false;
    }

    public static boolean isFullBlock(Block b) {
        final Material m = b.getType();
        final String q = m.toString();
        if (q.contains("RAIL") || m == Material.RAILS || q.contains("ANVIL"))
            return false;
        else if (q.contains("FLOWER") || q.contains("ROSE") || m == Material.DOUBLE_PLANT || m == Material.LONG_GRASS)
            return false;
        else if (q.contains("SAPLING"))
            return false;
        else if (q.contains("MUSHROOM"))
            return false;
        else if (q.contains("TORCH"))
            return false;
        else if (q.contains("SNOW") || q.contains("CARPET") || q.contains("DIODE") || m == Material.LEVER
                || m == Material.BREWING_STAND)
            return false;
        else if (q.contains("REDSTONE") || q.contains("FENCE"))
            return false;
        else if (q.contains("PORTAL"))
            return false;
        else if (q.contains("TRAPDOOR"))
            return false;
        else if (q.contains("SUGAR_CANE"))
            return false;
        else if (q.contains("STEM") || q.contains("DAYLIGHT") || m == Material.CROPS || m == Material.NETHER_WARTS
                || m == Material.COCOA || m == Material.VINE)
            return false;
        else if (q.contains("BUTTON") || q.contains("TRIPWIRE") || m == Material.FIRE)
            return false;
        else if (q.contains("SIGN") || m == Material.TRAP_DOOR || q.contains("DOOR") || q.contains("PLATE"))
            return false;
        else if (m == Material.PISTON_EXTENSION || m == Material.PISTON_MOVING_PIECE)
            return false;
        else if (q.contains("CHEST") || m == Material.WEB)
            return false;
        else if (m == Material.LADDER)
            return false;
        else if (m == Material.AIR)
            return false;
        else if (m == Material.ENDER_PORTAL_FRAME || m == Material.ENCHANTMENT_TABLE || q.contains("FENCE_GATE"))
            return false;
        else if (slab(m) || stairs(m) || q.contains("WALL") || m == Material.BED_BLOCK || m == Material.BED)
            return false;
        else return !b.isLiquid();
    }

    public static boolean isUnpassable(Block block) {
        return !isThruable(block) && isFullBlock(block);
    }

    public static boolean isThruable(Block block) {
        Material type = block.getType();
        return (isOpenFenceGate(block) && PearlConfig.THRUFENCEGATE.getBooleanValue())
                || (type == Material.WEB && PearlConfig.THRUCOBWEB.getBooleanValue())
                || (type == Material.TRIPWIRE && PearlConfig.THRUSTRING.getBooleanValue())
                || (PearlUtil.isPlantBukkit(block) && PearlConfig.THRUPLANTS.getBooleanValue());
    }

    public static boolean isOpenFenceGate(Block block) {
        return block.getState().getData() instanceof Gate && ((Gate) block.getState().getData()).isOpen();
    }

    public static boolean isFenceGateButClosed(Block block) {
        return block.getState().getData() instanceof Gate && !((Gate) block.getState().getData()).isOpen();
    }

    public static boolean isThruableAndDisabled(Block block) {
        Material type = block.getType();
        return (isOpenFenceGate(block) && !PearlConfig.THRUFENCEGATE.getBooleanValue())
                || (type == Material.WEB && !PearlConfig.THRUCOBWEB.getBooleanValue())
                || (type == Material.TRIPWIRE && !PearlConfig.THRUSTRING.getBooleanValue())
                || (PearlUtil.isPlantBukkit(block) && !PearlConfig.THRUPLANTS.getBooleanValue());
    }

    public static boolean isRiskyBlock(Block b) {
        final Material m = b.getType();
        final String q = m.toString();
        if (q.contains("RAIL") || m == Material.RAILS)
            return false;
        else if (q.contains("FLOWER") || q.contains("ROSE") || q.contains("DIODE") || m == Material.DOUBLE_PLANT
                || m == Material.LONG_GRASS)
            return false;
        else if (q.contains("SAPLING"))
            return false;
        else if (q.contains("MUSHROOM"))
            return false;
        else if (q.contains("TORCH"))
            return false;
        else if (q.contains("SNOW") || q.contains("CARPET") || m == Material.LEVER)
            return false;
        else if (q.contains("REDSTONE"))
            return false;
        else if (q.contains("PORTAL"))
            return false;
        else if (q.contains("SUGAR_CANE"))
            return false;
        else if (q.contains("STEM") || m == Material.CROPS || m == Material.NETHER_WARTS || m == Material.COCOA)
            return false;
        else if (q.contains("BUTTON") || q.contains("WALL") || q.contains("TRIPWIRE") || m == Material.FIRE
                || m == Material.VINE)
            return false;
        else if (q.contains("DOOR"))
            return false;
        else if (m == Material.PISTON_EXTENSION || m == Material.PISTON_MOVING_PIECE)
            return false;
        else if (m == Material.WEB)
            return false;
        else if (m == Material.LADDER)
            return false;
        else if (m == Material.AIR)
            return false;
        else if (q.contains("FENCE_GATE"))
            return false;
        else return !b.isLiquid();
    }

    public static boolean isCritBlock(Block b) {
        final Material m = b.getType();
        final String q = m.toString();
        if (q.contains("RAIL") || m == Material.RAILS)
            return false;
        else if (q.contains("FLOWER") || q.contains("ROSE") || q.contains("DIODE") || m == Material.DOUBLE_PLANT
                || m == Material.LONG_GRASS)
            return false;
        else if (q.contains("SAPLING"))
            return false;
        else if (q.contains("MUSHROOM"))
            return false;
        else if (q.contains("TORCH"))
            return false;
        else if (q.contains("SNOW") || q.contains("CARPET") || m == Material.LEVER)
            return false;
        else if (q.contains("REDSTONE"))
            return false;
        else if (q.contains("PORTAL"))
            return false;
        else if (q.contains("SUGAR_CANE"))
            return false;
        else if (q.contains("STEM") || m == Material.CROPS || m == Material.NETHER_WARTS || m == Material.COCOA
                || m == Material.VINE)
            return false;
        else if (q.contains("BUTTON") || q.contains("WALL") || q.contains("TRIPWIRE") || m == Material.FIRE)
            return false;
        else if (q.contains("SIGN") || q.contains("DOOR") || q.contains("PLATE"))
            return false;
        else if (m == Material.WEB)
            return false;
        else if (m == Material.LADDER)
            return false;
        else if (m == Material.AIR)
            return false;
        else if (q.contains("FENCE_GATE"))
            return false;
        else return !b.isLiquid();
    }

    public static PearlConfig getSettingByItemStack(Material mm) {
        final String m = mm.toString();
        if (slab(mm))
            return PearlConfig.SLABS;
        else if (stairs(mm))
            return PearlConfig.STAIRS;
        else if (mm == Material.COBBLE_WALL)
            return PearlConfig.COBBLEWALL;
        else if (mm == Material.BED_BLOCK || mm == Material.BED)
            return PearlConfig.BED;
        else if (mm == Material.PISTON_EXTENSION || mm == Material.PISTON_MOVING_PIECE)
            return PearlConfig.PISTONS;
        else if (mm == Material.ENDER_PORTAL_FRAME)
            return PearlConfig.PORTALFRAME;
        else if (mm == Material.ENCHANTMENT_TABLE)
            return PearlConfig.ENCHANTTABLE;
        else if (m.contains("CHEST"))
            return PearlConfig.CHESTS;
        else if (m.contains("ANVIL"))
            return PearlConfig.ANVIL;
        else if (m.contains("DAYLIGHT"))
            return PearlConfig.DAYLIGHTSENSOR;
        else if (m.equals("IRON_TRAPDOOR") || mm == Material.TRAP_DOOR)
            return PearlConfig.TRAPDOOR;
        return null;
    }

    public static boolean isChestOrSign(String mat) {
        return mat.contains("CHEST") || mat.contains("SIGN");
    }

    public static boolean isTrapDoorOpen(Block b) {
        final String t = b.getType().toString();
        return (t.equals("IRON_TRAPDOOR") || t.equals("TRAP_DOOR")) && ((TrapDoor) b.getState().getData()).isOpen();
    }

    public static BlockFace getNextIfPossible(BlockFace direction) {
        switch (direction) {
            case WEST:
            case EAST:
            case SOUTH:
            case NORTH:
                return direction;
            default:
                return null;
        }
    }

}
