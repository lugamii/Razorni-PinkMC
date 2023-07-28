package dev.razorni.hcfactions.commands.type.essential;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

public class KillCommand extends Command {

    public KillCommand(CommandManager manager) {
        super(manager, "kill");
        this.setPermissible("azurite.kill");
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                EntityDamageEvent event = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.SUICIDE, new EnumMap(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, 100.0)), new EnumMap(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, Functions.constant(-0.0))));
                player.setLastDamageCause(event);
                player.setHealth(0.0);
                this.sendMessage(sender, this.getLanguageConfig().getString("KILL_COMMAND.KILLED_SELF"));
                return;
            }
            this.sendUsage(sender);
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
                return;
            }
            EntityDamageEvent event = new EntityDamageEvent(target, EntityDamageEvent.DamageCause.SUICIDE, new EnumMap(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, 100.0)), new EnumMap(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, Functions.constant(-0.0))));
            target.setLastDamageCause(event);
            target.setHealth(0.0);
            this.sendMessage(sender, this.getLanguageConfig().getString("KILL_COMMAND.KILLED_TARGET").replaceAll("%player%", target.getName()));
        }
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("KILL_COMMAND.USAGE");
    }
}
