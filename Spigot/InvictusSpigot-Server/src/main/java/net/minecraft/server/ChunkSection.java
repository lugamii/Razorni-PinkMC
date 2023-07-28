package net.minecraft.server;

import eu.vortexdev.api.chunk.ChunkSectionSnapshot;

public class ChunkSection {

    boolean isDirty; // PaperSpigot
    private int yPos;
    private int nonEmptyBlockCount;
    private int tickingBlockCount;
    private char[] blockIds;
    private NibbleArray emittedLight;
    private NibbleArray skyLight;

    public ChunkSection(int i, boolean flag) {
        this.yPos = i;
        this.blockIds = new char[4096];
        this.emittedLight = new NibbleArray();
        if (flag) {
            this.skyLight = new NibbleArray();
        }
    }

    // CraftBukkit start
    public ChunkSection(int y, boolean flag, char[] blockIds) {
        this.yPos = y;
        this.blockIds = blockIds;
        this.emittedLight = new NibbleArray();
        if (flag) {
            this.skyLight = new NibbleArray();
        }
        recalcBlockCounts();
    }
    // CraftBukkit end

    public IBlockData getType(int i, int j, int k) {
        IBlockData iblockdata = Block.d.a(blockIds[j << 8 | k << 4 | i]);

        return iblockdata != null ? iblockdata : Blocks.AIR.getBlockData();
    }

    public IBlockData getType(int id) {
        IBlockData iblockdata = Block.d.a(blockIds[id]);

        return iblockdata != null ? iblockdata : Blocks.AIR.getBlockData();
    }

    public void setType(int i, int j, int k, IBlockData iblockdata) {
        IBlockData iblockdata1 = getType(i, j, k);
        Block block = iblockdata1.getBlock();
        Block block1 = iblockdata.getBlock();
        int id = j << 8 | k << 4 | i;

        if (block != Blocks.AIR) {
            --nonEmptyBlockCount;
            if (block.isTicking()) {
                --tickingBlockCount;
            }
        }

        if (block1 != Blocks.AIR) {
            ++nonEmptyBlockCount;
            if (block1.isTicking()) {
                ++tickingBlockCount;
            }
        }

        blockIds[id] = (char) Block.d.b(iblockdata);
        isDirty = true; // PaperSpigot
    }

    public Block b(int i, int j, int k) {
        return this.getType(i, j, k).getBlock();
    }

    public int c(int i, int j, int k) {
        IBlockData iblockdata = this.getType(i, j, k);

        return iblockdata.getBlock().toLegacyData(iblockdata);
    }

    public boolean a() {
        return this.nonEmptyBlockCount == 0;
    }

    public boolean shouldTick() {
        return this.tickingBlockCount > 0;
    }

    public int getYPosition() {
        return this.yPos;
    }

    public void a(int i, int j, int k, int l) {
        this.skyLight.a(i, j, k, l);
        isDirty = true; // PaperSpigot
    }

    public int d(int i, int j, int k) {
        return this.skyLight.a(i, j, k);
    }

    public void b(int i, int j, int k, int l) {
        this.emittedLight.a(i, j, k, l);
        isDirty = true; // PaperSpigot
    }

    public int e(int i, int j, int k) {
        return this.emittedLight.a(i, j, k);
    }

    public void recalcBlockCounts() {
        this.nonEmptyBlockCount = 0;
        this.tickingBlockCount = 0;

        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                for (int k = 0; k < 16; ++k) {
                    int id = j << 8 | k << 4 | i;
                    Block block = getType(id).getBlock();

                    if (block != Blocks.AIR) {
                        ++nonEmptyBlockCount;
                        if (block.isTicking()) {
                            ++tickingBlockCount;
                        }
                    }
                }
            }
        }

    }

    public char[] getIdArray() {
        return this.blockIds;
    }

    public void a(char[] achar) {
        this.blockIds = achar;
    }

    public NibbleArray getEmittedLightArray() {
        return this.emittedLight;
    }

    public NibbleArray getSkyLightArray() {
        return this.skyLight;
    }

    public void a(NibbleArray nibblearray) {
        this.emittedLight = nibblearray;
    }

    public void b(NibbleArray nibblearray) {
        this.skyLight = nibblearray;
    }

    private char[] clone(char[] chars) {
        if (chars != null)
            return chars.clone();
        return null;
    }

    private NibbleArray clone(NibbleArray array) {
        if (array != null)
            return array.clone();
        return null;
    }

    public ChunkSectionSnapshot createSnapshot() {
        return new ChunkSectionSnapshot(
                yPos,
                nonEmptyBlockCount,
                tickingBlockCount,
                clone(blockIds),
                clone(emittedLight),
                clone(skyLight),
                isDirty);
    }

    public void restoreSnapshot(ChunkSectionSnapshot snap) {
        yPos = snap.getYPos();
        nonEmptyBlockCount = snap.getNonEmptyBlockCount();
        tickingBlockCount = snap.getTickingBlockCount();
        blockIds = clone(snap.getBlockIds());
        emittedLight = clone(snap.getEmittedLight());
        skyLight = clone(snap.getSkyLight());
        isDirty = snap.isDirty();
    }
}
