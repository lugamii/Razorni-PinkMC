package dev.razorni.crates.command.parameter;

import dev.razorni.crates.Crates;
import dev.razorni.crates.lootbox.LootBox;
import cc.invictusgames.ilib.command.parameter.ParameterType;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class LootBoxParameter implements ParameterType<LootBox> {

    private final Crates plugin;

    @Override
    public LootBox parse(CommandSender commandSender, String source) {
        LootBox lootBox = plugin.getLootBoxManager().getLootBox(source);

        if (lootBox == null) {
            commandSender.sendMessage(ChatColor.RED + "This lootbox does not exist.");
            return null;
        }

        return lootBox;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, List<String> flags) {
        List<String> names = new ArrayList<>();

        for (LootBox lootbox : plugin.getLootBoxManager().getLootBoxMap().values())
            names.add(lootbox.getName());

        return names;
    }
}
