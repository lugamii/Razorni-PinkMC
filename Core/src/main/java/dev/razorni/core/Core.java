package dev.razorni.core;

import dev.razorni.core.chat.Chat;
import dev.razorni.core.chat.listener.ChatListener;
import dev.razorni.core.database.mongo.MongoHandler;
import dev.razorni.core.database.redis.RedisHandler;
import dev.razorni.core.database.redis.packets.global.ServerRebootPacket;
import dev.razorni.core.extras.holograms.HologramsManager;
import dev.razorni.core.extras.hook.VaultProvider;
import dev.razorni.core.extras.placeholderapi.PAPIHook;
import dev.razorni.core.extras.rank.Rank;
import dev.razorni.core.extras.rank.RankParameterType;
import dev.razorni.core.extras.reminder.ReminderHandler;
import dev.razorni.core.extras.report.ReportHandler;
import dev.razorni.core.extras.tag.TagHandler;
import dev.razorni.core.extras.tips.TipManager;
import dev.razorni.core.extras.xpacket.FrozenXPacketHandler;
import dev.razorni.core.listener.EssentialsListener;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.ProfileListener;
import dev.razorni.core.profile.ProfileParameterType;
import dev.razorni.core.profile.freeze.FreezeListener;
import dev.razorni.core.profile.grant.listener.GrantListener;
import dev.razorni.core.profile.punishment.listener.PunishmentListener;
import dev.razorni.core.profile.staffmode.StaffModeManager;
import dev.razorni.core.server.ServerType;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemUtils;
import dev.razorni.core.util.TaskUtil;
import dev.razorni.core.util.command.FrozenCommandHandler;
import dev.razorni.core.util.menu.MenuListener;
import dev.razorni.core.extras.namemc.handlers.VerificationHandler;
import dev.razorni.core.util.uuid.UniqueIDCache;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;

import java.util.Arrays;

@Getter
public class Core extends JavaPlugin {

    @Getter public static Core instance;

    private MongoHandler mongoHandler;
    private TagHandler tagHandler;
    private TipManager tipManager;
    private ReportHandler reportHandler;
    private ReminderHandler reminderHandler;
    private RedisHandler redisHandler;
    private Chat chat;
    private ServerType serverType;
    private StaffModeManager staffManager;

    @Setter private JedisPool localJedisPool;
    @Setter private JedisPool backboneJedisPool;

    @Getter
    @Setter
    private CoreAPI CoreAPI;

    @Getter private VerificationHandler verificationHandler;

    private boolean loaded;

    @SneakyThrows
    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        loadConfigs();

        this.verificationHandler = new VerificationHandler();

        this.getServer().getScheduler().runTaskAsynchronously(this, () -> this.verificationHandler.load());

        getServer().getServicesManager().register(Permission.class, new VaultProvider(), this, ServicePriority.Highest);

        mongoHandler = new MongoHandler();
        redisHandler = new RedisHandler(this);

        this.CoreAPI = new CoreAPI();
        new PAPIHook();
        new HologramsManager(this);

        UniqueIDCache.init();
        FrozenCommandHandler.init();
        FrozenXPacketHandler.init();

        FrozenCommandHandler.registerParameterType(Rank.class, new RankParameterType());
        FrozenCommandHandler.registerParameterType(Profile.class, new ProfileParameterType());
        FrozenCommandHandler.registerAll(this);

        Rank.init();
        Profile.init();

        chat = new Chat(this);
        tipManager = new TipManager(this);
        reportHandler = new ReportHandler();
        reminderHandler = new ReminderHandler();
        tagHandler = new TagHandler();

        if (this.getServerType() == ServerType.HUB) {
            staffManager = new StaffModeManager(this);
        }

        Arrays.asList(
                new ProfileListener(),
                new MenuListener(this),
                new EssentialsListener(),
                new ChatListener(),
                new GrantListener(),
                new FreezeListener(),
                new PunishmentListener()
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        ItemUtils.load();

        TaskUtil.runLater(() -> {
            this.loaded = true;
            new ServerRebootPacket(Core.getInstance().getConfig().getString("SERVER_NAME"), CC.translate("&aOnline &band will be joinable in few seconds.")).send();
        }, 35L);
    }

    @Override
    public void onDisable() {
        this.verificationHandler.save();

        new ServerRebootPacket(Core.getInstance().getConfig().getString("SERVER_NAME"), CC.translate("&cOffline &bands its no longer joinable.")).send();

        this.localJedisPool.close();
        this.backboneJedisPool.close();
    }

    public void loadConfigs() {
        this.serverType = ServerType.valueOf(Core.getInstance().getConfig().getString("SERVER_TYPE"));
    }

}
