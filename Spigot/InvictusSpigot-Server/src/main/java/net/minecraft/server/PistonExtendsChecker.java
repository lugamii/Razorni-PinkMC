package net.minecraft.server;

import com.google.common.collect.Lists;
import eu.vortexdev.invictusspigot.config.InvictusConfig;

import java.util.ArrayList;
import java.util.List;

public class PistonExtendsChecker {
    private final World a;
    private final BlockPosition b, c;
    private final EnumDirection d;
    private final List<BlockPosition> e = Lists.newArrayList();
    private final List<BlockPosition> f = Lists.newArrayList();

    public PistonExtendsChecker(World paramWorld, BlockPosition paramBlockPosition, EnumDirection paramEnumDirection, boolean paramBoolean) {
        this.a = paramWorld;
        this.b = paramBlockPosition;
        if (paramBoolean) {
            this.d = paramEnumDirection;
            this.c = paramBlockPosition.shift(paramEnumDirection);
        } else {
            this.d = paramEnumDirection.opposite();
            this.c = paramBlockPosition.shift(paramEnumDirection, 2);
        }
    }

    public boolean a() {
        this.e.clear();
        this.f.clear();
        Block block = this.a.getType(this.c).getBlock();
        if (!BlockPiston.a(block, this.a, this.c, this.d, false)) {
            if (block.k() != 1)
                return false;
            this.f.add(this.c);
            return true;
        }
        if (!a(this.c))
            return false;
        for (byte b = 0; b < this.e.size(); b++) {
            BlockPosition blockPosition = this.e.get(b);
            if (this.a.getType(blockPosition).getBlock() == Blocks.SLIME &&
                    !b(blockPosition))
                return false;
        }
        return true;
    }

    private boolean a(BlockPosition paramBlockPosition) {
        Block block = this.a.getType(paramBlockPosition).getBlock();
        if (block.getMaterial() == Material.AIR || !BlockPiston.a(block, this.a, paramBlockPosition, this.d, false) || paramBlockPosition.equals(this.b) || this.e.contains(paramBlockPosition))
            return true;
        byte b1 = 1;
        if (b1 + this.e.size() > InvictusConfig.maxPistonPush)
            return false;
        while (block == Blocks.SLIME) {
            BlockPosition blockPosition = paramBlockPosition.shift(this.d.opposite(), b1);
            block = this.a.getType(blockPosition).getBlock();
            if (block.getMaterial() == Material.AIR || !BlockPiston.a(block, this.a, blockPosition, this.d, false) || blockPosition.equals(this.b))
                break;
            b1++;
            if (b1 + this.e.size() > InvictusConfig.maxPistonPush)
                return false;
        }
        byte b2 = 0;
        int i;
        for (i = b1 - 1; i >= 0; i--) {
            this.e.add(paramBlockPosition.shift(this.d.opposite(), i));
            b2++;
        }
        for (i = 1; ; i++) {
            BlockPosition blockPosition = paramBlockPosition.shift(this.d, i);
            int j = this.e.indexOf(blockPosition);
            if (j > -1) {
                a(b2, j);
                for (byte b = 0; b <= j + b2; b++) {
                    BlockPosition blockPosition1 = this.e.get(b);
                    if (this.a.getType(blockPosition1).getBlock() == Blocks.SLIME &&
                            !b(blockPosition1))
                        return false;
                }
                return true;
            }
            block = this.a.getType(blockPosition).getBlock();
            if (block.getMaterial() == Material.AIR)
                return true;
            if (!BlockPiston.a(block, this.a, blockPosition, this.d, true) || blockPosition.equals(this.b))
                return false;
            if (block.k() == 1) {
                this.f.add(blockPosition);
                return true;
            }
            if (this.e.size() >= InvictusConfig.maxPistonPush)
                return false;
            this.e.add(blockPosition);
            b2++;
        }
    }

    private void a(int paramInt1, int paramInt2) {
        ArrayList<BlockPosition> arrayList1 = Lists.newArrayList();
        ArrayList<BlockPosition> arrayList2 = Lists.newArrayList();
        ArrayList<BlockPosition> arrayList3 = Lists.newArrayList();
        arrayList1.addAll(this.e.subList(0, paramInt2));
        arrayList2.addAll(this.e.subList(this.e.size() - paramInt1, this.e.size()));
        arrayList3.addAll(this.e.subList(paramInt2, this.e.size() - paramInt1));
        this.e.clear();
        this.e.addAll(arrayList1);
        this.e.addAll(arrayList2);
        this.e.addAll(arrayList3);
    }

    private boolean b(BlockPosition paramBlockPosition) {
        for (EnumDirection enumDirection : EnumDirection.values()) {
            if (enumDirection.k() != this.d.k() &&
                    !a(paramBlockPosition.shift(enumDirection)))
                return false;
        }
        return true;
    }

    public List<BlockPosition> getMovedBlocks() {
        return this.e;
    }

    public List<BlockPosition> getBrokenBlocks() {
        return this.f;
    }
}
