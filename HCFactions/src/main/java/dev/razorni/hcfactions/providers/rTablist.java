package dev.razorni.hcfactions.providers;

import dev.razorni.hcfactions.utils.menuapi.CC;
import dev.razorni.hcfactions.events.koth.Koth;
import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.player.Member;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.users.settings.TeamListSetting;
import dev.razorni.hcfactions.utils.Formatter;
import dev.razorni.hcfactions.utils.Utils;
import dev.razorni.hcfactions.utils.extra.FastReplaceString;
import dev.razorni.hcfactions.utils.tablist.Tablist;
import dev.razorni.hcfactions.utils.tablist.TablistAdapter;
import dev.razorni.hcfactions.utils.tablist.TablistManager;
import dev.razorni.hcfactions.utils.tablist.extra.TablistEntry;
import dev.razorni.hcfactions.utils.tablist.extra.TablistSkin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class rTablist extends Module<TablistManager> implements TablistAdapter {
    private final List<String> farRightTablist;
    private final List<String> leftTablist;
    private final List<String> middleTablist;
    private final List<String> rightTablist;
    private final String[] footer;
    private final String[] header;
    private long lastMillisFooter = System.currentTimeMillis();
    private long lastMillisTitle = System.currentTimeMillis();
    private int iFooter = 0;
    private int iTitle = 0;

    public rTablist(TablistManager manager) {
        super(manager);
        this.header = this.getTablistConfig().getStringList("TABLIST_INFO.HEADER").toArray(new String[0]);
        this.footer = this.getTablistConfig().getStringList("TABLIST_INFO.FOOTER").toArray(new String[0]);
        this.leftTablist = this.getTablistConfig().getStringList("LEFT");
        this.middleTablist = this.getTablistConfig().getStringList("MIDDLE");
        this.rightTablist = this.getTablistConfig().getStringList("RIGHT");
        this.farRightTablist = this.getTablistConfig().getStringList("FAR_RIGHT");
        this.load();
    }

    private static String getDirection(Player player) {
        double rot = (player.getLocation().getYaw() - 90.0f) % 360.0f;
        if (rot < 0) {
            rot += 360.0;
        }
        if (0 <= rot && rot < 22.5) {
            return CC.translate("W");
        } else if (22.5 <= rot && rot < 67.5) {
            return CC.translate("NW");
        } else if (67.5 <= rot && rot < 112.5) {
            return CC.translate("N");
        } else if (112.5 <= rot && rot < 157.5) {
            return CC.translate("NE");
        } else if (157.5 <= rot && rot < 202.5) {
            return CC.translate("E");
        } else if (202.5 <= rot && rot < 247.5) {
            return CC.translate("SE");
        } else if (247.5 <= rot && rot < 292.5) {
            return CC.translate("S");
        } else if (292.5 <= rot && rot < 337.5) {
            return CC.translate("SW");
        } else if (337.5 <= rot && rot < 360.0) {
            return CC.translate("W");
        } else {
            return null;
        }
    }

    public String kothname() {
        for (Koth koth : this.getInstance().getKothManager().getKoths().values()) {
            if (koth.isActive()) {
                return "Name: " + CC.RED + koth.getName();
            }
        }
        return "None";
    }

    public String kothtime() {
        for (Koth koth : this.getInstance().getKothManager().getKoths().values()) {
            if (koth.isActive()) {
                return CC.translate("&fTime: &c" + Formatter.formatMMSS(koth.getRemaining()));
            }
        }
        return "";
    }

    @Override
    public Tablist getInfo(Player player) {
        Tablist tablist = this.getManager().getTablists().get(player.getUniqueId());
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(player.getLocation());
        Location location = player.getLocation();
        List<PlayerTeam> teams = this.getInstance().getTeamManager().getTeamSorting().getList(player);
        PlayerTeam playerTeam = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        for (int i = 0; i < 20; ++i) {
            tablist.add(0, i, this.leftTablist.get(i));
            tablist.add(1, i, this.middleTablist.get(i));
            tablist.add(2, i, this.rightTablist.get(i));
            tablist.add(3, i, this.farRightTablist.get(i));
        }
        for (TablistEntry entry : tablist.getEntries().values()) {
            String text = entry.getText();
            entry.setPing(5);
            if (text.isEmpty()) {
                continue;
            }
            if (text.contains("%kothname%")) {
                text = Utils.fastReplace(text, "%kothname%", kothname());
            }
            if (text.contains("%kothtime%")) {
                text = Utils.fastReplace(text, "%kothtime%", kothtime());
            }
            if (text.contains("%skinlocation%")) {
                text = Utils.fastReplace(text, "%skinlocation%", String.valueOf(this.getManager().getSkins().put("SKINS", new TablistSkin("eyJ0aW1lc3RhbXAiOjE1MTE3Mzk5MDc1MzksInByb2ZpbGVJZCI6IjIzZDE4YjNhN2E1NjQyM2E4NDZmZGJlNGVjYjJmNzJmIiwicHJvZmlsZU5hbWUiOiJHZW1pbml4UGxheXMiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2E1NmUyMWE5MzYyODE1ZDJiNzJiNjg5ZTc2NmZhZmQzYmVlY2U5OTRjM2QxMDI3ODg3ZjM3MmEyZjkyOWZmMyJ9fX0=", "IOC895nkomyPq/eU8RNbWV543JMrY6we0XPyqaZ4i1mFW+wtv6GRx6fB6/N8QM+FgF9l6lqoADeij8tbJoqvmOYp4zvcE0B3zVmlH9si61V//6uAxzYTZNZUymKENI9rTv6PS9YasvnN2ybcARe0P+C9tVPE1rUcyL6PUObW9vew3yT9XVRJDuv5NEySOWHr+q+tG7xuOH5c+1h1HX+Lnmpg/lMqJvkfNbBGcVtbvcyHUCslwx0b6o03AbJ+lfPyRJ4S4VB9X0UJFSC6aGG5vGijGYatrwcCBB1HKqRVyF0AzVZ4rNmDeHGBvXDWwrYAF0K8Bny4QBHQUctJKCiYVF5hk6gkQPABxKsMDMqe3tK6Zs7riI28L1JXGxjG4EsnsG9r+bWawNrJXUJnLxD3vG4Wq7EXVBwTKt3a5SzV5MtWVHwQ66ROQCOjgIc/BHgFwQkEk01S08u1zH3PECqgcWnFyUQeq/ujIuxftz5i0NS2YiMLXAevx1jGavOl330FXaKWJ6j4RTaUVO7c8iPLo1kr4p+pcrIVdGjDSLYjI1N4R3M3EmKipcrOzqj6MPDU/qFRKYPKFcIf6Yt5IYnSUzC86piomaiks/A13YbhyIij/DWMu9tGZZgf4r1Ev/kprHJDRSM/1uwAZAkUgk0qVha/vIu8DhqtI8EGbvibM5o="))));
            }
            if (text.contains("%title%")) {
                text = Utils.fastReplace(text, "%title%", titles());
            }
            if (text.contains("%factionname%")) {
                if (playerTeam != null) {
                    text = Utils.fastReplace(text, "%factionname%", "Name: " + playerTeam.getName());
                } else {
                    text = Utils.fastReplace(text, "%factionname%", "None");
                }
            }
            if (text.contains("%factionbalance%")) {
                if (playerTeam != null) {
                    text = Utils.fastReplace(text, "%factionbalance%", CC.translate("&6&l┃&f Balance: $" + playerTeam.getBalance()));
                } else {
                    text = Utils.fastReplace(text, "%factionbalance%", "");
                }
            }
            if (text.contains("%factionhq%")) {
                if (playerTeam != null) {
                    text = Utils.fastReplace(text, "%factionhq%", CC.translate("&6&l┃&f HQ: " + playerTeam.getHQFormatted()));
                } else {
                    text = Utils.fastReplace(text, "%factionhq%", "");
                }
            }
            if (playerTeam != null) {
                List<String> teamFormat = this.getTablistConfig().getStringList("TEAM_FORMAT.IN_TEAM");
                List<Member> teamMembers = playerTeam.getOnlineMembers();
                teamMembers.sort((x, y) -> y.getRole().ordinal() - x.getRole().ordinal());
                text = Utils.fastReplace(text, "%team%", playerTeam.getDisplayName(player));
                TablistSkin playerlocation1312 = new TablistSkin("eyJ0aW1lc3RhbXAiOjE1MTE3Mzk5MDc1MzksInByb2ZpbGVJZCI6IjIzZDE4YjNhN2E1NjQyM2E4NDZmZGJlNGVjYjJmNzJmIiwicHJvZmlsZU5hbWUiOiJHZW1pbml4UGxheXMiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2E1NmUyMWE5MzYyODE1ZDJiNzJiNjg5ZTc2NmZhZmQzYmVlY2U5OTRjM2QxMDI3ODg3ZjM3MmEyZjkyOWZmMyJ9fX0=", "IOC895nkomyPq/eU8RNbWV543JMrY6we0XPyqaZ4i1mFW+wtv6GRx6fB6/N8QM+FgF9l6lqoADeij8tbJoqvmOYp4zvcE0B3zVmlH9si61V//6uAxzYTZNZUymKENI9rTv6PS9YasvnN2ybcARe0P+C9tVPE1rUcyL6PUObW9vew3yT9XVRJDuv5NEySOWHr+q+tG7xuOH5c+1h1HX+Lnmpg/lMqJvkfNbBGcVtbvcyHUCslwx0b6o03AbJ+lfPyRJ4S4VB9X0UJFSC6aGG5vGijGYatrwcCBB1HKqRVyF0AzVZ4rNmDeHGBvXDWwrYAF0K8Bny4QBHQUctJKCiYVF5hk6gkQPABxKsMDMqe3tK6Zs7riI28L1JXGxjG4EsnsG9r+bWawNrJXUJnLxD3vG4Wq7EXVBwTKt3a5SzV5MtWVHwQ66ROQCOjgIc/BHgFwQkEk01S08u1zH3PECqgcWnFyUQeq/ujIuxftz5i0NS2YiMLXAevx1jGavOl330FXaKWJ6j4RTaUVO7c8iPLo1kr4p+pcrIVdGjDSLYjI1N4R3M3EmKipcrOzqj6MPDU/qFRKYPKFcIf6Yt5IYnSUzC86piomaiks/A13YbhyIij/DWMu9tGZZgf4r1Ev/kprHJDRSM/1uwAZAkUgk0qVha/vIu8DhqtI8EGbvibM5o=");
                for (int i = 0; i < teamFormat.size(); ++i) {
                    String dtrColor = new FastReplaceString(teamFormat.get(i)).replaceAll("%name%", playerTeam.getName()).replaceAll("%balance%", String.valueOf(playerTeam.getBalance())).replaceAll("%team-dtr-color%", playerTeam.getDtrColor()).replaceAll("%team-dtr%", playerTeam.getDtrString()).replaceAll("%team-dtr-symbol%", playerTeam.getDtrSymbol()).replaceAll("%team-hq%", playerTeam.getHQFormatted()).endResult();
                    text = Utils.fastReplace(text, "%teaminfo-" + i + "%", dtrColor);
                }
                for (int i = 0; i < teamMembers.size(); ++i) {
                    Member member = teamMembers.get(i);
                    String memberFormat = new FastReplaceString(this.getTablistConfig().getString("TEAM_FORMAT.MEMBER_FORMAT")).replaceAll("%role%", member.getAsterisk()).replaceAll("%player%", Bukkit.getPlayer(member.getUniqueID()).getName()).endResult();
                    text = Utils.fastReplace(text, "%member-" + i + "%", memberFormat);
                }
            } else {
                List<String> noTeamFormat = this.getTablistConfig().getStringList("TEAM_FORMAT.NO_TEAM");
                text = Utils.fastReplace(text, "%team%", "");
                for (int i = 0; i < noTeamFormat.size(); ++i) {
                    String tt = noTeamFormat.get(i);
                    text = Utils.fastReplace(text, "%teaminfo-" + i + "%", tt);
                }
            }
            for (int i = 0; i < teams.size() && i != 19; ++i) {
                TeamListSetting setting = user.getTeamListSetting();
                PlayerTeam targetTeam = teams.get(i);
                String dtr = setting.name().contains("DTR") ? "DTR" : "ONLINE";
                String listFormat = new FastReplaceString(this.getTablistConfig().getString("TEAM_FORMAT.LIST_FORMAT." + dtr)).replaceAll("%team-name%", targetTeam.getDisplayName(player)).replaceAll("%dtr-color%", targetTeam.getDtrColor()).replaceAll("%dtr%", targetTeam.getDtrString()).replaceAll("%dtr-symbol%", targetTeam.getDtrSymbol()).replaceAll("%max-dtr%", Formatter.formatDtr(targetTeam.getMaxDtr())).replaceAll("%team-online%", String.valueOf(targetTeam.getOnlinePlayers().size())).replaceAll("%team-max-online%", String.valueOf(targetTeam.getPlayers().size())).endResult();
                text = Utils.fastReplace(text, "%team-" + i + "%", listFormat);
            }
            if (text.contains("%team-") || text.contains("%teaminfo-") || text.contains("%member-")) {
                entry.setText("");
            } else {
                entry.setText(new FastReplaceString(text).replaceAll("%balance%", String.valueOf(user.getBalance())).replaceAll("%lives%", String.valueOf(user.getLives())).replaceAll("%kills%", String.valueOf(user.getKills())).replaceAll("%deaths%", String.valueOf(user.getDeaths())).replaceAll("%online%", String.valueOf(Bukkit.getOnlinePlayers().size())).replaceAll("%max-online%", String.valueOf(Bukkit.getMaxPlayers())).replaceAll("%location%", "(" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockZ() + ") [" + getDirection(player) + "]").replaceAll("%claim%", team.getDisplayName(player)).endResult());
            }
        }
        return tablist;
    }

    private void load() {
        for (int i = 0; i < 20; ++i) {
            String[] left = this.leftTablist.get(i).split(";");
            this.leftTablist.set(i, (left.length == 1) ? "" : left[1]);
            String[] middle = this.middleTablist.get(i).split(";");
            this.middleTablist.set(i, (middle.length == 1) ? "" : middle[1]);
            String[] right = this.rightTablist.get(i).split(";");
            this.rightTablist.set(i, (right.length == 1) ? "" : right[1]);
            String[] farRight = this.farRightTablist.get(i).split(";");
            this.farRightTablist.set(i, (farRight.length == 1) ? "" : farRight[1]);
        }
    }

    private String footer() {
        List<String> footers = CC.translate(this.getTablistConfig().getStringList("TABLIST_INFO.TAB-FOOTER_CONFIG.CHANGES"));
        long time = System.currentTimeMillis();
        long interval = TimeUnit.MILLISECONDS.toMillis(this.getTablistConfig().getInt("TABLIST_INFO.TAB-FOOTER_CONFIG.CHANGER_TICKS"));

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

    private String titles() {
        List<String> titles = CC.translate(this.getTablistConfig().getStringList("TABLIST_INFO.CHANGES-TITLE"));
        long time = System.currentTimeMillis();
        long interval = TimeUnit.MILLISECONDS.toMillis(this.getTablistConfig().getInt("TABLIST_INFO.CHANGER_TICKS-TITLE"));

        if (lastMillisTitle + interval <= time) {
            if (iTitle != titles.size() - 1) {
                iTitle++;
            } else {
                iTitle = 0;
            }
            lastMillisTitle = time;
        }
        return titles.get(iTitle);
    }

    @Override
    public String[] getFooter(Player player) {
        String[] footer = this.footer.clone();
        for (int i = 0; i < footer.length; ++i) {
            String text = footer[i];
            footer[i] = text.replaceAll("%footer%", footer()).replaceAll("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()).replaceAll("%maxonline%", String.valueOf(Bukkit.getMaxPlayers())));
        }
        return footer;
    }

    @Override
    public String[] getHeader(Player player) {
        String[] header = this.header.clone();
        for (int i = 0; i < header.length; ++i) {
            String text = header[i];
            header[i] = text.replaceAll("%title%", titles()).replaceAll("%maxonline%", String.valueOf(Bukkit.getMaxPlayers())).replaceAll("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()));
        }
        return header;
    }
}