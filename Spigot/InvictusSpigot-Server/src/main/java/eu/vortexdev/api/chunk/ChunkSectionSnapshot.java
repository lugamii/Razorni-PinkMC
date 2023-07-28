package eu.vortexdev.api.chunk;

import net.minecraft.server.NibbleArray;

public class ChunkSectionSnapshot {

    private final int yPos;
    private final int nonEmptyBlockCount;
    private final int tickingBlockCount;
    private final char[] blockIds;
    private final NibbleArray emittedLight;
    private final NibbleArray skyLight;
    boolean isDirty; // PaperSpigot

    public ChunkSectionSnapshot(int yPos, int nonEmptyBlockCount, int tickingBlockCount, char[] blockIds, NibbleArray emittedLight, NibbleArray skyLight, boolean isDirty) {
        this.yPos = yPos;
        this.nonEmptyBlockCount = nonEmptyBlockCount;
        this.tickingBlockCount = tickingBlockCount;
        this.blockIds = blockIds;
        this.emittedLight = emittedLight;
        this.skyLight = skyLight;
        this.isDirty = isDirty;
    }

    public int getYPos() {
        return yPos;
    }

    public int getNonEmptyBlockCount() {
        return nonEmptyBlockCount;
    }

    public int getTickingBlockCount() {
        return tickingBlockCount;
    }

    public char[] getBlockIds() {
        return blockIds;
    }

    public NibbleArray getEmittedLight() {
        return emittedLight;
    }

    public NibbleArray getSkyLight() {
        return skyLight;
    }

    public boolean isDirty() {
        return isDirty;
    }

}
