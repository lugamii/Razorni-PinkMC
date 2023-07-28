package net.minecraft.server;

public class ItemDoor extends Item {

    private Block a;

    public ItemDoor(Block block) {
        this.a = block;
        this.a(CreativeModeTab.d);
    }

    public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2) {
        if (enumdirection != EnumDirection.UP) {
            return false;
        } else {
            IBlockData iblockdata = world.getType(blockposition);
            Block block = iblockdata.getBlock();

            if (!block.a(world, blockposition)) {
                blockposition = blockposition.shift(enumdirection);
            }

            if (!entityhuman.a(blockposition, enumdirection, itemstack)) {
                return false;
            } else if (!this.a.canPlace(world, blockposition)) {
                return false;
            } else {
                a(world, blockposition, EnumDirection.fromAngle(entityhuman.yaw), this.a);
                --itemstack.count;
                return true;
            }
        }
    }

    public static void a(World world, BlockPosition blockposition, EnumDirection enumdirection, Block block) {
        BlockPosition blockposition1 = blockposition.shift(enumdirection.e());
        BlockPosition blockposition2 = blockposition.shift(enumdirection.f());

        Block block2 = world.getType(blockposition2).getBlock();
        Block block1 = world.getType(blockposition1).getBlock();
        Block block3 = world.getType(blockposition2.getX(), blockposition2.getY() + 1, blockposition2.getZ()).getBlock();
        Block block4 = world.getType(blockposition1.getX(), blockposition1.getY() + 1, blockposition1.getZ()).getBlock();

        int i = (block2.isOccluding() ? 1 : 0) + (block3.isOccluding() ? 1 : 0);
        int j = (block1.isOccluding() ? 1 : 0) + (block4.isOccluding() ? 1 : 0);
        boolean flag = block2 == block || block3 == block;
        boolean flag1 = block1 == block || block4 == block;
        boolean flag2 = flag && !flag1 || j > i;

        BlockPosition blockposition3 = blockposition.up();
        IBlockData iblockdata = block.getBlockData().set(BlockDoor.FACING, enumdirection).set(BlockDoor.HINGE, flag2 ? BlockDoor.EnumDoorHinge.RIGHT : BlockDoor.EnumDoorHinge.LEFT);

        // Spigot start - update physics after the block multi place event
        world.setTypeAndData(blockposition, iblockdata.set(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER), 3);
        world.setTypeAndData(blockposition3, iblockdata.set(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), 3);
        // world.applyPhysics(blockposition, block);
        // world.applyPhysics(blockposition3, block);
        // Spigot end
    }
}
