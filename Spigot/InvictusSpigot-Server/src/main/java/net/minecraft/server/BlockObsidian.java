package net.minecraft.server;

import java.util.Random;

public class BlockObsidian extends Block {

    public BlockObsidian() {
        super(Material.STONE);
        this.a(CreativeModeTab.b);
        c(50.0F);
        b(2000.0F);
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Item.getItemOf(Blocks.OBSIDIAN);
    }

    public MaterialMapColor g(IBlockData iblockdata) {
        return MaterialMapColor.E;
    }

}
