package dev.razorni.hcfactions.listeners.type;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.teams.claims.Claim;
import dev.razorni.hcfactions.teams.player.Member;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.utils.Crowbar;
import dev.razorni.hcfactions.utils.ItemBuilder2;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class CrowbarListener implements Listener {

    private final HCF plugin;

    public CrowbarListener(HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasItem()) {
            Optional<Crowbar> crowbarOptional = Crowbar.fromStack(event.getItem());
            if (crowbarOptional.isPresent()) {
                event.setCancelled(true);

                Player player = event.getPlayer();
                World world = player.getWorld();
                if (world.getEnvironment() != World.Environment.NORMAL) {
                    player.sendMessage(ChatColor.RED + "Crowbars can only be used in the overworld");
                    return;
                }

                Block block = event.getClickedBlock();
                Location blockLocation = block.getLocation();
                if (!HCF.getPlugin().getTeamManager().canBuild(player, blockLocation)) {
                    player.sendMessage(ChatColor.YELLOW + "You cannot do this in this territory" + ChatColor.YELLOW + '.');
                    return;
                }

                int remainingUses;
                Crowbar crowbar = crowbarOptional.get();
                BlockState blockState = block.getState();
                if (blockState instanceof CreatureSpawner) {
                    remainingUses = crowbar.getSpawnerUses();
                    if (remainingUses <= 0) {
                        player.sendMessage(ChatColor.RED + "This crowbar has no more spawner uses.");
                        return;
                    }

                    crowbar.setSpawnerUses(remainingUses - 1);
                    player.setItemInHand(crowbar.getItemIfPresent());

                    CreatureSpawner spawner = (CreatureSpawner) blockState;
                    block.setType(Material.AIR);
                    blockState.update();

                    world.dropItemNaturally(blockLocation, new ItemBuilder2(Material.MOB_SPAWNER).
                            displayName(ChatColor.AQUA + "Mob Spawner").data(spawner.getData().getData()).
                            loreLine(ChatColor.YELLOW + "Entity Type: " + ChatColor.AQUA + WordUtils.capitalizeFully(spawner.getSpawnedType().name())).build());
                } else if (block.getType() == Material.ENDER_PORTAL_FRAME) {
                    remainingUses = crowbar.getEndFrameUses();
                    if (remainingUses <= 0) {
                        player.sendMessage(ChatColor.RED + "This crowbar has no more endportal uses.");
                        return;
                    }

                    boolean destroyed = false;
                    int blockX = blockLocation.getBlockX();
                    int blockY = blockLocation.getBlockY();
                    int blockZ = blockLocation.getBlockZ();

                    int searchRadius = 4;
                    for (int x = blockX - searchRadius; x <= blockX + searchRadius; x++) {
                        for (int z = blockZ - searchRadius; z <= blockZ + searchRadius; z++) {
                            Block next = world.getBlockAt(x, blockY, z);
                            if (next.getType() == Material.ENDER_PORTAL) {
                                next.setType(Material.AIR);
                                next.getState().update();
                                destroyed = true;
                            }
                        }
                    }

                    if (destroyed) {
                        PlayerTeam playerFaction = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
                        player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Ender Portal is no longer active");
                        if (playerFaction != null) {
                            boolean informFaction = false;
                            for (Claim claim : playerFaction.getClaims()) {
                                if (claim.contains(blockLocation)) {
                                    informFaction = true;
                                    break;
                                }
                            }

                            if (informFaction) {
                                Member factionMember = playerFaction.getMember(player.getUniqueId());
                                String astrix = factionMember.getAsterisk();
                                playerFaction.broadcast(ChatColor.RED + astrix + " has used a Crowbar de-activating one of the factions' end portals.");
                            }
                        }
                    }

                    crowbar.setEndFrameUses(remainingUses - 1);
                    player.setItemInHand(crowbar.getItemIfPresent());

                    block.setType(Material.AIR);
                    blockState.update();
                    world.dropItemNaturally(blockLocation, new ItemStack(Material.ENDER_PORTAL_FRAME, 1));
                }

                if (event.getItem().getType() == Material.AIR) {
                    player.playSound(blockLocation, Sound.ITEM_BREAK, 1.0F, 1.0F);
                } else {
                    player.playSound(blockLocation, Sound.LEVEL_UP, 1.0F, 1.0F);
                }
            }
        }
    }

}
