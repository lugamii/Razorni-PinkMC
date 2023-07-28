package dev.razorni.core.profile.grant;

import dev.razorni.core.profile.punishment.Punishment;
import dev.razorni.core.extras.rank.Rank;
import dev.razorni.core.util.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

public class Grant {

	public static GrantJsonSerializer SERIALIZER = new GrantJsonSerializer();
	public static GrantJsonDeserializer DESERIALIZER = new GrantJsonDeserializer();

	@Getter private final UUID uuid;
	@Getter private final Rank rank;
	@Getter @Setter private UUID addedBy;
	@Getter private final long addedAt;
	@Getter private final String addedReason;
	@Getter private final long duration;
	@Getter @Setter private UUID removedBy;
	@Getter @Setter private long removedAt;
	@Getter @Setter private String removedReason;
	@Getter @Setter private boolean removed;

	public Grant(UUID uuid, Rank rank, UUID addedBy, long addedAt, String addedReason, long duration) {
		this.uuid = uuid;
		this.rank = rank;
		this.addedBy = addedBy;
		this.addedAt = addedAt;
		this.addedReason = addedReason;
		this.duration = duration;
	}

	public boolean isPermanent() {
		return duration == Integer.MAX_VALUE;
	}

	public boolean hasExpired() {
		return (!isPermanent()) && (System.currentTimeMillis() >= addedAt + duration);
	}

	public String getAddedAtDate() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("AEST"));
		return simpleDateFormat.format(new Date(addedAt));
	}

	public String getExpiresAtDate() {
		return duration == Integer.MAX_VALUE ? "Never" : TimeUtil.dateToString(new Date(addedAt + duration));
	}

	public String getTimeRemaining() {
		if (removed) {
			return "Expired";
		}

		if (isPermanent()) {
			return "Permanent";
		}

		return Punishment.TimeUtils.formatLongIntoDetailedString((duration + addedAt - System.currentTimeMillis()) / 1000L);
	}

	public String getRemovedAtDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.setTimeZone(TimeZone.getTimeZone("AEST"));
		return sdf.format(new Date(this.removedAt));
	}

	@Override
	public boolean equals(Object object) {
		return object != null && object instanceof Grant && ((Grant) object).uuid.equals(uuid);
	}

}
