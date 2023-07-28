package dev.razorni.hcfactions.commands;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.commands.type.*;
import dev.razorni.hcfactions.commands.type.essential.*;
import dev.razorni.hcfactions.deathban.command.DeathbanCommand;
import dev.razorni.hcfactions.events.king.command.KingCommand;
import dev.razorni.hcfactions.events.koth.command.KothCommand;
import dev.razorni.hcfactions.extras.ability.command.AbilitiesCommand;
import dev.razorni.hcfactions.extras.ability.command.AbilityCommand;
import dev.razorni.hcfactions.extras.dailyrewards.command.CoinsResetCommand;
import dev.razorni.hcfactions.extras.dailyrewards.command.DailyCommand;
import dev.razorni.hcfactions.extras.dailyrewards.command.DailyResetCommand;
import dev.razorni.hcfactions.extras.framework.Manager;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.extras.mountain.command.GlowstoneCommand;
import dev.razorni.hcfactions.extras.redeem.command.RedeemCommand;
import dev.razorni.hcfactions.extras.redeem.command.ResetRedeemCommand;
import dev.razorni.hcfactions.extras.shop.command.OpenShopCommand;
import dev.razorni.hcfactions.extras.spawners.command.SpawnerCommand;
import dev.razorni.hcfactions.extras.supplydrop.command.SupplyDropCommand;
import dev.razorni.hcfactions.kits.commands.KitCommand;
import dev.razorni.hcfactions.reclaims.command.ReclaimCommand;
import dev.razorni.hcfactions.reclaims.command.ResetReclaimCommand;
import dev.razorni.hcfactions.staff.command.FreezeCommand;
import dev.razorni.hcfactions.staff.command.StaffBuildCommand;
import dev.razorni.hcfactions.staff.command.StaffCommand;
import dev.razorni.hcfactions.staff.command.VanishCommand;
import dev.razorni.hcfactions.teams.commands.citadel.CitadelCommand;
import dev.razorni.hcfactions.teams.commands.systeam.SysTeamCommand;
import dev.razorni.hcfactions.teams.commands.team.TeamCommand;
import dev.razorni.hcfactions.timers.command.customtimer.CTimerCommand;
import dev.razorni.hcfactions.timers.command.keyall.KeyAllCommand;
import dev.razorni.hcfactions.timers.command.timer.TimerCommand;
import dev.razorni.hcfactions.utils.scheduler.command.SchedulesCommand;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class CommandManager extends Manager {
    private final List<Command> commands;

    public CommandManager(HCF plugin) {
        super(plugin);
        this.commands = new ArrayList<Command>();
        this.load();
        this.checkCommands();
        plugin.getVersionManager().getVersion().getCommandMap().registerAll("azurite", (List) this.commands.stream().map(Command::asBukkitCommand).collect(Collectors.toList()));
        this.commands.clear();
    }

    private void load() {
        this.commands.add(new TeamCommand(this));
        this.commands.add(new SysTeamCommand(this));
        this.commands.add(new TimerCommand(this));
        this.commands.add(new CTimerCommand(this));
        this.commands.add(new DeathbanCommand(this));
        this.commands.add(new KitCommand(this));
        this.commands.add(new DailyCommand(this));
        this.commands.add(new DailyResetCommand(this));
        this.commands.add(new CoinsResetCommand(this));
        this.commands.add(new GlowstoneCommand(this));
        this.commands.add(new KothCommand(this));
        this.commands.add(new KingCommand(this));
        this.commands.add(new CitadelCommand(this));
        this.commands.add(new KeyAllCommand(this));
        this.commands.add(new CraftCommand(this));
        this.commands.add(new TopCommand(this));
        this.commands.add(new RenameCommand(this));
        this.commands.add(new RepairCommand(this));
        this.commands.add(new GappleCommand(this));
        this.commands.add(new StaffCommand(this));
        this.commands.add(new GamemodeCommand(this));
        this.commands.add(new GMCCommand(this));
        this.commands.add(new GMSCommand(this));
        this.commands.add(new PvPCommand(this));
        this.commands.add(new BalanceCommand(this));
        this.commands.add(new EcoManageCommand(this));
        this.commands.add(new WorldCommand(this));
        this.commands.add(new TLCommand(this));
        this.commands.add(new SettingsCommand(this));
        this.commands.add(new LogoutCommand(this));
        this.commands.add(new TpCommand(this));
        this.commands.add(new TpHereCommand(this));
        this.commands.add(new TpRandomCommand(this));
        this.commands.add(new TpLocCommand(this));
        this.commands.add(new HealCommand(this));
        this.commands.add(new FeedCommand(this));
        this.commands.add(new BroadcastCommand(this));
        this.commands.add(new MessageCommand(this));
        this.commands.add(new ReplyCommand(this));
        this.commands.add(new IgnoreCommand(this));
        this.commands.add(new ClearCommand(this));
        this.commands.add(new ClearChatCommand(this));
        this.commands.add(new ToggleSoundsCommand(this));
        this.commands.add(new TogglePMCommand(this));
        this.commands.add(new LivesCommand(this));
        this.commands.add(new PayCommand(this));
        this.commands.add(new KillCommand(this));
        this.commands.add(new SupplyDropCommand(this));
        this.commands.add(new ReclaimCommand(this));
        this.commands.add(new RedeemCommand(this));
        this.commands.add(new ResetRedeemCommand(this));
        this.commands.add(new ResetReclaimCommand(this));
        this.commands.add(new SpawnerCommand(this));
        this.commands.add(new AbilityCommand(this));
        this.commands.add(new AbilitiesCommand(this));
        this.commands.add(new LivesManageCommand(this));
        this.commands.add(new SOTWCommand(this));
        this.commands.add(new SetEndCommand(this));
        this.commands.add(new TpAllCommand(this));
        this.commands.add(new PingCommand(this));
        this.commands.add(new SchedulesCommand(this));
        this.commands.add(new StaffBuildCommand(this));
        this.commands.add(new VanishCommand(this));
        this.commands.add(new StrengthNerfCommand(this));
        this.commands.add(new FreezeCommand(this));
        this.commands.add(new OpenShopCommand(this));
        this.commands.add(new SpawnCommand(this));
        this.commands.add(new StatsCommand(this));
        this.commands.add(new LeaderboardsCommand(this));
    }

    private void checkCommands() {
        List<String> disabled = this.getConfig().getStringList("DISABLED_COMMANDS.MAIN_COMMANDS");
        Iterator<Command> cmds = this.commands.iterator();
        while (cmds.hasNext()) {
            Command cmd = cmds.next();
            if (disabled.contains(cmd.getName().toLowerCase())) {
                cmds.remove();
            } else {
                for (String s : cmd.aliases()) {
                    if (!disabled.contains(s.toLowerCase())) {
                        continue;
                    }
                    cmds.remove();
                    break;
                }
            }
        }
    }
}
