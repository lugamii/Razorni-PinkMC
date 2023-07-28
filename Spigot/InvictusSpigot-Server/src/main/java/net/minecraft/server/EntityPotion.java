package net.minecraft.server;

// CraftBukkit start

import eu.vortexdev.invictusspigot.config.InvictusConfig;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.PotionEffectAddEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class EntityPotion extends EntityProjectile {

    public ItemStack item;

    public EntityPotion(World world) {
        super(world);
    }

    public EntityPotion(World world, EntityLiving entityliving, int i) {
        this(world, entityliving, new ItemStack(Items.POTION, 1, i));
    }

    public EntityPotion(World world, EntityLiving entityliving, ItemStack itemstack) {
        super(world, entityliving);
        this.item = itemstack;
        this.healPotion = item.getData() == 16421;
    }

    public EntityPotion(World world, double d0, double d1, double d2, ItemStack itemstack) {
        super(world, d0, d1, d2);
        this.item = itemstack;
        this.healPotion = item.getData() == 16421;
    }

    protected float m() {
        return InvictusConfig.potionFallSpeed;
    }

    protected float j() {
        return InvictusConfig.potionThrowMultiplier;
    }

    protected float l() {
        return InvictusConfig.potionThrowOffset;
    }

    // Vortex start
    @Override
    public int getAddRemoveDelay() {
        return 1;
    }
    // Vortex End

    public int getPotionValue() {
        if (this.item == null) {
            this.item = new ItemStack(Items.POTION, 1, 0);
        }

        return this.item.getData();
    }

    public void setPotionValue(int i) {
        if (this.item == null) {
            this.item = new ItemStack(Items.POTION, 1, 0);
        }

        this.item.setData(i);
    }

    protected void a(MovingObjectPosition movingobjectposition) {

        List<MobEffect> list = Items.POTION.h(this.item);

        AxisAlignedBB axisalignedbb = this.getBoundingBox().grow(4.0D, 2.0D, 4.0D);
        List<EntityLiving> list1 = this.world.a(EntityLiving.class, axisalignedbb);

        Iterator<EntityLiving> iterator = list1.iterator();

        // CraftBukkit
        HashMap<LivingEntity, Double> affected = new HashMap<>();

        while (iterator.hasNext()) {
            EntityLiving entityliving = iterator.next();
            double d0 = this.h(entityliving);

            if (d0 < 16.0D) {
                double d1 = 1.0D - Math.sqrt(d0) / 4.0D;

                if (entityliving == movingobjectposition.entity) {
                    d1 = 1.0D;
                }

                // CraftBukkit start
                affected.put((LivingEntity) entityliving.getBukkitEntity(), d1);
            }
        }

        org.bukkit.event.entity.PotionSplashEvent event = org.bukkit.craftbukkit.event.CraftEventFactory
                .callPotionSplashEvent(this, affected);

        EntityLiving shooter = getShooter();

        if (!event.isCancelled() && list != null && !list.isEmpty()) { // do not process effects if there are no effects
            // to process
            for (LivingEntity victim : event.getAffectedEntities()) {
                if (!(victim instanceof CraftLivingEntity)) {
                    continue;
                }

                EntityLiving entityliving = ((CraftLivingEntity) victim).getHandle();

                if (shooter != null && entityliving instanceof EntityPlayer && ((EntityPlayer)entityliving).getBukkitEntity() != null && !((EntityPlayer)entityliving).getBukkitEntity().canSeeEntity(shooter.getBukkitEntity()))
                    continue;

                double d1 = event.getIntensity(victim);
                // CraftBukkit end

                for (MobEffect mobeffect : list) {
                    int i = mobeffect.getEffectId();

                    // CraftBukkit start - Abide by PVP settings - for players only!
                    if (!this.world.pvpMode && shooter instanceof EntityPlayer && entityliving instanceof EntityPlayer
                            && entityliving != shooter) {
                        // Block SLOWER_MOVEMENT, SLOWER_DIG, HARM, BLINDNESS, HUNGER, WEAKNESS and
                        // POISON potions
                        if (i == 2 || i == 4 || i == 7 || i == 15 || i == 17 || i == 18 || i == 19)
                            continue;
                    }
                    // CraftBukkit end

                    if (MobEffectList.byId[i].isInstant()) {
                        MobEffectList.byId[i].applyInstantEffect(this, this.getShooter(), entityliving,
                                mobeffect.getAmplifier(), d1);
                    } else {
                        int j = (int) (d1 * mobeffect.getDuration() + 0.5D);

                        if (j > 20) {
                            entityliving.addEffect(new MobEffect(i, j, mobeffect.getAmplifier()), PotionEffectAddEvent.EffectCause.POTION_SPLASH);
                        }
                    }
                }
            }
        }

        world.triggerEffect(2002, new BlockPosition(this), getPotionValue());

        this.die();
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("Potion", 10)) {
            this.item = ItemStack.createStack(nbttagcompound.getCompound("Potion"));
        } else {
            this.setPotionValue(nbttagcompound.getInt("potionValue"));
        }

        if (this.item == null) {
            this.die();
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        if (this.item != null) {
            nbttagcompound.set("Potion", this.item.save(new NBTTagCompound()));
        }

    }
}
