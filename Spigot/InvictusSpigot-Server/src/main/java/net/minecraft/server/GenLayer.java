package net.minecraft.server;

public abstract class GenLayer {

    protected GenLayer a;
    protected long b;
    private long c;
    private long d;

    public GenLayer(long i) {
        this.b = i;
        this.b *= this.b * 6364136223846793005L + 1442695040888963407L;
        this.b += i;
        this.b *= this.b * 6364136223846793005L + 1442695040888963407L;
        this.b += i;
        this.b *= this.b * 6364136223846793005L + 1442695040888963407L;
        this.b += i;
    }

    public static GenLayer[] a(long i, WorldType worldtype, String s, World world) {
        GenLayerIsland genlayerisland = new GenLayerIsland(1L, new GenLayerZoomFuzzy(2000L, new LayerIsland(1L, world)));
        GenLayerZoom genlayerzoom = new GenLayerZoom(2001L, genlayerisland);

        genlayerisland = new GenLayerIsland(2L, genlayerzoom);
        genlayerisland = new GenLayerIsland(50L, genlayerisland);
        genlayerisland = new GenLayerIsland(70L, genlayerisland);
        GenLayerIcePlains genlayericeplains = new GenLayerIcePlains(2L, genlayerisland);
        GenLayerTopSoil genlayertopsoil = new GenLayerTopSoil(2L, genlayericeplains);

        genlayerisland = new GenLayerIsland(3L, genlayertopsoil);
        GenLayerSpecial genlayerspecial = new GenLayerSpecial(2L, genlayerisland, GenLayerSpecial.EnumGenLayerSpecial.COOL_WARM);

        genlayerspecial = new GenLayerSpecial(2L, genlayerspecial, GenLayerSpecial.EnumGenLayerSpecial.HEAT_ICE);
        genlayerspecial = new GenLayerSpecial(3L, genlayerspecial, GenLayerSpecial.EnumGenLayerSpecial.SPECIAL);
        genlayerzoom = new GenLayerZoom(2002L, genlayerspecial);
        genlayerzoom = new GenLayerZoom(2003L, genlayerzoom);
        genlayerisland = new GenLayerIsland(4L, genlayerzoom);
        GenLayerMushroomIsland genlayermushroomisland = new GenLayerMushroomIsland(5L, genlayerisland);
        GenLayerDeepOcean genlayerdeepocean = new GenLayerDeepOcean(4L, genlayermushroomisland);
        GenLayer genlayer = GenLayerZoom.b(1000L, genlayerdeepocean, 0);
        CustomWorldSettingsFinal customworldsettingsfinal;
        int j = 4;
        int k = j;

        if (worldtype == WorldType.CUSTOMIZED && s.length() > 0) {
            customworldsettingsfinal = CustomWorldSettingsFinal.CustomWorldSettings.a(s).b();
            j = customworldsettingsfinal.G;
            k = customworldsettingsfinal.H;
        }

        if (worldtype == WorldType.LARGE_BIOMES) {
            j = 6;
        }

        GenLayer genlayer1 = GenLayerZoom.b(1000L, genlayer, 0);
        GenLayerCleaner genlayercleaner = new GenLayerCleaner(100L, genlayer1);
        GenLayerBiome genlayerbiome = new GenLayerBiome(200L, genlayer, worldtype, s, world);
        GenLayer genlayer2 = GenLayerZoom.b(1000L, genlayerbiome, 2);
        GenLayerDesert genlayerdesert = new GenLayerDesert(1000L, genlayer2);
        GenLayer genlayer3 = GenLayerZoom.b(1000L, genlayercleaner, 2);
        GenLayerRegionHills genlayerregionhills = new GenLayerRegionHills(1000L, genlayerdesert, genlayer3, world);

        genlayer1 = GenLayerZoom.b(1000L, genlayercleaner, 2);
        genlayer1 = GenLayerZoom.b(1000L, genlayer1, k);
        GenLayer genlayerriver = new GenLayerRiver(1L, genlayer1);

        if (world.generatorConfig.spawnBiomeRadius > 0 && !world.generatorConfig.spawnBiomeRivers) {
            genlayerriver = new GenLayerRemoveSpawnRivers(genlayerriver, world);
        }

        GenLayerSmooth genlayersmooth = new GenLayerSmooth(1000L, genlayerriver);
        GenLayer object = new GenLayerPlains(1001L, genlayerregionhills);

        if (world.generatorConfig.spawnBiomeRadius > 0) {
            object = new GenLayerSpawnBiome(object, j, world);
        }

        for (int l = 0; l < j; ++l) {
            object = new GenLayerZoom(1000 + l, object);
            if (l == 0) {
                object = new GenLayerIsland(3L, object);
            }

            if (l == 1 || j == 1) {
                object = new GenLayerMushroomShore(1000L, object);
            }
        }

        GenLayerSmooth genlayersmooth1 = new GenLayerSmooth(1000L, object);
        GenLayerRiverMix genlayerrivermix = new GenLayerRiverMix(100L, genlayersmooth1, genlayersmooth);
        GenLayerZoomVoronoi genlayerzoomvoronoi = new GenLayerZoomVoronoi(10L, genlayerrivermix);

        genlayerrivermix.a(i);
        genlayerzoomvoronoi.a(i);
        return new GenLayer[]{genlayerrivermix, genlayerzoomvoronoi, genlayerrivermix};
    }

    protected static boolean a(int i, int j) {
        if (i == j) {
            return true;
        } else if (i != BiomeBase.MESA_PLATEAU_F.id && i != BiomeBase.MESA_PLATEAU.id) {
            BiomeBase biomebase = BiomeBase.getBiome(i);
            BiomeBase biomebase1 = BiomeBase.getBiome(j);

            try {
                return biomebase != null && biomebase1 != null && biomebase.a(biomebase1);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Comparing biomes");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Biomes being compared");

                crashreportsystemdetails.a("Biome A ID", i);
                crashreportsystemdetails.a("Biome B ID", j);
                crashreportsystemdetails.a("Biome A", () -> String.valueOf(biomebase));
                crashreportsystemdetails.a("Biome B", () -> String.valueOf(biomebase));
                throw new ReportedException(crashreport);
            }
        } else {
            return j == BiomeBase.MESA_PLATEAU_F.id || j == BiomeBase.MESA_PLATEAU.id;
        }
    }

    protected static boolean b(int i) {
        return i == BiomeBase.OCEAN.id || i == BiomeBase.DEEP_OCEAN.id || i == BiomeBase.FROZEN_OCEAN.id;
    }

    public void a(long i) {
        this.c = i;
        if (this.a != null) {
            this.a.a(i);
        }

        this.c *= this.c * 6364136223846793005L + 1442695040888963407L;
        this.c += this.b;
        this.c *= this.c * 6364136223846793005L + 1442695040888963407L;
        this.c += this.b;
        this.c *= this.c * 6364136223846793005L + 1442695040888963407L;
        this.c += this.b;
    }

    public void a(long i, long j) {
        this.d = this.c;
        this.d *= this.d * 6364136223846793005L + 1442695040888963407L;
        this.d += i;
        this.d *= this.d * 6364136223846793005L + 1442695040888963407L;
        this.d += j;
        this.d *= this.d * 6364136223846793005L + 1442695040888963407L;
        this.d += i;
        this.d *= this.d * 6364136223846793005L + 1442695040888963407L;
        this.d += j;
    }

    protected int a(int i) {
        int j = (int) ((this.d >> 24) % (long) i);

        if (j < 0) {
            j += i;
        }

        this.d *= this.d * 6364136223846793005L + 1442695040888963407L;
        this.d += this.c;
        return j;
    }

    public abstract int[] a(int i, int j, int k, int l);

    protected int a(int... aint) {
        return aint[this.a(aint.length)];
    }

    protected int b(int i, int j, int k, int l) {
        return j == k && k == l ? j : (i == j && i == k ? i : (i == j && i == l ? i : (i == k && i == l ? i : (i == j && k != l ? i : (i == k && j != l ? i : (i == l && j != k ? i : (j == k && i != l ? j : (j == l && i != k ? j : (k == l && i != j ? k : this.a(new int[]{i, j, k, l}))))))))));
    }
}
