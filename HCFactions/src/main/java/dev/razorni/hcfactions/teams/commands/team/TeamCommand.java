package dev.razorni.hcfactions.teams.commands.team;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.teams.commands.team.args.*;
import dev.razorni.hcfactions.teams.commands.team.args.captain.TeamInviteArg;
import dev.razorni.hcfactions.teams.commands.team.args.captain.TeamKickArg;
import dev.razorni.hcfactions.teams.commands.team.args.captain.TeamUninviteArg;
import dev.razorni.hcfactions.teams.commands.team.args.captain.TeamWithdrawArg;
import dev.razorni.hcfactions.teams.commands.team.args.co_leader.*;
import dev.razorni.hcfactions.teams.commands.team.args.leader.TeamDisbandArg;
import dev.razorni.hcfactions.teams.commands.team.args.leader.TeamLeaderArg;
import dev.razorni.hcfactions.teams.commands.team.args.leader.TeamRenameArg;
import dev.razorni.hcfactions.teams.commands.team.args.staff.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TeamCommand extends Command {
    private final List<Argument> arguments;

    public TeamCommand(CommandManager manager) {
        super(manager, "team");
        this.arguments = new ArrayList<>(Arrays.asList(new TeamSetBalArg(manager), new TeamSetPointsArg(manager), new TeamSetCapsArg(manager), new TeamSetDtrArg(manager), new TeamTeleportArg(manager), new TeamSetRegenArg(manager), new TeamSetLeaderArg(manager), new TeamCreateArg(manager), new TeamInviteArg(manager), new TeamDisbandArg(manager), new TeamClaimArg(manager), new TeamInfoArg(manager), new TeamMapArg(manager), new TeamKickArg(manager), new TeamLeaveArg(manager), new TeamListArg(manager), new TeamAllyArg(manager), new TeamSetHQArg(manager), new TeamSortArg(manager), new TeamJoinArg(manager), new TeamTopArg(manager), new TeamStuckArg(manager), new TeamHQArg(manager), new TeamFocusArg(manager), new TeamUnfocusArg(manager), new TeamRallyArg(manager), new TeamUnrallyArg(manager), new TeamDepositArg(manager), new TeamWithdrawArg(manager), new TeamRenameArg(manager), new TeamChatArg(manager), new TeamPromoteArg(manager), new TeamLeaderArg(manager), new TeamUninviteArg(manager), new TeamUnclaimArg(manager), new TeamDemoteArg(manager), new TeamLockClaimArg(manager), new TeamUnallyArg(manager)));
        this.checkArguments();
        this.handleArguments(this.arguments);
        this.arguments.clear();
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("t", "f", "faction");
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("TEAM_COMMAND.USAGE");
    }

    private void checkArguments() {
        List<String> disableds = this.getConfig().getStringList("DISABLED_COMMANDS.TEAM_SUBCOMMANDS");
        Iterator<Argument> iterator = this.arguments.iterator();
        while (iterator.hasNext()) {
            Argument argument = iterator.next();
            for (String name : argument.getNames()) {
                if (!disableds.contains(name.toLowerCase())) {
                    continue;
                }
                iterator.remove();
            }
        }
    }
}