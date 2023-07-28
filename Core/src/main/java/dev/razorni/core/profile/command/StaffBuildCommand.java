package dev.razorni.core.profile.command;

import dev.razorni.core.Core;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.builder.TitleBuilder;
import dev.razorni.core.util.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class StaffBuildCommand {

    @Command(names = { "staffbuild", "build" }, permission = "gravity.command.build")
    public static void toggleBuild(Player player) {
        if (!player.hasMetadata("buildmode")) {
            player.setMetadata("buildmode", new FixedMetadataValue(Core.getInstance(), player));
            player.sendMessage(CC.translate("&fYou have just &aenabled &fyour &6Build Mode&f."));
            TitleBuilder title = new TitleBuilder("&d&lBUILD MODE", "&fEnabled", 20, 60, 20);
            title.send(player);
        } else {
            player.removeMetadata("buildmode", Core.getInstance());
            player.sendMessage(CC.translate("&fYou have just &cdisabled &fyour &6Build Mode&f."));
            TitleBuilder title = new TitleBuilder("&d&lBUILD MODE", "&fDisabled", 20, 60, 20);
            title.send(player);

        }
    }

}
