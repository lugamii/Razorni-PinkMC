package net.minecraft.server;

import com.google.common.collect.Multimap;

import java.util.Set;

public class ItemTool extends Item {

    private final Set<Block> c;
    protected float a;
    private final float d;
    protected EnumToolMaterial b;

    protected ItemTool(float f, EnumToolMaterial item_enumtoolmaterial, Set<Block> set) {
        this.b = item_enumtoolmaterial;
        this.c = set;
        this.maxStackSize = 1;
        this.setMaxDurability(item_enumtoolmaterial.a());
        this.a = item_enumtoolmaterial.b();
        this.d = f + item_enumtoolmaterial.c();
        this.a(CreativeModeTab.i);
    }

    public float getDestroySpeed(ItemStack itemstack, Block block) {
        return this.c.contains(block) ? this.a : 1.0F;
    }

    public boolean a(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1) {
        itemstack.damage(2, entityliving1);
        return true;
    }

    public boolean a(ItemStack itemstack, World world, Block block, BlockPosition blockposition, EntityLiving entityliving) {
        if ((double) block.g(world, blockposition) != 0.0D) {
            itemstack.damage(1, entityliving);
        }

        return true;
    }

    public EnumToolMaterial g() {
        return this.b;
    }

    public int b() {
        return this.b.e();
    }

    public String h() {
        return this.b.toString();
    }

    public boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return this.b.f() == itemstack1.getItem() || super.a(itemstack, itemstack1);
    }

    public Multimap<String, AttributeModifier> i() {
        Multimap<String, AttributeModifier> multimap = super.i();

        multimap.put(GenericAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ItemTool.f, "Tool modifier", this.d, 0));
        return multimap;
    }
}
