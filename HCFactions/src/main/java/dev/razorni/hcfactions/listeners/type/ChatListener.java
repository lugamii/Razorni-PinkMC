package dev.razorni.hcfactions.listeners.type;

import dev.razorni.core.profile.Profile;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.listeners.ListenerManager;
import dev.razorni.hcfactions.teams.player.Role;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.users.settings.TeamChatSetting;
import dev.razorni.hcfactions.utils.extra.Cooldown;
import dev.razorni.hcfactions.utils.extra.Pair;
import dev.razorni.hcfactions.utils.menuapi.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;
import java.util.stream.Collectors;

public class ChatListener extends Module<ListenerManager> {
    private final String coLeaderChatFormat;
    private final String teamChatFormat;
    private final String officerChatFormat;
    private final Cooldown chatCooldown;
    private final List<String> deniedChatMessages;
    private final Map<String, TeamChatSetting> shortcuts;
    private final String allyChatFormat;

    public ChatListener(ListenerManager manager) {
        super(manager);
        this.chatCooldown = new Cooldown(manager);
        this.shortcuts = new HashMap<>();
        this.deniedChatMessages = new ArrayList<>();
        this.teamChatFormat = this.getConfig().getString("CHAT_FORMAT.TEAM_CHAT.FORMAT");
        this.allyChatFormat = this.getConfig().getString("CHAT_FORMAT.ALLY_CHAT.FORMAT");
        this.officerChatFormat = this.getConfig().getString("CHAT_FORMAT.OFFICER_CHAT.FORMAT");
        this.coLeaderChatFormat = this.getConfig().getString("CHAT_FORMAT.CO_LEADER_CHAT.FORMAT");
        this.load();
    }

    private void load() {
        this.deniedChatMessages.addAll(this.getConfig().getStringList("CHAT_FORMAT.DENIED_WORDS").stream().map(String::toUpperCase).collect(Collectors.toList()));
        TeamChatSetting[] settings = TeamChatSetting.values();
        for (TeamChatSetting setting : settings) {
            if (setting != TeamChatSetting.PUBLIC) {
                String s = this.getConfig().getString("CHAT_FORMAT." + setting.name() + "_CHAT.SHORTCUT");
                if (!s.isEmpty()) {
                    this.shortcuts.put(s, setting);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        String prefix = this.getInstance().getUserManager().getPrefix(player);
        String message = event.getMessage();
        String kTop = "";
        String brackets = "";
        String fTop = "";
        String spacer = "";
        if (player.hasPermission("core.spacer")) {
            spacer = " ";
        }
        Pair<TeamChatSetting, String> pair = this.getShortcut(message);
        TeamChatSetting chatSetting = user.getTeamChatSetting();
        String prime = "";
        if (Profile.getByUuid(player.getUniqueId()).isPrime()) {
            prime = CC.translate("&6✪");
        }
        String pPrefix = CC.translate(HCF.getPlugin().getRankManager().getRankPrefix(player) + prime + HCF.getPlugin().getRankManager().getRankColor(player) + player.getName());
        String pSuffix = CC.translate(this.getInstance().getRankManager().getRankSuffix(player));
        String pColor = CC.translate(this.getInstance().getRankManager().getRankColor(player));
        String pTag = CC.translate(this.getInstance().getTagManager().getTag(player));
        boolean bypass = player.hasPermission("azurite.chat.bypass");
        if (team != null) {
            if (pair != null) {
                chatSetting = pair.getKey();
                message = pair.getValue();
            }
            String position = team.getTeamPosition();
            fTop = ((position == null) ? "" : this.getConfig().getString("CHAT_FORMAT.FTOP_FORMAT").replace("%ftop%", position));
        }
        if (prefix != null) {
            kTop = this.getConfig().getString("CHAT_FORMAT.KILL_TOP_FORMAT").replace("%killtop%", prefix);
        }
        if (event.isCancelled() && chatSetting == TeamChatSetting.PUBLIC) {
            return;
        }
        if (!player.hasPermission("azurite.profanity.bypass") && chatSetting == TeamChatSetting.PUBLIC) {
            for (String s : this.deniedChatMessages) {
                if (!message.toUpperCase().contains(s)) {
                    continue;
                }
                event.setCancelled(true);
                player.sendMessage(this.getLanguageConfig().getString("CHAT_LISTENER.FORBIDDEN_MESSAGE"));
                return;
            }
        }
        if (this.chatCooldown.hasCooldown(player) && !bypass) {
            event.setCancelled(true);
            player.sendMessage(this.getLanguageConfig().getString("CHAT_LISTENER.COOLDOWN").replaceAll("%seconds%", this.chatCooldown.getRemaining(player)));
            return;
        }
        if (!bypass && chatSetting == TeamChatSetting.PUBLIC) {
            this.chatCooldown.applyCooldown(player, this.getConfig().getInt("CHAT_FORMAT.COOLDOWN"));
        }
        event.setCancelled(true);
        switch (chatSetting) {
            case PUBLIC: {
                String msg = "";
                for (Player online : event.getRecipients()) {
                    User userRecipient = this.getInstance().getUserManager().getByUUID(online.getUniqueId());
                    if (!userRecipient.isPublicChat()) {
                        continue;
                    }
                    if (team == null) {
                        msg = String.format(this.getConfig().getString("CHAT_FORMAT.PUBLIC_NO_TEAM").replace("%reputation%", HCF.getPlugin().getRepmanager().getPrefix(player)).replace("%prefix%", pPrefix).replace("%suffix%", pSuffix).replace("%color%", pColor).replace("%tag%", pTag).replace("%ftop%", fTop).replace("%killtop%", kTop).replace("%player%", player.getName()), message);
                        online.sendMessage(msg);
                    } else {
                        String bracketsleft = team.getTeamPosBracketsLeft();
                        String bracketsright = team.getTeamPosBracketsRight();
                        msg = String.format(this.getConfig().getString("CHAT_FORMAT.PUBLIC_TEAM").replace("%reputation%", HCF.getPlugin().getRepmanager().getPrefix(player)).replace("%prime%", prime).replace("%bracketright%", bracketsright).replace("%bracketleft%", bracketsleft).replace("%prefix%", pPrefix).replace("%suffix%", pSuffix).replace("%color%", pColor).replace("%tag%", pTag).replace("%ftop%", fTop).replace("%killtop%", kTop).replace("%player%", player.getName()).replace("%team%", team.getDisplayName(online)), message);
                        online.sendMessage(msg);
                    }
                }
                Bukkit.getConsoleSender().sendMessage(msg);
                break;
            }
            case TEAM: {
                String msg = "";
                if (team == null) {
                    user.setTeamChatSetting(TeamChatSetting.PUBLIC);
                    user.save();
                    for (Player online : event.getRecipients()) {
                        User recipient = this.getInstance().getUserManager().getByUUID(online.getUniqueId());
                        if (!recipient.isPublicChat()) {
                            continue;
                        }
                        msg = String.format(this.getConfig().getString("CHAT_FORMAT.PUBLIC_NO_TEAM").replace("%prefix%", pPrefix).replace("%suffix%", pSuffix).replace("%color%", pColor).replace("%tag%", pTag).replace("%ftop%", fTop).replace("%killtop%", kTop).replace("%player%", player.getName()), message);
                        online.sendMessage(msg);
                    }
                    Bukkit.getConsoleSender().sendMessage(msg);
                    return;
                }
                for (Player teamOnline : team.getOnlinePlayers()) {
                    msg = String.format(this.teamChatFormat.replace("%prefix%", pPrefix).replace("%suffix%", pSuffix).replace("%color%", pColor).replace("%player%", player.getName()), message);
                    teamOnline.sendMessage(msg);
                }
                Bukkit.getConsoleSender().sendMessage(msg);
                break;
            }
            case ALLY: {
                String msg = "";
                if (team == null) {
                    user.setTeamChatSetting(TeamChatSetting.PUBLIC);
                    user.save();
                    for (Player online : event.getRecipients()) {
                        User recipient = this.getInstance().getUserManager().getByUUID(online.getUniqueId());
                        if (!recipient.isPublicChat()) {
                            continue;
                        }
                        msg = String.format(this.getConfig().getString("CHAT_FORMAT.PUBLIC_NO_TEAM").replace("%prefix%", pPrefix).replace("%suffix%", pSuffix).replace("%color%", pColor).replace("%tag%", pTag).replace("%ftop%", fTop).replace("%killtop%", kTop).replace("%player%", player.getName()), message);
                        online.sendMessage(msg);
                    }
                    Bukkit.getConsoleSender().sendMessage(msg);
                    return;
                }
                for (Player teamOnline : team.getOnlinePlayers()) {
                    msg = String.format(this.allyChatFormat.replace("%prefix%", pPrefix).replace("%suffix%", pSuffix).replace("%color%", pColor).replace("%player%", player.getName()), message);
                    teamOnline.sendMessage(msg);
                }
                for (UUID allie : team.getAllies()) {
                    PlayerTeam teamAllie = this.getInstance().getTeamManager().getPlayerTeam(allie);
                    if (teamAllie == null) {
                        continue;
                    }
                    for (Player allieOnline : teamAllie.getOnlinePlayers()) {
                        allieOnline.sendMessage(msg);
                    }
                }
                Bukkit.getConsoleSender().sendMessage(msg);
                break;
            }
            case OFFICER: {
                String msg = "";
                if (team == null) {
                    user.setTeamChatSetting(TeamChatSetting.PUBLIC);
                    user.save();
                    for (Player online : event.getRecipients()) {
                        User recipient = this.getInstance().getUserManager().getByUUID(online.getUniqueId());
                        if (!recipient.isPublicChat()) {
                            continue;
                        }
                        msg = String.format(this.getConfig().getString("CHAT_FORMAT.PUBLIC_NO_TEAM").replace("%prefix%", pPrefix).replace("%suffix%", pSuffix).replace("%color%", pColor).replace("%tag%", pTag).replace("%ftop%", fTop).replace("%killtop%", kTop).replace("%player%", player.getName()), message);
                        online.sendMessage(msg);
                    }
                    Bukkit.getConsoleSender().sendMessage(msg);
                    return;
                }
                for (Player teamOnline : team.getOnlinePlayers()) {
                    if (!team.checkRole(teamOnline, Role.CAPTAIN)) {
                        continue;
                    }
                    msg = String.format(this.officerChatFormat.replace("%prefix%", pPrefix).replace("%suffix%", pSuffix).replace("%color%", pColor).replace("%player%", player.getName()), message);
                    teamOnline.sendMessage(msg);
                }
                Bukkit.getConsoleSender().sendMessage(msg);
                break;
            }
            case CO_LEADER: {
                String msg = "";
                if (team == null) {
                    user.setTeamChatSetting(TeamChatSetting.PUBLIC);
                    user.save();
                    for (Player online : event.getRecipients()) {
                        User recipient = this.getInstance().getUserManager().getByUUID(online.getUniqueId());
                        if (!recipient.isPublicChat()) {
                            continue;
                        }
                        msg = String.format(this.getConfig().getString("CHAT_FORMAT.PUBLIC_NO_TEAM").replace("%prefix%", pPrefix).replace("%suffix%", pSuffix).replace("%color%", pColor).replace("%tag%", pTag).replace("%ftop%", fTop).replace("%killtop%", kTop).replace("%player%", player.getName()), message);
                        online.sendMessage(msg);
                    }
                    Bukkit.getConsoleSender().sendMessage(msg);
                    return;
                }
                for (Player teamOnline : team.getOnlinePlayers()) {
                    if (!team.checkRole(teamOnline, Role.CO_LEADER)) {
                        continue;
                    }
                    msg = String.format(this.coLeaderChatFormat.replace("%prefix%", pPrefix).replace("%suffix%", pSuffix).replace("%color%", pColor).replace("%player%", player.getName()), message);
                    teamOnline.sendMessage(msg);
                }
                Bukkit.getConsoleSender().sendMessage(msg);
                break;
            }
        }
    }

    private Pair<TeamChatSetting, String> getShortcut(String shortcut) {
        if (shortcut.isEmpty()) {
            return null;
        }
        TeamChatSetting setting = this.shortcuts.get(String.valueOf(shortcut.charAt(0)));
        if (setting == null) {
            return null;
        }
        for (String s : this.shortcuts.keySet()) {
            shortcut = shortcut.replaceAll(s, "");
        }
        return new Pair<>(setting, shortcut);
    }
}
