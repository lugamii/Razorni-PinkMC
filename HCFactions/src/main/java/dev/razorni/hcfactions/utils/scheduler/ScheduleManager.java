package dev.razorni.hcfactions.utils.scheduler;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.framework.Manager;
import dev.razorni.hcfactions.utils.scheduler.extra.ScheduleDay;
import dev.razorni.hcfactions.utils.Tasks;
import lombok.Getter;

import java.util.*;

@Getter
public class ScheduleManager extends Manager {
    private final ScheduleDay[] days;
    private final Map<Integer, List<Schedule>> hourly;
    private final Map<Long, List<Schedule>> daily;
    private final Table<Integer, Long, List<Schedule>> normal;
    private final TimeZone timeZone;
    private int oldMin;

    public ScheduleManager(HCF plugin) {
        super(plugin);
        this.normal = Tables.newCustomTable(new LinkedHashMap<>(), LinkedHashMap::new);
        this.daily = new LinkedHashMap<>();
        this.hourly = new LinkedHashMap<>();
        this.timeZone = TimeZone.getTimeZone(this.getSchedulesConfig().getString("TIME_ZONE"));
        this.days = ScheduleDay.values();
        this.load();
        Tasks.executeScheduled(this, 20, this::tick);
    }

    private void tick() {
        Calendar calendar = Calendar.getInstance(this.timeZone);
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if (this.oldMin == minute) {
            return;
        }
        this.oldMin = minute;
        long time = this.toLong(hour, minute);
        List<Schedule> normal = this.normal.get(day, time);
        List<Schedule> daily = this.daily.get(time);
        List<Schedule> ourly = this.hourly.get(minute);
        if (normal != null) {
            normal.forEach(Schedule::execute);
        }
        if (daily != null) {
            daily.forEach(Schedule::execute);
        }
        if (ourly != null) {
            ourly.forEach(Schedule::execute);
        }
    }

    private void load() {
        for (String s : this.getSchedulesConfig().getConfigurationSection("SCHEDULES.HOURLY").getKeys(false)) {
            String hourly = "SCHEDULES.HOURLY." + s + ".";
            Schedule schedule = new Schedule(this.getSchedulesConfig().getString(hourly + "NAME"), this.getSchedulesConfig().getString(hourly + "TIME"), this.getSchedulesConfig().getStringList(hourly + "COMMANDS"));
            this.hourly.putIfAbsent(schedule.getMinute(), new ArrayList<>());
            this.hourly.get(schedule.getMinute()).add(schedule);
        }
        for (String s : this.getSchedulesConfig().getConfigurationSection("SCHEDULES.DAILY").getKeys(false)) {
            String daily = "SCHEDULES.DAILY." + s + ".";
            Schedule schedule = new Schedule(this.getSchedulesConfig().getString(daily + "NAME"), this.getSchedulesConfig().getString(daily + "TIME"), ScheduleDay.NONE, this.getSchedulesConfig().getStringList(daily + "COMMANDS"));
            long time = this.toLong(schedule.getHour(), schedule.getMinute());
            this.daily.putIfAbsent(time, new ArrayList<>());
            this.daily.get(time).add(schedule);
        }
        for (String s : this.getSchedulesConfig().getConfigurationSection("SCHEDULES.NORMAL").getKeys(false)) {
            String normal = "SCHEDULES.NORMAL." + s + ".";
            Schedule schedule = new Schedule(this.getSchedulesConfig().getString(normal + "NAME"), this.getSchedulesConfig().getString(normal + "TIME"), ScheduleDay.valueOf(this.getSchedulesConfig().getString(normal + "DAY")), this.getSchedulesConfig().getStringList(normal + "COMMANDS"));
            long time = this.toLong(schedule.getHour(), schedule.getMinute());
            int day = schedule.getDay().ordinal();
            if (this.normal.get(day, time) == null) {
                this.normal.put(day, time, new ArrayList<>());
            }
            this.normal.get(day, time).add(schedule);
        }
    }

    private long toLong(int i1, int i2) {
        return ((long) i1 << 32) + (long) i2 - Integer.MIN_VALUE;
    }

}