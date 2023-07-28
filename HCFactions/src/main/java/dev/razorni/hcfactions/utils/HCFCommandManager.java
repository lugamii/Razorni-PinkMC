package dev.razorni.hcfactions.utils;

import cc.invictusgames.ilib.command.CommandService;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.commands.type.CrowbarCommand;
import dev.razorni.hcfactions.commands.type.EndPortalCommand;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HCFCommandManager {

    private final HCF plugin;

    public void loadCommands() {

        CommandService.register(HCF.getPlugin(),
                new CrowbarCommand(),
                new EndPortalCommand(plugin)
        );
    }

}
