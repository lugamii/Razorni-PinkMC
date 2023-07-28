package dev.razorni.core.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.razorni.core.Core;
import dev.razorni.core.extras.friends.Friend;
import dev.razorni.core.extras.friends.request.FriendRequest;
import dev.razorni.core.extras.global.GlobalInfo;
import dev.razorni.core.extras.global.pastfaction.PastFaction;
import dev.razorni.core.extras.tag.Tag;
import dev.razorni.core.profile.grant.Grant;
import dev.razorni.core.profile.grant.event.GrantExpireEvent;
import dev.razorni.core.profile.option.ProfileOptions;
import dev.razorni.core.profile.option.staff.ProfileStaffOptions;
import dev.razorni.core.profile.option.staff.StaffInfo;
import dev.razorni.core.profile.permission.TimedPermission;
import dev.razorni.core.profile.punishment.Punishment;
import dev.razorni.core.profile.punishment.PunishmentType;
import dev.razorni.core.extras.reminder.Reminder;
import dev.razorni.core.extras.rank.Rank;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.Cooldown;
import dev.razorni.core.util.uuid.UniqueIDCache;
import dev.razorni.core.util.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
public class Profile {

	@Getter private static final Map<UUID, Profile> profiles = new HashMap<>();
	@Getter private static MongoCollection<Document> collection;

	private final UUID uuid;

	private final ProfileOptions options = new ProfileOptions();
	private final ProfileStaffOptions staffOptions = new ProfileStaffOptions();
	private final GlobalInfo globalInfo = new GlobalInfo();
	private final StaffInfo staffInfo = new StaffInfo();

	private final List<Reminder> readReminders;
	private final List<Grant> grants;
	private final List<Friend> friends;
	@Setter private Tag tag;
	private final List<FriendRequest> friendRequests;
	private final List<Punishment> punishments;
	private List<String> ipAddresses = new ArrayList<>();
	private List<UUID> knownAlts = new ArrayList<>();
	private List<String> individualPermissions;
	private List<TimedPermission> timedPermissions;
	private List<UUID> ignored;


	@Setter private String username;
	@Setter private Long firstSeen;
	@Setter private Long primedaily = 0L;
	@Setter private Long lastSeen;
	@Setter private Map<String, Integer> playTimeMap;
	@Setter private String serverOn;
	@Setter private String currentAddress;

	@Setter private boolean inQueue;
	@Setter private long queueTime;
	// Coins System
	@Setter private int coins;
	private Grant activeGrant;
	@Setter private UUID replyTo;
	@Setter private boolean loaded;
	@Setter private boolean online;
	@Setter private boolean prime;
	@Setter private boolean nameMcVerified;
	@Setter private boolean sentNameMCRelogMessage;
	@Setter private boolean nameMcNotify;
	@Setter private Cooldown requestCooldown = new Cooldown(0);

	@Setter private Cooldown reportCooldown = new Cooldown(0);
	@Setter private Cooldown friendCooldown = new Cooldown(0);
	@Setter private Cooldown chatCooldown = new Cooldown(1000);

	public Profile(String username, UUID uuid) {
		this.playTimeMap = new HashMap<>();
		this.grants = new ArrayList<>();
		this.punishments = new ArrayList<>();
		this.individualPermissions = new ArrayList<>();
		this.ignored = new ArrayList<>();
		this.readReminders = new ArrayList<>();
		this.friends = new ArrayList<>();
		this.friendRequests = new ArrayList<>();
		this.timedPermissions = new ArrayList<>();
		this.username = username;
		this.uuid = uuid;
		load();
	}

	public static void init() {
		collection = Core.getInstance().getMongoHandler().getMongoDatabase().getCollection("profiles");

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Profile profile : Profile.getProfiles().values()) {
					profile.checkGrants();
				}
			}
		}.runTaskTimerAsynchronously(Core.getInstance(), 20 * 10, 20 * 10);
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Profile profile : Profile.getProfiles().values()) {
					profile.checkGrants();
					if (profile.getPlayer() != null) {
						if (profile.getPlayer().hasPermission("gravity.staff")) {
							String server = Core.getInstance().getConfig().getString("SERVER_NAME");
							profile.getPlayTimeMap().put(server, profile.getPlayTimeMap().getOrDefault(server, 0) + 5);
						}
					}
				}
			}
		}.runTaskTimerAsynchronously(Core.getInstance(), 20 * 5, 20 * 5);

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Profile profile : Profile.getProfiles().values()) {
					if (!profile.getTimedPermissions().isEmpty()) {
						if (profile.getPlayer() == null) return;
						profile.setupPermissionsAttachment(Core.getInstance(), profile.getPlayer());
					}
				}
			}
		}.runTaskTimerAsynchronously(Core.getInstance(), 20 * 10, 20 * 30L);
	}

	public static Profile getByUuid(UUID uuid) {
		if (profiles.containsKey(uuid)) {
			return profiles.get(uuid);
		}

		return new Profile(UniqueIDCache.name(uuid), uuid);
	}

	public static List<Profile> getByIpAddress(String ipAddress) {
		List<Profile> profiles = new ArrayList<>();

		try (MongoCursor<Document> cursor = collection.find(Filters.eq("currentAddress", ipAddress)).iterator()) {
			while (cursor.hasNext()) {
				Document document = cursor.next();
				profiles.add(new Profile(document.getString("username"), UUID.fromString(document.getString("uuid"))));
			}
		}

		return profiles;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	public String getColoredUsername() {
		return activeGrant.getRank().getColor() + username;
	}

	public Punishment getActiveMute() {
		for (Punishment punishment : punishments) {
			if (punishment.getType() == PunishmentType.MUTE && punishment.isActive()) {
				return punishment;
			}
		}

		return null;
	}

	public Punishment getActiveBlacklist() {
		for (Punishment punishment : punishments) {
			if (punishment.getType() == PunishmentType.BLACKLIST && punishment.isActive()) {
				return punishment;
			}
		}
		return null;
	}

	public Punishment getActiveBan() {
		for (Punishment punishment : punishments) {
			if (punishment.getType().isBan() && punishment.isActive()) {
				return punishment;
			}
		}

		return null;
	}

	public int getPunishmentCountByType(PunishmentType type) {
		int i = 0;

		for (Punishment punishment : punishments) {
			if (punishment.getType() == type) i++;
		}

		return i;
	}

	public Rank getActiveRank() {
		return this.activeGrant.getRank();
	}

	public void nullActiveGrant() {
		this.activeGrant = null;
	}

	public void setActiveGrant(Grant grant) {
		this.activeGrant = grant;
		Player player = this.getPlayer();
		if (player != null) {
			player.setDisplayName(grant.getRank().getPrefix() + player.getName() + grant.getRank().getSuffix());
			setupPermissionsAttachment(Core.getInstance(), player);
		}

	}

	public Rank getRank() {
		return activeGrant != null ? this.activeGrant.getRank() : Rank.getRankByDisplayName("Default");
	}

	public void activateNextGrant() {
		List<Grant> grants = new ArrayList(this.grants);
		grants.sort(Comparator.comparingInt((grantx) -> grantx.getRank().getWeight()));

		for (Grant grant : grants) {
			if (!Rank.getRanks().containsKey(grant.getRank().getUuid())) continue;
			if (!grant.isRemoved() && !grant.hasExpired()) {
				this.setActiveGrant(grant);
			}
		}

	}

	public void checkGrants() {

		if (this.activeGrant != null) {
			if (!Rank.getRanks().containsKey(getActiveRank().getUuid())) {
				this.activateNextGrant();
				return;
			}
		}

		for (Grant grant : this.grants) {
			if (!grant.isRemoved() && grant.hasExpired()) {
				grant.setRemovedAt(System.currentTimeMillis());
				grant.setRemovedReason("Expired");
				grant.setRemoved(true);
				if (this.activeGrant != null && this.activeGrant.equals(grant)) {
					this.activeGrant = null;
				}

				Player player = this.getPlayer();
				if (player != null) {
					(new GrantExpireEvent(player, grant)).call();
				}
			}
		}

		if (this.activeGrant == null) {
			this.activateNextGrant();
			if (this.activeGrant != null) {
				return;
			}

			Grant grant = new Grant(UUID.randomUUID(), Rank.getDefaultRank(), null, System.currentTimeMillis(), "Default", Integer.MAX_VALUE);
			this.grants.add(grant);
			this.setActiveGrant(grant);
		}

	}

	public void setupBukkitPlayer(Player player) {
		if (player == null) {
			return;
		}

		setupPermissionsAttachment(Core.getInstance(), player);

		String displayName = activeGrant.getRank().getPrefix() + player.getName() + activeGrant.getRank().getSuffix();
		String coloredName = this.getColoredUsername();

		player.setDisplayName(displayName);

		player.setPlayerListName(coloredName);
	}

	public void setupPermissionsAttachment(Core core, Player player) {
		for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
			if (attachmentInfo.getAttachment() == null) {
				continue;
			}

			attachmentInfo.getAttachment().getPermissions().forEach((permission, value) -> {
				attachmentInfo.getAttachment().unsetPermission(permission);
			});
		}

		PermissionAttachment attachment = player.addAttachment(core);

		for (String perm : activeGrant.getRank().getAllPermissions()) { // Rank permissions
			attachment.setPermission(perm, true);
		}

		// Check for 'null' permissions
		individualPermissions.removeIf(s -> s == null || s.isEmpty());

		for (TimedPermission timedPermission : this.timedPermissions) {
			if (timedPermission.getRemaining() > 0) {
				attachment.setPermission(timedPermission.getPermission(), true);
			}
		}

		for (String permission : individualPermissions) { // Individual permissions
			attachment.setPermission(permission, true);
		}

		player.recalculatePermissions();
	}

	public TimedPermission timedPermissionByPerm(String perm) {
		for (TimedPermission timedPermission : this.timedPermissions) {
			if (timedPermission.getPermission().equalsIgnoreCase(perm))
				return timedPermission;
		}
		return null;
	}

	public void load() {

		final Document document = collection.find(Filters.eq("uuid", uuid.toString())).first();

		if (document != null) {
			if (username == null) {
				username = document.getString("username");
			}

			this.nameMcVerified = document.getBoolean("nameMCVerified");
			this.sentNameMCRelogMessage = document.getBoolean("nameMCMessage");
			this.nameMcNotify = document.getBoolean("nameMCNotify");
			this.serverOn = document.getString("serverOn");
			this.primedaily = document.getLong("primedaily");
			this.firstSeen = document.getLong("firstSeen");
			this.lastSeen = document.getLong("lastSeen");
			this.coins = document.getInteger("coins");
			this.currentAddress = document.getString("currentAddress");
			this.inQueue = document.getBoolean("inQueue");
			this.queueTime = document.getLong("queueTime");
			this.online = document.getBoolean("isOnline");
			this.prime = document.getBoolean("isPrime");
			this.ipAddresses = Core.getInstance().getMongoHandler().getGSON().fromJson(document.getString("ipAddresses"), Core.getInstance().getMongoHandler().getLIST_STRING_TYPE());
			this.ignored = Core.getInstance().getMongoHandler().getGSON().fromJson(document.getString("ignored"), Core.getInstance().getMongoHandler().getLIST_UUID_TYPE());
			this.individualPermissions = Core.getInstance().getMongoHandler().getGSON().fromJson(document.getString("individualPermissions"), Core.getInstance().getMongoHandler().getLIST_STRING_TYPE());

			Tag prefix = Core.getInstance().getTagHandler().customgetTagByName(document.getString("prefix"));
			Tag prefix1 = Core.getInstance().getTagHandler().symbolgetTagByName(document.getString("prefix"));
			Tag prefix2 = Core.getInstance().getTagHandler().textgetTagByName(document.getString("prefix"));
			Tag prefix3 = Core.getInstance().getTagHandler().countrygetTagByName(document.getString("prefix"));

			if (prefix != null) {
				this.tag = prefix;
			} else if (prefix1 != null) {
				this.tag = prefix1;
			} else if (prefix2 != null) {
				this.tag = prefix2;
			} else if (prefix3 != null)  {
				this.tag = prefix3;
			} else {
				this.tag = Core.getInstance().getTagHandler().getDefault();
			}

			String server = Core.getInstance().getConfig().getString("SERVER_NAME");
			for (Map.Entry<String, Object> entry : document.entrySet()) {
				if (entry.getKey().equals(server)) {
					this.playTimeMap.put(entry.getKey(), (Integer) entry.getValue());
				}
			}

			Document globalInfoDocument = (Document) document.get("globalinfo");

			globalInfo.setHcfCurrentKillstreak(globalInfoDocument.getInteger("hcfCurrentKillstreak"));
			globalInfo.setHcfHighestKillstreak(globalInfoDocument.getInteger("hcfHighestKillstreak"));
			globalInfo.setHcfKills(globalInfoDocument.getInteger("hcfKills"));
			globalInfo.setHcfDeaths(globalInfoDocument.getInteger("hcfDeaths"));
			globalInfo.setHcfMadeRaidable(globalInfoDocument.getInteger("hcfMadeRaidable"));
			globalInfo.setHcfMapsPlayed(globalInfoDocument.getInteger("hcfMapsPlayed"));
			globalInfo.setHcfCitadelCaps(globalInfoDocument.getInteger("hcfCitadelCaps"));
			globalInfo.setHcfConquestCaps(globalInfoDocument.getInteger("hcfConquestCaps"));
			globalInfo.setHcfKothCaps(globalInfoDocument.getInteger("hcfKothCaps"));

			globalInfo.setKitsCurrentKillstreak(globalInfoDocument.getInteger("kitsCurrentKillstreak"));
			globalInfo.setKitsHighestKillstreak(globalInfoDocument.getInteger("kitsHighestKillstreak"));
			globalInfo.setKitsKills(globalInfoDocument.getInteger("kitsKills"));
			globalInfo.setKitsDeaths(globalInfoDocument.getInteger("kitsDeaths"));
			globalInfo.setKitsTournyLoses(globalInfoDocument.getInteger("kitsTournyLoses"));
			globalInfo.setKitsTournyWins(globalInfoDocument.getInteger("kitsTournyWins"));
			globalInfo.setKitsTournyPlayed(globalInfoDocument.getInteger("kitsTournyPlayed"));
			globalInfo.setKitsSeasonsPlayed(globalInfoDocument.getInteger("kitsSeasonsPlayed"));
			globalInfo.setKitsCitadelCaps(globalInfoDocument.getInteger("kitsCitadelCaps"));
			globalInfo.setKitsConquestCaps(globalInfoDocument.getInteger("kitsConquestCaps"));
			globalInfo.setKitsKothCaps(globalInfoDocument.getInteger("kitsKothCaps"));

			globalInfo.setPracticeCurrentWinstreak(globalInfoDocument.getInteger("practiceCurrentWinstreak"));
			globalInfo.setPracticeHighestWinstreak(globalInfoDocument.getInteger("practiceHighestWinstreak"));
			globalInfo.setPracticeKills(globalInfoDocument.getInteger("practiceKills"));
			globalInfo.setPracticeDeaths(globalInfoDocument.getInteger("practiceDeaths"));
			globalInfo.setPracticeWins(globalInfoDocument.getInteger("practiceWins"));
			globalInfo.setPracticeLoses(globalInfoDocument.getInteger("practiceLoses"));
			globalInfo.setPracticeGamesPlayed(globalInfoDocument.getInteger("practiceGamesPlayed"));
			globalInfo.setPracticeSeasonsPlayed(globalInfoDocument.getInteger("practiceSeasonsPlayed"));

			JsonArray pastfactions = new JsonParser().parse(globalInfoDocument.getString("pastfactions")).getAsJsonArray();
			for (JsonElement jsonElement : pastfactions) {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				this.globalInfo.getPastFactions().add(PastFaction.getDeserializer().deserialize(jsonObject));
			}

			try {
				Document timedPermsDocument = (Document) document.get("timedPerms");
				JsonArray timedPerms = new JsonParser().parse(timedPermsDocument.getString("timedpermissions")).getAsJsonArray();
				for (JsonElement jsonElement : timedPerms) {
					JsonObject jsonObject = jsonElement.getAsJsonObject();
					this.timedPermissions.add(TimedPermission.deserializer.deserialize(jsonObject));
				}
			} catch (Exception ignored) {

			}

			Document staffInfoDocument = (Document) document.get("staffinfo");
			staffInfo.setBans(staffInfoDocument.getInteger("bans"));
			staffInfo.setBlacklists(staffInfoDocument.getInteger("blacklists"));
			staffInfo.setKicks(staffInfoDocument.getInteger("kicks"));
			staffInfo.setWarns(staffInfoDocument.getInteger("warns"));
			staffInfo.setMutes(staffInfoDocument.getInteger("mutes"));
			staffInfo.setReportsResolved(staffInfoDocument.getInteger("reportsResolved"));
			staffInfo.setStrikes(staffInfoDocument.getInteger("strikes"));
			staffInfo.setPunishmentResolved(staffInfoDocument.getInteger("punishmentsResolved"));
			JsonArray sentPunishments = new JsonParser().parse(staffInfoDocument.getString("punishments")).getAsJsonArray();
			for (JsonElement jsonElement : sentPunishments) {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				this.staffInfo.getPunishments().add(Punishment.DESERIALIZER.deserialize(jsonObject));
			}

			Document optionsDocument = (Document) document.get("options");
			options.setPublicChatEnabled(optionsDocument.getBoolean("publicChatEnabled"));
			options.setPrivateChatEnabled(optionsDocument.getBoolean("privateChatEnabled"));
			options.setPrivateChatSoundsEnabled(optionsDocument.getBoolean("privateChatSoundsEnabled"));
			options.setFriendRequestsEnabled(optionsDocument.getBoolean("friendRequestsEnabled"));
			options.setFriendRightClickEnabled(optionsDocument.getBoolean("friendRightClickEnabled"));
			options.setTipsEnabled(optionsDocument.getBoolean("tipsToggled"));
			options.setFrozen(optionsDocument.getBoolean("frozen"));

			JsonArray grants = new JsonParser().parse(document.getString("grants")).getAsJsonArray();
			for (JsonElement jsonElement : grants) {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				Rank rank = Rank.getRankByUuid(UUID.fromString(jsonObject.get("rank").getAsString()));

				if (rank != null) {
					this.grants.add(Grant.DESERIALIZER.deserialize(jsonObject));
				}
			}

			JsonArray punishments = new JsonParser().parse(document.getString("punishments")).getAsJsonArray();
			for (JsonElement jsonElement : punishments) {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				this.punishments.add(Punishment.DESERIALIZER.deserialize(jsonObject));
			}

			JsonArray reminders = new JsonParser().parse(document.getString("reminders")).getAsJsonArray();
			for (JsonElement jsonElement : reminders) {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				this.readReminders.add(Reminder.getDeserializer().deserialize(jsonObject));
			}

			JsonArray fr = new JsonParser().parse(document.getString("friendrequests")).getAsJsonArray();
			for (JsonElement jsonElement : fr) {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				this.friendRequests.add(FriendRequest.getDeserializer().deserialize(jsonObject));
			}

			JsonArray frs = new JsonParser().parse(document.getString("friends")).getAsJsonArray();
			for (JsonElement jsonElement : frs) {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				this.friends.add(Friend.getDeserializer().deserialize(jsonObject));
			}
		}

		checkGrants();

		// Set loaded to true
		loaded = true;
	}

	public void asyncSave() {
		Bukkit.getServer().getScheduler().runTaskAsynchronously(Core.getInstance(), this::save);
	}

	public void save() {
		CompletableFuture.runAsync(() -> {

			Document document = new Document();
			document.put("username", this.username);
			document.put("uuid", this.uuid.toString());
			document.put("firstSeen", this.firstSeen);
			document.put("lastSeen", this.lastSeen);
			document.put("serverOn", this.serverOn);
			document.put("primedaily", this.primedaily);
			document.put("inQueue", this.inQueue);
			document.put("queueTime", this.queueTime);
			document.put("nameMCVerified", this.nameMcVerified);
			document.put("nameMCMessage", this.sentNameMCRelogMessage);
			document.put("nameMCNotify", this.nameMcNotify);
			document.put("isOnline", this.online);
			document.put("isPrime", this.prime);
			document.put("coins", this.coins);
			document.put("currentAddress", this.currentAddress);
			document.put("ipAddresses", Core.getInstance().getMongoHandler().getGSON().toJson(this.ipAddresses, Core.getInstance().getMongoHandler().getLIST_STRING_TYPE()));
			document.put("individualPermissions", Core.getInstance().getMongoHandler().getGSON().toJson(this.individualPermissions, Core.getInstance().getMongoHandler().getLIST_STRING_TYPE()));
			document.put("ignored", Core.getInstance().getMongoHandler().getGSON().toJson(this.ignored, Core.getInstance().getMongoHandler().getLIST_UUID_TYPE()));

			if (this.tag != null) {
				document.put("prefix", this.tag.getName());
			}

			String server = Core.getInstance().getConfig().getString("SERVER_NAME");
			for (Map.Entry<String, Integer> entry : this.playTimeMap.entrySet()) {
				document.put(server, entry.getValue());
			}

			Document timedPermDocument = new Document();
			JsonArray timedPerms = new JsonArray();
			for (TimedPermission timedPermission : this.timedPermissions) {
				timedPerms.add(TimedPermission.serializer.serialize(timedPermission));
			}
			timedPermDocument.put("timedpermissions", timedPerms.toString());

			document.put("timedPerms", timedPermDocument);

			Document staffInfoDocument = new Document();
			staffInfoDocument.put("bans", staffInfo.getBans());
			staffInfoDocument.put("kicks", staffInfo.getKicks());
			staffInfoDocument.put("blacklists", staffInfo.getBlacklists());
			staffInfoDocument.put("mutes", staffInfo.getMutes());
			staffInfoDocument.put("warns", staffInfo.getWarns());
			staffInfoDocument.put("strikes", staffInfo.getStrikes());
			staffInfoDocument.put("punishmentsResolved", staffInfo.getPunishmentResolved());
			staffInfoDocument.put("reportsResolved", staffInfo.getReportsResolved());

			JsonArray sentPunishments = new JsonArray();
			for (Punishment punishment : this.staffInfo.getPunishments()) {
				sentPunishments.add(Punishment.SERIALIZER.serialize(punishment));
			}
			staffInfoDocument.put("punishments", sentPunishments.toString());

			document.put("staffinfo", staffInfoDocument);

			Document optionsDocument = new Document();
			optionsDocument.put("publicChatEnabled", options.isPublicChatEnabled());
			optionsDocument.put("privateChatEnabled", options.isPrivateChatEnabled());
			optionsDocument.put("privateChatSoundsEnabled", options.isPrivateChatSoundsEnabled());
			optionsDocument.put("friendRequestsEnabled", options.isFriendRequestsEnabled());
			optionsDocument.put("friendRightClickEnabled", options.isFriendRightClickEnabled());
			optionsDocument.put("tipsToggled", options.isTipsEnabled());
			optionsDocument.put("frozen", options.isFrozen());
			document.put("options", optionsDocument);

			JsonArray grants = new JsonArray();
			for (Grant grant : this.grants) {
				grants.add(Grant.SERIALIZER.serialize(grant));
			}
			document.put("grants", grants.toString());

			JsonArray friendRequests = new JsonArray();
			for (FriendRequest friendRequest : this.friendRequests) {
				friendRequests.add(FriendRequest.getSerializer().serialize(friendRequest));
			}
			document.put("friendrequests", friendRequests.toString());

			JsonArray friends = new JsonArray();
			for (Friend friend : this.friends) {
				friends.add(Friend.getSerializer().serialize(friend));
			}
			document.put("friends", friends.toString());

			JsonArray reminders = new JsonArray();
			for (Reminder reminder : this.readReminders) {
				reminders.add(Reminder.getSerializer().serialize(reminder));
			}
			document.put("reminders", reminders.toString());

			Document globalInfoDocument = new Document();
			globalInfoDocument.put("hcfCurrentKillstreak", this.globalInfo.getHcfCurrentKillstreak());
			globalInfoDocument.put("hcfHighestKillstreak", this.globalInfo.getHcfHighestKillstreak());
			globalInfoDocument.put("hcfKills", this.globalInfo.getHcfKills());
			globalInfoDocument.put("hcfDeaths", this.globalInfo.getHcfDeaths());
			globalInfoDocument.put("hcfMadeRaidable", this.globalInfo.getHcfMadeRaidable());
			globalInfoDocument.put("hcfMapsPlayed", this.globalInfo.getHcfMapsPlayed());
			globalInfoDocument.put("hcfCitadelCaps", this.globalInfo.getHcfCitadelCaps());
			globalInfoDocument.put("hcfKothCaps", this.globalInfo.getHcfKothCaps());
			globalInfoDocument.put("hcfConquestCaps", this.globalInfo.getHcfConquestCaps());

			globalInfoDocument.put("kitsCurrentKillstreak", this.globalInfo.getKitsCurrentKillstreak());
			globalInfoDocument.put("kitsHighestKillstreak", this.globalInfo.getKitsHighestKillstreak());
			globalInfoDocument.put("kitsKills", this.globalInfo.getKitsDeaths());
			globalInfoDocument.put("kitsDeaths", this.globalInfo.getKitsDeaths());
			globalInfoDocument.put("kitsTournyPlayed", this.globalInfo.getKitsTournyPlayed());
			globalInfoDocument.put("kitsTournyWins", this.globalInfo.getKitsTournyWins());
			globalInfoDocument.put("kitsTournyLoses", this.globalInfo.getKitsTournyLoses());
			globalInfoDocument.put("kitsSeasonsPlayed", this.globalInfo.getKitsSeasonsPlayed());
			globalInfoDocument.put("kitsCitadelCaps", this.globalInfo.getKitsCitadelCaps());
			globalInfoDocument.put("kitsKothCaps", this.globalInfo.getKitsKothCaps());
			globalInfoDocument.put("kitsConquestCaps", this.globalInfo.getKitsConquestCaps());

			globalInfoDocument.put("practiceCurrentWinstreak", this.globalInfo.getPracticeCurrentWinstreak());
			globalInfoDocument.put("practiceHighestWinstreak", this.globalInfo.getPracticeHighestWinstreak());
			globalInfoDocument.put("practiceKills", this.globalInfo.getPracticeKills());
			globalInfoDocument.put("practiceDeaths", this.globalInfo.getPracticeDeaths());
			globalInfoDocument.put("practiceGamesPlayed", this.globalInfo.getPracticeGamesPlayed());
			globalInfoDocument.put("practiceWins", this.globalInfo.getPracticeWins());
			globalInfoDocument.put("practiceLoses", this.globalInfo.getPracticeLoses());
			globalInfoDocument.put("practiceSeasonsPlayed", this.globalInfo.getPracticeSeasonsPlayed());

			JsonArray pfs = new JsonArray();
			for (PastFaction faction : this.globalInfo.getPastFactions()) {
				pfs.add(PastFaction.getSerializer().serialize(faction));
			}
			globalInfoDocument.put("pastfactions", pfs.toString());

			document.put("globalinfo", globalInfoDocument);

			document.put("punishments", punishments.toString());

			JsonArray punishments = new JsonArray();
			for (Punishment punishment : this.punishments) {
				punishments.add(Punishment.SERIALIZER.serialize(punishment));
			}
			document.put("punishments", punishments.toString());

			collection.replaceOne(Filters.eq("uuid", uuid.toString()), document, new ReplaceOptions().upsert(true));
		});
	}

	public String getDisplayName() {
		return getActiveRank().getPrefix() + getActiveRank().getColor() + getUsername();
	}

	public void sendNameMCRemind() {
		if (getPlayer() != null) {
			getPlayer().sendMessage(CC.translate("&cYou still haven't claimed your free rank by liking our page on NameMC. For more information run the command /namemc on any of our servers."));
			getPlayer().sendMessage(CC.translate(" "));

			FancyMessage message = new FancyMessage();

			message.text("If you do not want to see these notifications anymore click ")
					.color(ChatColor.RED);

			message.then().text("here")
					.color(ChatColor.YELLOW)
					.tooltip(CC.translate("&eClick here to disable NameMC reminder notifications"))
					.command("/namemc togglenotifs");

			message.then().text(".")
					.color(ChatColor.RED);

			message.send(getPlayer());
		}
	}

	public PastFaction pfByName(String toSearch) {
		for (PastFaction pastFaction : this.globalInfo.getPastFactions()) {
			if (pastFaction.getFaction().equals(toSearch)) {
				return pastFaction;
			}
		}
		return null;
	}

	public void setPrimeTime(long time) {
		this.primedaily = System.currentTimeMillis() + time;
	}

	public Friend friendByName(UUID toSearch) {
		for (Friend friend : this.getFriends()) {
			if (friend.getFriend().equals(toSearch)) {
				return friend;
			}
		}
		return null;
	}

	public FriendRequest friendRequestByName(UUID toSearch) {
		for (FriendRequest friendRequest : this.getFriendRequests()) {
			if (friendRequest.getSender().equals(toSearch)) {
				return friendRequest;
			}
		}
		return null;
	}

	public List<String> colorAlts() {
		List<String> coloredAlts = new ArrayList<>();

		for (UUID id : getKnownAlts()) {
			Profile profile = Profile.getByUuid(id);
			if (profile == null || !profile.isLoaded())
				continue;
			if (profile.getActiveBan() != null) {
				coloredAlts.add(CC.translate("&c" + UniqueIDCache.name(id)));
				continue;
			}
			if (profile.getActiveBlacklist() != null) {
				coloredAlts.add(CC.translate("&4" + UniqueIDCache.name(id)));
				continue;
			}
			if (profile.getPlayer() == null || !profile.getPlayer().isOnline()) {
				coloredAlts.add(CC.translate("&7" + UniqueIDCache.name(id)));
				continue;
			}
			if (profile.getPlayer().isOnline()) {
				coloredAlts.add(CC.translate("&a" + UniqueIDCache.name(id)));
			}
		}

		return coloredAlts;
	}

	public boolean isLoaded() {
		if (this.username == null || this.username.equals(""))
			return false;

		return this.loaded;
	}

}
