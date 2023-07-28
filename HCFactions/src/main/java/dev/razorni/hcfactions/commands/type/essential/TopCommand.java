package dev.razorni.hcfactions.commands.type.essential;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TopCommand extends Command {
    public TopCommand(CommandManager manager) {
        super(manager, "top");
        this.setPermissible("azurite.top");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        Player player = (Player) sender;
        Location location = this.getHighestSafe(player.getLocation());
        if (location == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TOP_COMMAND.NOT_SAFE"));
            return;
        }
        player.teleport(location.add(0.5, 1.0, 0.5));
        this.sendMessage(sender, this.getLanguageConfig().getString("TOP_COMMAND.TELEPORTED"));
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("highestpoint");
    }

    private Location getHighestSafe(Location location) {
        Location l = location.clone();
        int x = l.getBlockX();
        int max = location.getWorld().getMaxHeight();
        int z = l.getBlockZ();
        while (max > location.getBlockY()) {
            Block block = location.getWorld().getBlockAt(x, --max, z);
            if (!block.isEmpty()) {
                Location toReturn = block.getLocation();
                toReturn.setPitch(location.getPitch());
                toReturn.setYaw(location.getYaw());
                return toReturn;
            }
        }
        return null;
    }

    @Override
    public List<String> usage() {
        return null;
    }
}
