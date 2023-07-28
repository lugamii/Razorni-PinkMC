package dev.razorni.hcfactions.extras.killstreaks;

import cc.invictusgames.ilib.uuid.UUIDCache;
import com.google.common.collect.Lists;
import dev.razorni.hcfactions.utils.commandapi.command.Command;
import dev.razorni.hcfactions.utils.commandapi.command.FrozenCommandHandler;
import dev.razorni.hcfactions.utils.commandapi.command.Param;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.killstreaks.prizes.*;
import dev.razorni.hcfactions.users.User;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class KillstreakHandler implements Listener {

    @Getter private List<Killstreak> killstreaks = Lists.newArrayList();
    @Getter private List<PersistentKillstreak> persistentKillstreaks = Lists.newArrayList();

    public KillstreakHandler() {
        HCF.getPlugin().getServer().getPluginManager().registerEvents(this, HCF.getPlugin());
        FrozenCommandHandler.registerClass(this.getClass());
        FrozenCommandHandler.registerPackage(HCF.getPlugin(), "dev.razorni.hcfactions.extras.killstreaks.command");

        killstreaks.add(new Debuffs());
        killstreaks.add(new Gapple());
        killstreaks.add(new GoldenApples());
        
        persistentKillstreaks.add(new Invis());
        persistentKillstreaks.add(new PermSpeed2());
        persistentKillstreaks.add(new Speed2());
        persistentKillstreaks.add(new Strength());

        killstreaks.sort((first, second) -> {
            int firstNumber = first.getKills()[0];
            int secondNumber = second.getKills()[0];

            if (firstNumber < secondNumber) {
                return -1;
            }
            return 1;

        });

        persistentKillstreaks.sort((first, second) -> {
            int firstNumber = first.getKillsRequired();
            int secondNumber = second.getKillsRequired();

            if (firstNumber < secondNumber) {
                return -1;
            }
            return 1;

        });
    }

    public Killstreak check(int kills) {
        for (Killstreak killstreak : killstreaks) {
            for (int kill : killstreak.getKills()) {
                if (kills == kill) {
                    return killstreak;
                }
            }
        }

        return null;
    }

    public List<PersistentKillstreak> getPersistentKillstreaks(Player player, int count) {
        return persistentKillstreaks.stream().filter(s -> s.check(count)).collect(Collectors.toList());
    }

    @Command(names = "setks", permission = "azurite.setks")
    public static void setKillstreak(CommandSender sender, @Param(name = "target") UUID target, @Param(name = "killstreak") int killstreak) {
        User statsEntry = HCF.getPlugin().getUserManager().getByUUID(target);
        statsEntry.setKillstreak(killstreak);
        statsEntry.save();

        sender.sendMessage(ChatColor.GREEN + "You set " + UUIDCache.getName(target) + " killstreak to: " + killstreak);
    }

}
