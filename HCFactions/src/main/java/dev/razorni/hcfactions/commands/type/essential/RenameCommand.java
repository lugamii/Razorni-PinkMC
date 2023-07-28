package dev.razorni.hcfactions.commands.type.essential;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RenameCommand extends Command {
    private final List<Material> deniedItems;
    private final List<String> deniedNames;

    public RenameCommand(CommandManager manager) {
        super(manager, "rename");
        this.deniedItems = new ArrayList<>();
        this.deniedNames = new ArrayList<>();
        this.setPermissible("azurite.rename");
        this.load();
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    private void load() {
        this.deniedNames.addAll(this.getConfig().getStringList("RENAMING.DENIED_NAMES"));
        this.deniedItems.addAll(this.getConfig().getStringList("RENAMING.DENIED_ITEMS").stream().map(Material::valueOf).collect(Collectors.toList()));
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
        String text = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
        ItemStack stack = this.getManager().getItemInHand(player);
        if (stack == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("RENAME_COMMAND.EMPTY_HAND"));
            return;
        }
        if (this.deniedItems.contains(this.manager.getItemInHand(player).getType())) {
            this.sendMessage(sender, this.getLanguageConfig().getString("RENAME_COMMAND.FORBIDDEN_ITEM"));
            return;
        }
        if (this.deniedNames.contains(text)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("RENAME_COMMAND.FORBIDDEN_NAME"));
            return;
        }
        ItemBuilder builder = new ItemBuilder(stack);
        builder.setName(text);
        this.manager.setItemInHand(player, builder.toItemStack());
        this.sendMessage(sender, this.getLanguageConfig().getString("RENAME_COMMAND.RENAMED").replaceAll("%name%", text));
    }
}
