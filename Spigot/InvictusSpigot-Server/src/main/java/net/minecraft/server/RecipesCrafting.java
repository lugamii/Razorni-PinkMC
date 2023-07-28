package net.minecraft.server;

public class RecipesCrafting {
    public RecipesCrafting() {
    }

    public void a(CraftingManager var1) {
        var1.registerShapedRecipe(new ItemStack(Blocks.CHEST), new Object[]{"###", "# #", "###", '#', Blocks.PLANKS});
        var1.registerShapedRecipe(new ItemStack(Blocks.TRAPPED_CHEST), new Object[]{"#-", '#', Blocks.CHEST, '-', Blocks.TRIPWIRE_HOOK});
        var1.registerShapedRecipe(new ItemStack(Blocks.ENDER_CHEST), new Object[]{"###", "#E#", "###", '#', Blocks.OBSIDIAN, 'E', Items.ENDER_EYE});
        var1.registerShapedRecipe(new ItemStack(Blocks.FURNACE), new Object[]{"###", "# #", "###", '#', Blocks.COBBLESTONE});
        var1.registerShapedRecipe(new ItemStack(Blocks.CRAFTING_TABLE), new Object[]{"##", "##", '#', Blocks.PLANKS});
        var1.registerShapedRecipe(new ItemStack(Blocks.SANDSTONE), new Object[]{"##", "##", '#', new ItemStack(Blocks.SAND, 1, BlockSand.EnumSandVariant.SAND.a())});
        var1.registerShapedRecipe(new ItemStack(Blocks.RED_SANDSTONE), new Object[]{"##", "##", '#', new ItemStack(Blocks.SAND, 1, BlockSand.EnumSandVariant.RED_SAND.a())});
        var1.registerShapedRecipe(new ItemStack(Blocks.STONEBRICK, 4), new Object[]{"##", "##", '#', new ItemStack(Blocks.STONE, 1, BlockStone.EnumStoneVariant.STONE.a())});
        var1.registerShapedRecipe(new ItemStack(Blocks.STONEBRICK, 1, BlockSmoothBrick.P), new Object[]{"#", "#", '#', new ItemStack(Blocks.STONE_SLAB, 1, BlockDoubleStepAbstract.EnumStoneSlabVariant.SMOOTHBRICK.a())});
        var1.registerShapelessRecipe(new ItemStack(Blocks.STONEBRICK, 1, BlockSmoothBrick.N), new Object[]{Blocks.STONEBRICK, Blocks.VINE});
        var1.registerShapelessRecipe(new ItemStack(Blocks.MOSSY_COBBLESTONE, 1), new Object[]{Blocks.COBBLESTONE, Blocks.VINE});
        var1.registerShapedRecipe(new ItemStack(Blocks.IRON_BARS, 16), new Object[]{"###", "###", '#', Items.IRON_INGOT});
        var1.registerShapedRecipe(new ItemStack(Blocks.GLASS_PANE, 16), new Object[]{"###", "###", '#', Blocks.GLASS});
        var1.registerShapedRecipe(new ItemStack(Blocks.REDSTONE_LAMP, 1), new Object[]{" R ", "RGR", " R ", 'R', Items.REDSTONE, 'G', Blocks.GLOWSTONE});
        var1.registerShapedRecipe(new ItemStack(Blocks.BEACON, 1), new Object[]{"GGG", "GSG", "OOO", 'G', Blocks.GLASS, 'S', Items.NETHER_STAR, 'O', Blocks.OBSIDIAN});
        var1.registerShapedRecipe(new ItemStack(Blocks.NETHER_BRICK, 1), new Object[]{"NN", "NN", 'N', Items.NETHERBRICK});
        var1.registerShapedRecipe(new ItemStack(Blocks.STONE, 2, BlockStone.EnumStoneVariant.DIORITE.a()), new Object[]{"CQ", "QC", 'C', Blocks.COBBLESTONE, 'Q', Items.QUARTZ});
        var1.registerShapelessRecipe(new ItemStack(Blocks.STONE, 1, BlockStone.EnumStoneVariant.GRANITE.a()), new Object[]{new ItemStack(Blocks.STONE, 1, BlockStone.EnumStoneVariant.DIORITE.a()), Items.QUARTZ});
        var1.registerShapelessRecipe(new ItemStack(Blocks.STONE, 2, BlockStone.EnumStoneVariant.ANDESITE.a()), new Object[]{new ItemStack(Blocks.STONE, 1, BlockStone.EnumStoneVariant.DIORITE.a()), Blocks.COBBLESTONE});
        var1.registerShapedRecipe(new ItemStack(Blocks.DIRT, 4, BlockDirt.EnumDirtVariant.COARSE_DIRT.a()), new Object[]{"DG", "GD", 'D', new ItemStack(Blocks.DIRT, 1, BlockDirt.EnumDirtVariant.DIRT.a()), 'G', Blocks.GRAVEL});
        var1.registerShapedRecipe(new ItemStack(Blocks.STONE, 4, BlockStone.EnumStoneVariant.DIORITE_SMOOTH.a()), new Object[]{"SS", "SS", 'S', new ItemStack(Blocks.STONE, 1, BlockStone.EnumStoneVariant.DIORITE.a())});
        var1.registerShapedRecipe(new ItemStack(Blocks.STONE, 4, BlockStone.EnumStoneVariant.GRANITE_SMOOTH.a()), new Object[]{"SS", "SS", 'S', new ItemStack(Blocks.STONE, 1, BlockStone.EnumStoneVariant.GRANITE.a())});
        var1.registerShapedRecipe(new ItemStack(Blocks.STONE, 4, BlockStone.EnumStoneVariant.ANDESITE_SMOOTH.a()), new Object[]{"SS", "SS", 'S', new ItemStack(Blocks.STONE, 1, BlockStone.EnumStoneVariant.ANDESITE.a())});
    }
}
