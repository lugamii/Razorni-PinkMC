package dev.razorni.hcfactions.extras.spawners.command;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.spawners.Spawner;
import dev.razorni.hcfactions.utils.commandapi.command.Command;
import dev.razorni.hcfactions.utils.commandapi.command.Param;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SpawnerGiveCommand {

    @Command(names = "spawners give", permission = "azurite.spawners")
    public static void spawnersgive(CommandSender sender, @Param(name = "target") Player target, @Param(name = "type") String spawnername) {
        EntityType type;
        try {
            type = EntityType.valueOf(spawnername.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            type = null;
        }
        Spawner spawner = HCF.getPlugin().getSpawnerManager().getSpawners().get(type);
        target.getInventory().addItem(spawner.getItemStack());
        target.updateInventory();
    }

}