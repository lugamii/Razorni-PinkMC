package dev.razorni.hcfactions;

import cc.invictusgames.ilib.configuration.ConfigurationService;
import cc.invictusgames.ilib.configuration.JsonConfigurationService;
import cc.invictusgames.ilib.task.impl.AsynchronousTaskChain;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import dev.razorni.hcfactions.balance.BalanceManager;
import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.commands.type.EndPortalCommand;
import dev.razorni.hcfactions.commands.type.essential.EnchantCommand;
import dev.razorni.hcfactions.deathban.DeathbanManager;
import dev.razorni.hcfactions.events.eotw.EotwHandler;
import dev.razorni.hcfactions.events.king.KingManager;
import dev.razorni.hcfactions.events.koth.KothManager;
import dev.razorni.hcfactions.extras.ability.AbilityManager;
import dev.razorni.hcfactions.extras.bounty.BountyListener;
import dev.razorni.hcfactions.extras.chatgames.ChatGameManager;
import dev.razorni.hcfactions.extras.dailyrewards.DailyManager;
import dev.razorni.hcfactions.extras.framework.Manager;
import dev.razorni.hcfactions.extras.framework.extra.Configs;
import dev.razorni.hcfactions.extras.framework.menu.MenuManager;
import dev.razorni.hcfactions.extras.holograms.HologramsManager;
import dev.razorni.hcfactions.extras.killstreaks.KillstreakHandler;
import dev.razorni.hcfactions.extras.mountain.GlowstoneMountainManager;
import dev.razorni.hcfactions.extras.nametags.NametagManager;
import dev.razorni.hcfactions.extras.redeem.RedeemManager;
import dev.razorni.hcfactions.extras.reputation.ReputationManager;
import dev.razorni.hcfactions.extras.shop.listener.BuyMenuListener;
import dev.razorni.hcfactions.extras.shop.listener.SellMenuListener;
import dev.razorni.hcfactions.extras.shop.listener.SpawnerShopListener;
import dev.razorni.hcfactions.extras.spawners.SpawnerManager;
import dev.razorni.hcfactions.extras.supplydrop.SupplyDropManager;
import dev.razorni.hcfactions.extras.trade.TradeListener;
import dev.razorni.hcfactions.extras.vouchers.Voucher;
import dev.razorni.hcfactions.extras.vouchers.listener.VoucherListener;
import dev.razorni.hcfactions.extras.walls.WallManager;
import dev.razorni.hcfactions.extras.waypoints.WaypointManager;
import dev.razorni.hcfactions.extras.workload.WorkLoadQueue;
import dev.razorni.hcfactions.kits.KitManager;
import dev.razorni.hcfactions.listeners.ListenerManager;
import dev.razorni.hcfactions.listeners.type.CrowbarListener;
import dev.razorni.hcfactions.listeners.type.GlitchListener;
import dev.razorni.hcfactions.listeners.type.MainListener;
import dev.razorni.hcfactions.loggers.LoggerManager;
import dev.razorni.hcfactions.pvpclass.PvPClassManager;
import dev.razorni.hcfactions.reclaims.ReclaimManager;
import dev.razorni.hcfactions.signs.CustomSignManager;
import dev.razorni.hcfactions.staff.StaffManager;
import dev.razorni.hcfactions.teams.TeamManager;
import dev.razorni.hcfactions.teams.utils.LunarListener;
import dev.razorni.hcfactions.timers.TimerManager;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.users.UserManager;
import dev.razorni.hcfactions.utils.HCFCommandManager;
import dev.razorni.hcfactions.utils.ItemBuilder;
import dev.razorni.hcfactions.utils.Logger;
import dev.razorni.hcfactions.utils.board.BoardManager;
import dev.razorni.hcfactions.utils.commandapi.command.FrozenCommandHandler;
import dev.razorni.hcfactions.utils.configs.ConfigYML;
import dev.razorni.hcfactions.utils.event.ArmorListener;
import dev.razorni.hcfactions.utils.glass.GlassManager;
import dev.razorni.hcfactions.utils.hooks.ranks.RankManager;
import dev.razorni.hcfactions.utils.hooks.tags.TagManager;
import dev.razorni.hcfactions.utils.map.MapConfig;
import dev.razorni.hcfactions.utils.menuapi.menu.MenuListener;
import dev.razorni.hcfactions.utils.scheduler.ScheduleManager;
import dev.razorni.hcfactions.utils.storage.StorageManager;
import dev.razorni.hcfactions.utils.tablist.TablistManager;
import dev.razorni.hcfactions.utils.trash.file.FileConfig;
import dev.razorni.hcfactions.utils.trash.menu.ButtonListener;
import dev.razorni.hcfactions.utils.trash.task.TaskManager;
import dev.razorni.hcfactions.utils.versions.VersionManager;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
public class HCF extends JavaPlugin {

    public static final Random RANDOM = new Random();
    public static Gson PLAIN_GSON;
    public static Gson GSON;
    private static HCF plugin;
    private Gson gson;
    private AsynchronousTaskChain taskChain;
    public BountyListener bountyListener;
    private PacketPlayOutChat packet;
    //Managers
    private List<Manager> managers;
    private UserManager userManager;
    private WorkLoadQueue workLoadQueue;
    private RedeemManager redeemManager;
    private WaypointManager waypointManager;
    private ScheduleManager scheduleManager;
    private NametagManager nametagManager;
    private StorageManager storageManager;
    private WorldEditPlugin worldEdit;
    private TeamManager teamManager;
    private HologramsManager HologramsAPI;
    private DeathbanManager deathbanManager;
    private RankManager rankManager;
    private SpawnerManager spawnerManager;
    private PvPClassManager classManager;
    @Getter
    private KillstreakHandler killstreakHandler;
    private KitManager kitManager;
    private SupplyDropManager supplyDropManager;
    private BalanceManager balanceManager;
    private LoggerManager loggerManager;
    private GlassManager glassManager;
    private TimerManager timerManager;
    private ChatGameManager chatGameHandler;
    private ReclaimManager reclaimManager;
    private KingManager kingManager;
    private TaskManager taskManager;
    private dev.razorni.hcfactions.utils.trash.command.CommandManager commandManager;
    private boolean isGlobalCooldown;
    private MapConfig mapConfig;
    private TagManager tagManager;
    private WallManager wallManager;
    private ReputationManager repmanager;
    private VersionManager versionManager;
    private AbilityManager abilityManager;
    private ConfigurationService configurationService;
    private EotwHandler eotwHandler;
    private KothManager kothManager;
    private GlowstoneMountainManager glowstoneMountainManager;
    private StaffManager staffManager;
    private FileConfig packagesConfig;
    private MenuManager menuManager;
    private GlitchListener glitchListener;
    private boolean kits;
    private List<ConfigYML> configs;

    public static HCF getPlugin() {
        return HCF.plugin;
    }

    @SneakyThrows
    public void onEnable() {
        HCF.plugin = this;
        this.managers = new ArrayList<>();
        taskChain = new AsynchronousTaskChain();
        this.configs = new ArrayList<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        new Configs().load(this);
        this.userManager = new UserManager(this);
        this.supplyDropManager = new SupplyDropManager();
        this.glowstoneMountainManager = new GlowstoneMountainManager();
        this.redeemManager = new RedeemManager(this);
        this.versionManager = new VersionManager(this);
        this.timerManager = new TimerManager(this);
        this.balanceManager = new BalanceManager(this);
        this.repmanager = new ReputationManager();
        this.waypointManager = new WaypointManager(this);
        this.nametagManager = new NametagManager(this);
        this.teamManager = new TeamManager(this);
        this.glassManager = new GlassManager();
        this.wallManager = new WallManager(this);
        this.workLoadQueue = new WorkLoadQueue();
        this.eotwHandler = new EotwHandler(this);
        killstreakHandler = new KillstreakHandler();
        this.spawnerManager = new SpawnerManager(this);
        this.deathbanManager = new DeathbanManager(this);
        this.reclaimManager = new ReclaimManager(this);
        this.kitManager = new KitManager(this);
        this.rankManager = new RankManager(this);
        this.tagManager = new TagManager(this);
        this.chatGameHandler = new ChatGameManager();
        this.classManager = new PvPClassManager(this);
        this.loggerManager = new LoggerManager(this);
        this.abilityManager = new AbilityManager(this);
        this.storageManager = new StorageManager(this);
        this.scheduleManager = new ScheduleManager(this);
        this.staffManager = new StaffManager(this);
        this.taskManager = new TaskManager(this);
        this.commandManager = new dev.razorni.hcfactions.utils.trash.command.CommandManager(this);
        this.menuManager = new MenuManager(this);
        this.kothManager = new KothManager(this);
        this.kingManager = new KingManager(this);
        this.glitchListener = new GlitchListener(new ListenerManager(this));
        this.HologramsAPI = new HologramsManager(this);
        this.kits = this.getConfig().getBoolean("KITMAP_MODE");
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new LunarListener(), 20L, 20L);
        Plugin wep = Bukkit.getPluginManager().getPlugin("WorldEdit");
        HCF.getPlugin().worldEdit = ((wep instanceof WorldEditPlugin && wep.isEnabled()) ? ((WorldEditPlugin) wep) : null);
        new HCFCommandManager(this).loadCommands();
        new BoardManager(this);
        new TablistManager(this);
        new ButtonListener(this);
        new CommandManager(this);
        new CustomSignManager(this);
        registerConfiguration();
        updateActionBar();
        loadVouchers();
        FrozenCommandHandler.init();
        FrozenCommandHandler.registerPackage(this, "dev.razorni.hcfactions.staff.command");
        FrozenCommandHandler.registerPackage(this, "dev.razorni.hcfactions.extras.mountain.command");
        FrozenCommandHandler.registerPackage(this, "dev.razorni.hcfactions.events.eotw.command");
        FrozenCommandHandler.registerPackage(this, "dev.razorni.hcfactions.extras.reputation.command");
        FrozenCommandHandler.registerPackage(this, "dev.razorni.hcfactions.extras.spawners.command");
        FrozenCommandHandler.registerPackage(this, "dev.razorni.hcfactions.extras.workload.commands");
        FrozenCommandHandler.registerPackage(this, "dev.razorni.hcfactions.events.koth.command.args");
        FrozenCommandHandler.registerPackage(this, "dev.razorni.hcfactions.extras.ecoshop");
        FrozenCommandHandler.registerPackage(this, "dev.razorni.hcfactions.extras.cshop");
        FrozenCommandHandler.registerPackage(this, "dev.razorni.hcfactions.commands.type");
        FrozenCommandHandler.registerPackage(this, "dev.razorni.hcfactions.extras.killstreaks.command");
        FrozenCommandHandler.registerPackage(this, "dev.razorni.hcfactions.extras.trade.command");
        FrozenCommandHandler.registerPackage(this, "dev.razorni.hcfactions.extras.killstreaks");
        this.managers.forEach(Manager::enable);
        this.userManager.setLoaded(true);
        Logger.state("Enabled", this.managers.size(), this.teamManager.getTeams().size(), this.userManager.getUsers().size());
    }

    private void registerConfiguration() {
        Bukkit.getServer().getPluginManager().registerEvents(new ArmorListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new SpawnerShopListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new TradeListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new SellMenuListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new BuyMenuListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new DailyManager(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new EndPortalCommand(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new CrowbarListener(this), this);
        getCommand("enchant").setExecutor((CommandExecutor) new EnchantCommand(this));
        this.configurationService = new JsonConfigurationService();
        glowstoneMountainManager = configurationService.loadConfiguration(GlowstoneMountainManager.class,
                new File(getDataFolder(), "glowstone.json"));
    }

    public void saveData() throws IOException {
        try {
            this.configurationService.saveConfiguration(mapConfig, new File(getDataFolder(), "map.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadVouchers() {
        if (getConfig().getConfigurationSection("vouchers") == null) {
            return;
        }
        for (String voucher : getConfig().getConfigurationSection("vouchers").getKeys(false)) {
            new Voucher(voucher.toLowerCase(),
                    getConfig().getStringList("vouchers." + voucher + ".commands"), ItemBuilder.of(
                    Material.getMaterial(getConfig().getString("vouchers." + voucher + ".material.type")))
                    .setName(getConfig().getString("vouchers." + voucher + ".material.name"))
                    .setLore(getConfig().getStringList("vouchers." + voucher + ".material.lore")).toItemStack());
        }
        Bukkit.getServer().getPluginManager().registerEvents(new VoucherListener(), this);
        FrozenCommandHandler.registerPackage(this, "dev.razorni.hcfactions.extras.vouchers.command");
    }

    public void updateActionBar() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(HCF.getPlugin(), new Runnable() {

            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    sendActionBar(p);
                }
            }
        }, 0L, 1L);
    }

    private void sendActionBar(Player player) {
        if (!HCF.getPlugin().getStaffManager().isStaffEnabled(player) && this.getAbilityManager().getGlobalCooldown().hasCooldown(player)) {
            PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§dPartner Item " + HCF.getPlugin().getAbilityManager().getStatus(player) + " §f" + this.getAbilityManager().getGlobalCooldown().getRemaining(player) + "\"}"), (byte) 2);
            this.packet = packet;
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @SneakyThrows
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOnline()) {
                User profile = this.getUserManager().getByUUID(player.getUniqueId());

                MainListener.getPlaySession().setEndTime(System.currentTimeMillis());

                profile.setPlaytime(profile.getPlaytime() + MainListener.getPlaySession().getTime());

                profile.save();

                player.kickPlayer("Server is restarting. Please try again in a few minutes.");
            }
        }
        workLoadQueue.shutdown();
        glassManager.disable();
        saveData();
        this.managers.forEach(Manager::disable);
        Logger.state("Disabled", this.managers.size(), this.teamManager.getTeams().size(), this.userManager.getUsers().size());
    }
}
