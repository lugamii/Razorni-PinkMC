package dev.razorni.hcfactions.reclaims;

import lombok.Getter;

import java.util.List;

@Getter
public class Reclaim {
    private final String name;
    private final List<String> commands;
    private final int priority;


    public Reclaim(String name, List<String> commands, int priority) {
        this.name = name;
        this.commands = commands;
        this.priority = priority;
    }

}