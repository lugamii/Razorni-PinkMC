package dev.razorni.hub.providers;

import dev.razorni.core.Core;
import dev.razorni.core.extras.rank.Rank;
import dev.razorni.core.profile.Profile;
import dev.razorni.hub.Hub;
import dev.razorni.hub.utils.BungeeListener;
import dev.razorni.hub.utils.shits.CC;
import dev.razorni.hub.utils.tab.manager.TabColumn;
import dev.razorni.hub.utils.tab.manager.TabLayout;
import dev.razorni.hub.utils.tab.manager.TabProvider;
import dev.razorni.hub.utils.tab.skin.Skin;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class rTab implements TabProvider {

    FileConfiguration config;

    private long lastMillisFooter = System.currentTimeMillis();
    private long lastMillisTitle = System.currentTimeMillis();
    private int iFooter = 0;
    private long lastMillisOfflineLoading = System.currentTimeMillis();
    private int OfflineLoading = 0;
    private int iTitle = 0;
    boolean kitsonline = false;

    public rTab() {
        this.config = Hub.getInstance().getSettingsConfig().getConfig();
    }

    @Override
    public List<String> getFooter(Player player) {
        return CC.translate(Hub.getInstance().getSettingsConfig().getConfig().getStringList("TABLIST_INFO.FOOTER"))
                .stream().map(message -> message
                        .replace("|", "\u2503")
                        .replace("%footer%", footer())
                        .replace("%online%", String.valueOf(BungeeListener.PLAYER_COUNT))).collect(Collectors.toList());
    }

    @Override
    public List<String> getHeader(Player player) {
        return CC.translate(Hub.getInstance().getSettingsConfig().getConfig().getStringList("TABLIST_INFO.HEADER"))
                .stream()
                .map(CC::translate)
                .map(line -> PlaceholderAPI.setPlaceholders(player, line))
                .map(line -> line.replace("%header%", titles()))
                .collect(Collectors.toList());
    }

    @SuppressWarnings({"deprecation", "unchecked", "rawtypes"})
    public String factions(Player player, String path) {
        PlaceholderAPI.setPlaceholders(player, path);
        if (path.contains("%max_players%")) {
            path = path.replace("%max_players%", String.valueOf(Bukkit.getMaxPlayers()));
        }
        if (path.contains("%title%")) {
            path = path.replace("%title%", titles());
        }
        if (path.contains("%online%")) {
            path = path.replace("%online%", String.valueOf(BungeeListener.PLAYER_COUNT));
        }
        if (path.contains("%rank%")) {
            path = path.replace("%rank%", Core.getInstance().getCoreAPI().getRankColor(player) + Core.getInstance().getCoreAPI().getRankName(player));
        }
        if (path.contains("%coins%")) {
            path = path.replace("%coins%", String.valueOf(Core.getInstance().getCoreAPI().getCoins(player)));
        }
        if (path.contains("%statushcf%")) {
            path = path.replace("%statushcf%", checkKitsStatus());
        }
        if (path.contains("%offline%")) {
            path = path.replace("%offline%", CC.translate(OfflineLoading()));
        }
        return path;
    }

    private String checkKitsStatus() {
        String status = "";
        if (KitsOnline()) {
            status = ChatColor.GREEN + "Online";
        } else {
            status = CC.translate(OfflineLoading());
        }
        return status;
    }


    private TabLayout getInfo(int slot, TabColumn place, Player p) {
        TabLayout object = new TabLayout();

        String text = "";
        switch (place) {
            case LEFT: {
                if (this.config.getString("TABLIST_INFO.LEFT." + slot + ".TEXT") == null ||
                        this.config.getString("TABLIST_INFO.LEFT." + slot + ".TEXT") == " " ||
                        this.config.getString("TABLIST_INFO.LEFT." + slot + ".TEXT") == "") {
                    text = " ";
                    break;
                }
                text = this.config.getString("TABLIST_INFO.LEFT." + slot + ".TEXT");
                break;
            }
            case RIGHT: {
                if (this.config.getString("TABLIST_INFO.RIGHT." + slot + ".TEXT") == null ||
                        this.config.getString("TABLIST_INFO.RIGHT." + slot + ".TEXT") == " " ||
                        this.config.getString("TABLIST_INFO.RIGHT." + slot + ".TEXT") == "") {
                    text = " ";
                    break;
                }
                text = this.config.getString("TABLIST_INFO.RIGHT." + slot + ".TEXT");
                break;
            }
            case MIDDLE: {
                if (this.config.getString("TABLIST_INFO.CENTER." + slot + ".TEXT") == null ||
                        this.config.getString("TABLIST_INFO.CENTER." + slot + ".TEXT") == " " ||
                        this.config.getString("TABLIST_INFO.CENTER." + slot + ".TEXT") == "") {
                    text = " ";
                    break;
                }
                text = this.config.getString("TABLIST_INFO.CENTER." + slot + ".TEXT");
                break;
            }
            case FAR_RIGHT: {
                if (this.config.getString("TABLIST_INFO.FARRIGHT." + slot + ".TEXT") == null ||
                        this.config.getString("TABLIST_INFO.FARRIGHT." + slot + ".TEXT") == " " ||
                        this.config.getString("TABLIST_INFO.FARRIGHT." + slot + ".TEXT") == "") {
                    text = " ";
                    break;
                }
                text = this.config.getString("TABLIST_INFO.FARRIGHT." + slot + ".TEXT");
                break;
            }
        }
        text = factions(p, text);
        object.setColumn(place);
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {

            object.setText(PlaceholderAPI.setPlaceholders(p, text));
        } else {
            object.setText(text);

        }
        object.setSlot(slot);

        String skin = "";
        switch (place) {
            case LEFT: {
                if (this.config.getString("TABLIST_INFO.LEFT." + slot + ".SKIN") == null || this.config.getString("TABLIST_INFO.LEFT." + slot + ".SKIN") == " " || this.config.getString("TABLIST_INFO.LEFT." + slot + ".SKIN") == "") {
                    skin = " ";
                    break;
                }
                skin = this.config.getString("TABLIST_INFO.LEFT." + slot + ".SKIN");
                break;
            }
            case RIGHT: {
                if (this.config.getString("TABLIST_INFO.RIGHT." + slot + ".SKIN") == null || this.config.getString("TABLIST_INFO.RIGHT." + slot + ".SKIN") == " " || this.config.getString("TABLIST_INFO.RIGHT." + slot + ".SKIN") == "") {
                    skin = " ";
                    break;
                }
                skin = this.config.getString("TABLIST_INFO.RIGHT." + slot + ".SKIN");
                break;
            }
            case MIDDLE: {
                if (this.config.getString("TABLIST_INFO.CENTER." + slot + ".SKIN") == null || this.config.getString("TABLIST_INFO.CENTER." + slot + ".SKIN") == " " || this.config.getString("TABLIST_INFO.CENTER." + slot + ".SKIN") == "") {
                    skin = " ";
                    break;
                }
                skin = this.config.getString("TABLIST_INFO.CENTER." + slot + ".SKIN");
                break;
            }
            case FAR_RIGHT: {
                if (this.config.getString("TABLIST_INFO.FARRIGHT." + slot + ".SKIN") == null || this.config.getString("TABLIST_INFO.FARRIGHT." + slot + ".SKIN") == " " || this.config.getString("TABLIST_INFO.FARRIGHT." + slot + ".SKIN") == "") {
                    skin = " ";
                    break;
                }
                skin = this.config.getString("TABLIST_INFO.FARRIGHT." + slot + ".SKIN");
                break;
            }
        }

        object.setSkin(skins(p, skin));
        return object;
    }


    public Skin skins(Player player, String skinTab) {
        Skin skinDefault = Skin.DEFAULT;

        Rank rank = Profile.getByUuid(player.getUniqueId()).getActiveRank();
        if (skinTab.contains("%ranksquare%")) {
            if (rank.getColor() == ChatColor.RED) {
                skinDefault = Skin.getDot(ChatColor.RED);
            } else if (rank.getColor() == ChatColor.DARK_RED) {
                skinDefault = Skin.getDot(ChatColor.DARK_RED);
            } else if (rank.getColor() == ChatColor.DARK_AQUA) {
                skinDefault = Skin.getDot(ChatColor.DARK_AQUA);
            } else if (rank.getColor() == ChatColor.AQUA) {
                skinDefault = Skin.getDot(ChatColor.AQUA);
            } else if (rank.getColor() == ChatColor.BLUE) {
                skinDefault = Skin.getDot(ChatColor.BLUE);
            } else if (rank.getColor() == ChatColor.DARK_BLUE) {
                skinDefault = Skin.getDot(ChatColor.DARK_BLUE);
            } else if (rank.getColor() == ChatColor.GRAY) {
                skinDefault = Skin.getDot(ChatColor.GRAY);
            } else if (rank.getColor() == ChatColor.DARK_GRAY) {
                skinDefault = Skin.getDot(ChatColor.DARK_GRAY);
            } else if (rank.getColor() == ChatColor.WHITE) {
                skinDefault = Skin.getDot(ChatColor.WHITE);
            } else if (rank.getColor() == ChatColor.YELLOW) {
                skinDefault = Skin.getDot(ChatColor.YELLOW);
            } else if (rank.getColor() == ChatColor.GREEN) {
                skinDefault = Skin.getDot(ChatColor.GREEN);
            } else if (rank.getColor() == ChatColor.DARK_GREEN) {
                skinDefault = Skin.getDot(ChatColor.DARK_GREEN);
            } else if (rank.getColor() == ChatColor.GOLD) {
                skinDefault = Skin.getDot(ChatColor.GOLD);
            } else if (rank.getColor() == ChatColor.LIGHT_PURPLE) {
                skinDefault = Skin.getDot(ChatColor.LIGHT_PURPLE);
            } else if (rank.getColor() == ChatColor.DARK_PURPLE) {
                skinDefault = Skin.getDot(ChatColor.DARK_PURPLE);
            }
        }
        if (skinTab.contains("%statuskits%")) {
            if (KitsOnline()) {
                skinDefault = Skin.ONLINE_SKIN;
            } else {
                skinDefault = Skin.OFFLINE_SKIN;
            }
        }
        if (skinTab.contains("%player%")) {
            skinDefault = Skin.getSkin(player);
        }
        if (skinTab.contains("%discord%")) {
            skinDefault = Skin.DISCORD_SKIN;
        }
        if (skinTab.contains("%youtube%")) {
            skinDefault = Skin.YOUTUBE_SKIN;
        }
        if (skinTab.contains("%twitter%")) {
            skinDefault = Skin.TWITTER_SKIN;

        }
        if (skinTab.contains("%facebook%")) {
            skinDefault = Skin.FACEBOOK_SKIN;

        }
        if (skinTab.contains("%store%")) {
            skinDefault = Skin.STORE_SKIN;
        }

        if (skinTab.contains("%green%")) {
            skinDefault = Skin.getDot(ChatColor.GREEN);
        }
        if (skinTab.contains("%blue%")) {
            skinDefault = Skin.getDot(ChatColor.BLUE);
        }
        if (skinTab.contains("%dark_blue%")) {
            skinDefault = Skin.getDot(ChatColor.DARK_BLUE);
        }
        if (skinTab.contains("%dark_aqua%")) {
            skinDefault = Skin.getDot(ChatColor.DARK_AQUA);
        }
        if (skinTab.contains("%dark_purple%")) {
            skinDefault = Skin.getDot(ChatColor.DARK_PURPLE);
        }
        if (skinTab.contains("%light_purple%")) {
            skinDefault = Skin.getDot(ChatColor.LIGHT_PURPLE);
        }
        if (skinTab.contains("%gray%")) {
            skinDefault = Skin.getDot(ChatColor.GRAY);
        }
        if (skinTab.contains("%red%")) {
            skinDefault = Skin.getDot(ChatColor.RED);
        }
        if (skinTab.contains("%yellow%")) {
            skinDefault = Skin.getDot(ChatColor.YELLOW);
        }
        if (skinTab.contains("%dark_green%")) {
            skinDefault = Skin.getDot(ChatColor.DARK_GREEN);
        }
        if (skinTab.contains("%dark_red%")) {
            skinDefault = Skin.getDot(ChatColor.DARK_RED);
        }
        if (skinTab.contains("%gold%")) {
            skinDefault = Skin.getDot(ChatColor.GOLD);
        }
        if (skinTab.contains("%aqua%")) {
            skinDefault = Skin.getDot(ChatColor.AQUA);
        }
        if (skinTab.contains("%white%")) {
            skinDefault = Skin.getDot(ChatColor.WHITE);
        }
        if (skinTab.contains("%dark_gray%")) {
            skinDefault = Skin.getDot(ChatColor.DARK_GRAY);
        }
        if (skinTab.contains("%black%")) {
            skinDefault = Skin.getDot(ChatColor.BLACK);
        }
        if (skinTab.contains("%warning%")) {
            skinDefault = Skin.WARNING_SKIN;
        }
        if (skinTab.contains("%website%")) {
            skinDefault = Skin.WEBSITE_SKIN;
        }
        if (skinTab.contains("%watch%")) {
            skinDefault = Skin.QUEUE_SKIN;
        }
        if (skinTab.contains("%information%")) {
            skinDefault = Skin.INFORMATION_SKIN;
        }
        if (skinTab.contains("%wood_shield%")) {
            skinDefault = Skin.WOOD_SHIELD_SKIN;
        }
        if (skinTab.contains("%diamond_shield%")) {
            skinDefault = Skin.DIAMOND_SHIELD_SKIN;
        }
        if (skinTab.contains("%bow%")) {
            skinDefault = Skin.BOW_SKIN;
        }
        if (skinTab.contains("%potion%")) {
            skinDefault = Skin.POTION_SKIN;
        }
        if (skinTab.contains("%telegram%")) {
            skinDefault = Skin.TELEGRAM_SKIN;
        }
        if (skinTab.contains("%enderchest%")) {
            skinDefault = Skin.ENDERCHEST_SKIN;
        }
        if (skinTab.contains("%coin%")) {
            skinDefault = Skin.COIN_SKIN;
        }
        if (skinTab.contains("%heart%")) {
            skinDefault = Skin.HEART_SKIN;
        }
        if (skinTab.contains("%earth%")) {
            skinDefault = Skin.EARTH_SKIN;
        }
        if (skinTab.contains("%crown%")) {
            skinDefault = Skin.CROWN_SKIN;
        }
        if (skinTab.contains("%castle%")) {
            skinDefault = Skin.CASTLE_SKIN;
        }
        if (skinTab.contains("%ping%")) {
            skinDefault = Skin.PING_SKIN;
        }
        if (skinTab.contains("%stats%")) {
            skinDefault = Skin.STATS_SKIN;
        }
        if (skinTab.contains("%compass%")) {
            skinDefault = Skin.COMPASS_SKIN;
        }
        if (skinTab.contains("%offline%")) {
            skinDefault = Skin.OFFLINE_SKIN;
        }

        return skinDefault;

    }

    private String footer() {
        List<String> footers = CC.translate(Hub.getInstance().getSettingsConfig().getConfig().getStringList("TABLIST_INFO.TAB-FOOTER_CONFIG.CHANGES"));
        long time = System.currentTimeMillis();
        long interval = TimeUnit.MILLISECONDS.toMillis(Hub.getInstance().getSettingsConfig().getConfig().getInt("TABLIST_INFO.TAB-FOOTER_CONFIG.CHANGER_TICKS"));

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
        List<String> titles = CC.translate(Hub.getInstance().getSettingsConfig().getConfig().getStringList("TABLIST_INFO.CHANGES-TITLE"));
        long time = System.currentTimeMillis();
        long interval = TimeUnit.MILLISECONDS.toMillis(Hub.getInstance().getSettingsConfig().getConfig().getInt("TABLIST_INFO.CHANGER_TICKS-TITLE"));

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

    private String OfflineLoading() {
        List<String> titles = CC.translate(Hub.getInstance().getSettingsConfig().getConfig().getStringList("TABLIST_INFO.OFFLINE-LOADING.CHANGES"));
        long time = System.currentTimeMillis();
        long interval = TimeUnit.MILLISECONDS.toMillis(Hub.getInstance().getSettingsConfig().getConfig().getInt("TABLIST_INFO.OFFLINE-LOADING.CHANGER_TICKS"));

        if (lastMillisOfflineLoading + interval <= time) {
            if (OfflineLoading != titles.size() - 1) {
                OfflineLoading++;
            } else {
                OfflineLoading = 0;
            }
            lastMillisOfflineLoading = time;
        }
        return titles.get(OfflineLoading);
    }

    public boolean KitsOnline() {
        try {
            Socket s = new Socket();
            s.connect(new InetSocketAddress("172.18.0.1", 30006), 10); //good timeout is 10-20
            kitsonline = true;
            s.close();
        } catch (UnknownHostException e) {
            // OFFLINE
        } catch (IOException e) {
            // OFFLINE
        }
        return kitsonline;
    }


    @Override
    public Set<TabLayout> getProvider(Player player) {
        Set<TabLayout> toReturn = new HashSet<>();


        for (int i = 1; i < 21; ++i) {
            toReturn.add(this.getInfo(i, TabColumn.LEFT, player));
        }
        for (int i = 1; i < 21; ++i) {
            toReturn.add(this.getInfo(i, TabColumn.MIDDLE, player));
        }
        for (int i = 1; i < 21; ++i) {
            toReturn.add(this.getInfo(i, TabColumn.RIGHT, player));
        }
        for (int i = 1; i < 21; ++i) {
            toReturn.add(this.getInfo(i, TabColumn.FAR_RIGHT, player));
        }

        return toReturn;
    }


}