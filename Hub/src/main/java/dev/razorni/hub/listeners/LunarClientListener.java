package dev.razorni.hub.listeners;

import com.lunarclient.bukkitapi.LunarClientAPI;
import dev.razorni.core.Core;
import dev.razorni.core.profile.Profile;
import dev.razorni.hub.Hub;
import dev.razorni.hub.utils.shits.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class LunarClientListener {

    public LunarClientListener() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (Player other : Bukkit.getOnlinePlayers()) {
                        LunarClientAPI.getInstance().overrideNametag(other, fetchNametag(other, player), player);
                    }
                }
            }
        }.runTaskTimerAsynchronously(Hub.getInstance(), 20, 40);
    }

    public List<String> fetchNametag(Player target, Player viewer) {
        List<String> tag = new ArrayList<>();

        String prime = "";
        if (Profile.getByUuid(target.getUniqueId()).isPrime()) {
            prime = CC.translate(" &6✪");
        }

        String nameTag = CC.chat(Core.getInstance().getCoreAPI().getRankColor(target) + target.getName() + prime);

        if (Core.getInstance().getStaffManager().isStaffMode(target)) {
            tag.add(CC.translate("&7[Mod Mode]"));
        }

        tag.add(CC.translate(Core.getInstance().getCoreAPI().getRankColor(target) + Core.getInstance().getCoreAPI().getRankName(target)));

        if (Hub.getInstance().getQueueManager().inQueue(target) && !Core.getInstance().getStaffManager().isStaffMode(target))  {
            tag.add(CC.translate("&6&l┃ &fQueue: &6" + Hub.getInstance().getQueueManager().getQueueIn(target)));
            tag.add(CC.translate("&6&l┃ &fPos: &6#" + Hub.getInstance().getQueueManager().getPosition(target) + " &fout of &6#" + Hub.getInstance().getQueueManager().getInQueue(Hub.getInstance().getQueueManager().getQueueIn(target))));
        }

        tag.add(nameTag);
        return tag;
    }

}
