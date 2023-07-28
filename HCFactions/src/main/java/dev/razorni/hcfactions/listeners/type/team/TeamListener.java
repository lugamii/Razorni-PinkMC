package dev.razorni.hcfactions.listeners.type.team;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.TeamManager;
import dev.razorni.hcfactions.teams.type.*;
import dev.razorni.hcfactions.utils.CC;
import dev.razorni.hcfactions.utils.ItemUtils;
import dev.razorni.hcfactions.utils.Utils;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TeamListener extends Module<TeamManager> {
    private final List<Material> deniedInteract;
    private PacketPlayOutChat packet;

    public TeamListener(TeamManager manager) {
        super(manager);
        this.deniedInteract = new ArrayList<>();
        this.load();
    }

    @EventHandler
    public void onStickyPistonExtend(BlockPistonExtendEvent event) {
        Block block = event.getBlock();
        Block relative = block.getRelative(event.getDirection(), event.getLength() + 1);
        if (relative.isEmpty() || relative.isLiquid()) {
            Team team1 = this.getManager().getClaimManager().getTeam(relative.getLocation());
            Team team2 = this.getManager().getClaimManager().getTeam(block.getLocation());
            if (team1 != team2) {
                if (team1 instanceof PlayerTeam) {
                    PlayerTeam playerTeam = (PlayerTeam) team1;
                    if (playerTeam.isRaidable()) {
                        return;
                    }
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntity(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(entity.getLocation());
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) {
            return;
        }
        if (team instanceof WarzoneTeam && !this.getConfig().getBoolean("MOB_SPAWN_WARZONE")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        this.checkClaim(event.getPlayer(), event.getTo(), event.getFrom());
    }

    @EventHandler
    public void onFade(BlockFadeEvent event) {
        Team team = this.getManager().getClaimManager().getTeam(event.getBlock().getLocation());
        if (team instanceof WildernessTeam || team instanceof PlayerTeam) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onCitadelPearl(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }
        if (event.getItem() == null) {
            return;
        }
        if (event.getItem().getType() != Material.ENDER_PEARL) {
            return;
        }
        Player player = event.getPlayer();
        Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(player.getLocation());
        if (team instanceof CitadelTeam) {
            event.setCancelled(true);
            player.updateInventory();
            player.sendMessage(this.getLanguageConfig().getString("CITADEL.DENIED_PEARL"));
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(player.getLocation());
        if (team instanceof SafezoneTeam) {
            event.setCancelled(true);
            player.setSaturation(20.0f);
            player.setFoodLevel(20);
        }
    }

    private void load() {
        for (String s : this.getTeamConfig().getStringList("SYSTEM_TEAMS.DENIED_INTERACT")) {
            this.deniedInteract.add(ItemUtils.getMat(s));
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(event.getBlockPlaced().getLocation());
        Location locaton = event.getBlockPlaced().getLocation();
        if (team instanceof WarzoneTeam && event.getBlock().getType() == ItemUtils.getMat("WEB")) {
            WarzoneTeam warzoneTeam = (WarzoneTeam) team;
            if (!warzoneTeam.canBreak(locaton) && this.getConfig().getBoolean("COBWEBS_CONFIG.PLACE_WARZONE")) {
                warzoneTeam.getWarzoneCobwebs().put(locaton, new BukkitRunnable() {
                    public void run() {
                        event.getBlock().setType(event.getBlockPlaced().getType());
                    }
                }.runTaskLater(this.getInstance(), this.getConfig().getInt("COBWEBS_CONFIG.DESPAWN_TIME") * 20L));
            }
            return;
        }
        if (!this.getManager().canBuild(player, locaton)) {
            event.setCancelled(true);
            player.sendMessage(this.getLanguageConfig().getString("TEAM_LISTENER.BLOCK_PLACE").replaceAll("%team%", team.getDisplayName(player)));
        }
    }

    @EventHandler
    public void onCitadelTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(event.getTo());
        if (team instanceof CitadelTeam) {
            event.setCancelled(true);
            this.getInstance().getTimerManager().getEnderpearlTimer().removeTimer(player);
            player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
            player.sendMessage(this.getLanguageConfig().getString("CITADEL.DENIED_TELEPORT"));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player damager = Utils.getDamager(event.getDamager());
        Player damaged = (Player) event.getEntity();
        if (damager == null) {
            return;
        }
        if (damager == damaged && event.getDamager() instanceof EnderPearl) {
            return;
        }
        if (!this.getManager().canHit(damager, damaged, true)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemFrameBreak(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getRemover();
        if (!this.getManager().canBuild(player, event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onStickyPistonRetract(BlockPistonRetractEvent event) {
        if (!event.isSticky()) {
            return;
        }
        Location location = event.getRetractLocation();
        Block block = location.getBlock();
        if (!block.isEmpty() && !block.isLiquid()) {
            Block solidBlock = event.getBlock();
            Team team1 = this.getManager().getClaimManager().getTeam(location);
            Team team2 = this.getManager().getClaimManager().getTeam(solidBlock.getLocation());
            if (team1 != team2) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(event.getBlockClicked().getLocation());
        if (!this.getManager().canBuild(player, event.getBlockClicked().getLocation())) {
            event.setCancelled(true);
            player.sendMessage(this.getLanguageConfig().getString("TEAM_LISTENER.BLOCK_INTERACT").replaceAll("%team%", team.getDisplayName(player)));
        }
    }

    @EventHandler
    public void onEntityBlockChange(EntityChangeBlockEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (!this.getManager().canBuild(player, event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockFromTo(BlockFromToEvent event) {
        Block toBlock = event.getToBlock();
        Block fromBlock = event.getBlock();
        Material fromMaterial = fromBlock.getType();
        Material toMaterial = toBlock.getType();
        Team toTeam = this.getManager().getClaimManager().getTeam(toBlock.getLocation());
        Team fromTeam = this.getManager().getClaimManager().getTeam(fromBlock.getLocation());
        if (!this.getConfig().getBoolean("FIXES.OBSIDIAN_GENS") && (toMaterial == Material.REDSTONE_WIRE || toMaterial == Material.TRIPWIRE) && (fromMaterial == Material.AIR || fromMaterial.name().contains("WATER") || fromMaterial.name().contains("LAVA"))) {
            toBlock.setType(Material.AIR);
        }
        if ((fromMaterial.name().contains("WATER") || fromMaterial.name().contains("LAVA")) && toTeam != fromTeam) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(event.getBlock().getLocation());
        if (!this.getManager().canBuild(player, event.getBlock().getLocation())) {
            event.setCancelled(true);
            player.sendMessage(this.getLanguageConfig().getString("TEAM_LISTENER.BLOCK_DIG").replaceAll("%team%", team.getDisplayName(player)));
        }
    }

    @EventHandler
    public void onPotion(PotionSplashEvent event) {
        if (!Utils.isDebuff(event.getPotion())) {
            return;
        }
        Player player = Utils.getDamager(event.getEntity());
        if (player == null) {
            return;
        }
        if (this.getManager().getClaimManager().getTeam(player.getLocation()) instanceof SafezoneTeam) {
            event.setCancelled(true);
            return;
        }
        for (LivingEntity entity : event.getAffectedEntities()) {
            if (!(entity instanceof Player)) {
                return;
            }
            if (entity == player) {
                return;
            }
            Team team = this.getManager().getClaimManager().getTeam(entity.getLocation());
            if (!(team instanceof SafezoneTeam)) {
                continue;
            }
            event.setIntensity(entity, 0.0);
        }
    }

    @EventHandler
    public void onDecay(LeavesDecayEvent event) {
        Team team = this.getManager().getClaimManager().getTeam(event.getBlock().getLocation());
        if (team instanceof WildernessTeam || team instanceof PlayerTeam) {
            return;
        }
        event.setCancelled(true);
    }

    private void checkClaim(Player player, Location pos1, Location pos2) {
        Team team1 = this.getManager().getClaimManager().getTeam(pos1);
        Team team2 = this.getManager().getClaimManager().getTeam(pos2);
        if (team2 == team1) {
            return;
        }
        for (String s : this.getLanguageConfig().getStringList("TEAM_LISTENER.CLAIM_MESSAGE.MESSAGE")) {
            player.sendMessage(CC.t(s).replaceAll("%to-team%", team1.getDisplayName(player)).replaceAll("%from-team%", team2.getDisplayName(player)).replaceAll("%to-deathban%", team1.isDeathban() ? this.getLanguageConfig().getString("TEAM_LISTENER.CLAIM_MESSAGE.DEATHBAN_FORMAT.DEATHBAN") : this.getLanguageConfig().getString("TEAM_LISTENER.CLAIM_MESSAGE.DEATHBAN_FORMAT.NON_DEATHBAN")).replaceAll("%from-deathban%", team2.isDeathban() ? this.getLanguageConfig().getString("TEAM_LISTENER.CLAIM_MESSAGE.DEATHBAN_FORMAT.DEATHBAN") : this.getLanguageConfig().getString("TEAM_LISTENER.CLAIM_MESSAGE.DEATHBAN_FORMAT.NON_DEATHBAN")));
        }
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eEntering to " + team1.getDisplayName(player) + "§e, Leaving from " + team2.getDisplayName(player) + "\"}"), (byte) 2);
        this.packet = packet;
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @EventHandler
    public void onIgnite(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemFrameHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Hanging)) {
            return;
        }
        Player player = Utils.getDamager(event.getDamager());
        if (player == null) {
            return;
        }
        if (!this.getManager().canBuild(player, event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(event.getBlockClicked().getLocation());
        if (!this.getManager().canBuild(player, event.getBlockClicked().getLocation())) {
            event.setCancelled(true);
            player.sendMessage(this.getLanguageConfig().getString("TEAM_LISTENER.BLOCK_INTERACT").replaceAll("%team%", team.getDisplayName(player)));
        }
    }

    @EventHandler
    public void onForm(BlockFormEvent event) {
        Team team = this.getManager().getClaimManager().getTeam(event.getBlock().getLocation());
        if (team instanceof WildernessTeam || team instanceof PlayerTeam) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemFrameRotate(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Hanging)) {
            return;
        }
        Player player = event.getPlayer();
        if (!this.getManager().canBuild(player, event.getRightClicked().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (!(event.getTarget() instanceof Player)) {
            return;
        }
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        if (event.getReason() != EntityTargetEvent.TargetReason.CLOSEST_PLAYER || event.getReason() != EntityTargetEvent.TargetReason.RANDOM_TARGET) {
            return;
        }
        Player target = (Player) event.getTarget();
        PlayerTeam targetTeam = this.getManager().getByPlayer(target.getUniqueId());
        Team playerTeam = this.getManager().getClaimManager().getTeam(event.getEntity().getLocation());
        if (playerTeam instanceof SafezoneTeam || playerTeam == targetTeam) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemFramePlace(HangingPlaceEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (!this.getManager().canBuild(player, event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!this.deniedInteract.contains(event.getClickedBlock().getType())) {
            return;
        }
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(event.getClickedBlock().getLocation());
        if (team instanceof CitadelTeam) {
            CitadelTeam citadelTeam = (CitadelTeam) team;
            if (citadelTeam.getChests().contains(block.getLocation())) {
                return;
            }
        }
        if (team instanceof WarzoneTeam) {
            WarzoneTeam warzoneTeam = (WarzoneTeam) team;
            if (warzoneTeam.canInteract(block.getLocation())) {
                return;
            }
        }
        if (!this.getManager().canBuild(player, block.getLocation())) {
            event.setCancelled(true);
            if (!(event.getClickedBlock().getType() == Material.CHEST)) {
                player.sendMessage(this.getLanguageConfig().getString("TEAM_LISTENER.BLOCK_INTERACT").replaceAll("%team%", team.getDisplayName(player)));
            }
        }
    }

    @EventHandler
    public void onBurn(BlockBurnEvent event) {
        Team team = this.getManager().getClaimManager().getTeam(event.getBlock().getLocation());
        if (team instanceof WildernessTeam || team instanceof PlayerTeam) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMove(PlayerMoveEvent evnet) {
        if (evnet.isCancelled()) {
            return;
        }
        if (evnet.getFrom().getBlockX() == evnet.getTo().getBlockX() && evnet.getFrom().getBlockZ() == evnet.getTo().getBlockZ()) {
            return;
        }
        this.checkClaim(evnet.getPlayer(), evnet.getTo(), evnet.getFrom());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent evnet) {
        if (!(evnet.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) evnet.getEntity();
        Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(player.getLocation());
        if (team instanceof SafezoneTeam) {
            if (!HCF.getPlugin().getEotwHandler().isEndOfTheWorld()) {
                evnet.setCancelled(true);
            }
        }
    }
}
