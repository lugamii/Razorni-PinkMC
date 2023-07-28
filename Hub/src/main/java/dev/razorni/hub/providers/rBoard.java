package dev.razorni.hub.providers;

import dev.razorni.core.Core;
import dev.razorni.core.extras.friends.Friend;
import dev.razorni.core.profile.Profile;
import dev.razorni.hub.Hub;
import dev.razorni.hub.framework.Module;
import dev.razorni.hub.utils.BungeeListener;
import dev.razorni.hub.utils.board.BoardAdapter;
import dev.razorni.hub.utils.board.BoardManager;
import dev.razorni.hub.utils.shits.CC;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class rBoard extends Module<BoardManager> implements BoardAdapter {

    private long lastMillisFooter = System.currentTimeMillis();
    private int iFooter = 0;

    public rBoard(BoardManager manager) {
        super(manager);
    }

    public String getString(String string) {
        String s = Hub.getInstance().getSettingsConfig().getConfig().getString(string);
        return s.equals("") ? null : s;
    }

    @Override
    public String getTitle(Player player) {
        return this.getString("SCOREBOARD_INFO.TITLE");
    }


    public int onlineFriends(Player player) {
        int friends = 0;
        for (Friend friend : Profile.getByUuid(player.getUniqueId()).getFriends()) {
            Profile friendProfile = Profile.getByUuid(friend.getFriend());
            if (friendProfile.isOnline()) {
                friends++;
            }
        }
        return friends;
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> toReturn = new ArrayList<>();
        String prime = "";
        if (Profile.getByUuid(player.getUniqueId()).isPrime()) {
            prime = CC.translate("&6✪");
        }
        if (Profile.getByUuid(player.getUniqueId()).getActiveBlacklist() != null) {
            toReturn = Hub.getInstance().getSettingsConfig().getConfig().getStringList("LINES.BLACKLISTED")
                    .stream()
                    .map(CC::translate)
                    .map(line -> PlaceholderAPI.setPlaceholders(player, line))
                    .map(line -> line.replace("%footer%", footer()))
                    .map(line -> line.replace("%friends%", String.valueOf(onlineFriends(player))))
                    .map(line -> line.replace("%reason%", Profile.getByUuid(player.getUniqueId()).getActiveBlacklist().getAddedReason()))
                    .map(line -> line.replace("%player%", player.getName()))
                    .map(line -> line.replace("%coins%", String.valueOf(Core.getInstance().getCoreAPI().getCoins(player))))
                    .map(line -> line.replace("%rank%", Core.getInstance().getCoreAPI().getRankColor(player) + Core.getInstance().getCoreAPI().getRankName(player)))
                    .map(line -> line.replace("%online%", String.valueOf(BungeeListener.PLAYER_COUNT)))
                    .collect(Collectors.toList());
        } else if (Profile.getByUuid(player.getUniqueId()).getActiveBan() != null) {
              toReturn = Hub.getInstance().getSettingsConfig().getConfig().getStringList("LINES.BANNED")
                      .stream()
                      .map(CC::translate)
                      .map(line -> PlaceholderAPI.setPlaceholders(player, line))
                      .map(line -> line.replace("%footer%", footer()))
                      .map(line -> line.replace("%friends%", String.valueOf(onlineFriends(player))))
                      .map(line -> line.replace("%expires%", Profile.getByUuid(player.getUniqueId()).getActiveBan().getTimeRemaining2()))
                      .map(line -> line.replace("%reason%", Profile.getByUuid(player.getUniqueId()).getActiveBan().getAddedReason()))
                      .map(line -> line.replace("%player%", player.getName()))
                      .map(line -> line.replace("%coins%", String.valueOf(Core.getInstance().getCoreAPI().getCoins(player))))
                      .map(line -> line.replace("%rank%", Core.getInstance().getCoreAPI().getRankColor(player) + Core.getInstance().getCoreAPI().getRankName(player)))
                      .map(line -> line.replace("%online%", String.valueOf(BungeeListener.PLAYER_COUNT)))
                      .collect(Collectors.toList());
          } else if (Hub.getInstance().getQueueManager().inQueue(player)) {
            String finalPrime = prime;
            toReturn = Hub.getInstance().getSettingsConfig().getConfig().getStringList("LINES.QUEUED")
                        .stream()
                        .map(CC::translate)
                        .map(line -> PlaceholderAPI.setPlaceholders(player, line))
                        .map(line -> line.replace("%friends%", String.valueOf(onlineFriends(player))))
                        .map(line -> line.replace("%rank%", Core.getInstance().getCoreAPI().getRankColor(player) + Core.getInstance().getCoreAPI().getRankName(player)))
                        .map(line -> line.replace("%coins%", String.valueOf(Core.getInstance().getCoreAPI().getCoins(player))))
                        .map(line -> line.replace("%footer%", footer()))
                        .map(line -> line.replace("%prime%", finalPrime))
                        .map(line -> line.replace("%server%", String.valueOf(Hub.getInstance().getQueueManager().getQueueIn(player))))
                        .map(line -> line.replace("%position%",String.valueOf(Hub.getInstance().getQueueManager().getPosition(player))))
                        .map(line -> line.replace("%online%", String.valueOf(BungeeListener.PLAYER_COUNT)))
                        .map(line -> line.replace("%size%", String.valueOf(Hub.getInstance().getQueueManager().getInQueue(Hub.getInstance().getQueueManager().getQueueIn(player)))))
                        .collect(Collectors.toList());

            } else {
            String finalPrime1 = prime;
            toReturn = Hub.getInstance().getSettingsConfig().getConfig().getStringList("LINES.NORMAL")
                        .stream()
                        .map(CC::translate)
                        .map(line -> PlaceholderAPI.setPlaceholders(player, line))
                        .map(line -> line.replace("%footer%", footer()))
                        .map(line -> line.replace("%prime%", finalPrime1))
                        .map(line -> line.replace("%friends%", String.valueOf(onlineFriends(player))))
                        .map(line -> line.replace("%player%", player.getName()))
                        .map(line -> line.replace("%coins%", String.valueOf(Core.getInstance().getCoreAPI().getCoins(player))))
                        .map(line -> line.replace("%rank%", Core.getInstance().getCoreAPI().getRankColor(player) + Core.getInstance().getCoreAPI().getRankName(player)))
                        .map(line -> line.replace("%online%", String.valueOf(BungeeListener.PLAYER_COUNT)))
                        .collect(Collectors.toList());
            }
            if (Hub.getInstance().getSettingsConfig().getConfig().getBoolean("FOOTER_CONFIG.CHANGER.ENABLED")) {
                String footer = footer();
                toReturn = toReturn.stream().map(s -> s.replace("%footer%", footer)).collect(Collectors.toList());
            }
            return toReturn;
    }

    private String footer() {
        List<String> footers = CC.translate(Hub.getInstance().getSettingsConfig().getConfig().getStringList("FOOTER_CONFIG.CHANGES"));
        long time = System.currentTimeMillis();
        long interval = TimeUnit.MILLISECONDS.toMillis(Hub.getInstance().getSettingsConfig().getConfig().getInt("FOOTER_CONFIG.CHANGER_TICKS"));

        if (lastMillisFooter + interval <= time) {
            if (iFooter != footers.size() - 1) {
                iFooter++;
            } else {
                iFooter = 0;
            }
            lastMillisFooter = time;
        }
        return footers.get(iFooter);
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
                String fDays = days > 0 ? " " + days + ":" + (days > 1 ? "" : "") : "";
                String fHours = hours > 0 ? " " + hours + ":" + (hours > 1 ? "" : "") : "";
                String fMinutes = minutes > 0 ? " " + minutes + ":" + (minutes > 1 ? "" : "") : "";
                String fSeconds = seconds > 0 ? " " + seconds + ":" + (seconds > 1 ? "" : "") : "";
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

    public String getBanTimeRemaining(Player player) {
        if (Profile.getByUuid(player.getUniqueId()).getActiveBan().isPermanent()) {
            return "Never";
        }

        return TimeUtils.formatLongIntoDetailedString((Profile.getByUuid(player.getUniqueId()).getActiveBan().getDuration() - System.currentTimeMillis()) / 1000L);
    }

}
