package dev.razorni.core.profile.punishment;

import dev.razorni.core.Core;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.fanciful.FancyMessage;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

@Getter
public class Punishment {

    public static PunishmentJsonSerializer SERIALIZER = new PunishmentJsonSerializer();
    public static PunishmentJsonDeserializer DESERIALIZER = new PunishmentJsonDeserializer();
    
    private final UUID uuid;
    
    private final PunishmentType type;
    
    @Setter
    private UUID addedBy;
    
    final private long addedAt;
    
    private final String addedReason;
    
    final private long duration;
    
    @Setter
    private UUID resolvedBy;
    
    @Setter
    private long resolvedAt;
    
    @Setter
    private String resolvedReason;
    
    @Setter
    private boolean resolved;

    public Punishment(UUID uuid, PunishmentType type, long addedAt, String addedReason, long duration) {
        this.uuid = uuid;
        this.type = type;
        this.addedAt = addedAt;
        this.addedReason = addedReason;
        this.duration = duration;
    }

    public boolean isPermanent() {
        return type == PunishmentType.BLACKLIST || duration == Integer.MAX_VALUE;
    }

    public boolean isActive() {
        return !resolved && (isPermanent() || getMillisRemaining() < 0);
    }

    public long getMillisRemaining() {
        return System.currentTimeMillis() - (addedAt + duration);
    }

    public String getAddedAtDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("AEST"));
        return simpleDateFormat.format(new Date(addedAt));
    }

    public String getTimeRemaining() {
        if (resolved) {
            return "Resolved";
        }

        if (isPermanent()) {
            return "Permanent";
        }

        if (!isActive()) {
            return "Expired";
        }

        return TimeUtils.formatLongIntoDetailedString((duration + addedAt - System.currentTimeMillis()) / 1000L);
    }

    public String getTimeRemaining2() {
        if (resolved) {
            return "Resolved";
        }

        if (isPermanent()) {
            return "Permanent";
        }

        if (!isActive()) {
            return "Expired";
        }

        return TimeUtils2.formatLongIntoDetailedString((duration + addedAt - System.currentTimeMillis()) / 1000L);
    }

    public String getSilentText(boolean silent) {
        if (!silent) {
            return "";
        } else {
            return CC.translate("&esilently &a");
        }
    }

    public String getContext() {
        if (!(type == PunishmentType.BAN || type == PunishmentType.MUTE)) {
            return resolved ? type.getUndoContext() : type.getContext();
        }

        if (isPermanent()) {
            return (resolved ? type.getUndoContext() : "permanently " + type.getContext());
        } else {
            return (resolved ? type.getUndoContext() : "temporarily " + type.getContext());
        }
    }

    public String getContext(boolean silent) {
        if (!(type == PunishmentType.BAN || type == PunishmentType.MUTE)) {
            return resolved ? type.getUndoContext() : type.getContext();
        }

        if (isPermanent()) {
            return (resolved ? type.getUndoContext() : getSilentText(silent) + "permanently " + type.getContext());
        } else {
            return (resolved ? type.getUndoContext() : getSilentText(silent) + "temporarily " + type.getContext());
        }
    }

    public void broadcast(String sender, String target, boolean silent) {
        String msg = CC.translate(Core.getInstance().getConfig().getString("PUNISHMENTS.BROADCAST")
                .replace("{context}", getContext(true))
                .replace("{target}", target)
                .replace("{sender}", sender));
        if (silent) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                FancyMessage message = new FancyMessage();
                if (player.hasPermission("gravity.staff")) {
                    message.text(msg).tooltip(CC.translate(Arrays.asList(
                            "&fReason&7: &6" + this.getAddedReason(),
                            "&fDuration&7: &6" + (!isPermanent() ? getTimeRemaining() : "Permanent"
                            ))));
                    message.send(player);
                }
            });
        } else {
            Bukkit.getOnlinePlayers().forEach(player -> {
                FancyMessage message = new FancyMessage();
                if (player.hasPermission("gravity.staff")) {
                    message.text(msg).tooltip(CC.translate(Arrays.asList(
                            "&fReason&7: &6" + this.getAddedReason(),
                            "&fDuration&7: &6" + (!isPermanent() ? getTimeRemaining() : "Permanent"
                    ))));
                    message.send(player);
                } else {
                    player.sendMessage(msg);
                }
            });
        }
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    public String getKickMessage() {
        String kickMessage;

        if (type == PunishmentType.BAN) {
            kickMessage = CC.translate(Core.getInstance().getConfig().getString("PUNISHMENTS.BAN.KICK"));
            String temporary = "";

            if (!isPermanent()) {
                temporary = CC.translate(Core.getInstance().getConfig().getString("PUNISHMENTS.BAN.TEMPORARY"));
                temporary = CC.translate(temporary.replace("{time-remaining}", getTimeRemaining()));
            }

            kickMessage = CC.translate(kickMessage.replace("{context}", getContext())
                    .replace("{temporary}", temporary));
        } else if (type == PunishmentType.KICK) {
            kickMessage = CC.translate(Core.getInstance().getConfig().getString("PUNISHMENTS.KICK.KICK")
                    .replace("{context}", getContext())
                    .replace("{reason}", addedReason));
        } else if (type == PunishmentType.BLACKLIST) {
            kickMessage = CC.translate(Core.getInstance().getConfig().getString("PUNISHMENTS.BLACKLISTED", "&cYour account is blacklisted from the Orbit Network.\n\n&cThis punishment cannot be appealed."));
        } else {
            kickMessage = null;
        }

        return CC.translate(kickMessage);
    }

    @Override
    public boolean equals(Object object) {
        return object != null && object instanceof Punishment && ((Punishment) object).uuid.equals(uuid);
    }

    public static final class TimeUtils {
        private static final ThreadLocal<StringBuilder> mmssBuilder = ThreadLocal.withInitial(StringBuilder::new);
        private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");

        private TimeUtils() {
        }

        public static String formatIntoHHMMSS(int secs) {
            return formatIntoMMSS(secs);
        }

        public static String formatLongIntoHHMMSS(long secs) {
            int unconvertedSeconds = (int)secs;
            return formatIntoMMSS(unconvertedSeconds);
        }

        public static String formatIntoMMSS(int secs) {
            int seconds = secs % 60;
            secs -= seconds;
            long minutesCount = (long)(secs / 60);
            long minutes = minutesCount % 60L;
            minutesCount -= minutes;
            long hours = minutesCount / 60L;
            StringBuilder result = (StringBuilder)mmssBuilder.get();
            result.setLength(0);
            if (hours > 0L) {
                if (hours < 10L) {
                    result.append("0");
                }

                result.append(hours);
                result.append(":");
            }

            if (minutes < 10L) {
                result.append("0");
            }

            result.append(minutes);
            result.append(":");
            if (seconds < 10) {
                result.append("0");
            }

            result.append(seconds);
            return result.toString();
        }

        public static String formatLongIntoMMSS(long secs) {
            int unconvertedSeconds = (int)secs;
            return formatIntoMMSS(unconvertedSeconds);
        }

        public static String formatIntoDetailedString(int secs) {
            if (secs == 0) {
                return "0 seconds";
            } else {
                int remainder = secs % 86400;
                int days = secs / 86400;
                int hours = remainder / 3600;
                int minutes = remainder / 60 - hours * 60;
                int seconds = remainder % 3600 - minutes * 60;
                String fDays = days > 0 ? " " + days + " day" + (days > 1 ? "s" : "") : "";
                String fHours = hours > 0 ? " " + hours + " hour" + (hours > 1 ? "s" : "") : "";
                String fMinutes = minutes > 0 ? " " + minutes + " minute" + (minutes > 1 ? "s" : "") : "";
                String fSeconds = seconds > 0 ? " " + seconds + " second" + (seconds > 1 ? "s" : "") : "";
                return (fDays + fHours + fMinutes + fSeconds).trim();
            }
        }

        public static String formatLongIntoDetailedString(long secs) {
            int unconvertedSeconds = (int)secs;
            return formatIntoDetailedString(unconvertedSeconds);
        }

        public static String formatIntoCalendarString(Date date) {
            return dateFormat.format(date);
        }

        public static int parseTime(String time) {
            if (!time.equals("0") && !time.equals("")) {
                String[] lifeMatch = new String[]{"w", "d", "h", "m", "s"};
                int[] lifeInterval = new int[]{604800, 86400, 3600, 60, 1};
                int seconds = -1;

                for(int i = 0; i < lifeMatch.length; ++i) {
                    for(Matcher matcher = Pattern.compile("([0-9]+)" + lifeMatch[i]).matcher(time); matcher.find(); seconds += Integer.parseInt(matcher.group(1)) * lifeInterval[i]) {
                        if (seconds == -1) {
                            seconds = 0;
                        }
                    }
                }

                if (seconds == -1) {
                    throw new IllegalArgumentException("Invalid time provided.");
                } else {
                    return seconds;
                }
            } else {
                return 0;
            }
        }

        public static long parseTimeToLong(String time) {
            int unconvertedSeconds = parseTime(time);
            long seconds = (long)unconvertedSeconds;
            return seconds;
        }

        public static int getSecondsBetween(Date a, Date b) {
            return (int)getSecondsBetweenLong(a, b);
        }

        public static long getSecondsBetweenLong(Date a, Date b) {
            long diff = a.getTime() - b.getTime();
            long absDiff = Math.abs(diff);
            return absDiff / 1000L;
        }
    }

    public static final class TimeUtils2 {
        private static final ThreadLocal<StringBuilder> mmssBuilder = ThreadLocal.withInitial(StringBuilder::new);
        private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");

        private TimeUtils2() {
        }

        public static String formatIntoHHMMSS(int secs) {
            return formatIntoMMSS(secs);
        }

        public static String formatLongIntoHHMMSS(long secs) {
            int unconvertedSeconds = (int)secs;
            return formatIntoMMSS(unconvertedSeconds);
        }

        public static String formatIntoMMSS(int secs) {
            int seconds = secs % 60;
            secs -= seconds;
            long minutesCount = (long)(secs / 60);
            long minutes = minutesCount % 60L;
            minutesCount -= minutes;
            long hours = minutesCount / 60L;
            StringBuilder result = (StringBuilder)mmssBuilder.get();
            result.setLength(0);
            if (hours > 0L) {
                if (hours < 10L) {
                    result.append("0");
                }

                result.append(hours);
                result.append(":");
            }

            if (minutes < 10L) {
                result.append("0");
            }

            result.append(minutes);
            result.append(":");
            if (seconds < 10) {
                result.append("0");
            }

            result.append(seconds);
            return result.toString();
        }

        public static String formatLongIntoMMSS(long secs) {
            int unconvertedSeconds = (int)secs;
            return formatIntoMMSS(unconvertedSeconds);
        }

        public static String formatIntoDetailedString(int secs) {
            if (secs == 0) {
                return "0 seconds";
            } else {
                int remainder = secs % 86400;
                int days = secs / 86400;
                int hours = remainder / 3600;
                int minutes = remainder / 60 - hours * 60;
                int seconds = remainder % 3600 - minutes * 60;
                String fDays = days > 0 ? "" + days + ":" + (days > 1 ? "" : "") : "";
                String fHours = hours > 0 ? "" + hours + ":" + (hours > 1 ? "" : "") : "";
                String fMinutes = minutes > 0 ? "" + minutes + ":" + (minutes > 1 ? "" : "") : "";
                String fSeconds = seconds > 0 ? "" + seconds + "" + (seconds > 1 ? "" : "") : "";
                return (fDays + fHours + fMinutes + fSeconds).trim();
            }
        }

        public static String formatLongIntoDetailedString(long secs) {
            int unconvertedSeconds = (int)secs;
            return formatIntoDetailedString(unconvertedSeconds);
        }

        public static String formatIntoCalendarString(Date date) {
            return dateFormat.format(date);
        }

        public static int parseTime(String time) {
            if (!time.equals("0") && !time.equals("")) {
                String[] lifeMatch = new String[]{"w", "d", "h", "m", "s"};
                int[] lifeInterval = new int[]{604800, 86400, 3600, 60, 1};
                int seconds = -1;

                for(int i = 0; i < lifeMatch.length; ++i) {
                    for(Matcher matcher = Pattern.compile("([0-9]+)" + lifeMatch[i]).matcher(time); matcher.find(); seconds += Integer.parseInt(matcher.group(1)) * lifeInterval[i]) {
                        if (seconds == -1) {
                            seconds = 0;
                        }
                    }
                }

                if (seconds == -1) {
                    throw new IllegalArgumentException("Invalid time provided.");
                } else {
                    return seconds;
                }
            } else {
                return 0;
            }
        }

        public static long parseTimeToLong(String time) {
            int unconvertedSeconds = parseTime(time);
            long seconds = (long)unconvertedSeconds;
            return seconds;
        }

        public static int getSecondsBetween(Date a, Date b) {
            return (int)getSecondsBetweenLong(a, b);
        }

        public static long getSecondsBetweenLong(Date a, Date b) {
            long diff = a.getTime() - b.getTime();
            long absDiff = Math.abs(diff);
            return absDiff / 1000L;
        }
    }


}
