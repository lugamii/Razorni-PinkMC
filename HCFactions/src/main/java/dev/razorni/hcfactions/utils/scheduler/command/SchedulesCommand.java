package dev.razorni.hcfactions.utils.scheduler.command;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.utils.scheduler.Schedule;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class SchedulesCommand extends Command {
    public SchedulesCommand(CommandManager manager) {
        super(manager, "schedule");
        this.setAsync(true);
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("koths", "schedules");
    }

    private String calcTime(int i1, int i2) {
        return i1 + ":" + ((String.valueOf(i2).length() < 2) ? "0" + i2 : Integer.valueOf(i2)) + ((i1 > 12) ? "PM" : "AM");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Calendar calendar = Calendar.getInstance(this.getInstance().getScheduleManager().getTimeZone());
        List<String> commands = new ArrayList<>();
        List<String> daylis = new ArrayList<>();
        List<String> hourlys = new ArrayList<>();
        for (List<Schedule> normal : this.getInstance().getScheduleManager().getNormal().values()) {
            for (Schedule schedule : normal) {
                if (schedule.getName().equalsIgnoreCase("NONE")) {
                    continue;
                }
                commands.add(this.getLanguageConfig().getString("SCHEDULES_COMMAND.NORMAL_FORMAT").replaceAll("%name%", schedule.getName()).replaceAll("%time%", schedule.getTime()).replaceAll("%day%", schedule.getDay().getName()));
            }
        }
        for (List<Schedule> daily : this.getInstance().getScheduleManager().getDaily().values()) {
            for (Schedule schedule : daily) {
                if (schedule.getName().equalsIgnoreCase("NONE")) {
                    continue;
                }
                daylis.add(this.getLanguageConfig().getString("SCHEDULES_COMMAND.DAILY_FORMAT").replaceAll("%name%", schedule.getName()).replaceAll("%time%", schedule.getTime()).replaceAll("%day%", schedule.getDay().getName()));
            }
        }
        for (List<Schedule> hourly : this.getInstance().getScheduleManager().getHourly().values()) {
            for (Schedule schedule : hourly) {
                if (schedule.getName().equalsIgnoreCase("NONE")) {
                    continue;
                }
                hourlys.add(this.getLanguageConfig().getString("SCHEDULES_COMMAND.HOURLY_FORMAT").replaceAll("%name%", schedule.getName()).replaceAll("%time%", schedule.getTime()));
            }
        }
        for (String s : this.getLanguageConfig().getStringList("SCHEDULES_COMMAND.SCHEDULES")) {
            if (s.equalsIgnoreCase("%normal%")) {
                for (String normal : commands) {
                    this.sendMessage(sender, normal);
                }
            } else if (s.equalsIgnoreCase("%daily%")) {
                for (String daily : daylis) {
                    this.sendMessage(sender, daily);
                }
            } else if (s.equalsIgnoreCase("%hourly%")) {
                for (String hourly : hourlys) {
                    this.sendMessage(sender, hourly);
                }
            } else {
                this.sendMessage(sender, s.replaceAll("%time%", this.calcTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))));
            }
        }
    }

    @Override
    public List<String> usage() {
        return null;
    }
}
