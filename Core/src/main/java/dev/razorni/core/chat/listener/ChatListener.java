package dev.razorni.core.chat.listener;

import dev.razorni.core.Core;
import dev.razorni.core.chat.ChatAttempt;
import dev.razorni.core.chat.event.ChatAttemptEvent;
import dev.razorni.core.database.redis.packets.staff.StaffChatPacket;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.server.ServerType;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.Cooldown;
import dev.razorni.hcfactions.HCF;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final Core core = Core.getInstance();

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());

        ChatAttempt chatAttempt = core.getChat().attemptChatMessage(event.getPlayer(), event.getMessage());
        ChatAttemptEvent chatAttemptEvent = new ChatAttemptEvent(event.getPlayer(), chatAttempt, event.getMessage());

        if (player.hasPermission("gravity.staff")) {
            if (profile.getStaffOptions().isStaffChat()) {

//                JsonBuilder builder = new JsonBuilder();
//                builder.addProperty("sender", player.getName());
//                builder.addProperty("message", event.getMessage());
//                builder.addProperty("server", Core.getInstance().getConfig().getString("SERVER_NAME"));
//
//                new StaffChatPacket(builder).send();

                new StaffChatPacket(player.getDisplayName(), event.getMessage(), Core.getInstance().getConfig().getString("SERVER_NAME")).send();

//                Core.getInstance().getPacketBase().sendPacket(new PacketStaffChat(player.getDisplayName(), Core.getInstance().getConfig().getString("SERVER_NAME"), event.getMessage()));
                event.setCancelled(true);
                return;
            }
        }

        if (!player.hasPermission("gravity.staff")) {
            if (profile.getStaffOptions().isStaffChat()) {
                profile.getStaffOptions().setStaffChat(false);
            }
            if (!profile.getChatCooldown().hasExpired()) {
                player.sendMessage(CC.translate("&cYou may chat again in &c&l%time%s&c.".replace("%time%", profile.getChatCooldown().getTimeLeft())));
                event.setCancelled(true);
                return;
            } else {
                profile.setChatCooldown(new Cooldown(Core.getInstance().getChat().getDelayTime() * 1000));
            }
        }

        core.getServer().getPluginManager().callEvent(chatAttemptEvent);

        if (!chatAttemptEvent.isCancelled()) {
            switch (chatAttempt.getResponse()) {
                case ALLOWED: {
                    String prime = "";
                    if (Profile.getByUuid(player.getUniqueId()).isPrime()) {
                        prime = CC.translate("&6✪");
                    }
                    String hcfkills = "";
                    if (Core.getInstance().getServerType() == ServerType.HCF) {
                        hcfkills = HCF.getPlugin().getUserManager().getPrefix(player) + " ";
                    }
                    if (profile.getTag() == null) {
                        event.setFormat(hcfkills + Core.getInstance().getCoreAPI().getRankPrefix(player.getPlayer()) + prime + Core.getInstance().getCoreAPI().getRankColor(player) + player.getName() + profile.getActiveGrant().getRank().getSuffix() + CC.RESET + ": %2$s");
                    } else {
                        event.setFormat((CC.translate(profile.getTag().getPrefix())) + hcfkills + Core.getInstance().getCoreAPI().getRankPrefix(player.getPlayer()) + prime + Core.getInstance().getCoreAPI().getRankColor(player) + player.getName() + profile.getActiveGrant().getRank().getSuffix() + CC.RESET + ": %2$s");
                    }
                }
                break;
                case MESSAGE_FILTERED: {
                    event.setCancelled(true);
                    chatAttempt.getFilterFlagged().punish(event.getPlayer());
                }
                break;
                case PLAYER_MUTED: {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(CC.RED + "You are currently muted.");
                    event.getPlayer().sendMessage(CC.WHITE + " » Reason: " + CC.GOLD + chatAttempt.getPunishment().getAddedReason());
                    event.getPlayer().sendMessage(CC.WHITE + " » Expires: " + CC.GOLD + chatAttempt.getPunishment().getTimeRemaining());
                }
                break;
                case CHAT_MUTED: {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(CC.RED + "Public chat is currently muted.");
                }
                break;
                case CHAT_DELAYED: {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(CC.RED + "You may chat again in {time}.".replaceAll("%time%", profile.getChatCooldown().getTimeLeft()));
                }
                break;
            }
        }
    }
}
