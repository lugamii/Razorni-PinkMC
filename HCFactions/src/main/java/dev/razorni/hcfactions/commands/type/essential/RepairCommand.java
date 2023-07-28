package dev.razorni.hcfactions.commands.type.essential;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RepairCommand extends Command {
    private final List<Material> deniedItems;

    public RepairCommand(CommandManager manager) {
        super(manager, "repair");
        this.deniedItems = new ArrayList<>();
        this.setPermissible("azurite.repair");
        this.load();
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
        ItemStack stack = this.getManager().getItemInHand(player);
        if (args.length == 1 && sender.hasPermission("azurite.repair.all") && args[0].equalsIgnoreCase("ALL")) {
            ItemStack[] contents = player.getInventory().getContents();
            int lenght = contents.length;
            for (ItemStack st : contents) {
                if (st != null) {
                    String name = st.getType().name().toLowerCase();
                    if (name.contains("helmet") || name.contains("chestplate") || name.contains("leggings") || name.contains("boots") || name.contains("sword") || name.contains("shovel") || name.contains("pickaxe") || name.contains("axe") || name.contains("hoe") || name.contains("bow")) {
                        this.getManager().setData(st, 0);
                    }
                }
            }
            player.updateInventory();
            this.sendMessage(sender, this.getLanguageConfig().getString("REPAIR_COMMAND.REPAIRED_ALL"));
            return;
        }
        if (stack == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("REPAIR_COMMAND.EMPTY_HAND"));
            return;
        }
        if (this.deniedItems.contains(stack.getType())) {
            this.sendMessage(sender, this.getLanguageConfig().getString("REPAIR_COMMAND.FORBIDDEN_ITEM"));
            return;
        }
        this.getManager().setData(stack, 0);
        player.updateInventory();
        this.sendMessage(sender, this.getLanguageConfig().getString("REPAIR_COMMAND.REPAIRED"));
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("fix", "fixhand");
    }

    @Override
    public List<String> usage() {
        return null;
    }

    private void load() {
        this.deniedItems.addAll(this.getConfig().getStringList("REPAIRING.DENIED_ITEMS").stream().map(Material::valueOf).collect(Collectors.toList()));
    }
}
