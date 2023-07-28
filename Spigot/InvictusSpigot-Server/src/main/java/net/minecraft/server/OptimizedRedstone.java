package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import eu.vortexdev.invictusspigot.util.java.LinkedArraySet;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.*;

public class OptimizedRedstone extends BlockRedstoneWire {
    private static final BaseBlockPosition[] surroundingBlocksOffset;
    private static final EnumDirection[]
            facingsHorizontal = new EnumDirection[]{EnumDirection.WEST, EnumDirection.EAST, EnumDirection.NORTH, EnumDirection.SOUTH},
            facingsVertical = new EnumDirection[]{EnumDirection.DOWN, EnumDirection.UP},
            facings = ArrayUtils.addAll(facingsVertical, facingsHorizontal);
    static {
        List<BaseBlockPosition> list = new ArrayList<>();
        for (EnumDirection facing : facings) {
            list.add(facing.getBaseBlockPosition());
        }
        for (EnumDirection facing : facings) {
            BaseBlockPosition pos = facing.getBaseBlockPosition();
            for (EnumDirection facing3 : facings) {
                BaseBlockPosition v2 = facing3.getBaseBlockPosition();
                list.add(new BlockPosition(pos.getX() + v2.getX(), pos.getY() + v2.getY(), pos.getZ() + v2.getZ()));
            }
        }
        list.remove(BlockPosition.ZERO);
        surroundingBlocksOffset = list.toArray(new BaseBlockPosition[0]);
    }

    private final List<BlockPosition> turnOff = new ArrayList<>(), turnOn = new ArrayList<>();
    private final Set<BlockPosition> updatedRedstoneWire = new LinkedArraySet<>();
    private boolean g;

    public OptimizedRedstone() {
        this.g = true;
        this.c(0.0f);
        this.a(Block.e);
        this.c("redstoneDust");
        this.K();
    }

    private void e(World world, BlockPosition blockposition) {
        calculateCurrentChanges(world, blockposition);

        Set<BlockPosition> blocksNeedingUpdate = Sets.newLinkedHashSet();
        for (BlockPosition pos : updatedRedstoneWire) {
            addBlocksNeedingUpdate(world, pos, blocksNeedingUpdate);
        }

        Iterator<BlockPosition> iterator = Lists.newLinkedList(updatedRedstoneWire).descendingIterator();
        while (iterator.hasNext()) {
            addAllSurroundingBlocks(iterator.next(), blocksNeedingUpdate);
        }

        blocksNeedingUpdate.removeAll(updatedRedstoneWire);
        updatedRedstoneWire.clear();

        for (BlockPosition pos : blocksNeedingUpdate) {
            world.redstonePhysics(pos.getX(), pos.getY(), pos.getZ(), this);
        }
    }

    private void calculateCurrentChanges(World world, BlockPosition blockposition) {
        if (world.getType(blockposition).getBlock() == this) {
            turnOff.add(blockposition);
        } else {
            checkSurroundingWires(world, blockposition);
        }

        while (!turnOff.isEmpty()) {
            BlockPosition pos = turnOff.remove(0);
            IBlockData state = world.getType(pos);
            int oldPower = state.get(POWER);

            g = false;
            int blockPower = world.A(pos);
            g = true;

            int wirePower = getSurroundingWirePower(world, pos);
            --wirePower;

            int newPower = Math.max(blockPower, wirePower);
            if (newPower < oldPower) {
                if (blockPower > 0 && !this.turnOn.contains(pos)) {
                    this.turnOn.add(pos);
                }
                setWireState(world, pos, state, 0);
            } else if (newPower > oldPower) {
                setWireState(world, pos, state, newPower);
            }

            checkSurroundingWires(world, pos);
        }

        while (!turnOn.isEmpty()) {
            BlockPosition pos = turnOn.remove(0);
            IBlockData state = world.getType(pos);
            int oldPower = state.get(POWER);

            g = false;
            int blockPower = world.A(pos);
            g = true;

            int wirePower = getSurroundingWirePower(world, pos);
            --wirePower;

            int newPower = Math.max(blockPower, wirePower);
            if (oldPower != newPower) {
                BlockRedstoneEvent event = new BlockRedstoneEvent(world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), oldPower, newPower);
                world.getServer().getPluginManager().callEvent(event);
                newPower = event.getNewCurrent();
            }
            if (newPower > oldPower) {
                setWireState(world, pos, state, newPower);
            }

            checkSurroundingWires(world, pos);
        }

        turnOff.clear();
        turnOn.clear();
    }

    private void addWireToList(World worldIn, BlockPosition pos, int otherPower) {
        IBlockData state = worldIn.getType(pos);

        if (state.getBlock() == this) {
            int power = state.get(POWER);

            if (power < otherPower - 1 && !turnOn.contains(pos)) {
                turnOn.add(pos);
            }

            if (power > otherPower && !turnOff.contains(pos)) {
                turnOff.add(pos);
            }
        }
    }

    private void checkSurroundingWires(World worldIn, BlockPosition pos) {
        IBlockData state = worldIn.getType(pos);

        int ownPower = 0;
        if (state.getBlock() == this) {
            ownPower = state.get(POWER);
        }

        for (EnumDirection facing : facingsHorizontal) {
            BlockPosition offsetPos = pos.shift(facing);
            if (facing.k().c()) {
                addWireToList(worldIn, offsetPos, ownPower);
            }
        }

        for (EnumDirection facingVertical : facingsVertical) {
            BlockPosition offsetPos = pos.shift(facingVertical);
            boolean solidBlock = worldIn.getType(offsetPos).getBlock().u();
            for (EnumDirection facingHorizontal : facingsHorizontal) {
                if ((facingVertical == EnumDirection.UP && !solidBlock) || (facingVertical == EnumDirection.DOWN && solidBlock && !worldIn.getType(offsetPos.shift(facingHorizontal)).getBlock().isOccluding())) {
                    addWireToList(worldIn, offsetPos.shift(facingHorizontal), ownPower);
                }
            }
        }
    }

    private int getSurroundingWirePower(World worldIn, BlockPosition pos) {
        boolean upOccluding = worldIn.getType(pos.getX(), pos.getY() + 1, pos.getZ()).getBlock().isOccluding();
        int wirePower = 0;
        for (EnumDirection enumfacing : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
            BlockPosition offsetPos = pos.shift(enumfacing);

            IBlockData iblockdata = worldIn.getType(offsetPos);
            boolean currentOccluding = iblockdata.getBlock().isOccluding();

            wirePower = getPower(iblockdata, wirePower);

            if (currentOccluding && !upOccluding) {
                wirePower = getPower(worldIn, offsetPos.up(), wirePower);
            } else if (!currentOccluding) {
                wirePower = getPower(worldIn, offsetPos.down(), wirePower);
            }
        }
        return wirePower;
    }

    private void addBlocksNeedingUpdate(World worldIn, BlockPosition pos, Set<BlockPosition> set) {
        List<EnumDirection> connectedSides = getSidesToPower(worldIn, pos);
        for (EnumDirection facing : facings) {
            BlockPosition offsetPos = pos.shift(facing);

            boolean facingCheck = connectedSides.contains(facing.opposite()) || facing == EnumDirection.DOWN;

            if(facingCheck || (facing.k().c() && a(worldIn.getType(offsetPos), facing)) && canBlockBePoweredFromSide(worldIn.getType(offsetPos), facing, true)) {
                set.add(offsetPos);
            }

            if (facingCheck && worldIn.getType(offsetPos).getBlock().isOccluding()) {
                for (EnumDirection facing2 : facings) {
                    if (canBlockBePoweredFromSide(worldIn.getType(offsetPos.shift(facing2)), facing2, false)) {
                        set.add(offsetPos.shift(facing2));
                    }
                }
            }
        }
    }

    private boolean canBlockBePoweredFromSide(IBlockData state, EnumDirection side, boolean isWire) {
        Block block = state.getBlock();
        if (block instanceof BlockPiston && state.get(BlockPiston.FACING) == side.opposite())
            return false;

        if (block instanceof BlockDiodeAbstract && state.get(BlockDiodeAbstract.FACING) != side.opposite())
            return isWire && block instanceof BlockRedstoneComparator && state.get(BlockRedstoneComparator.FACING).k() != side.k() && side.k().c();

        return !(block instanceof BlockRedstoneTorch) || (!isWire && state.get(BlockRedstoneTorch.FACING) == side);
    }

    private List<EnumDirection> getSidesToPower(IBlockAccess worldIn, BlockPosition pos) {
        List<EnumDirection> retval = Lists.newArrayList();
        for (EnumDirection facing : facingsHorizontal) {
            if (d(worldIn, pos, facing)) {
                retval.add(facing);
            }
        }

        if (retval.isEmpty())
            return new ArrayList<>(Arrays.asList(facingsHorizontal));

        boolean NS = retval.contains(EnumDirection.NORTH) || retval.contains(EnumDirection.SOUTH), EW = retval.contains(EnumDirection.EAST) || retval.contains(EnumDirection.WEST);
        if (NS) {
            retval.remove(EnumDirection.EAST);
            retval.remove(EnumDirection.WEST);
        }
        if (EW) {
            retval.remove(EnumDirection.NORTH);
            retval.remove(EnumDirection.SOUTH);
        }

        return retval;
    }

    private void addAllSurroundingBlocks(BlockPosition pos, Set<BlockPosition> set) {
        for (BaseBlockPosition vect : surroundingBlocksOffset) {
            set.add(pos.a(vect));
        }
    }

    private void setWireState(World worldIn, BlockPosition pos, IBlockData state, int power) {
        state = state.set(POWER, power);
        worldIn.setTypeAndData(pos, state, 2);
        updatedRedstoneWire.add(pos);
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        e(world, blockposition);
        for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.VERTICAL) {
            world.applyPhysics(blockposition.getX() + enumdirection.getAdjacentX(), blockposition.getY() + enumdirection.getAdjacentY(), blockposition.getZ() + enumdirection.getAdjacentZ(), this);
        }
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        super.remove(world, blockposition, iblockdata);
        for (EnumDirection enumdirection : EnumDirection.values()) {
            world.applyPhysics(blockposition.getX() + enumdirection.getAdjacentX(), blockposition.getY() + enumdirection.getAdjacentY(), blockposition.getZ() + enumdirection.getAdjacentZ(), this);
        }
        e(world, blockposition);
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        if (canPlace(world, blockposition)) {
            e(world, blockposition);
        } else {
            b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
        }
    }

    public int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
        return a(iblockaccess, blockposition, iblockdata, enumdirection);
    }

    public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
        if (!g)
            return 0;

        int i = iblockdata.get(BlockRedstoneWire.POWER);
        if (i == 0)
            return 0;

        if (enumdirection == EnumDirection.UP || getSidesToPower(iblockaccess, blockposition).contains(enumdirection))
            return i;

        return 0;
    }

    public int getPower(IBlockData iblockdata, int i) {
        if (iblockdata.getBlock() != this) {
            return i;
        } else {
            return Math.max(iblockdata.get(BlockRedstoneWire.POWER), i);
        }
    }

    private boolean d(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        BlockPosition blockposition2 = blockposition.shift(enumdirection);
        IBlockData iblockdata = iblockaccess.getType(blockposition2);
        Block block = iblockdata.getBlock();
        boolean flag = block.isOccluding();
        boolean flag2 = iblockaccess.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()).getBlock().isOccluding();
        return (!flag2 && flag && e(iblockaccess, blockposition2.getX(), blockposition2.getY() + 1, blockposition2.getZ())) || a(iblockdata, enumdirection) || (block == Blocks.POWERED_REPEATER && iblockdata.get(BlockDiodeAbstract.FACING) == enumdirection) || (!flag && e(iblockaccess, blockposition2.getX(), blockposition2.getY() - 1, blockposition2.getZ()));
    }

    protected static boolean e(IBlockAccess world, int x, int y, int z) {
        return world.getType(x, y, z).getBlock() == Blocks.REDSTONE_WIRE;
    }

    public boolean isPowerSource() {
        return g;
    }

}