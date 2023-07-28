package dev.razorni.hcfactions.extras.mountain.command;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.extras.framework.commands.extra.TabCompletion;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class GlowstoneCommand extends Command {

    public GlowstoneCommand(CommandManager manager) {
        super(manager, "glowstone");
        this.setPermissible("azurite.glowtone");
        this.completions.add(new TabCompletion(Arrays.asList("scan", "reset"), 0));
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("glow");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        Player player = (Player) sender;
        switch (args[0].toLowerCase()) {
            case "scan": {
                if (HCF.getPlugin().getGlowstoneMountainManager().getClaims().isEmpty()) {
                    sender.sendMessage(ChatColor.RED + "Glowstone mountain is not claimed.");
                    return;
                }
                HCF.getPlugin().getGlowstoneMountainManager().scanGlowStone();
                HCF.getPlugin().getGlowstoneMountainManager().saveConfig();
                sender.sendMessage(ChatColor.GOLD + "[Glowstone Mountain] "
                        + ChatColor.GREEN + "Successfully finished scanning glowstone.");
                return;
            }
            case "reset": {
                if (HCF.getPlugin().getGlowstoneMountainManager().getClaims().isEmpty()) {
                    player.sendMessage(ChatColor.RED + "Glowstone mountain is not claimed.");
                    return;
                }

                HCF.getPlugin().getGlowstoneMountainManager().reset();
                player.sendMessage(ChatColor.GOLD + "[Glowstone Mountain] "
                        + ChatColor.GREEN + "Successfully reset glowstone mountain.");
                return;
            }
        }
        this.sendUsage(sender);
    }
}
