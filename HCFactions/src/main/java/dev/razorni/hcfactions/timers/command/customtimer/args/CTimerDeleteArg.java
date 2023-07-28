package dev.razorni.hcfactions.timers.command.customtimer.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.timers.type.CustomTimer;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CTimerDeleteArg extends Argument {
    public CTimerDeleteArg(CommandManager manager) {
        super(manager, Arrays.asList("delete", "remove", "stop"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String other = args[args.length - 1];
            return this.getInstance().getTimerManager().getCustomTimers().keySet().stream().filter(llllllllllllllllIIlIIlIlIIlIIllI -> llllllllllllllllIIlIIlIlIIlIIllI.regionMatches(true, 0, other, 0, other.length())).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        CustomTimer timer = this.getInstance().getTimerManager().getCustomTimer(args[0]);
        if (timer == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("CTIMER_COMMAND.CTIMER_DELETE.NOT_FOUND").replaceAll("%name%", args[0]));
            return;
        }
        this.getInstance().getTimerManager().getCustomTimers().remove(timer.getName());
        this.sendMessage(sender, this.getLanguageConfig().getString("CTIMER_COMMAND.CTIMER_DELETE.DELETED").replaceAll("%name%", args[0]));
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("CTIMER_COMMAND.CTIMER_DELETE.USAGE");
    }
}
