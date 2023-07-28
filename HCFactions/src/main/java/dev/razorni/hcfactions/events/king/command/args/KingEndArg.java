package dev.razorni.hcfactions.events.king.command.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class KingEndArg extends Argument {
    public KingEndArg(CommandManager manager) {
        super(manager, Arrays.asList("stop", "end"));
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!this.getInstance().getKingManager().isActive()) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KING_COMMAND.KING_END.NOT_ACTIVE"));
            return;
        }
        this.getInstance().getKingManager().stopKing(true);
        this.getInstance().getNametagManager().update();
    }
}
