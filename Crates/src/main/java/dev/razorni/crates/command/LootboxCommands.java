package dev.razorni.crates.command;

import dev.razorni.crates.Crates;
import dev.razorni.crates.lootbox.LootBox;
import dev.razorni.crates.lootbox.LootBoxManager;
import dev.razorni.crates.lootbox.menu.editor.LootBoxEditMenu;
import cc.invictusgames.ilib.command.annotation.Command;
import cc.invictusgames.ilib.command.annotation.Param;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class LootboxCommands {

    private final Crates plugin;
    private final LootBoxManager lootBoxManager;

    @Command(names = {"lootbox create"},
            permission = "op",
            description = "Create a lootbox.")
    public boolean create(CommandSender commandSender, @Param(name = "name") String name) {
        if (lootBoxManager.lootBoxExists(name)) {
            commandSender.sendMessage(ChatColor.RED + "This lootbox already exists.");
            return true;
        }

        LootBox lootBox = lootBoxManager.createLootBox(name);
        commandSender.sendMessage(ChatColor.GREEN + "You have created a lootbox called "
                + ChatColor.BOLD + lootBox.getDisplayName() + ChatColor.GREEN + ".");
        return true;
    }

    @Command(names = "lootbox delete",
            permission = "op",
            description = "Delete a lootbox.")
    public boolean delete(CommandSender commandSender, @Param(name = "name") LootBox lootBox) {
        lootBoxManager.deleteLootBox(lootBox);
        commandSender.sendMessage(ChatColor.GREEN + "You have deleted the lootbox called "
                + ChatColor.BOLD + lootBox.getDisplayName() + ChatColor.GREEN + ".");
        return true;
    }

    @Command(names = "lootbox list",
            permission = "op",
            description = "List all available lootboxes.")
    public boolean list(CommandSender commandSender) {
        List<String> names = new ArrayList<>();
        Map<UUID, LootBox> lootBoxMap = lootBoxManager.getLootBoxMap();

        for (LootBox lootBox : lootBoxMap.values())
            names.add(lootBox.getName());

        commandSender.sendMessage(ChatColor.YELLOW + "Lootboxes: " + ChatColor.RED + (lootBoxMap.isEmpty() ? "None" :
                StringUtils.join(names, ", ")));
        return true;
    }

    @Command(names = "lootbox give",
            permission = "argon.command.lootbox.argument.give",
            description = "Give a player a lootbox.")
    public boolean give(CommandSender commandSender,
                        @Param(name = "player") Player player,
                        @Param(name = "name") LootBox lootBox,
                        @Param(name = "amount", defaultValue = "1") int amount) {
        player.getInventory().addItem(lootBox.getLootBox(amount)).values().forEach(itemStack ->
                player.getWorld().dropItemNaturally(player.getLocation(), itemStack));
        return true;
    }

    @Command(names = "lootbox giveall",
            permission = "argon.command.lootbox.argument.givell",
            description = "Give all players a lootbox.")
    public boolean giveall(CommandSender commandSender,
                           @Param(name = "name") LootBox lootBox,
                           @Param(name = "amount", defaultValue = "1") int amount) {
        Bukkit.getOnlinePlayers().forEach(player ->
                player.getInventory().addItem(lootBox.getLootBox(amount)).values().forEach(itemStack ->
                        player.getWorld().dropItemNaturally(player.getLocation(), itemStack)));
        return true;
    }

    @Command(names = "lootbox edit",
            permission = "op",
            description = "Edit a lootbox.")
    public boolean edit(Player commandSender, @Param(name = "name") LootBox lootBox) {
        new LootBoxEditMenu(lootBox, plugin).openMenu(commandSender);
        return true;
    }


}
