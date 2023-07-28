package net.minecraft.server;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class WorldGenVillage extends StructureGenerator {

    public static final List<BiomeBase> d = Arrays.asList(BiomeBase.PLAINS, BiomeBase.DESERT, BiomeBase.SAVANNA);
    private int f;
    private int g;
    private final int h;

    public WorldGenVillage() {
        this.g = 32;
        this.h = 8;
    }

    public WorldGenVillage(Map<String, String> map) {
        this();

        for (Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey().equals("size")) {
                this.f = MathHelper.a(entry.getValue(), this.f, 0);
            } else if (entry.getKey().equals("distance")) {
                this.g = MathHelper.a(entry.getValue(), this.g, this.h + 1);
            }
        }

    }

    public String a() {
        return "Village";
    }

    protected boolean a(int i, int j) {
        int k = i;
        int l = j;

        if (i < 0) {
            i -= this.g - 1;
        }

        if (j < 0) {
            j -= this.g - 1;
        }

        int i1 = i / this.g;
        int j1 = j / this.g;
        Random random = this.c.a(i1, j1, this.c.spigotConfig.villageSeed); // Spigot

        i1 *= this.g;
        j1 *= this.g;
        i1 += random.nextInt(this.g - this.h);
        j1 += random.nextInt(this.g - this.h);
        if (k == i1 && l == j1) {
            return c.getWorldChunkManager().a(k * 16 + 8, l * 16 + 8, 0, WorldGenVillage.d);
        }

        return false;
    }

    protected StructureStart b(int i, int j) {
        return new WorldGenVillageStart(this.c, this.b, i, j, this.f);
    }

    public static class WorldGenVillageStart extends StructureStart {

        private boolean c;

        public WorldGenVillageStart() {
        }

        public WorldGenVillageStart(World world, Random random, int i, int j, int k) {
            super(i, j);
            List<WorldGenVillagePieces.WorldGenVillagePieceWeight> list = WorldGenVillagePieces.a(random, k);
            WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece = new WorldGenVillagePieces.WorldGenVillageStartPiece(world.getWorldChunkManager(), 0, random, (i << 4) + 2, (j << 4) + 2, list, k);

            this.a.add(worldgenvillagepieces_worldgenvillagestartpiece);
            worldgenvillagepieces_worldgenvillagestartpiece.a(worldgenvillagepieces_worldgenvillagestartpiece, this.a, random);
            List<StructurePiece> list1 = worldgenvillagepieces_worldgenvillagestartpiece.g;
            List<StructurePiece> list2 = worldgenvillagepieces_worldgenvillagestartpiece.f;

            int l;

            while (!list1.isEmpty() || !list2.isEmpty()) {
                StructurePiece structurepiece;

                if (list1.isEmpty()) {
                    l = random.nextInt(list2.size());
                    structurepiece = list2.remove(l);
                } else {
                    l = random.nextInt(list1.size());
                    structurepiece = list1.remove(l);
                }
                structurepiece.a(worldgenvillagepieces_worldgenvillagestartpiece, this.a, random);
            }

            this.c();
            l = 0;

            for (StructurePiece structurepiece1 : this.a) {
                if (!(structurepiece1 instanceof WorldGenVillagePieces.WorldGenVillageRoadPiece)) {
                    ++l;
                }
            }

            this.c = l > 2;
        }

        public boolean d() {
            return this.c;
        }

        public void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            nbttagcompound.setBoolean("Valid", this.c);
        }

        public void b(NBTTagCompound nbttagcompound) {
            super.b(nbttagcompound);
            this.c = nbttagcompound.getBoolean("Valid");
        }
    }
}
