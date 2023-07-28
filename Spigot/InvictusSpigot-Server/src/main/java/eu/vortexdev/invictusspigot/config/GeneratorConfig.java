package eu.vortexdev.invictusspigot.config;

import eu.vortexdev.invictusspigot.util.JavaUtil;
import net.minecraft.server.BiomeBase;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.stream.Stream;

// mSpigot - generator system
public class GeneratorConfig {

    private final YamlConfiguration config;

    public boolean oceans;
    public boolean biomePlains;
    public boolean biomeDesert;
    public boolean biomeDesertHills;
    public boolean biomeExtremeHills;
    public boolean biomeExtremeHillsPlus;
    public boolean biomeForest;
    public boolean biomeForestHills;
    public boolean biomeTaiga;
    public boolean biomeTaigaHills;
    public boolean biomeSwampland;
    public boolean biomeIcePlains;
    public boolean biomeIceMountains;
    public boolean biomeJungle;
    public boolean biomeJungleHills;
    public boolean biomeBirchForest;
    public boolean biomeBirchForestHills;
    public boolean biomeRoofedForest;
    public boolean biomeColdTaiga;
    public boolean biomeColdTaigaHills;
    public boolean biomeMegaTaiga;
    public boolean biomeMegaTaigaHills;
    public boolean biomeSavanna;
    public boolean biomeSavannaPlateau;
    public boolean biomeMesa;
    public boolean biomeMesaPlateauF;
    public boolean biomeMesaPlateau;

    public boolean genWaterLakes;
    public boolean genLavaLakes;

    public BiomeBase spawnBiome;
    public int spawnBiomeRadius;
    public boolean spawnBiomeRivers;

    public float cavesMultiplier;
    public float sugarCaneMultiplier;

    public float emeraldMultiplier;
    public float coalMultiplier;
    public int coalSize;
    public boolean coalMustTouchAir;
    public float ironMultiplier;
    public int ironSize;
    public boolean ironMustTouchAir;
    public float goldMultiplier;
    public int goldSize;
    public boolean goldMustTouchAir;
    public float redstoneMultiplier;
    public int redstoneSize;
    public boolean redstoneMustTouchAir;
    public float diamondMultiplier;
    public int diamondSize;
    public boolean diamondMustTouchAir;
    public float lapisMultiplier;
    public int lapisSize;
    public boolean lapisMustTouchAir;

    public GeneratorConfig(String worldName) {
        File file = new File("generator", worldName + ".yml");
        JavaUtil.createIfNotExists(file);
        config = YamlConfiguration.loadConfiguration(file);
        config.options().copyDefaults(true);

        oceans = getBoolean("generate.oceans", true);
        genLavaLakes = getBoolean("generate.lava-lakes", true);
        genWaterLakes = getBoolean("generate.water-lakes", true);
        cavesMultiplier = (float) getDouble("generate.cave-multiplier", 1.0);
        sugarCaneMultiplier = (float) getDouble("generate.sugar-cane-multiplier", 1.0);

        biomePlains = getBoolean("biome.plains", true);
        biomeDesert = getBoolean("biome.desert", true);
        biomeDesertHills = getBoolean("biome.desert-hills", true);
        biomeExtremeHills = getBoolean("biome.extreme-hills", true);
        biomeExtremeHillsPlus = getBoolean("biome.extreme-hills-plus", true);
        biomeForest = getBoolean("biome.forest", true);
        biomeForestHills = getBoolean("biome.forest-hills", true);
        biomeTaiga = getBoolean("biome.taiga", true);
        biomeTaigaHills = getBoolean("biome.taiga-hills", true);
        biomeSwampland = getBoolean("biome.swampland", true);
        biomeIcePlains = getBoolean("biome.ice-plains", true);
        biomeIceMountains = getBoolean("biome.ice-mountains", true);
        biomeJungle = getBoolean("biome.jungle", true);
        biomeJungleHills = getBoolean("biome.jungle-hills", true);
        biomeBirchForest = getBoolean("biome.birch-forest", true);
        biomeBirchForestHills = getBoolean("biome.birch-forest-hills", true);
        biomeRoofedForest = getBoolean("biome.roofed-forest", true);
        biomeColdTaiga = getBoolean("biome.cold-taiga", true);
        biomeColdTaigaHills = getBoolean("biome.cold-taiga-hills", true);
        biomeMegaTaiga = getBoolean("biome.mega-taiga", true);
        biomeMegaTaigaHills = getBoolean("biome.mega-taiga-hills", true);
        biomeSavanna = getBoolean("biome.savanna", true);
        biomeSavannaPlateau = getBoolean("biome.savanna-plateau", true);
        biomeMesa = getBoolean("biome.mesa", true);
        biomeMesaPlateauF = getBoolean("biome.mesa-plateau-f", true);
        biomeMesaPlateau = getBoolean("biome.mesa-plateau", true);

        String biome = getString("spawn.biome", "plains");
        spawnBiome = Stream.of(BiomeBase.getBiomes()).filter(biomeBase -> biomeBase.ah.equalsIgnoreCase(biome)).findAny().orElse(BiomeBase.PLAINS);
        spawnBiomeRadius = getInt("spawn.radius", 0);
        spawnBiomeRivers = getBoolean("spawn.rivers", false);

        emeraldMultiplier = (float) getDouble("ores.emerald.multiplier", 1.0);
        coalMultiplier = (float) getDouble("ores.coal.multiplier", 1.0);
        coalSize = getInt("ores.coal.size", 17);
        coalMustTouchAir = getBoolean("ores.coal.must-touch-air", false);
        ironMultiplier = (float) getDouble("ores.iron.multiplier", 1.0);
        ironSize = getInt("ores.iron.size", 9);
        ironMustTouchAir = getBoolean("ores.iron.must-touch-air", false);
        goldMultiplier = (float) getDouble("ores.gold.multiplier", 1.0);
        goldSize = getInt("ores.gold.size", 9);
        goldMustTouchAir = getBoolean("ores.gold.must-touch-air", false);
        redstoneMultiplier = (float) getDouble("ores.redstone.multiplier", 1.0);
        redstoneSize = getInt("ores.redstone.size", 8);
        redstoneMustTouchAir = getBoolean("ores.redstone.must-touch-air", false);
        diamondMultiplier = (float) getDouble("ores.diamond.multiplier", 1.0);
        diamondSize = getInt("ores.diamond.size", 8);
        diamondMustTouchAir = getBoolean("ores.diamond.must-touch-air", false);
        lapisMultiplier = (float) getDouble("ores.lapis.multiplier", 1.0);
        lapisSize = getInt("ores.lapis.size", 7);
        lapisMustTouchAir = getBoolean("ores.lapis.must-touch-air", false);

        JavaUtil.saveFile(config, file);
    }

    private boolean getBoolean(String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, def);
    }

    private String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, def);
    }

    private int getInt(String path, int def) {
        config.addDefault(path, def);
        return config.getInt(path, def);
    }

    private double getDouble(String path, double def) {
        config.addDefault(path, def);
        return config.getDouble(path, def);
    }

}
