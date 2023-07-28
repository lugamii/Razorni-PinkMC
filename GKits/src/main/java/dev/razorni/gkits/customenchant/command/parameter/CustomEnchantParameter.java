package dev.razorni.gkits.customenchant.command.parameter;

import dev.razorni.gkits.GKits;
import dev.razorni.gkits.customenchant.CustomEnchant;
import cc.invictusgames.ilib.command.parameter.ParameterType;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CustomEnchantParameter implements ParameterType<CustomEnchant> {

    private final GKits plugin;

    @Override
    public CustomEnchant parse(CommandSender commandSender, String string) {
        CustomEnchant enchantByName = plugin.getCustomEnchantManager()
                .getEnchantByName(string);

        if (enchantByName == null) {
            commandSender.sendMessage(ChatColor.RED + "This enchant does not exist.");
            return null;
        }

        return enchantByName;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, List<String> flags) {
        List<String> names = new ArrayList<>();

        for (CustomEnchant customEnchant : plugin.getCustomEnchantManager().getCustomEnchantList())
            names.add(customEnchant.getName().replace(" ", ""));

        return names;
    }
}
