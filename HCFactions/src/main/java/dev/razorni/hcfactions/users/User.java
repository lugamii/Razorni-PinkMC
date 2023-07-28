package dev.razorni.hcfactions.users;

import dev.razorni.hcfactions.deathban.Deathban;
import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.users.settings.TeamChatSetting;
import dev.razorni.hcfactions.users.settings.TeamListSetting;
import dev.razorni.hcfactions.utils.Serializer;
import dev.razorni.hcfactions.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class User extends Module<UserManager> {
    private static DecimalFormat KDR_FORMAT;

    static {
        KDR_FORMAT = new DecimalFormat("0.00");
    }

    private boolean scoreboardClaim;
    private boolean EOTWKilled;
    private boolean privateMessagesSound;
    private UUID uniqueID;
    private int diamonds;
    private int reputations;
    private boolean reclaimed;
    private boolean scoreboard;
    private int lives;
    private long dailyTimeLeft;
    private boolean privateMessages;
    private int kills;
    private TeamListSetting teamListSetting;
    private int deaths;
    private int highestKillstreak;
    private boolean claimsShown;
    private List<UUID> ignoring;
    private boolean foundDiamondAlerts;
    private long coinsleft;
    private PlaySession playSession;
    private long playtime;
    private int killstreak;
    private Deathban deathban;
    private boolean cobblePickup;
    private boolean publicChat;
    private int balance;
    private TeamChatSetting teamChatSetting;
    private UUID replied;
    private boolean redeemed;
    private boolean reward1;
    private boolean reward2;
    private boolean reward3;
    private boolean reward4;
    private boolean reward5;
    private boolean reward6;
    private boolean rewardseven;

    public User(UserManager manager, UUID uuid) {
        super(manager);
        this.uniqueID = uuid;
        this.replied = null;
        this.deathban = null;
        this.teamListSetting = TeamListSetting.ONLINE_LOW;
        this.teamChatSetting = TeamChatSetting.PUBLIC;
        this.ignoring = new ArrayList<>();
        this.balance = manager.getConfig().getInt("STARTING_BALANCE");
        this.kills = 0;
        this.deaths = 0;
        this.diamonds = 0;
        this.reputations = 0;
        this.lives = 0;
        this.dailyTimeLeft = 0L;
        this.killstreak = 0;
        this.highestKillstreak = 0;
        this.privateMessages = true;
        this.privateMessagesSound = true;
        this.reclaimed = false;
        this.reward1 = false;
        this.reward2 = false;
        this.reward3 = false;
        this.reward4 = false;
        this.reward5 = false;
        this.reward6 = false;
        this.rewardseven = false;
        this.coinsleft = 0L;
        this.playtime = 0L;
        this.redeemed = false;
        this.scoreboardClaim = this.getConfig().getBoolean("DEFAULT_CLAIM_SCOREBOARD");
        this.scoreboard = true;
        this.EOTWKilled = false;
        this.publicChat = true;
        this.cobblePickup = true;
        this.foundDiamondAlerts = true;
        this.claimsShown = false;
        manager.getUsers().put(uuid, this);
    }

    public User(UserManager manager, Map<String, Object> map) {
        super(manager);
        this.uniqueID = UUID.fromString((String) map.get("uniqueID"));
        this.replied = null;
        this.teamListSetting = TeamListSetting.valueOf((String) map.get("listSetting"));
        this.teamChatSetting = TeamChatSetting.valueOf((String) map.get("chatSetting"));
        this.ignoring = Utils.createList(map.get("ignoring"), String.class).stream().map(UUID::fromString).collect(Collectors.toList());
        this.balance = Integer.parseInt((String) map.get("balance"));
        this.kills = Integer.parseInt((String) map.get("kills"));
        this.deaths = Integer.parseInt((String) map.get("deaths"));
        this.diamonds = Integer.parseInt((String) map.get("diamonds"));
        this.reputations = Integer.parseInt((String) map.get("reputations"));
        this.lives = Integer.parseInt((String) map.get("lives"));
        this.killstreak = Integer.parseInt((String) map.get("killstreak"));
        this.highestKillstreak = Integer.parseInt((String) map.get("highestKillstreak"));
        this.reclaimed = Boolean.parseBoolean((String) map.get("reclaimed"));
        this.redeemed = Boolean.parseBoolean((String) map.get("redeemed"));
        this.reward1 = Boolean.parseBoolean((String) map.get("reward1"));
        this.reward2 = Boolean.parseBoolean((String) map.get("reward2"));
        this.reward3 = Boolean.parseBoolean((String) map.get("reward3"));
        this.reward4 = Boolean.parseBoolean((String) map.get("reward4"));
        this.reward5 = Boolean.parseBoolean((String) map.get("reward5"));
        this.reward6 = Boolean.parseBoolean((String) map.get("reward6"));
        this.rewardseven = Boolean.parseBoolean((String) map.get("rewardseven"));
        this.dailyTimeLeft = Long.parseLong((String) map.get("dailyTimeLeft"));
        this.scoreboardClaim = Boolean.parseBoolean((String) map.get("scoreboardClaim"));
        this.EOTWKilled = Boolean.parseBoolean((String) map.get("EOTWKilled"));
        this.scoreboard = Boolean.parseBoolean((String) map.get("scoreboard"));
        this.coinsleft = Long.parseLong((String) map.get("coinsleft"));
        this.playtime = Long.parseLong((String) map.get("playtime"));
        this.publicChat = Boolean.parseBoolean((String) map.get("publicChat"));
        this.cobblePickup = Boolean.parseBoolean((String) map.get("cobblePickup"));
        this.foundDiamondAlerts = Boolean.parseBoolean((String) map.get("foundDiamondAlerts"));
        this.privateMessages = true;
        this.privateMessagesSound = true;
        this.claimsShown = false;
        if (map.containsKey("deathban")) {
            this.deathban = Serializer.deserializeDeathban(this.getInstance().getDeathbanManager(), (String) map.get("deathban"));
        }
        manager.getUsers().put(this.uniqueID, this);
    }

    public double getKDR() {
        double dtr = this.kills / (double) this.deaths;
        return Double.isNaN(dtr) ? 0.0 : dtr;
    }


    public long getDailyTime() {
        return this.dailyTimeLeft;
    }

    public void setDailyTime(long time) {
        this.dailyTimeLeft = System.currentTimeMillis() + time;
    }

    public boolean hasDailyTime() {
        return System.currentTimeMillis() < this.dailyTimeLeft;
    }

    public boolean hasCoinsTime() {
        return System.currentTimeMillis() < this.coinsleft;
    }

    public void setCoinsTime(long time) {
        this.coinsleft = System.currentTimeMillis() + time;
    }

    public void addReputation(int amount) {
        this.reputations = this.reputations + amount;
    }

    public void removeReputation(int amount) {
        this.reputations = this.reputations - amount;
    }

    public long getTotalPlayTime() {
        return this.playtime + playSession.getCurrentSession();
    }

    public String getKDRString() {
        return KDR_FORMAT.format(this.getKDR());
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("uniqueID", this.uniqueID.toString());
        map.put("listSetting", this.teamListSetting.toString());
        map.put("chatSetting", this.teamChatSetting.toString());
        map.put("ignoring", this.ignoring.stream().map(UUID::toString).collect(Collectors.toList()));
        map.put("balance", this.balance + "");
        map.put("kills", this.kills + "");
        map.put("deaths", this.deaths + "");
        map.put("diamonds", this.diamonds + "");
        map.put("reputations", this.reputations + "");
        map.put("lives", this.lives + "");
        map.put("killstreak", this.killstreak + "");
        map.put("highestKillstreak", this.highestKillstreak + "");
        map.put("reclaimed", this.reclaimed + "");
        map.put("redeemed", this.redeemed + "");
        map.put("reward1", this.reward1 + "");
        map.put("reward2", this.reward2 + "");
        map.put("reward3", this.reward3 + "");
        map.put("reward4", this.reward4 + "");
        map.put("reward5", this.reward5 + "");
        map.put("reward6", this.reward6 + "");
        map.put("rewardseven", this.rewardseven + "");
        map.put("scoreboardClaim", this.scoreboardClaim + "");
        map.put("EOTWKilled", this.EOTWKilled + "");
        map.put("dailyTimeLeft", Long.toString(this.dailyTimeLeft));
        map.put("scoreboard", this.scoreboard + "");
        map.put("publicChat", this.publicChat + "");
        map.put("coinsleft", Long.toString(this.coinsleft));
        map.put("playtime", Long.toString(this.playtime));
        map.put("cobblePickup", this.cobblePickup + "");
        map.put("foundDiamondAlerts", this.foundDiamondAlerts + "");
        if (this.deathban != null) {
            map.put("deathban", Serializer.serializeDeathban(this.deathban));
        }
        return map;
    }

    public void save() {
        this.getInstance().getStorageManager().getStorage().saveUser(this, true);
    }
}