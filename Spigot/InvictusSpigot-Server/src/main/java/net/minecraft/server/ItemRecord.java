package net.minecraft.server;

import com.google.common.collect.Maps;

import java.util.Map;

public class ItemRecord extends Item {

    private static final Map<String, ItemRecord> b = Maps.newHashMap();
    public final String a;

    protected ItemRecord(String s) {
        this.a = s;
        this.maxStackSize = 1;
        this.a(CreativeModeTab.f);
        ItemRecord.b.put("records." + s, this);
    }

    public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2) {
        IBlockData iblockdata = world.getType(blockposition);
        return iblockdata.getBlock() == Blocks.JUKEBOX && !iblockdata.get(BlockJukeBox.HAS_RECORD);
    }

    public EnumItemRarity g(ItemStack itemstack) {
        return EnumItemRarity.RARE;
    }
}
