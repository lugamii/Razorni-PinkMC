package net.minecraft.server;

import com.google.common.collect.Lists;
import net.jafama.FastMath;

import java.util.Iterator;
import java.util.List;

public class PersistentVillage extends PersistentBase {

    private World world;
    private final List<BlockPosition> c = Lists.newArrayList();
    private final List<VillageDoor> d = Lists.newArrayList();
    private final List<Village> villages = Lists.newArrayList();
    private int time;

    public PersistentVillage(String s) {
        super(s);
    }

    public PersistentVillage(World world) {
        super(a(world.worldProvider));
        this.world = world;
        this.c();
    }

    public void a(World world) {
        this.world = world;

        for (Village village : this.villages) {
            village.a(world);
        }

    }

    public void a(BlockPosition blockposition) {
        if (this.c.size() <= 64) {
            if (!this.e(blockposition)) {
                this.c.add(blockposition);
            }

        }
    }

    public void tick() {
        ++this.time;

        for (Village village : this.villages) {
            village.a(this.time);
        }

        this.e();
        this.f();
        this.g();
        if (this.time % 400 == 0) {
            this.c();
        }

    }

    private void e() {
        Iterator<Village> iterator = this.villages.iterator();

        while (iterator.hasNext()) {
            if (iterator.next().g()) {
                iterator.remove();
                this.c();
            }
        }

    }

    public List<Village> getVillages() {
        return this.villages;
    }

    public Village getClosestVillage(BlockPosition blockposition, int i) {
        return getClosestVillage(blockposition.getX(), blockposition.getY(), blockposition.getZ(), i);
    }

    public Village getClosestVillage(int x, int y, int z, int i) {
        Village village = null;
        double d0 = 3.4028234663852886E38D;

        for (Village village1 : this.villages) {
            double d1 = village1.a().i(x, y, z);

            if (d1 < d0) {
                float f = (float) (i + village1.b());

                if (d1 <= (double) (f * f)) {
                    village = village1;
                    d0 = d1;
                }
            }
        }

        return village;
    }

    private void f() {
        if (!this.c.isEmpty()) {
            this.b(this.c.remove(0));
        }
    }

    private void g() {
        for (VillageDoor villagedoor : this.d) {
            Village village = this.getClosestVillage(villagedoor.d(), 32);

            if (village == null) {
                village = new Village(this.world);
                this.villages.add(village);
                this.c();
            }

            village.a(villagedoor);
        }

        this.d.clear();
    }

    private void b(BlockPosition blockposition) {
        byte b0 = 16;
        byte b1 = 4;
        byte b2 = 16;

        for (int i = -b0; i < b0; ++i) {
            for (int j = -b1; j < b1; ++j) {
                for (int k = -b2; k < b2; ++k) {
                    BlockPosition blockposition1 = blockposition.a(i, j, k);

                    if (this.f(blockposition1)) {
                        VillageDoor villagedoor = this.c(blockposition1);

                        if (villagedoor == null) {
                            this.d(blockposition1);
                        } else {
                            villagedoor.a(this.time);
                        }
                    }
                }
            }
        }

    }

    private VillageDoor c(BlockPosition blockposition) {
        Iterator<?> iterator = this.d.iterator();

        VillageDoor villagedoor;

        do {
            if (!iterator.hasNext()) {
                iterator = this.villages.iterator();

                VillageDoor villagedoor1;

                do {
                    if (!iterator.hasNext()) {
                        return null;
                    }

                    villagedoor1 = ((Village) iterator.next()).e(blockposition);
                } while (villagedoor1 == null);

                return villagedoor1;
            }

            villagedoor = (VillageDoor) iterator.next();
        } while (villagedoor.d().getX() != blockposition.getX() || villagedoor.d().getZ() != blockposition.getZ() || FastMath.abs(villagedoor.d().getY() - blockposition.getY()) > 1);

        return villagedoor;
    }

    private void d(BlockPosition blockposition) {
        EnumDirection enumdirection = BlockDoor.h(this.world, blockposition);
        EnumDirection enumdirection1 = enumdirection.opposite();
        int i = this.a(blockposition, enumdirection, 5);
        int j = this.a(blockposition, enumdirection1, i + 1);

        if (i != j) {
            this.d.add(new VillageDoor(blockposition, i < j ? enumdirection : enumdirection1, this.time));
        }

    }

    private int a(BlockPosition blockposition, EnumDirection enumdirection, int i) {
        int j = 0;

        for (int k = 1; k <= 5; ++k) {
            if (this.world.i(blockposition.shift(enumdirection, k))) {
                ++j;
                if (j >= i) {
                    return j;
                }
            }
        }

        return j;
    }

    private boolean e(BlockPosition blockposition) {
        Iterator<BlockPosition> iterator = this.c.iterator();

        BlockPosition blockposition1;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            blockposition1 = iterator.next();
        } while (!blockposition1.equals(blockposition));

        return true;
    }

    private boolean f(BlockPosition blockposition) {
        Block block = this.world.getType(blockposition).getBlock();

        return block instanceof BlockDoor && block.getMaterial() == Material.WOOD;
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.time = nbttagcompound.getInt("Tick");
        NBTTagList nbttaglist = nbttagcompound.getList("Villages", 10);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            Village village = new Village();

            village.a(nbttaglist.get(i));
            this.villages.add(village);
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("Tick", this.time);
        NBTTagList nbttaglist = new NBTTagList();

        for (Village village : this.villages) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            village.b(nbttagcompound1);
            nbttaglist.add(nbttagcompound1);
        }

        nbttagcompound.set("Villages", nbttaglist);
    }

    public static String a(WorldProvider worldprovider) {
        return "villages" + worldprovider.getSuffix();
    }
}
