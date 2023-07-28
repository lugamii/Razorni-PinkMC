package net.minecraft.server;

import com.mysql.jdbc.StringUtils;
import eu.vortexdev.invictusspigot.config.PearlConfig;
import eu.vortexdev.invictusspigot.util.PearlUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCrossPearlEvent;
import org.bukkit.event.player.PlayerPearlRefundEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Gate;
import org.bukkit.material.Stairs;

import java.util.HashSet;

public class EntityEnderPearl extends EntityProjectile {

    private EntityLiving c;
    private boolean edited, badPearl, diagonalPearl, trapDoorAndFence, skip, hitThruBlock;
    private Location hit;

    public EntityEnderPearl(World world) {
        super(world);
        loadChunks = world.paperSpigotConfig.loadUnloadedEnderPearls;
    }

    public EntityEnderPearl(World world, EntityLiving entityliving) {
        super(world, entityliving);
        c = entityliving;
        loadChunks = world.paperSpigotConfig.loadUnloadedEnderPearls;
    }

    /* "onHit" Method */
    protected void a(MovingObjectPosition moving) {
        EntityLiving entityliving = getShooter();

        if (!skip) {
            BlockPosition pos = moving.a();
            if (pos != null) {
                IBlockData data = world.getType(pos);
                net.minecraft.server.Block nmsBlock = data.getBlock();
                if ((PearlUtil.isPlant(nmsBlock.getName()) && PearlConfig.THRUPLANTS.getBooleanValue())
                        || (nmsBlock.getName().contains("Fence Gate") && data.get(BlockFenceGate.OPEN) && PearlConfig.THRUFENCEGATE.getBooleanValue())
                        || (nmsBlock == Blocks.WEB && PearlConfig.THRUCOBWEB.getBooleanValue())
                        || (nmsBlock == Blocks.TRIPWIRE && PearlConfig.THRUSTRING.getBooleanValue())) {
                    hit = getBukkitEntity().getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()).getLocation();
                    return;
                }
            }
        }

        double damage = PearlConfig.PEARLDAMAGE.getDoubleValue();
        if (damage > 0 && moving.entity != null) {
            if (moving.entity == c)
                return;
            moving.entity.damageEntity(DamageSource.projectile(this, entityliving), 0);
        }

        if (inUnloadedChunk && world.paperSpigotConfig.removeUnloadedEnderPearls)
            die();

        if (entityliving instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entityliving;
            if (entityplayer.playerConnection.networkManager.g() && entityplayer.world == world && !entityplayer.isSleeping()) {
                CraftPlayer player = entityplayer.getBukkitEntity();
                Location pl = player.getLocation();
                Location location = getBukkitEntity().getLocation();
                location.setPitch(pl.getPitch());
                location.setYaw(pl.getYaw());

                if (!skip) {
                    org.bukkit.block.Block block = location.getBlock();
                    String material = block.getType().toString();

                    Material pBlockUp;

                    if (checkForEntity(location, player, moving)) {
                        hit = block.getLocation();
                        return;
                    } else if (
                            PearlUtil.thruDisabledAndThruable(block.getType())

                                    /* Anti TrapDoor Glitch */
                                    || (material.equals("IRON_TRAPDOOR") || material.equals("TRAP_DOOR")
                                    && ((pBlockUp = pl.getBlock().getRelative(BlockFace.UP).getType()) == Material.TRAP_DOOR
                                    || pBlockUp.toString().equals("IRON_TRAPDOOR")))

                                    || material.contains("SKULL")
                                    || (material.contains("FENCE") && !material.contains("GATE"))

                                    || (!checkPearlThru(block, location, player))) {
                        refundPearl(player);
                        die();
                        return;
                    }

                    if (!edited) {
                        if (checkForTrapDoor(location)) {
                            edited = true;
                            PearlUtil.setToCenter(location);
                        } else if (!badPearl &&
                                (PearlConfig.REFUNDRISKYPEARL.getBooleanValue() && !trapDoorAndFence && !checkRisky(location, player, moving))
                                || (PearlConfig.REFUNDIFSOCLOSE.getBooleanValue() && !checkClose(location, player, moving))
                                || (PearlConfig.REFUNDIFSUFFOCATING.getBooleanValue() && PearlUtil.isFullBlock(block))) {
                            refundPearl(player);
                            die();
                            return;
                        }
                    }

                    if (PearlConfig.FIXFENCEGATEGLITCH.getBooleanValue() && !trapDoorAndFence && !checkFenceGate(location, player)) {
                        refundPearl(player);
                        die();
                        return;
                    } else if (PearlConfig.FIXWALLGLITCH.getBooleanValue() && !edited) {
                        PearlUtil.setToCenter(location);
                    }
                }

                PlayerTeleportEvent teleEvent = new PlayerTeleportEvent(player, player.getLocation(), location, PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
                Bukkit.getPluginManager().callEvent(teleEvent);
                if (!teleEvent.isCancelled() && !entityplayer.playerConnection.isDisconnected()) {
                    if (entityplayer.au())
                        entityplayer.mount(null);
                    entityplayer.playerConnection.teleport(teleEvent.getTo());
                    entityplayer.fallDistance = 0;

                    if (damage > 0) {
                        CraftEventFactory.entityDamage = this;
                        entityplayer.damageEntity(DamageSource.FALL, (float) damage);
                        CraftEventFactory.entityDamage = null;
                    }
                }
            }
        }

        for (int i = 0; i < 32; ++i)
            world.addParticle(EnumParticle.PORTAL, locX, locY + random.nextDouble() * 2, locZ, random.nextGaussian(), 0, random.nextGaussian(), EnumParticle.EMPTY_ARRAY);

        die();
    }

    public void refundPearl(Player player) {
        if (PearlConfig.ONGLITCHRETURNPEARL.getBooleanValue()) {
            player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
            player.updateInventory();
        }
        String message = PearlConfig.ONGLITCHMESSAGE.getStringValue();
        String consoleCommand = PearlConfig.ONGLITCHCONSOLECOMMAND.getStringValue();
        if (!StringUtils.isNullOrEmpty(message))
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        if (!StringUtils.isNullOrEmpty(consoleCommand))
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consoleCommand.replace("%player%", player.getName()));

        Bukkit.getPluginManager().callEvent(new PlayerPearlRefundEvent(player));
    }

    /*
     * This check is bullshit.
     *
     * If someone has an idea, how to change it, please, contact me.
     *
     */
    private boolean checkForEntity(Location location, Player player, MovingObjectPosition moving) {
        Entity en = moving.entity;
        if (en != null) {
            Block block = location.getBlock();
            if (en instanceof EntityProjectile || en == c || block.getType().toString().contains("FENCE_GATE"))
                return false;

            Location entityLoc = en.getBukkitEntity().getLocation(), playerLoc = player.getLocation();
            if (entityLoc.distance(playerLoc) <= 0.5 && PearlConfig.GETOUTFROMONEBYONE.getBooleanValue())
                return true;

            if (PearlConfig.BETTERHITDETECTION.getBooleanValue()) {
                BlockFace direction = PearlUtil.direction(location);

                if (PearlUtil.isDiagonal(direction)) {
                    Pair<BlockFace, BlockFace> pair = PearlUtil.getPair(direction);
                    if (PearlUtil.isGood(block.getRelative(pair.getLeft())) && PearlUtil.isGood(block.getRelative(pair.getRight()))) {
                        location.setX(en.locX);
                        location.setZ(en.locZ);
                    }
                    return false;
                }
                Block next = block.getRelative(direction);
                if ((entityLoc.distance(playerLoc) <= 2.1 && !PearlUtil.isFullBlock(block) && PearlUtil.isGood(next) && PearlUtil.isGood(next.getRelative(direction))) || (entityLoc.distance(location) <= 2.05 && !PearlUtil.isFullBlock(block) && PearlUtil.isGood(next))) {
                    location.setX(en.locX);
                    location.setZ(en.locZ);
                }

            }
        }
        return false;
    }

    private boolean checkClose(Location location, Player player, MovingObjectPosition mov) {
        if (mov.entity != null)
            return true;

        Block block = location.getBlock();

        Location pLocation = player.getLocation();
        if (location.distance(pLocation) <= 1.75) {
            Block tBlock = player.getTargetBlock((HashSet<Byte>) null, 1);
            if (PearlUtil.distance(tBlock, pLocation, 1.75) && PearlUtil.thruEnabled(block.getType()) || PearlUtil.thruEnabled(tBlock.getRelative(BlockFace.DOWN).getType())) {
                return true;
            }
        }

        if (!PearlUtil.isFullBlock(block)) {
            double distance = PearlConfig.REFUNDIFSOCLOSEDISTANCE.getDoubleValue();
            BlockFace blockFace = PearlUtil.direction(pLocation);
            Block next;

            return (!PearlUtil.containsE(blockFace) || !PearlUtil.isFullBlock(next = block.getRelative(BlockFace.EAST))
                    || !PearlUtil.distance(next, pLocation, distance))
                    && (!PearlUtil.containsW(blockFace) || !PearlUtil.isFullBlock(next = block.getRelative(BlockFace.WEST))
                    || !PearlUtil.distance(next, pLocation, distance))
                    && (!PearlUtil.containsN(blockFace) || !PearlUtil.isFullBlock(next = block.getRelative(BlockFace.NORTH))
                    || !PearlUtil.distance(next, pLocation, distance))
                    && (!PearlUtil.isFullBlock(next = block.getRelative(BlockFace.SOUTH))
                    || !PearlUtil.distance(next, pLocation, distance));

        }
        return true;
    }

    private boolean checkRisky(Location location, Player player, MovingObjectPosition mov) {
        Block block = location.getBlock();
        String material;
        if (!PearlUtil.isFullBlock(block) && !((material = block.getType().toString()).contains("CHEST") || material.contains("SIGN")) && mov.entity == null) {
            if (PearlUtil.isRiskyBlock(block.getRelative(BlockFace.DOWN)) && PearlUtil.isRiskyBlock(block.getRelative(BlockFace.UP))) {
                BlockFace blockFace = PearlUtil.direction(player.getLocation());
                Block next;

                return (PearlUtil.containsE(blockFace) && !PearlUtil.isFullBlock(next = block.getRelative(BlockFace.EAST)) && !PearlUtil.isChestOrSign(next.getType().toString()) && !PearlUtil.isFullBlock(next.getRelative(BlockFace.UP)))
                        || (PearlUtil.containsW(blockFace) && !PearlUtil.isFullBlock(next = block.getRelative(BlockFace.WEST)) && !PearlUtil.isChestOrSign(next.getType().toString()) && !PearlUtil.isFullBlock(next.getRelative(BlockFace.UP)))
                        || (PearlUtil.containsN(blockFace) && !PearlUtil.isFullBlock(next = block.getRelative(BlockFace.NORTH)) && !PearlUtil.isChestOrSign(next.getType().toString()) && !PearlUtil.isFullBlock(next.getRelative(BlockFace.UP)))
                        || (!PearlUtil.isFullBlock(next = block.getRelative(BlockFace.SOUTH)) && !PearlUtil.isChestOrSign(next.getType().toString()) && !PearlUtil.isFullBlock(next.getRelative(BlockFace.UP)));
            }
        }
        return true;
    }

    private boolean checkFenceGate(Location location, Player p) {
        Block block = location.getBlock();

        if (block.getType().toString().contains("FENCE_GATE")) {
            Block down = block.getRelative(BlockFace.DOWN);
            if (!((Gate) block.getState().getData()).isOpen() && !PearlUtil.isOpenFenceGate(down)) {
                return false;
            } else if (down.getType().toString().contains("CHEST")) {
                return !PearlUtil.distance(down, p.getLocation(), 2) && PearlConfig.CHESTFENCEENABLED.getBooleanValue();
            } else if (down.getType() == Material.HOPPER) {
                return !PearlUtil.distance(down, p.getLocation(), 2) && PearlConfig.HOPPERFENCENABLED.getBooleanValue();
            } else {
                edited = true;
                location.setX(location.getBlockX() + 0.5);
                location.setY(location.getBlockY());
                location.setZ(location.getBlockZ() + 0.5);
                return true;
            }
        }

        return true;
    }

    /*
     * Fix for the fencegate and trapdoor above trapping
     *
     * (Guy can't pearl and hit you at all)
     *
     */
    private boolean checkForTrapDoor(Location location) {
        Block block = location.getBlock();
        BlockFace direction;

        if (!PearlUtil.isFullBlock(block) && !PearlUtil.isDiagonal(direction = PearlUtil.direction(location))) {
            Block next = block.getRelative(direction);
            if (next.getType().toString().contains("FENCE_GATE") && PearlUtil.isTrapDoorOpen(next.getRelative(BlockFace.UP))) {
                PearlUtil.addToLocation(direction, location, 1.0);
                trapDoorAndFence = true;
                return true;
            }
        }

        return false;
    }

    private boolean checkPearlThru(Block bl, Location location, Player player) {
        Block block = getThruBlock(bl, location, player);
        if (block != null) {
            badPearl = true;
            BlockFace direction;

            if (diagonalPearl) {
                direction = PearlUtil.direction(player.getLocation());
                /*
                 * Diagonal Pearl Check
                 *
                 * Checks if the right or left block is diagonable, and changes tp location to
                 * safe air block, if it's not found it refunds the pearl.
                 *
                 */
                Pair<BlockFace, BlockFace> pair = PearlUtil.getPair(direction);
                Location loc = block.getLocation().clone();
                if (!PearlUtil.isFullBlock(block.getRelative(pair.getLeft()))) {
                    if (direction == BlockFace.NORTH_EAST || direction == BlockFace.NORTH_WEST) {
                        location.setZ(loc.add(0, 0, -1).getZ());
                    } else {
                        location.setZ(loc.add(0, 0, 1).getZ());
                    }
                } else if (!PearlUtil.isFullBlock(block.getRelative(pair.getRight()))) {
                    if (direction == BlockFace.SOUTH_EAST || direction == BlockFace.NORTH_EAST) {
                        location.setX(loc.add(1, 0, 0).getX());
                    } else {
                        location.setX(loc.add(-1, 0, 0).getX());
                    }
                }
                location.setX(loc.getBlockX() + 0.5);
                location.setY(loc.getBlockY());
                location.setZ(loc.getBlockZ() + 0.5);
                edited = true;
                return true;

            } else if (!PearlUtil.isDiagonal(direction = PearlUtil.direction(player.getLocation()))) {
                if (PearlUtil.stairs(block.getType()) && PearlUtil.isNotVisible(((Stairs) block.getState().getData()).getFacing(), direction)) {
                    return false;
                }

                boolean hasNext = false;
                Block next = block.getRelative(direction);

                /*
                 * Checks if next block has a wall or invalid block behind, so we don't have to
                 * loop
                 */
                if (!PearlUtil.thruEnabled(next.getType()) && !PearlUtil.isFullBlock(next) || (!badPearl && PearlUtil.isFullBlock(next.getRelative(BlockFace.DOWN)))) {
                    if (PearlConfig.INSTANTLYPASTHRU.getBooleanValue()) {
                        PearlUtil.addToLocation(direction, location, hitThruBlock ? 1.5 : 1);
                    }
                    hasNext = true;
                }

                if (!hasNext) {
                    for (int i = 0; i < PearlConfig.MAXPEARLTHRUPASSBLOCKS.getIntValue() - 1; i++) {
                        Block nextCurrent = block.getRelative(direction);
                        if (PearlUtil.thruEnabled(nextCurrent.getType())) {
                            Block nextNextCurrent;

                            if (!isStairGood(nextCurrent, direction)) {
                                return false;

                                /*
                                 * Checks if next block has a wall or invalid block behind, so we can break the
                                 * loop
                                 */
                            } else if (!PearlUtil.thruEnabled((nextNextCurrent = nextCurrent.getRelative(direction)).getType())) {
                                if (PearlUtil.isFullBlock(nextNextCurrent) || (PearlUtil.isFullBlock(nextNextCurrent.getRelative(BlockFace.DOWN)) && PearlUtil.isFullBlock(nextNextCurrent.getRelative(BlockFace.UP))) || !isStairGood(nextCurrent, direction))
                                    return false;
                                PearlUtil.addToLocation(direction, location, 1);
                                break;
                            }
                            block = nextCurrent;
                            PearlUtil.addToLocation(direction, location, 1);
                        }
                    }
                }

                /* Checks for critblock */
                Block nextUp = block.getRelative(direction).getRelative(BlockFace.UP);
                if (PearlUtil.isTrapDoorOpen(nextUp) || PearlUtil.isCritBlock(nextUp)) {
                    if (!PearlUtil.critblock(block.getType())) {
                        return !PearlConfig.REFUNDONCRITBLOCK.getBooleanValue();
                    } else if (PearlConfig.TALITELEPORT.getBooleanValue()) {
                        location.setY(block.getY() - PearlConfig.TALITELEPORTY.getDoubleValue());
                    } else {
                        if(PearlConfig.INSTANTLYPASTHRU.getBooleanValue()) {
                            location.setY(block.getY() - 0.25);
                        } else {
                            PearlUtil.addToLocation(direction, location, 1);
                        }
                    }
                    PearlUtil.setToCenter(location);
                }
            }

        }
        return true;
    }

    private Block getThruBlock(Block block, Location location, Player player) {
        Location pLoc = player.getLocation();
        if (PearlUtil.distance(block, pLoc, 6)) {

            BlockFace direction = PearlUtil.direction(pLoc);

            if (PearlUtil.thruEnabled(block.getType())) {

                /*
                 * Checks if the right or left block is thruable, and return's it to process the
                 * diagonal check
                 *
                 */
                if (PearlUtil.isDiagonal(direction) && PearlUtil.diagonalPearl(block.getType()) && location.getBlockY() > block.getY()) {
                    Pair<BlockFace, BlockFace> pair = PearlUtil.getPair(direction);
                    if (PearlUtil.thruEnabled(block.getRelative(pair.getLeft().getOppositeFace()).getType()) || PearlUtil.thruEnabled(block.getRelative(pair.getRight().getOppositeFace()).getType())) {
                        diagonalPearl = true;
                    }
                }

                return block;

            } else if (!PearlUtil.isFullBlock(block)) {

                /* Anti CobbleWall & TrapDoor Trapping Check */
                Block targetBlock = player.getTargetBlock((HashSet<Byte>) null, 4);
                BlockFace nextFace = PearlUtil.getNextIfPossible(direction);
                if (targetBlock.getX() != pLoc.getBlockX() || targetBlock.getZ() != pLoc.getBlockZ() && targetBlock.getY() < pLoc.getBlockY() && (PearlUtil.isNeeded(targetBlock.getType().toString()) || (nextFace != null && PearlUtil.isNeeded((targetBlock = targetBlock.getRelative(nextFace).getRelative(BlockFace.DOWN)).getType().toString())))) {
                    if (!PearlUtil.isFullBlock(targetBlock.getRelative(BlockFace.UP))) {
                        Block behindUp = targetBlock.getRelative(direction.getOppositeFace()).getRelative(BlockFace.UP);
                        if (!PearlUtil.isFullBlock(behindUp) && PearlUtil.isFullBlock(behindUp.getRelative(BlockFace.UP))) {
                            edited = badPearl = true;
                            PearlUtil.copyLocation(location, targetBlock.getLocation());
                            location.setYaw(pLoc.getYaw());
                            location.setPitch(pLoc.getPitch());
                            return null;
                        }
                    }
                }

                /*
                 * Hit Block Check
                 *
                 * If pearl logically hits the thru-pearlable block, it returns this block, so
                 * it helps a lot to pearl.
                 *
                 */
                Block down = block.getRelative(BlockFace.DOWN);
                if (!PearlUtil.isFullBlock(down) && PearlConfig.HITTHRUBLOCK.getBooleanValue()) {
                    Block downNext = down.getRelative(direction);
                    if (PearlUtil.thruEnabled(downNext.getType()) && player.getTargetBlock((HashSet<Byte>) null, 2).getType() == downNext.getType() && location.getBlockY() > downNext.getY()) {
                        if (!isStairGood(downNext, direction))
                            return null;
                        PearlUtil.addToLocation(direction, location, 0.5);
                        location.setY(downNext.getY());
                        hitThruBlock = true;
                        return downNext;
                    }
                }

                if (down.getType() != Material.AIR) {
                    /*
                     * Cross pearls check.
                     *
                     * Checks the left and right block if it's type is crosspearlable,
                     *
                     */
                    if (PearlUtil.isDiagonal(direction)) {
                        Pair<BlockFace, BlockFace> pair = PearlUtil.getPair(direction);
                        Block right = block.getRelative(pair.getRight()), left = block.getRelative(pair.getLeft());
                        if (PearlUtil.isAble(right, location) && PearlUtil.crossPearlAndEnabled(right.getType()) && !left.getType().toString().contains("FENCE_GATE")) {
                            PlayerCrossPearlEvent event = new PlayerCrossPearlEvent(player, location, PearlUtil.addToLocation(direction, location.clone(), PearlConfig.CROSSPEARLMOVEHELPER.getDoubleValue()));
                            Bukkit.getServer().getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                PearlUtil.copyLocation(location, event.getTo());
                                edited = true;
                            }
                        } else if (PearlUtil.isAble(left, location) && PearlUtil.crossPearlAndEnabled(left.getType()) && !right.getType().toString().contains("FENCE_GATE")) {
                            PlayerCrossPearlEvent event = new PlayerCrossPearlEvent(player, location, PearlUtil.addToLocation(direction, location.clone(), PearlConfig.CROSSPEARLMOVEHELPER.getDoubleValue()));
                            Bukkit.getServer().getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                PearlUtil.copyLocation(location, event.getTo());
                                edited = true;
                            }
                        }
                        return null;
                    } else if (PearlConfig.PRETHRUBLOCK.getBooleanValue()) {
                        /*
                         * Pre Block Check
                         *
                         * If pearl lands infront of the thru-pearlable block, it returns it.
                         *
                         */
                        Block next = block.getRelative(direction);
                        if (PearlUtil.thruEnabled(next.getType()) && PearlUtil.distance(next, location, 0.75)) {
                            if (!isStairGood(next, direction) || !PearlUtil.isFullBlock(next.getRelative(BlockFace.DOWN)))
                                return null;
                            PearlUtil.addToLocation(direction, location, 0.5);
                            return next;
                        }
                    }
                }
            }
        }
        return null;
    }

    /* Tick Method. */
    public void t_() {
        EntityLiving entityliving = this.getShooter();
        if (entityliving instanceof EntityHuman) {
            if (!entityliving.isAlive()) {
                die();
                return;

                /*
                 * Fix for pearl phasing another blocks, after skipping block like fencegate,
                 * string...
                 *
                 * It's kinda messy, but anyway, it works nice.
                 *
                 */
            } else if (hit != null) {
                Location loc = getBukkitEntity().getLocation();
                Block block = loc.getBlock();
                if (PearlUtil.isUnpassable(block) || loc.getBlockX() == hit.getBlockX() && loc.getBlockZ() == hit.getBlockZ() && hit.getBlockY() != loc.getBlockY() && PearlUtil.isUnpassable(block.getRelative(BlockFace.UP))) {
                    fixLocation();
                    return;
                } else {
                    BlockFace direction = PearlUtil.direction(entityliving.getBukkitEntity().getLocation());
                    if (PearlUtil.isDiagonal(direction)) {
                        Pair<BlockFace, BlockFace> pair = PearlUtil.getPair(direction);
                        Block left = block.getRelative(pair.getLeft().getOppositeFace()), right = block.getRelative(pair.getRight().getOppositeFace()), leftHit, rightHit;
                        if ((PearlUtil.isUnpassable(left) && (PearlUtil.isUnpassable(right)
                                || ((leftHit = hit.getBlock().getRelative(pair.getLeft())).equals(left)
                                || (rightHit = hit.getBlock().getRelative(pair.getRight())).equals(right))

                                || (leftHit.equals(left.getRelative(BlockFace.DOWN))
                                || rightHit.equals(right.getRelative(BlockFace.DOWN)))))

                                || PearlUtil.isUnpassable(block.getRelative(direction.getOppositeFace()))) {
                            fixLocation();
                            return;
                        }
                    } else if (PearlUtil.isFenceGateButClosed(block) || !PearlUtil.isThruable(block.getRelative(direction.getOppositeFace())) || PearlUtil.isThruableAndDisabled(block.getRelative(direction))) {
                        fixLocation();
                        return;
                    }
                }
                hit = null;
            }

        }
        super.t_();
    }

    private void fixLocation() {
        Location location = hit;
        PearlUtil.copyLocation(location, hit);
        PearlUtil.setToCenter(location);
        setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        skip = true;
        MovingObjectPosition mop = new MovingObjectPosition(MovingObjectPosition.EnumMovingObjectType.BLOCK, new Vec3D(location.getX(), location.getY(), location.getZ()), null, null);
        a(mop);
    }

    private boolean isStairGood(Block block, BlockFace direction) {
        if (!PearlUtil.stairs(block.getType()))
            return true;
        boolean good = PearlUtil.isFacingNormal(direction, ((Stairs) block.getState().getData()).getFacing());
        if (!good)
            edited = true;
        return good;
    }
}
