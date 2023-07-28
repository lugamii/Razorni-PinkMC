package net.minecraft.server;

public class BlockHalfTransparent extends Block {

    protected BlockHalfTransparent(Material material, boolean flag) {
        this(material, flag, material.r());
    }

    protected BlockHalfTransparent(Material material, boolean flag, MaterialMapColor materialmapcolor) {
        super(material, materialmapcolor);
    }

    public boolean c() {
        return false;
    }
}
