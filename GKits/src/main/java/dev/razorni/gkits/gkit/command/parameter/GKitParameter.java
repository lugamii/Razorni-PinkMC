package dev.razorni.gkits.gkit.command.parameter;

import dev.razorni.gkits.GKits;
import dev.razorni.gkits.gkit.GKit;
import cc.invictusgames.ilib.command.parameter.ParameterType;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class GKitParameter implements ParameterType<GKit> {

    private final GKits plugin;

    @Override
    public GKit parse(CommandSender commandSender, String string) {
        GKit gKit = plugin.getGKitManager().getGKit(string);

        if (gKit == null) {
            commandSender.sendMessage(ChatColor.RED + "This gkit does not exist.");
            return null;
        }

        return gKit;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, List<String> flags) {
        List<String> names = new ArrayList<>();

        for (GKit gkit : plugin.getGKitManager().getKitMap().values())
            names.add(gkit.getName());

        return names;
    }
}
