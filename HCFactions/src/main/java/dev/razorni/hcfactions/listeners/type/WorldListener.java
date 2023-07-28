package dev.razorni.hcfactions.listeners.type;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.listeners.ListenerManager;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.type.SafezoneTeam;
import dev.razorni.hcfactions.utils.Tasks;
import dev.razorni.hcfactions.utils.menuapi.CC;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class WorldListener extends Module<ListenerManager> {

    public WorldListener(ListenerManager manager) {
        super(manager);
        this.load();
    }

    private String[] lines = new String[] { CC.translate(" "), CC.translate("&6Refill"), CC.translate("&6&l┃ &fYour Inventory"), CC.translate(" ")};

    private String[] repairlines = new String[] { CC.translate(" "), CC.translate("&6Repair"), CC.translate("&6&l┃ &fItem in Hand"), CC.translate(" ")};

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.PHYSICAL) {
            //The player triggered a physical interaction event

            if(event.getClickedBlock().getType() == Material.GOLD_PLATE) {
                //The player stepped on a stone pressure plate

                event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection().multiply(2).setY(1.0D));
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPortalEnter(PlayerPortalEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            return;
        }
        Location to = event.getTo();
        World toWorld = to.getWorld();
        if (toWorld == null) {
            return;
        }
        Player player = event.getPlayer();
        if (toWorld.getEnvironment() == World.Environment.THE_END) {
            event.useTravelAgent(false);
            event.setTo(new Location(Bukkit.getWorld("world_the_end"), 38.478, 57, -117.379));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPortalEntera(PlayerPortalEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            return;
        }
        Location to = event.getTo();
        World toWorld = to.getWorld();
        if (toWorld == null) {
            return;
        }
        Player player = event.getPlayer();
        if (toWorld.getEnvironment() == World.Environment.NORMAL) {
            event.useTravelAgent(false);
            event.setTo(new Location(Bukkit.getWorld("world"), 0.491, 70, 0.643));
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Squid) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL && !this.getConfig().getBoolean("MOB_NATURAL_SPAWN")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onExplode(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Wither || entity instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Team cTeam = HCF.getPlugin().getTeamManager().getClaimManager().getTeam(event.getPlayer().getLocation());
        if (cTeam instanceof SafezoneTeam) {
            if (event.getBlock().getType().equals(Material.SIGN)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Team cTeam = HCF.getPlugin().getTeamManager().getClaimManager().getTeam(event.getPlayer().getLocation());
        if (cTeam instanceof SafezoneTeam) {
            if (event.getBlock().getType().equals(Material.SIGN)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void outsideSpawn(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        Team cTeam = this.getInstance().getTeamManager().getClaimManager().getTeam(player.getLocation());
        if (!(cTeam instanceof SafezoneTeam)) {
            if (!HCF.getPlugin().getTimerManager().getSotwTimer().isActive()) {
                if (e.getMessage().equals("/coinshop") || e.getMessage().equals("/prizes") || e.getMessage().equals("/itemshop") || e.getMessage().equals("/ce") || e.getMessage().equals("/shop") || e.getMessage().equals("/openshop") || e.getMessage().equals("/redeem")) {
                    e.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You can use this command outside spawn only when SOTW Timer is active.");
                }
            }
        }
        if (e.getMessage().equals("/settings")) {
            e.setCancelled(true);
            player.chat("/setting");
        }
        if (e.getMessage().equals("/ver") || e.getMessage().equals("/version")) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "That command was not found.");
        }
        if (e.getMessage().equals("/tps")) {
            e.setCancelled(true);
            player.chat("/spark tps");
        }
    }


    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }

    public Inventory openMainInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "Refill");
        inv.setItem(36, new ItemStack(Material.POTION, 1, (short)8259));
        inv.setItem(37, new ItemStack(Material.POTION, 1, (short)8259));
        inv.setItem(38, new ItemStack(Material.POTION, 1, (short)8259));
        inv.setItem(39, new ItemStack(Material.POTION, 1, (short)8259));
        inv.setItem(40, new ItemStack(Material.ENDER_PEARL, 16));
        inv.setItem(41, new ItemStack(Material.POTION, 1, (short)8226));
        inv.setItem(42, new ItemStack(Material.POTION, 1, (short)8226));
        inv.setItem(43, new ItemStack(Material.POTION, 1, (short)8226));
        inv.setItem(44, new ItemStack(Material.POTION, 1, (short)8226));
        inv.setItem(18, new ItemStack(Material.POTION, 1, (short)16421));
        inv.setItem(29, new ItemStack(Material.POTION, 1, (short)16421));
        ItemStack goldsword = new ItemStack(Material.GOLD_SWORD, 1);
        ItemStack healpot = new ItemStack(Material.POTION, 1, (short)16421);
        int in1;
        for (in1 = 0; in1 < 9; in1++)
            inv.setItem(in1, healpot);
        for (in1 = 9; in1 < 18; in1++)
            inv.setItem(in1, healpot);
        for (in1 = 19; in1 < 29; in1++)
            inv.setItem(in1, healpot);
        for (in1 = 30; in1 < 36; in1++)
            inv.setItem(in1, healpot);
        for (in1 = 45; in1 < 54; in1++)
            inv.setItem(in1, goldsword);
        player.openInventory(inv);
        return inv;
    }

    @EventHandler
    public void onSignPlace(SignChangeEvent event) {
        if (event.getLine(0).equals("[Refill]")) {
            Player player = event.getPlayer();
            if (player.isOp()) {
                for (int i = 0; i < this.lines.length; i++)
                    event.setLine(i, this.lines[i]);
            }
        }
        if (event.getLine(0).equals("[Repair]")) {
            Player player = event.getPlayer();
            if (player.isOp()) {
                for (int i = 0; i < this.repairlines.length; i++)
                    event.setLine(i, this.repairlines[i]);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) && block.getState() instanceof Sign) {
            Sign sign = (Sign)block.getState();
            for (int i = 0; i < this.lines.length; i++) {
                if (!sign.getLine(i).equals(this.lines[i]))
                    return;
            }
            openMainInventory(player);
            for (int i = 0; i < this.repairlines.length; i++) {
                if (!sign.getLine(i).equals(this.repairlines[i]))
                    return;
            }
            if (HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId()).getBalance() < 500) {
                player.sendMessage(CC.RED + "You dont have enough money.");
                return;
            }
            if (player.getItemInHand().getDurability() == (short) 100) {
                player.sendMessage(CC.RED + "Item doesnt need to be repaired.");
                return;
            }
            player.getItemInHand().setDurability((short) 100);
            player.sendMessage(CC.GREEN + "Successfully repaired item in your hand.");
            HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId()).setBalance(HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId()).getBalance() - 500);
            HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId()).save();

        }
    }

    private void load() {
        Tasks.executeLater(this.getManager(), 200, () -> {
            for (World world : Bukkit.getServer().getWorlds()) {
                world.setWeatherDuration(Integer.MAX_VALUE);
                world.setThundering(false);
                world.setStorm(false);
                world.setGameRuleValue("mobGriefing", "false");
                if (this.getInstance().getVersionManager().isVer16()) {
                    world.setGameRuleValue("maxEntityCramming", "0");
                    world.setGameRuleValue("doTraderSpawning", "false");
                    world.setGameRuleValue("doPatrolSpawning", "false");
                    world.setGameRuleValue("doInsomnia", "false");
                    world.setGameRuleValue("disableRaids", "true");
                }
            }
        });
    }
}
