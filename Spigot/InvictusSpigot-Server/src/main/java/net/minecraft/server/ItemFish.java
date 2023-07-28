package net.minecraft.server;

import com.google.common.collect.Maps;

import java.util.Map;

public class ItemFish extends ItemFood {

    private final boolean b;

    public ItemFish(boolean flag) {
        super(0, 0.0F, false);
        this.b = flag;
    }

    public int getNutrition(ItemStack itemstack) {
        EnumFish itemfish_enumfish = EnumFish.a(itemstack);

        return this.b && itemfish_enumfish.g() ? itemfish_enumfish.e() : itemfish_enumfish.c();
    }

    public float getSaturationModifier(ItemStack itemstack) {
        EnumFish itemfish_enumfish = EnumFish.a(itemstack);

        return this.b && itemfish_enumfish.g() ? itemfish_enumfish.f() : itemfish_enumfish.d();
    }

    public String j(ItemStack itemstack) {
        return EnumFish.a(itemstack) == EnumFish.PUFFERFISH ? PotionBrewer.m : null;
    }

    protected void c(ItemStack itemstack, World world, EntityHuman entityhuman) {
        EnumFish itemfish_enumfish = EnumFish.a(itemstack);

        if (itemfish_enumfish == EnumFish.PUFFERFISH) {
            entityhuman.addEffect(new MobEffect(MobEffectList.POISON.id, 1200, 3));
            entityhuman.addEffect(new MobEffect(MobEffectList.HUNGER.id, 300, 2));
            entityhuman.addEffect(new MobEffect(MobEffectList.CONFUSION.id, 300, 1));
        }

        super.c(itemstack, world, entityhuman);
    }

    public String e_(ItemStack itemstack) {
        EnumFish itemfish_enumfish = EnumFish.a(itemstack);

        return this.getName() + "." + itemfish_enumfish.b() + "." + (this.b && itemfish_enumfish.g() ? "cooked" : "raw");
    }

    public static enum EnumFish {

        COD(0, "cod", 2, 0.1F, 5, 0.6F), SALMON(1, "salmon", 2, 0.1F, 6, 0.8F), CLOWNFISH(2, "clownfish", 1, 0.1F), PUFFERFISH(3, "pufferfish", 1, 0.1F);

        private static final Map<Integer, EnumFish> e = Maps.newHashMap();
        private final int f;
        private final String g;
        private final int h;
        private final float i;
        private final int j;
        private final float k;
        private final boolean l;

        EnumFish(int i, String s, int j, float f, int k, float f1) {
            this.f = i;
            this.g = s;
            this.h = j;
            this.i = f;
            this.j = k;
            this.k = f1;
            this.l = true;
        }

        EnumFish(int i, String s, int j, float f) {
            this.f = i;
            this.g = s;
            this.h = j;
            this.i = f;
            this.j = 0;
            this.k = 0.0F;
            this.l = false;
        }

        public int a() {
            return this.f;
        }

        public String b() {
            return this.g;
        }

        public int c() {
            return this.h;
        }

        public float d() {
            return this.i;
        }

        public int e() {
            return this.j;
        }

        public float f() {
            return this.k;
        }

        public boolean g() {
            return this.l;
        }

        public static EnumFish a(int i) {
            EnumFish itemfish_enumfish = (EnumFish) EnumFish.e.get(i);

            return itemfish_enumfish == null ? EnumFish.COD : itemfish_enumfish;
        }

        public static EnumFish a(ItemStack itemstack) {
            return itemstack.getItem() instanceof ItemFish ? a(itemstack.getData()) : EnumFish.COD;
        }

        static {
            for (EnumFish itemfish_enumfish : values()) {
                EnumFish.e.put(itemfish_enumfish.a(), itemfish_enumfish);
            }
        }
    }
}
