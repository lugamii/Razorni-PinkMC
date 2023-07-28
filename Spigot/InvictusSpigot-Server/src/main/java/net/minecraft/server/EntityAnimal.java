package net.minecraft.server;

public abstract class EntityAnimal extends EntityAgeable implements IAnimal {

    protected Block bn;
    private int bm;
    private EntityHuman bo;

    public EntityAnimal(World world) {
        super(world);
        this.bn = Blocks.GRASS;
    }

    protected void E() {
        if (this.getAge() != 0) {
            this.bm = 0;
        }

        super.E();
    }

    public void m() {
        super.m();
        if (this.getAge() != 0) {
            this.bm = 0;
        }

        if (this.bm > 0) {
            --this.bm;
        }

    }

    public float a(BlockPosition blockposition) {
        return this.world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock() == Blocks.GRASS ? 10.0F : this.world.o(blockposition) - 0.5F;
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("InLove", this.bm);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.bm = nbttagcompound.getInt("InLove");
    }

    public boolean bR() {
        int i = MathHelper.floor(locX), j = MathHelper.floor(boundingBox.b), k = MathHelper.floor(locZ);
        return this.world.getType(i, j - 1, k).getBlock() == this.bn && this.world.k(i, j, k) > 8 && super.bR();
    }

    public int w() {
        return 120;
    }

    protected boolean isTypeNotPersistent() {
        return false;
    }

    protected int getExpValue(EntityHuman entityhuman) {
        return 1 + this.world.random.nextInt(3);
    }

    public boolean d(ItemStack itemstack) {
        return itemstack != null && itemstack.getItem() == Items.WHEAT;
    }

    public boolean a(EntityHuman entityhuman) {
        ItemStack itemstack = entityhuman.inventory.getItemInHand();

        if (itemstack != null) {
            if (this.d(itemstack) && this.getAge() == 0 && this.bm <= 0) {
                this.a(entityhuman, itemstack);
                this.c(entityhuman);
                return true;
            }

            if (this.isBaby() && this.d(itemstack)) {
                this.a(entityhuman, itemstack);
                this.setAge((int) ((float) (-this.getAge() / 20) * 0.1F), true);
                return true;
            }
        }

        return super.a(entityhuman);
    }

    protected void a(EntityHuman entityhuman, ItemStack itemstack) {
        if (!entityhuman.abilities.canInstantlyBuild) {
            --itemstack.count;
            if (itemstack.count <= 0) {
                entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, null);
            }
        }

    }

    public void c(EntityHuman entityhuman) {
        this.bm = 600;
        this.bo = entityhuman;
        this.world.broadcastEntityEffect(this, (byte) 18);
    }

    public EntityHuman cq() {
        return this.bo;
    }

    public boolean isInLove() {
        return this.bm > 0;
    }

    public void cs() {
        this.bm = 0;
    }

    public boolean mate(EntityAnimal entityanimal) {
        return entityanimal != this && (entityanimal.getClass() == this.getClass() && this.isInLove() && entityanimal.isInLove());
    }
}
