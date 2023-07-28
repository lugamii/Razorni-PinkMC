package net.minecraft.server;

public class ItemHoe extends Item {

    protected EnumToolMaterial a;

    public ItemHoe(EnumToolMaterial item_enumtoolmaterial) {
        this.a = item_enumtoolmaterial;
        this.maxStackSize = 1;
        this.setMaxDurability(item_enumtoolmaterial.a());
        this.a(CreativeModeTab.i);
    }

    public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2) {
        if (entityhuman.a(blockposition.shift(enumdirection), enumdirection, itemstack)) {
            IBlockData iblockdata = world.getType(blockposition);
            Block block = iblockdata.getBlock();

            if (enumdirection != EnumDirection.DOWN && world.getType(blockposition.up()).getBlock().getMaterial() == Material.AIR) {
                if (block == Blocks.GRASS) {
                    return this.a(itemstack, entityhuman, world, blockposition, Blocks.FARMLAND.getBlockData());
                }

                if (block == Blocks.DIRT) {
                    switch (SyntheticClass_1.a[iblockdata.get(BlockDirt.VARIANT).ordinal()]) {
                        case 1:
                            return this.a(itemstack, entityhuman, world, blockposition, Blocks.FARMLAND.getBlockData());

                        case 2:
                            return this.a(itemstack, entityhuman, world, blockposition, Blocks.DIRT.getBlockData().set(BlockDirt.VARIANT, BlockDirt.EnumDirtVariant.DIRT));
                    }
                }
            }

        }
        return false;
    }

    protected boolean a(ItemStack itemstack, EntityHuman entityhuman, World world, BlockPosition blockposition, IBlockData iblockdata) {
        world.makeSound((float) blockposition.getX() + 0.5F, (float) blockposition.getY() + 0.5F, (float) blockposition.getZ() + 0.5F, iblockdata.getBlock().stepSound.getStepSound(), (iblockdata.getBlock().stepSound.getVolume1() + 1.0F) / 2.0F, iblockdata.getBlock().stepSound.getVolume2() * 0.8F);
        world.setTypeUpdate(blockposition, iblockdata);
        itemstack.damage(1, entityhuman);
        return true;
    }

    public String g() {
        return this.a.toString();
    }

    static class SyntheticClass_1 {

        static final int[] a = new int[BlockDirt.EnumDirtVariant.values().length];

        static {
            try {
                SyntheticClass_1.a[BlockDirt.EnumDirtVariant.DIRT.ordinal()] = 1;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                SyntheticClass_1.a[BlockDirt.EnumDirtVariant.COARSE_DIRT.ordinal()] = 2;
            } catch (NoSuchFieldError ignored) {
            }

        }
    }
}
