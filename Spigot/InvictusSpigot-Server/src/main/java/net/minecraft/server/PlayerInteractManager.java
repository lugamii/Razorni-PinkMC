package net.minecraft.server;

import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
// CraftBukkit end

public class PlayerInteractManager {

    public World world;
    public EntityPlayer player;
    // CraftBukkit start
    public boolean interactResult = false;
    public boolean firedInteract = false;
    private WorldSettings.EnumGamemode gamemode;
    private boolean d;
    private int lastDigTick;
    private BlockPosition f;
    private int currentTick;
    private boolean h;
    private BlockPosition i;
    private int j;
    private int k;

    public PlayerInteractManager(final World world) {
        this.gamemode = WorldSettings.EnumGamemode.NOT_SET;
        this.f = BlockPosition.ZERO;
        this.i = BlockPosition.ZERO;
        this.k = -1;
        this.world = world;
    }

    public WorldSettings.EnumGamemode getGameMode() {
        return this.gamemode;
    }

    public void setGameMode(final WorldSettings.EnumGamemode worldsettings_enumgamemode) {
        this.gamemode = worldsettings_enumgamemode;
        worldsettings_enumgamemode.a(this.player.abilities);
        this.player.updateAbilities();
        this.player.server.getPlayerList().sendAll(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE, this.player), this.player);
    }

    public boolean c() {
        return this.gamemode.e();
    }

    public boolean isCreative() {
        return this.gamemode.d();
    }

    public void b(final WorldSettings.EnumGamemode worldsettings_enumgamemode) {
        if (this.gamemode == WorldSettings.EnumGamemode.NOT_SET) {
            this.gamemode = worldsettings_enumgamemode;
        }

        this.setGameMode(this.gamemode);
    }

    public void a() {
        this.currentTick = MinecraftServer.currentTick; // CraftBukkit;
        float f;
        int i;

        if (this.h) {
            final int x = this.i.getX(), y = this.i.getY(), z = this.i.getZ();
            final Chunk ch = world.getChunkAtWorldCoords(x, z);
            final IBlockData iblockdata = world.getType(ch, x, y, z, true);
            final Block block = iblockdata.getBlock();

            if (block.getMaterial() == Material.AIR) {
                this.h = false;
            } else {
                f = block.getDamage(this.player, this.player.world, this.i) * (float) ((currentTick - this.j) + 1);
                i = (int) (f * 10.0F);
                if (i != this.k) {
                    this.world.c(this.player.getId(), this.i, i);
                    this.k = i;
                }

                if (f >= 1.0F) {
                    this.h = false;
                    this.breakBlock(ch.bukkitChunk.getBlock(x, y, z), iblockdata, block, ch, this.i);
                }
            }
        } else if (this.d) {
            final int x = this.f.getX(), y = this.f.getY(), z = this.f.getZ();
            final Block block1 = this.world.getType(x, y, z, true).getBlock();

            if (block1.getMaterial() == Material.AIR) {
                this.world.c(this.player.getId(), this.f, -1);
                this.k = -1;
                this.d = false;
            } else {
                f = block1.getDamage(this.player, this.player.world, this.i) * (float) ((this.currentTick - this.lastDigTick) + 1);
                i = (int) (f * 10.0F);
                if (i != this.k) {
                    this.world.c(this.player.getId(), this.f, i);
                    this.k = i;
                }
            }
        }

    }

    public void a(Chunk ch, final BlockPosition blockposition, final EnumDirection enumdirection) {
        // CraftBukkit start

        org.bukkit.block.Block bukkitBlock = ch.bukkitChunk.getBlock(blockposition.getX(), blockposition.getY(), blockposition.getZ());

        final PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(this.player, Action.LEFT_CLICK_BLOCK, bukkitBlock, blockposition, enumdirection, this.player.inventory.getItemInHand(), false);
        IBlockData blockdata = world.getType(ch, blockposition.getX(), blockposition.getY(), blockposition.getZ(), true);

        if (event.isCancelled()) {
            // Let the client know the block still exists
            player.playerConnection.sendPacket(new PacketPlayOutBlockChange(blockposition, blockdata));
            // Update any tile entity data for this block
            final TileEntity tileentity = ((WorldServer)world).getTileEntity(ch, blockposition, blockdata.getBlock());
            if (tileentity != null) {
                this.player.playerConnection.sendPacket(tileentity.getUpdatePacket());
            }
            return;
        }

        Block block = blockdata.getBlock();

        // CraftBukkit end
        if (this.isCreative()) {
            if (!this.world.douseFire(null, blockposition, enumdirection)) {
                this.breakBlock(bukkitBlock, blockdata, block, ch, blockposition);
            }
        } else {

            if (this.gamemode.c()) {
                if (this.gamemode == WorldSettings.EnumGamemode.SPECTATOR) {
                    return;
                }

                if (!this.player.cn()) {
                    final ItemStack itemstack = this.player.bZ();

                    if (itemstack == null || !itemstack.c(block)) {
                        return;
                    }
                }
            }

            // this.world.douseFire((EntityHuman) null, blockposition, enumdirection); //
            // CraftBukkit - Moved down
            this.lastDigTick = this.currentTick;
            float f = 1.0F;

            // CraftBukkit start - Swings at air do *NOT* exist.
            if (event.useInteractedBlock() == Event.Result.DENY) {
                // If we denied a door from opening, we need to send a correcting update to the
                // client, as it already opened the door.
                if (block == Blocks.WOODEN_DOOR) {
                    // For some reason *BOTH* the bottom/top part have to be marked updated.
                    boolean bottom = blockdata.get(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER;
                    player.playerConnection.sendPacket(new PacketPlayOutBlockChange(blockposition, blockdata));
                    player.playerConnection.sendPacket(new PacketPlayOutBlockChange(world, bottom ? blockposition.up() : blockposition.down()));
                } else if (block == Blocks.TRAPDOOR) {
                    player.playerConnection.sendPacket(new PacketPlayOutBlockChange(blockposition, blockdata));
                }
            } else if (block.getMaterial() != Material.AIR) {
                block.attack(this.world, blockposition, this.player);
                f = block.getDamage(this.player, this.player.world, blockposition);
                // Allow fire punching to be blocked
                this.world.douseFire(null, blockposition, enumdirection);
            }

            if (event.useItemInHand() == Event.Result.DENY) {
                // If we 'insta destroyed' then the client needs to be informed.
                if (f >= 1.0f) {
                    player.playerConnection.sendPacket(new PacketPlayOutBlockChange(blockposition, blockdata));
                }
                return;
            }
            org.bukkit.event.block.BlockDamageEvent blockEvent = CraftEventFactory.callBlockDamageEvent(this.player, bukkitBlock, this.player.inventory.getItemInHand(), f >= 1.0f);

            blockdata = world.getType(ch, blockposition.getX(), blockposition.getY(), blockposition.getZ(), true); // after event

            if (blockEvent.isCancelled()) {
                // Let the client know the block still exists
                player.playerConnection.sendPacket(new PacketPlayOutBlockChange(blockposition, blockdata));
                return;
            }

            if (blockEvent.getInstaBreak()) {
                f = 2.0f;
            }
            // CraftBukkit end

            if (block.getMaterial() != Material.AIR && f >= 1.0F) {
                this.breakBlock(bukkitBlock, blockdata, blockdata.getBlock(), ch, blockposition);
            } else {
                this.d = true;
                this.f = blockposition;
                int i = (int) (f * 10.0F);

                this.world.c(this.player.getId(), blockposition, i);
                this.k = i;
            }

        }
        world.spigotConfig.antiXrayInstance.updateNearbyBlocks(world, blockposition); // Spigot
    }

    public void a(Chunk ch, IBlockData iblockdata, final BlockPosition blockposition) {
        if (blockposition.equals(this.f)) {
            this.currentTick = MinecraftServer.currentTick; // CraftBukkit
            final Block block = iblockdata.getBlock();
            if (block.getMaterial() != Material.AIR) {
                if (block.getDamage(this.player, this.player.world, blockposition) * ((currentTick - lastDigTick) + 1) >= 0.7F) {
                    this.d = false;
                    this.world.c(this.player.getId(), blockposition, -1);
                    this.breakBlock(ch.bukkitChunk.getBlock(blockposition.getX(), blockposition.getY(), blockposition.getZ()), iblockdata, block, ch, blockposition);
                } else if (!this.h) {
                    this.d = false;
                    this.h = true;
                    this.i = blockposition;
                    this.j = this.lastDigTick;
                }
            }
            // CraftBukkit start - Force block reset to client
        } else {
            this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(blockposition, iblockdata));
            // CraftBukkit end
        }

    }

    public void e() {
        this.d = false;
        this.world.c(this.player.getId(), this.f, -1);
    }

    private boolean c(Chunk chunk, IBlockData iblockdata, BlockPosition blockposition) {
        iblockdata.getBlock().a(this.world, blockposition, iblockdata, this.player);
        boolean flag = this.world.setTypeAndDataWithChunk(chunk, blockposition, Blocks.AIR.getBlockData(), 3, true, false);
        if (flag)
            iblockdata.getBlock().postBreak(this.world, blockposition, iblockdata);
        return flag;
    }

    public boolean breakBlock(org.bukkit.block.Block bukkitBlock, IBlockData nmsData, Block nmsBlock, Chunk ch, final BlockPosition blockposition) {

        boolean isSwordNoBreak = this.gamemode.d() && this.player.bA() != null && this.player.bA().getItem() instanceof ItemSword;

        if (((WorldServer)world).getTileEntity(ch, blockposition, nmsBlock) == null && !isSwordNoBreak) {
            this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(blockposition, Blocks.AIR.getBlockData()));
        }

        BlockBreakEvent event = new BlockBreakEvent(bukkitBlock, this.player.getBukkitEntity());
        // Sword + Creative mode pre-cancel
        event.setCancelled(isSwordNoBreak);

        if (!event.isCancelled() && !this.isCreative() && this.player.b(nmsBlock)) {
            // Copied from block.a(World world, EntityHuman entityhuman, BlockPosition
            // blockposition, IBlockData iblockdata, TileEntity tileentity)
            if (!(nmsBlock.I() && EnchantmentManager.hasSilkTouchEnchantment(this.player))) {
                event.setExpToDrop(nmsBlock.getExpDrop(this.world, nmsData, EnchantmentManager.getBonusBlockLootEnchantmentLevel(this.player)));
            }
        }

        this.world.getServer().getPluginManager().callEvent(event);

        nmsData = this.world.getType(ch, blockposition.getX(), blockposition.getY(), blockposition.getZ(), true);
        nmsBlock = nmsData.getBlock();

        if (event.isCancelled()) {
            if (isSwordNoBreak)
                return false;
            player.playerConnection.sendPacket(new PacketPlayOutBlockChange(blockposition, nmsData));
            TileEntity tileEntity = ((WorldServer)world).getTileEntity(ch, blockposition, nmsBlock);
            if (tileEntity != null)
                player.playerConnection.sendPacket(tileEntity.getUpdatePacket());
            return false;
        }

        if (nmsBlock == Blocks.AIR) return false;
        // Special case skulls, their item data comes from a tile entity

        if (nmsBlock == Blocks.SKULL && !this.isCreative()) {
            nmsBlock.dropNaturally(world, blockposition, nmsData, 1.0F, 0);
            return this.c(ch, nmsData, blockposition);
        }

        if (this.gamemode.c()) {
            if (this.gamemode == WorldSettings.EnumGamemode.SPECTATOR) {
                return false;
            }

            if (!this.player.cn()) {
                ItemStack itemstack = this.player.bZ();
                if (itemstack == null || !itemstack.c(nmsBlock)) {
                    return false;
                }
            }
        }

        this.world.a(this.player, 2001, blockposition, Block.getCombinedId(nmsData));
        boolean flag = c(ch, nmsData, blockposition);
        if (isCreative()) {
            this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(this.world, blockposition));
        } else {
            ItemStack itemstack1 = this.player.bZ();
            if (itemstack1 != null) {
                itemstack1.a(this.world, nmsBlock, blockposition, this.player);
                if (itemstack1.count == 0)
                    this.player.ca();
            }
            if (flag && player.b(nmsBlock)) {
                nmsBlock.a(this.world, this.player, blockposition, nmsData, world.getTileEntity(ch, blockposition));
            }
        }
        if (flag)
            nmsBlock.dropExperience(this.world, blockposition, event.getExpToDrop());
        return flag;
    }

    public boolean useItem(final EntityHuman entityhuman, final World world, final ItemStack itemstack) {
        if (this.gamemode == WorldSettings.EnumGamemode.SPECTATOR) {
            return false;
        } else {
            int i = itemstack.count;
            int j = itemstack.getData();
            final ItemStack itemstack1 = itemstack.a(world, entityhuman);

            if (itemstack1 == itemstack && itemstack1.count == i && itemstack1.l() <= 0 && itemstack1.getData() == j) {
                return false;
            } else {
                entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = itemstack1;
                if (this.isCreative()) {
                    itemstack1.count = i;
                    if (itemstack1.e()) {
                        itemstack1.setData(j);
                    }
                }

                if (itemstack1.count == 0) {
                    entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = null;
                }

                if (!entityhuman.bS()) {
                    ((EntityPlayer) entityhuman).updateInventory(entityhuman.defaultContainer);
                }

                return true;
            }
        }
    }
    // CraftBukkit end

    public boolean interact(final EntityHuman entityhuman, final World world, final ItemStack itemstack, final BlockPosition blockposition, final EnumDirection enumdirection, final float f, final float f1, final float f2) {
        Chunk ch = world.getChunkAtWorldCoords(blockposition);
        final IBlockData blockdata = world.getType(ch, blockposition.getX(), blockposition.getY(), blockposition.getZ(), true);
        final Block block = blockdata.getBlock();
        boolean result = false;
        if (block != Blocks.AIR) {
            boolean cancelledBlock = false;
            TileEntity tileentity = ((WorldServer)world).getTileEntity(ch, blockposition, block);
            if (this.gamemode == WorldSettings.EnumGamemode.SPECTATOR) {
                cancelledBlock = !(tileentity instanceof IInventory);
            }

            if (!entityhuman.getBukkitEntity().isOp() && itemstack != null
                    && Block.asBlock(itemstack.getItem()) instanceof BlockCommand) {
                cancelledBlock = true;
            }

            final PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(entityhuman, Action.RIGHT_CLICK_BLOCK, ch.bukkitChunk.getBlock(blockposition.getX(), blockposition.getY(), blockposition.getZ()), blockposition, enumdirection, itemstack, cancelledBlock);
            firedInteract = true;
            interactResult = event.useItemInHand() == Event.Result.DENY;

            if (event.useInteractedBlock() == Event.Result.DENY) {
                // If we denied a door from opening, we need to send a correcting update to the
                // client, as it already opened the door.
                if (block instanceof BlockDoor) {
                    boolean bottom = blockdata.get(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER;
                    ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutBlockChange(world, bottom ? blockposition.up() : blockposition.down()));
                }
                result = (event.useItemInHand() != Event.Result.ALLOW);
            } else if (this.gamemode == WorldSettings.EnumGamemode.SPECTATOR) {
                if (tileentity instanceof ITileInventory) {
                    ITileInventory itileinventory = (ITileInventory) tileentity;

                    if (itileinventory instanceof TileEntityChest && block instanceof BlockChest) {
                        itileinventory = ((BlockChest) block).f(world, blockposition);
                    }

                    if (itileinventory != null) {
                        entityhuman.openContainer(itileinventory);
                        return true;
                    }
                } else if (tileentity instanceof IInventory) {
                    entityhuman.openContainer((IInventory) tileentity);
                    return true;
                }

                return false;
            } else if (!entityhuman.isSneaking() || itemstack == null) {
                result = block.interact(world, blockposition, blockdata, entityhuman, enumdirection, f, f1, f2);
            }

            if (itemstack != null && !result && !interactResult) { // add !interactResult SPIGOT-764
                final int j1 = itemstack.getData();
                final int k1 = itemstack.count;
                // invictusspigot start
                try {
                    world.interceptSounds();
                    result = itemstack.placeItem(entityhuman, world, blockposition, enumdirection, f, f1, f2);
                } finally {
                    if (result) {
                        world.sendInterceptedSounds();
                    } else {
                        world.clearInterceptedSounds();
                    }
                }
                // The item count should not decrement in Creative mode.
                if (this.isCreative()) {
                    itemstack.setData(j1);
                    itemstack.count = k1;
                }
            }
        }
        return result;
        // CraftBukkit end
    }

    public void a(final WorldServer worldserver) {
        this.world = worldserver;
    }
}