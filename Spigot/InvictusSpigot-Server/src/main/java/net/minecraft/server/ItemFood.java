package net.minecraft.server;

public class ItemFood extends Item {
    public final int a = 32;

    private final int b;

    private final float c;

    private final boolean d;

    private boolean k;

    private int l;

    private int m;

    private int n;

    private float o;

    public ItemFood(int paramInt, float paramFloat, boolean paramBoolean) {
        this.b = paramInt;
        this.d = paramBoolean;
        this.c = paramFloat;
        a(CreativeModeTab.h);
    }

    public ItemFood(int paramInt, boolean paramBoolean) {
        this(paramInt, 0.6F, paramBoolean);
    }

    public ItemStack b(ItemStack paramItemStack, World paramWorld, EntityHuman paramEntityHuman) {
        paramItemStack.count--;
        paramEntityHuman.getFoodData().a(this, paramItemStack);
        paramWorld.makeSound(paramEntityHuman, "random.burp", 0.5F, paramWorld.random.nextFloat() * 0.1F + 0.9F);
        c(paramItemStack, paramWorld, paramEntityHuman);
        return paramItemStack;
    }

    protected void c(ItemStack paramItemStack, World paramWorld, EntityHuman paramEntityHuman) {
        if (this.l > 0 && paramWorld.random.nextFloat() < this.o)
            paramEntityHuman.addEffect(new MobEffect(this.l, this.m * 20, this.n));
    }

    public int d(ItemStack paramItemStack) {
        return 32;
    }

    public EnumAnimation e(ItemStack paramItemStack) {
        return EnumAnimation.EAT;
    }

    public ItemStack a(ItemStack paramItemStack, World paramWorld, EntityHuman paramEntityHuman) {
        if (paramEntityHuman.j(this.k))
            paramEntityHuman.a(paramItemStack, d(paramItemStack));
        return paramItemStack;
    }

    public int getNutrition(ItemStack paramItemStack) {
        return this.b;
    }

    public float getSaturationModifier(ItemStack paramItemStack) {
        return this.c;
    }

    public boolean g() {
        return this.d;
    }

    public ItemFood a(int paramInt1, int paramInt2, int paramInt3, float paramFloat) {
        this.l = paramInt1;
        this.m = paramInt2;
        this.n = paramInt3;
        this.o = paramFloat;
        return this;
    }

    public ItemFood h() {
        this.k = true;
        return this;
    }
}
