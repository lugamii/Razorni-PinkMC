package dev.razorni.hcfactions.utils.scheduler.extra;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ScheduleDay {
    THURSDAY("Thursday"),
    NONE("None"),
    SUNDAY("Sunday"),
    FRIDAY("Friday"),
    TUESDAY("Tuesday"),
    MONDAY("Monday"),
    WEDNESDAY("Wednesday"),
    SATURDAY("Saturday");

    private final String name;

}