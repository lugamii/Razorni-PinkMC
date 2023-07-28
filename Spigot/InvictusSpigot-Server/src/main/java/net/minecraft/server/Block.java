package net.minecraft.server;

import org.spigotmc.AsyncCatcher;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Block {
    public static final RegistryID<IBlockData> d = new RegistryID<>();
    public static final StepSound e = new StepSound("stone", 1.0F, 1.0F);
    public static final StepSound f = new StepSound("wood", 1.0F, 1.0F);
    public static final StepSound g = new StepSound("gravel", 1.0F, 1.0F);
    public static final StepSound h = new StepSound("grass", 1.0F, 1.0F);
    public static final StepSound i = new StepSound("stone", 1.0F, 1.0F);
    public static final StepSound j = new StepSound("stone", 1.0F, 1.5F);
    public static final StepSound k = new StepSound("stone", 1.0F, 1.0F) {
        public String getBreakSound() {
            return "dig.glass";
        }

        public String getPlaceSound() {
            return "step.stone";
        }
    };
    public static final StepSound l = new StepSound("cloth", 1.0F, 1.0F);
    public static final StepSound m = new StepSound("sand", 1.0F, 1.0F);
    public static final StepSound n = new StepSound("snow", 1.0F, 1.0F);
    public static final StepSound o = new StepSound("ladder", 1.0F, 1.0F) {
        public String getBreakSound() {
            return "dig.wood";
        }
    };
    public static final StepSound p = new StepSound("anvil", 0.3F, 1.0F) {
        public String getBreakSound() {
            return "dig.stone";
        }

        public String getPlaceSound() {
            return "random.anvil_land";
        }
    };
    public static final StepSound q = new StepSound("slime", 1.0F, 1.0F) {
        public String getBreakSound() {
            return "mob.slime.big";
        }

        public String getPlaceSound() {
            return "mob.slime.big";
        }

        public String getStepSound() {
            return "mob.slime.small";
        }
    };
    private static final MinecraftKey a = new MinecraftKey("air");
    public static final RegistryBlocks<MinecraftKey, Block> REGISTRY = new RegistryBlocks<>(a);
    protected final Material material;
    protected final MaterialMapColor K;
    protected final BlockStateList blockStateList;
    public StepSound stepSound;
    public float I;
    public float frictionFactor;
    protected boolean r;
    protected int s;
    protected boolean t;
    protected int u;
    protected boolean v;
    protected float strength;
    public float durability;
    protected boolean y;
    protected boolean z;
    protected boolean isTileEntity;
    protected double minX;
    protected double minY;
    protected double minZ;
    protected double maxX;
    protected double maxY;
    protected double maxZ;
    private IBlockData blockData;

    private String name;

    public Block(Material material, MaterialMapColor materialmapcolor) {
        this.y = true;
        this.stepSound = e;
        this.I = 1.0F;
        this.frictionFactor = 0.6F;
        this.material = material;
        this.K = materialmapcolor;
        a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.r = c();
        this.s = c() ? 255 : 0;
        this.t = !material.blocksLight();
        this.blockStateList = getStateList();
        j(this.blockStateList.getBlockData());
    }

    protected Block(Material material) {
        this(material, material.r());
    }

    public static int getId(Block block) {
        return REGISTRY.b(block);
    }

    public static int getCombinedId(IBlockData iblockdata) {
        Block block = iblockdata.getBlock();
        return getId(block) + (block.toLegacyData(iblockdata) << 12);
    }

    public static Block getById(int i) {
        return REGISTRY.a(i);
    }

    public static IBlockData getByCombinedId(int i) {
        return getById(i & 0xFFF).fromLegacyData(i >> 12 & 0xF);
    }

    public static Block asBlock(Item item) {
        return (item instanceof ItemBlock) ? ((ItemBlock) item).d() : null;
    }

    public static Block getByName(String s) {
        MinecraftKey minecraftkey = new MinecraftKey(s);
        if (REGISTRY.d(minecraftkey))
            return REGISTRY.get(minecraftkey);
        try {
            return REGISTRY.a(Integer.parseInt(s));
        } catch (NumberFormatException numberFormatException) {
            return null;
        }
    }

    public static void a(World world, int x, int y, int z, ItemStack itemstack) {
        if (world.getGameRules().getBoolean("doTileDrops")) {
            EntityItem entityitem = new EntityItem(world, x + (world.random.nextFloat() * 0.5F) + 0.5F * 0.5D, y + (world.random.nextFloat() * 0.5F) + 0.5F * 0.5D, z + (world.random.nextFloat() * 0.5F) + 0.5F * 0.5D, itemstack);
            entityitem.p();
            world.addEntity(entityitem);
        }
    }

    public static void a(World world, BlockPosition blockposition, ItemStack itemstack) {
        a(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), itemstack);
    }

    public static boolean a(Block block, Block block1) {
        return block != null && block1 != null && (block == block1 || block.b(block1));
    }

    public static void S() {
        a(0, a, (new BlockAir()).c("air"));
        a(1, "stone", (new BlockStone()).c(1.5F).b(10.0F).a(Block.i).c("stone"));
        a(2, "grass", (new BlockGrass()).c(0.6F).a(h).c("grass"));
        a(3, "dirt", (new BlockDirt()).c(0.5F).a(g).c("dirt"));
        Block block = (new Block(Material.STONE)).c(2.0F).b(10.0F).a(Block.i).c("stonebrick").a(CreativeModeTab.b);
        a(4, "cobblestone", block);
        Block block1 = (new BlockWood()).c(2.0F).b(5.0F).a(f).c("wood");
        a(5, "planks", block1);
        a(6, "sapling", (new BlockSapling()).c(0.0F).a(h).c("sapling"));
        a(7, "bedrock", (new BlockBedrock()).a(Block.i).c("bedrock").K().a(CreativeModeTab.b));
        a(8, "flowing_water", (new BlockFlowing(Material.WATER)).c(100.0F).e(3).c("water").K());
        a(9, "water", (new BlockStationary(Material.WATER)).c(100.0F).e(3).c("water").K());
        a(10, "flowing_lava", (new BlockFlowing(Material.LAVA)).c(100.0F).a(1.0F).c("lava").K());
        a(11, "lava", (new BlockStationary(Material.LAVA)).c(100.0F).a(1.0F).c("lava").K());
        a(12, "sand", (new BlockSand()).c(0.5F).a(m).c("sand"));
        a(13, "gravel", (new BlockGravel()).c(0.6F).a(g).c("gravel"));
        a(14, "gold_ore", (new BlockOre()).c(3.0F).b(5.0F).a(Block.i).c("oreGold"));
        a(15, "iron_ore", (new BlockOre()).c(3.0F).b(5.0F).a(Block.i).c("oreIron"));
        a(16, "coal_ore", (new BlockOre()).c(3.0F).b(5.0F).a(Block.i).c("oreCoal"));
        a(17, "log", (new BlockLog1()).c("log"));
        a(18, "leaves", (new BlockLeaves1()).c("leaves"));
        a(19, "sponge", (new BlockSponge()).c(0.6F).a(h).c("sponge"));
        a(20, "glass", (new BlockGlass(Material.SHATTERABLE, false)).c(0.3F).a(k).c("glass"));
        a(21, "lapis_ore", (new BlockOre()).c(3.0F).b(5.0F).a(Block.i).c("oreLapis"));
        a(22, "lapis_block", (new Block(Material.ORE, MaterialMapColor.H)).c(3.0F).b(5.0F).a(Block.i).c("blockLapis").a(CreativeModeTab.b));
        a(23, "dispenser", (new BlockDispenser()).c(3.5F).a(Block.i).c("dispenser"));
        Block block2 = (new BlockSandStone()).a(Block.i).c(0.8F).c("sandStone");
        a(24, "sandstone", block2);
        a(25, "noteblock", (new BlockNote()).c(0.8F).c("musicBlock"));
        a(26, "bed", (new BlockBed()).a(f).c(0.2F).c("bed").K());
        a(27, "golden_rail", (new BlockPoweredRail()).c(0.7F).a(j).c("goldenRail"));
        a(28, "detector_rail", (new BlockMinecartDetector()).c(0.7F).a(j).c("detectorRail"));
        a(29, "sticky_piston", (new BlockPiston(true)).c("pistonStickyBase"));
        a(30, "web", (new BlockWeb()).e(1).c(4.0F).c("web"));
        a(31, "tallgrass", (new BlockLongGrass()).c(0.0F).a(h).c("tallgrass"));
        a(32, "deadbush", (new BlockDeadBush()).c(0.0F).a(h).c("deadbush"));
        a(33, "piston", (new BlockPiston(false)).c("pistonBase"));
        a(34, "piston_head", (new BlockPistonExtension()).c("pistonBase"));
        a(35, "wool", (new BlockCloth(Material.CLOTH)).c(0.8F).a(l).c("cloth"));
        a(36, "piston_extension", new BlockPistonMoving());
        a(37, "yellow_flower", (new BlockYellowFlowers()).c(0.0F).a(h).c("flower1"));
        a(38, "red_flower", (new BlockRedFlowers()).c(0.0F).a(h).c("flower2"));
        Block block3 = (new BlockMushroom()).c(0.0F).a(h).a(0.125F).c("mushroom");
        a(39, "brown_mushroom", block3);
        Block block4 = (new BlockMushroom()).c(0.0F).a(h).c("mushroom");
        a(40, "red_mushroom", block4);
        a(41, "gold_block", (new Block(Material.ORE, MaterialMapColor.F)).c(3.0F).b(10.0F).a(j).c("blockGold").a(CreativeModeTab.b));
        a(42, "iron_block", (new Block(Material.ORE, MaterialMapColor.h)).c(5.0F).b(10.0F).a(j).c("blockIron").a(CreativeModeTab.b));
        a(43, "double_stone_slab", (new BlockDoubleStep()).c(2.0F).b(10.0F).a(Block.i).c("stoneSlab"));
        a(44, "stone_slab", (new BlockStep()).c(2.0F).b(10.0F).a(Block.i).c("stoneSlab"));
        Block block5 = (new Block(Material.STONE, MaterialMapColor.D)).c(2.0F).b(10.0F).a(Block.i).c("brick").a(CreativeModeTab.b);
        a(45, "brick_block", block5);
        a(46, "tnt", (new BlockTNT()).c(0.0F).a(h).c("tnt"));
        a(47, "bookshelf", (new BlockBookshelf()).c(1.5F).a(f).c("bookshelf"));
        a(48, "mossy_cobblestone", (new Block(Material.STONE)).c(2.0F).b(10.0F).a(Block.i).c("stoneMoss").a(CreativeModeTab.b));
        a(49, "obsidian", (new BlockObsidian()).a(Block.i).c("obsidian"));
        a(50, "torch", (new BlockTorch()).c(0.0F).a(0.9375F).a(f).c("torch"));
        a(51, "fire", (new BlockFire()).c(0.0F).a(1.0F).a(l).c("fire").K());
        a(52, "mob_spawner", (new BlockMobSpawner()).c(5.0F).a(j).c("mobSpawner").K());
        a(53, "oak_stairs", (new BlockStairs(block1.getBlockData().set(BlockWood.VARIANT, BlockWood.EnumLogVariant.OAK))).c("stairsWood"));
        a(54, "chest", (new BlockChest(0)).c(2.5F).a(f).c("chest"));
        a(55, "redstone_wire", new OptimizedRedstone().c(0.0F).a(e).c("redstoneDust").K());
        a(56, "diamond_ore", (new BlockOre()).c(3.0F).b(5.0F).a(Block.i).c("oreDiamond"));
        a(57, "diamond_block", (new Block(Material.ORE, MaterialMapColor.G)).c(5.0F).b(10.0F).a(j).c("blockDiamond").a(CreativeModeTab.b));
        a(58, "crafting_table", (new BlockWorkbench()).c(2.5F).a(f).c("workbench"));
        a(59, "wheat", (new BlockCrops()).c("crops"));
        Block block6 = (new BlockSoil()).c(0.6F).a(g).c("farmland");
        a(60, "farmland", block6);
        a(61, "furnace", (new BlockFurnace(false)).c(3.5F).a(Block.i).c("furnace").a(CreativeModeTab.c));
        a(62, "lit_furnace", (new BlockFurnace(true)).c(3.5F).a(Block.i).a(0.875F).c("furnace"));
        a(63, "standing_sign", (new BlockFloorSign()).c(1.0F).a(f).c("sign").K());
        a(64, "wooden_door", (new BlockDoor(Material.WOOD)).c(3.0F).a(f).c("doorOak").K());
        a(65, "ladder", (new BlockLadder()).c(0.4F).a(o).c("ladder"));
        a(66, "rail", (new BlockMinecartTrack()).c(0.7F).a(j).c("rail"));
        a(67, "stone_stairs", (new BlockStairs(block.getBlockData())).c("stairsStone"));
        a(68, "wall_sign", (new BlockWallSign()).c(1.0F).a(f).c("sign").K());
        a(69, "lever", (new BlockLever()).c(0.5F).a(f).c("lever"));
        a(70, "stone_pressure_plate", (new BlockPressurePlateBinary(Material.STONE, BlockPressurePlateBinary.EnumMobType.MOBS)).c(0.5F).a(Block.i).c("pressurePlateStone"));
        a(71, "iron_door", (new BlockDoor(Material.ORE)).c(5.0F).a(j).c("doorIron").K());
        a(72, "wooden_pressure_plate", (new BlockPressurePlateBinary(Material.WOOD, BlockPressurePlateBinary.EnumMobType.EVERYTHING)).c(0.5F).a(f).c("pressurePlateWood"));
        a(73, "redstone_ore", (new BlockRedstoneOre(false)).c(3.0F).b(5.0F).a(Block.i).c("oreRedstone").a(CreativeModeTab.b));
        a(74, "lit_redstone_ore", (new BlockRedstoneOre(true)).a(0.625F).c(3.0F).b(5.0F).a(Block.i).c("oreRedstone"));
        a(75, "unlit_redstone_torch", (new BlockRedstoneTorch(false)).c(0.0F).a(f).c("notGate"));
        a(76, "redstone_torch", (new BlockRedstoneTorch(true)).c(0.0F).a(0.5F).a(f).c("notGate").a(CreativeModeTab.d));
        a(77, "stone_button", (new BlockStoneButton()).c(0.5F).a(Block.i).c("button"));
        a(78, "snow_layer", (new BlockSnow()).c(0.1F).a(n).c("snow").e(0));
        a(79, "ice", (new BlockIce()).c(0.5F).e(3).a(k).c("ice"));
        a(80, "snow", (new BlockSnowBlock()).c(0.2F).a(n).c("snow"));
        a(81, "cactus", (new BlockCactus()).c(0.4F).a(l).c("cactus"));
        a(82, "clay", (new BlockClay()).c(0.6F).a(g).c("clay"));
        a(83, "reeds", (new BlockReed()).c(0.0F).a(h).c("reeds").K());
        a(84, "jukebox", (new BlockJukeBox()).c(2.0F).b(10.0F).a(Block.i).c("jukebox"));
        a(85, "fence", (new BlockFence(Material.WOOD, BlockWood.EnumLogVariant.OAK.c())).c(2.0F).b(5.0F).a(f).c("fence"));
        Block block7 = (new BlockPumpkin()).c(1.0F).a(f).c("pumpkin");
        a(86, "pumpkin", block7);
        a(87, "netherrack", (new BlockBloodStone()).c(0.4F).a(Block.i).c("hellrock"));
        a(88, "soul_sand", (new BlockSlowSand()).c(0.5F).a(m).c("hellsand"));
        a(89, "glowstone", (new BlockLightStone(Material.SHATTERABLE)).c(0.3F).a(k).a(1.0F).c("lightgem"));
        a(90, "portal", (new BlockPortal()).c(-1.0F).a(k).a(0.75F).c("portal"));
        a(91, "lit_pumpkin", (new BlockPumpkin()).c(1.0F).a(f).a(1.0F).c("litpumpkin"));
        a(92, "cake", (new BlockCake()).c(0.5F).a(l).c("cake").K());
        a(93, "unpowered_repeater", (new BlockRepeater(false)).c(0.0F).a(f).c("diode").K());
        a(94, "powered_repeater", (new BlockRepeater(true)).c(0.0F).a(f).c("diode").K());
        a(95, "stained_glass", (new BlockStainedGlass(Material.SHATTERABLE)).c(0.3F).a(k).c("stainedGlass"));
        a(96, "trapdoor", (new BlockTrapdoor(Material.WOOD)).c(3.0F).a(f).c("trapdoor").K());
        a(97, "monster_egg", (new BlockMonsterEggs()).c(0.75F).c("monsterStoneEgg"));
        Block block8 = (new BlockSmoothBrick()).c(1.5F).b(10.0F).a(Block.i).c("stonebricksmooth");
        a(98, "stonebrick", block8);
        a(99, "brown_mushroom_block", (new BlockHugeMushroom(Material.WOOD, MaterialMapColor.l, block3)).c(0.2F).a(f).c("mushroom"));
        a(100, "red_mushroom_block", (new BlockHugeMushroom(Material.WOOD, MaterialMapColor.D, block4)).c(0.2F).a(f).c("mushroom"));
        a(101, "iron_bars", (new BlockThin(Material.ORE, true)).c(5.0F).b(10.0F).a(j).c("fenceIron"));
        a(102, "glass_pane", (new BlockThin(Material.SHATTERABLE, false)).c(0.3F).a(k).c("thinGlass"));
        Block block9 = (new BlockMelon()).c(1.0F).a(f).c("melon");
        a(103, "melon_block", block9);
        a(104, "pumpkin_stem", (new BlockStem(block7)).c(0.0F).a(f).c("pumpkinStem"));
        a(105, "melon_stem", (new BlockStem(block9)).c(0.0F).a(f).c("pumpkinStem"));
        a(106, "vine", (new BlockVine()).c(0.2F).a(h).c("vine"));
        a(107, "fence_gate", (new BlockFenceGate(BlockWood.EnumLogVariant.OAK)).c(2.0F).b(5.0F).a(f).c("fenceGate"));
        a(108, "brick_stairs", (new BlockStairs(block5.getBlockData())).c("stairsBrick"));
        a(109, "stone_brick_stairs", (new BlockStairs(block8.getBlockData().set(BlockSmoothBrick.VARIANT, BlockSmoothBrick.EnumStonebrickType.DEFAULT))).c("stairsStoneBrickSmooth"));
        a(110, "mycelium", (new BlockMycel()).c(0.6F).a(h).c("mycel"));
        a(111, "waterlily", (new BlockWaterLily()).c(0.0F).a(h).c("waterlily"));
        Block block10 = (new BlockNetherbrick()).c(2.0F).b(10.0F).a(Block.i).c("netherBrick").a(CreativeModeTab.b);
        a(112, "nether_brick", block10);
        a(113, "nether_brick_fence", (new BlockFence(Material.STONE, MaterialMapColor.K)).c(2.0F).b(10.0F).a(Block.i).c("netherFence"));
        a(114, "nether_brick_stairs", (new BlockStairs(block10.getBlockData())).c("stairsNetherBrick"));
        a(115, "nether_wart", (new BlockNetherWart()).c("netherStalk"));
        a(116, "enchanting_table", (new BlockEnchantmentTable()).c(5.0F).b(2000.0F).c("enchantmentTable"));
        a(117, "brewing_stand", (new BlockBrewingStand()).c(0.5F).a(0.125F).c("brewingStand"));
        a(118, "cauldron", (new BlockCauldron()).c(2.0F).c("cauldron"));
        a(119, "end_portal", (new BlockEnderPortal(Material.PORTAL)).c(-1.0F).b(6000000.0F));
        a(120, "end_portal_frame", (new BlockEnderPortalFrame()).a(k).a(0.125F).c(-1.0F).c("endPortalFrame").b(6000000.0F).a(CreativeModeTab.c));
        a(121, "end_stone", (new BlockEndstone(Material.STONE, MaterialMapColor.d)).a(Block.i).c("whiteStone").a(CreativeModeTab.b));        a(122, "dragon_egg", (new BlockDragonEgg()).c(3.0F).b(15.0F).a(Block.i).a(0.125F).c("dragonEgg"));
        a(123, "redstone_lamp", (new BlockRedstoneLamp(false)).c(0.3F).a(k).c("redstoneLight").a(CreativeModeTab.d));
        a(124, "lit_redstone_lamp", (new BlockRedstoneLamp(true)).c(0.3F).a(k).c("redstoneLight"));
        a(125, "double_wooden_slab", (new BlockDoubleWoodStep()).c(2.0F).b(5.0F).a(f).c("woodSlab"));
        a(126, "wooden_slab", (new BlockWoodStep()).c(2.0F).b(5.0F).a(f).c("woodSlab"));
        a(127, "cocoa", (new BlockCocoa()).c(0.2F).b(5.0F).a(f).c("cocoa"));
        a(128, "sandstone_stairs", (new BlockStairs(block2.getBlockData().set(BlockSandStone.TYPE, BlockSandStone.EnumSandstoneVariant.SMOOTH))).c("stairsSandStone"));
        a(129, "emerald_ore", (new BlockOre()).c(3.0F).b(5.0F).a(Block.i).c("oreEmerald"));
        a(130, "ender_chest", (new BlockEnderChest()).c(22.5F).b(1000.0F).a(Block.i).c("enderChest").a(0.5F));
        a(131, "tripwire_hook", (new BlockTripwireHook()).c("tripWireSource"));
        a(132, "tripwire", (new BlockTripwire()).c("tripWire"));
        a(133, "emerald_block", (new Block(Material.ORE, MaterialMapColor.I)).c(5.0F).b(10.0F).a(j).c("blockEmerald").a(CreativeModeTab.b));
        a(134, "spruce_stairs", (new BlockStairs(block1.getBlockData().set(BlockWood.VARIANT, BlockWood.EnumLogVariant.SPRUCE))).c("stairsWoodSpruce"));
        a(135, "birch_stairs", (new BlockStairs(block1.getBlockData().set(BlockWood.VARIANT, BlockWood.EnumLogVariant.BIRCH))).c("stairsWoodBirch"));
        a(136, "jungle_stairs", (new BlockStairs(block1.getBlockData().set(BlockWood.VARIANT, BlockWood.EnumLogVariant.JUNGLE))).c("stairsWoodJungle"));
        a(137, "command_block", (new BlockCommand()).x().b(6000000.0F).c("commandBlock"));
        a(138, "beacon", (new BlockBeacon()).c("beacon").a(1.0F));
        a(139, "cobblestone_wall", (new BlockCobbleWall(block)).c("cobbleWall"));
        a(140, "flower_pot", (new BlockFlowerPot()).c(0.0F).a(e).c("flowerPot"));
        a(141, "carrots", (new BlockCarrots()).c("carrots"));
        a(142, "potatoes", (new BlockPotatoes()).c("potatoes"));
        a(143, "wooden_button", (new BlockWoodButton()).c(0.5F).a(f).c("button"));
        a(144, "skull", (new BlockSkull()).c(1.0F).a(Block.i).c("skull"));
        a(145, "anvil", (new BlockAnvil()).c(5.0F).a(p).b(2000.0F).c("anvil"));
        a(146, "trapped_chest", (new BlockChest(1)).c(2.5F).a(f).c("chestTrap"));
        a(147, "light_weighted_pressure_plate", (new BlockPressurePlateWeighted(Material.ORE, 15, MaterialMapColor.F)).c(0.5F).a(f).c("weightedPlate_light"));
        a(148, "heavy_weighted_pressure_plate", (new BlockPressurePlateWeighted(Material.ORE, 150)).c(0.5F).a(f).c("weightedPlate_heavy"));
        a(149, "unpowered_comparator", (new BlockRedstoneComparator(false)).c(0.0F).a(f).c("comparator").K());
        a(150, "powered_comparator", (new BlockRedstoneComparator(true)).c(0.0F).a(0.625F).a(f).c("comparator").K());
        a(151, "daylight_detector", new BlockDaylightDetector(false));
        a(152, "redstone_block", (new BlockPowered(Material.ORE, MaterialMapColor.f)).c(5.0F).b(10.0F).a(j).c("blockRedstone").a(CreativeModeTab.d));
        a(153, "quartz_ore", (new BlockOre(MaterialMapColor.K)).c(3.0F).b(5.0F).a(Block.i).c("netherquartz"));
        a(154, "hopper", (new BlockHopper()).c(3.0F).b(8.0F).a(j).c("hopper"));
        Block block11 = (new BlockQuartz()).a(Block.i).c(0.8F).c("quartzBlock");
        a(155, "quartz_block", block11);
        a(156, "quartz_stairs", (new BlockStairs(block11.getBlockData().set(BlockQuartz.VARIANT, BlockQuartz.EnumQuartzVariant.DEFAULT))).c("stairsQuartz"));
        a(157, "activator_rail", (new BlockPoweredRail()).c(0.7F).a(j).c("activatorRail"));
        a(158, "dropper", (new BlockDropper()).c(3.5F).a(Block.i).c("dropper"));
        a(159, "stained_hardened_clay", (new BlockCloth(Material.STONE)).c(1.25F).b(7.0F).a(Block.i).c("clayHardenedStained"));
        a(160, "stained_glass_pane", (new BlockStainedGlassPane()).c(0.3F).a(k).c("thinStainedGlass"));
        a(161, "leaves2", (new BlockLeaves2()).c("leaves"));
        a(162, "log2", (new BlockLog2()).c("log"));
        a(163, "acacia_stairs", (new BlockStairs(block1.getBlockData().set(BlockWood.VARIANT, BlockWood.EnumLogVariant.ACACIA))).c("stairsWoodAcacia"));
        a(164, "dark_oak_stairs", (new BlockStairs(block1.getBlockData().set(BlockWood.VARIANT, BlockWood.EnumLogVariant.DARK_OAK))).c("stairsWoodDarkOak"));
        a(165, "slime", (new BlockSlime()).c("slime").a(q));
        a(166, "barrier", (new BlockBarrier()).c("barrier"));
        a(167, "iron_trapdoor", (new BlockTrapdoor(Material.ORE)).c(5.0F).a(j).c("ironTrapdoor").K());
        a(168, "prismarine", (new BlockPrismarine()).c(1.5F).b(10.0F).a(Block.i).c("prismarine"));
        a(169, "sea_lantern", (new BlockSeaLantern(Material.SHATTERABLE)).c(0.3F).a(k).a(1.0F).c("seaLantern"));
        a(170, "hay_block", (new BlockHay()).c(0.5F).a(h).c("hayBlock").a(CreativeModeTab.b));
        a(171, "carpet", (new BlockCarpet()).c(0.1F).a(l).c("woolCarpet").e(0));
        a(172, "hardened_clay", (new BlockHardenedClay()).c(1.25F).b(7.0F).a(Block.i).c("clayHardened"));
        a(173, "coal_block", (new Block(Material.STONE, MaterialMapColor.E)).c(5.0F).b(10.0F).a(Block.i).c("blockCoal").a(CreativeModeTab.b));
        a(174, "packed_ice", (new BlockPackedIce()).c(0.5F).a(k).c("icePacked"));
        a(175, "double_plant", new BlockTallPlant());
        a(176, "standing_banner", (new BlockBanner.BlockStandingBanner()).c(1.0F).a(f).c("banner").K());
        a(177, "wall_banner", (new BlockBanner.BlockWallBanner()).c(1.0F).a(f).c("banner").K());
        a(178, "daylight_detector_inverted", new BlockDaylightDetector(true));
        Block block12 = (new BlockRedSandstone()).a(Block.i).c(0.8F).c("redSandStone");
        a(179, "red_sandstone", block12);
        a(180, "red_sandstone_stairs", (new BlockStairs(block12.getBlockData().set(BlockRedSandstone.TYPE, BlockRedSandstone.EnumRedSandstoneVariant.SMOOTH))).c("stairsRedSandStone"));
        a(181, "double_stone_slab2", (new BlockDoubleStoneStep2()).c(2.0F).b(10.0F).a(Block.i).c("stoneSlab2"));
        a(182, "stone_slab2", (new BlockStoneStep2()).c(2.0F).b(10.0F).a(Block.i).c("stoneSlab2"));
        a(183, "spruce_fence_gate", (new BlockFenceGate(BlockWood.EnumLogVariant.SPRUCE)).c(2.0F).b(5.0F).a(f).c("spruceFenceGate"));
        a(184, "birch_fence_gate", (new BlockFenceGate(BlockWood.EnumLogVariant.BIRCH)).c(2.0F).b(5.0F).a(f).c("birchFenceGate"));
        a(185, "jungle_fence_gate", (new BlockFenceGate(BlockWood.EnumLogVariant.JUNGLE)).c(2.0F).b(5.0F).a(f).c("jungleFenceGate"));
        a(186, "dark_oak_fence_gate", (new BlockFenceGate(BlockWood.EnumLogVariant.DARK_OAK)).c(2.0F).b(5.0F).a(f).c("darkOakFenceGate"));
        a(187, "acacia_fence_gate", (new BlockFenceGate(BlockWood.EnumLogVariant.ACACIA)).c(2.0F).b(5.0F).a(f).c("acaciaFenceGate"));
        a(188, "spruce_fence", (new BlockFence(Material.WOOD, BlockWood.EnumLogVariant.SPRUCE.c())).c(2.0F).b(5.0F).a(f).c("spruceFence"));
        a(189, "birch_fence", (new BlockFence(Material.WOOD, BlockWood.EnumLogVariant.BIRCH.c())).c(2.0F).b(5.0F).a(f).c("birchFence"));
        a(190, "jungle_fence", (new BlockFence(Material.WOOD, BlockWood.EnumLogVariant.JUNGLE.c())).c(2.0F).b(5.0F).a(f).c("jungleFence"));
        a(191, "dark_oak_fence", (new BlockFence(Material.WOOD, BlockWood.EnumLogVariant.DARK_OAK.c())).c(2.0F).b(5.0F).a(f).c("darkOakFence"));
        a(192, "acacia_fence", (new BlockFence(Material.WOOD, BlockWood.EnumLogVariant.ACACIA.c())).c(2.0F).b(5.0F).a(f).c("acaciaFence"));
        a(193, "spruce_door", (new BlockDoor(Material.WOOD)).c(3.0F).a(f).c("doorSpruce").K());
        a(194, "birch_door", (new BlockDoor(Material.WOOD)).c(3.0F).a(f).c("doorBirch").K());
        a(195, "jungle_door", (new BlockDoor(Material.WOOD)).c(3.0F).a(f).c("doorJungle").K());
        a(196, "acacia_door", (new BlockDoor(Material.WOOD)).c(3.0F).a(f).c("doorAcacia").K());
        a(197, "dark_oak_door", (new BlockDoor(Material.WOOD)).c(3.0F).a(f).c("doorDarkOak").K());
        REGISTRY.a();
        Iterator<Block> iterator = REGISTRY.iterator();
        while (iterator.hasNext()) {
            Block block13 = iterator.next();
            if (block13.material == Material.AIR) {
                block13.v = false;
                continue;
            }
            block13.v = block13 instanceof BlockStairs || block13 instanceof BlockStepAbstract || block13 == block6 || block13.t || block13.s == 0;
        }
        iterator = REGISTRY.iterator();
        while (iterator.hasNext()) {
            Block block13 = iterator.next();
            for (IBlockData iblockdata : block13.P().a()) {
                d.a(iblockdata, REGISTRY.b(block13) << 4 | block13.toLegacyData(iblockdata));
            }
        }
    }

    private static void a(int i, MinecraftKey minecraftkey, Block block) {
        REGISTRY.a(i, minecraftkey, block);
    }

    private static void a(int i, String s, Block block) {
        a(i, new MinecraftKey(s), block);
    }

    public static float range(float min, float value, float max) {
        if (value < min)
            return min;
        return Math.min(value, max);
    }

    public boolean o() {
        return this.r;
    }

    public int p() {
        return this.s;
    }

    public int r() {
        return this.u;
    }

    public boolean s() {
        return this.v;
    }

    public Material getMaterial() {
        return this.material;
    }

    public MaterialMapColor g(IBlockData iblockdata) {
        return this.K;
    }

    public IBlockData fromLegacyData(int i) {
        return getBlockData();
    }

    public int toLegacyData(IBlockData iblockdata) {
        return 0;
    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata;
    }

    protected Block a(StepSound block_stepsound) {
        this.stepSound = block_stepsound;
        return this;
    }

    protected Block e(int i) {
        this.s = i;
        return this;
    }

    protected Block a(float f) {
        this.u = (int) (15.0F * f);
        return this;
    }

    protected Block b(float f) {
        this.durability = f * 3.0F;
        return this;
    }

    public boolean u() {
        return (this.material.isSolid() && d());
    }

    public boolean isOccluding() {
        return (this.material.k() && d() && !isPowerSource());
    }

    public boolean w() {
        return (this.material.isSolid() && d());
    }

    public boolean d() {
        return true;
    }

    public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return !this.material.isSolid();
    }

    public int b() {
        return 3;
    }

    public boolean a(World world, BlockPosition blockposition) {
        return false;
    }

    protected Block c(float f) {
        this.strength = f;
        if (this.durability < f * 5.0F)
            this.durability = f * 5.0F;
        return this;
    }

    protected Block x() {
        c(-1.0F);
        return this;
    }

    public float g(World world, BlockPosition blockposition) {
        return this.strength;
    }

    protected Block a(boolean flag) {
        this.z = flag;
        return this;
    }

    public boolean isTicking() {
        return this.z;
    }

    public boolean isTileEntity() {
        return this.isTileEntity;
    }

    protected final void a(float f, float f1, float f2, float f3, float f4, float f5) {
        this.minX = f;
        this.minY = f1;
        this.minZ = f2;
        this.maxX = f3;
        this.maxY = f4;
        this.maxZ = f5;
    }

    public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return iblockaccess.getType(blockposition).getBlock().getMaterial().isBuildable();
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, Entity entity) {
        AxisAlignedBB axisalignedbb1 = a(world, blockposition, iblockdata);
        if (axisalignedbb1 != null && axisalignedbb.b(axisalignedbb1))
            list.add(axisalignedbb1);
    }

    public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return new AxisAlignedBB(blockposition.getX() + this.minX, blockposition.getY() + this.minY, blockposition.getZ() + this.minZ, blockposition.getX() + this.maxX, blockposition.getY() + this.maxY, blockposition.getZ() + this.maxZ);
    }

    public boolean c() {
        return true;
    }

    public boolean a(IBlockData iblockdata, boolean flag) {
        return A();
    }

    public boolean A() {
        return true;
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        b(world, blockposition, iblockdata, random);
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
    }

    public void b(World world, Chunk chunk, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        b(world, blockposition, iblockdata, random);
    }

    public void postBreak(World world, BlockPosition blockposition, IBlockData iblockdata) {
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
    }

    public int a(World world) {
        return 10;
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        AsyncCatcher.catchOp("block onPlace");
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        AsyncCatcher.catchOp("block remove");
    }

    public int a(Random random) {
        return 1;
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Item.getItemOf(this);
    }

    public float getDamage(EntityHuman entityhuman, World world, BlockPosition blockposition) {
        float f = g(world, blockposition);
        return (f < 0.0F) ? 0.0F : (!entityhuman.b(this) ? (entityhuman.a(this) / f / 100.0F) : (entityhuman.a(this) / f / 30.0F));
    }

    public final void b(World world, BlockPosition blockposition, IBlockData iblockdata, int i) {
        dropNaturally(world, blockposition, iblockdata, 1.0F, i);
    }

    public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
        int j = getDropCount(i, world.random);
        for (int k = 0; k < j; k++) {
            if (world.random.nextFloat() < f) {
                Item item = getDropType(iblockdata, world.random, i);
                if (item != null)
                    a(world, blockposition, new ItemStack(item, 1, getDropData(iblockdata)));
            }
        }
    }

    protected void dropExperience(World world, BlockPosition blockposition, int i) {
        while (i > 0) {
            int j = EntityExperienceOrb.getOrbValue(i);
            i -= j;
            world.addEntity(new EntityExperienceOrb(world, blockposition.getX() + 0.5D, blockposition.getY() + 0.5D, blockposition.getZ() + 0.5D, j));
        }
    }

    public int getDropData(IBlockData iblockdata) {
        return 0;
    }

    public float a(Entity entity) {
        return this.durability / 5.0F;
    }

    public MovingObjectPosition a(World world, BlockPosition blockposition, Vec3D vec3d, Vec3D vec3d1) {
        updateShape(world, blockposition);
        vec3d = vec3d.add(-blockposition.getX(), -blockposition.getY(), -blockposition.getZ());
        vec3d1 = vec3d1.add(-blockposition.getX(), -blockposition.getY(), -blockposition.getZ());
        Vec3D vec3d2 = vec3d.a(vec3d1, this.minX);
        Vec3D vec3d3 = vec3d.a(vec3d1, this.maxX);
        Vec3D vec3d4 = vec3d.b(vec3d1, this.minY);
        Vec3D vec3d5 = vec3d.b(vec3d1, this.maxY);
        Vec3D vec3d6 = vec3d.c(vec3d1, this.minZ);
        Vec3D vec3d7 = vec3d.c(vec3d1, this.maxZ);
        if (!a(vec3d2))
            vec3d2 = null;
        if (!a(vec3d3))
            vec3d3 = null;
        if (!b(vec3d4))
            vec3d4 = null;
        if (!b(vec3d5))
            vec3d5 = null;
        if (!c(vec3d6))
            vec3d6 = null;
        if (!c(vec3d7))
            vec3d7 = null;
        Vec3D vec3d8 = null;
        if (vec3d2 != null)
            vec3d8 = vec3d2;
        if (vec3d3 != null && (vec3d8 == null || vec3d.distanceSquared(vec3d3) < vec3d.distanceSquared(vec3d8)))
            vec3d8 = vec3d3;
        if (vec3d4 != null && (vec3d8 == null || vec3d.distanceSquared(vec3d4) < vec3d.distanceSquared(vec3d8)))
            vec3d8 = vec3d4;
        if (vec3d5 != null && (vec3d8 == null || vec3d.distanceSquared(vec3d5) < vec3d.distanceSquared(vec3d8)))
            vec3d8 = vec3d5;
        if (vec3d6 != null && (vec3d8 == null || vec3d.distanceSquared(vec3d6) < vec3d.distanceSquared(vec3d8)))
            vec3d8 = vec3d6;
        if (vec3d7 != null && (vec3d8 == null || vec3d.distanceSquared(vec3d7) < vec3d.distanceSquared(vec3d8)))
            vec3d8 = vec3d7;
        if (vec3d8 == null)
            return null;
        EnumDirection enumdirection = null;
        if (vec3d8 == vec3d2)
            enumdirection = EnumDirection.WEST;
        if (vec3d8 == vec3d3)
            enumdirection = EnumDirection.EAST;
        if (vec3d8 == vec3d4)
            enumdirection = EnumDirection.DOWN;
        if (vec3d8 == vec3d5)
            enumdirection = EnumDirection.UP;
        if (vec3d8 == vec3d6)
            enumdirection = EnumDirection.NORTH;
        if (vec3d8 == vec3d7)
            enumdirection = EnumDirection.SOUTH;
        return new MovingObjectPosition(vec3d8.add(blockposition.getX(), blockposition.getY(), blockposition.getZ()), enumdirection, blockposition);
    }

    private boolean a(Vec3D vec3d) {
        return vec3d != null && (vec3d.b >= this.minY && vec3d.b <= this.maxY && vec3d.c >= this.minZ && vec3d.c <= this.maxZ);
    }

    private boolean b(Vec3D vec3d) {
        return vec3d != null && (vec3d.a >= this.minX && vec3d.a <= this.maxX && vec3d.c >= this.minZ && vec3d.c <= this.maxZ);
    }

    private boolean c(Vec3D vec3d) {
        return vec3d != null && (vec3d.a >= this.minX && vec3d.a <= this.maxX && vec3d.b >= this.minY && vec3d.b <= this.maxY);
    }

    public void wasExploded(World world, BlockPosition blockposition, Explosion explosion) {
    }

    public boolean canPlace(World world, BlockPosition blockposition, EnumDirection enumdirection, ItemStack itemstack) {
        return canPlace(world, blockposition, enumdirection);
    }

    public boolean canPlace(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        return canPlace(world, blockposition);
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return (world.getType(blockposition).getBlock()).material.isReplaceable();
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2) {
        return false;
    }

    public void a(World world, BlockPosition blockposition, Entity entity) {
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return fromLegacyData(i);
    }

    public void attack(World world, BlockPosition blockposition, EntityHuman entityhuman) {
    }

    public Vec3D a(World world, BlockPosition blockposition, Entity entity, Vec3D vec3d) {
        return vec3d;
    }

    public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
    }

    public final double B() {
        return this.minX;
    }

    public final double C() {
        return this.maxX;
    }

    public final double D() {
        return this.minY;
    }

    public final double E() {
        return this.maxY;
    }

    public final double F() {
        return this.minZ;
    }

    public final double G() {
        return this.maxZ;
    }

    public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
        return 0;
    }

    public boolean isPowerSource() {
        return false;
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
    }

    public int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
        return 0;
    }

    public void j() {
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, TileEntity tileentity) {
        entityhuman.applyExhaustion(world.paperSpigotConfig.blockBreakExhaustion);
        if (I() && EnchantmentManager.hasSilkTouchEnchantment(entityhuman)) {
            ItemStack itemstack = i(iblockdata);
            if (itemstack != null)
                a(world, blockposition, itemstack);
        } else {
            b(world, blockposition, iblockdata, EnchantmentManager.getBonusBlockLootEnchantmentLevel(entityhuman));
        }
    }

    protected boolean I() {
        return (d() && !this.isTileEntity);
    }

    protected ItemStack i(IBlockData iblockdata) {
        int i = 0;
        Item item = Item.getItemOf(this);
        if (item != null && item.k())
            i = toLegacyData(iblockdata);
        return new ItemStack(item, 1, i);
    }

    public int getDropCount(int i, Random random) {
        return a(random);
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
    }

    public boolean g() {
        return (!this.material.isBuildable() && !this.material.isLiquid());
    }

    public Block c(String s) {
        this.name = s;
        return this;
    }

    public String getName() {
        return LocaleI18n.get(a() + ".name");
    }

    public String a() {
        return "tile." + this.name;
    }

    public boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, int i, int j) {
        return false;
    }

    public boolean J() {
        return this.y;
    }

    protected Block K() {
        this.y = false;
        return this;
    }

    public int k() {
        return this.material.getPushReaction();
    }

    public void fallOn(World world, BlockPosition blockposition, Entity entity, float f) {
        entity.e(f, 1.0F);
    }

    public void a(World world, Entity entity) {
        entity.motY = 0.0D;
    }

    public int getDropData(World world, BlockPosition blockposition) {
        return getDropData(world.getType(blockposition));
    }

    public Block a(CreativeModeTab creativemodetab) {
        return this;
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
    }

    public void k(World world, BlockPosition blockposition) {
    }

    public boolean N() {
        return true;
    }

    public boolean a(Explosion explosion) {
        return true;
    }

    public boolean b(Block block) {
        return (this == block);
    }

    public boolean isComplexRedstone() {
        return false;
    }

    public int l(World world, BlockPosition blockposition) {
        return 0;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this);
    }

    public BlockStateList P() {
        return this.blockStateList;
    }

    protected final void j(IBlockData iblockdata) {
        this.blockData = iblockdata;
    }

    public final IBlockData getBlockData() {
        return this.blockData;
    }

    public String toString() {
        return "Block{" + REGISTRY.c(this) + "}";
    }

    public int getExpDrop(World world, IBlockData data, int enchantmentLevel) {
        return 0;
    }

    public static class StepSound {
        public final String a;

        public final float b;

        public final float c;

        public StepSound(String s, float f, float f1) {
            this.a = s;
            this.b = f;
            this.c = f1;
        }

        public float getVolume1() {
            return this.b;
        }

        public float getVolume2() {
            return this.c;
        }

        public String getBreakSound() {
            return "dig." + this.a;
        }

        public String getStepSound() {
            return "step." + this.a;
        }

        public String getPlaceSound() {
            return getBreakSound();
        }
    }
}
