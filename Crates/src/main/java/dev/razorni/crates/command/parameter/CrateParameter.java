package dev.razorni.crates.command.parameter;

import dev.razorni.crates.Crates;
import dev.razorni.crates.crate.Crate;
import cc.invictusgames.ilib.command.parameter.ParameterType;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CrateParameter implements ParameterType<Crate> {

    private final Crates plugin;

    @Override
    public Crate parse(CommandSender commandSender, String source) {
        Crate crate = plugin.getCrateManager().getCrate(source);

        if (crate == null) {
            commandSender.sendMessage(ChatColor.RED + "This create does not exist.");
            return null;
        }

        return crate;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, List<String> flags) {
        List<String> names = new ArrayList<>();

        for (Crate crate : plugin.getCrateManager().getCrateMap().values())
            names.add(crate.getName());

        return names;
    }
}
