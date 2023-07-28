package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class BlockStatePredicate implements Predicate<IBlockData> {

    private final BlockStateList a;
    private final Map<IBlockState, Predicate> b = Maps.newHashMap();

    private BlockStatePredicate(BlockStateList blockstatelist) {
        this.a = blockstatelist;
    }

    public static BlockStatePredicate a(Block block) {
        return new BlockStatePredicate(block.P());
    }

    public boolean a(IBlockData iblockdata) {
        if (iblockdata != null && iblockdata.getBlock().equals(this.a.getBlock())) {
            Iterator<Entry<IBlockState, Predicate>> iterator = this.b.entrySet().iterator();

            Entry<IBlockState, Predicate> entry;
            Comparable comparable;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                entry = iterator.next();
                comparable = iblockdata.get(entry.getKey());
            } while (entry.getValue().apply(comparable));

        }
        return false;
    }

    public <V extends Comparable<V>> BlockStatePredicate a(IBlockState<V> iblockstate, Predicate<? extends V> predicate) {
        if (!this.a.d().contains(iblockstate)) {
            throw new IllegalArgumentException(this.a + " cannot support property " + iblockstate);
        } else {
            this.b.put(iblockstate, predicate);
            return this;
        }
    }

    public boolean apply(IBlockData object) {
        return this.a(object);
    }
}
