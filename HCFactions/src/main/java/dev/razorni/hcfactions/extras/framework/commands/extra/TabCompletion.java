package dev.razorni.hcfactions.extras.framework.commands.extra;

import lombok.Getter;

import java.util.List;

@Getter
public class TabCompletion {
    private final String permission;
    private final int arg;
    private final List<String> names;

    public TabCompletion(List<String> names, int arg, String permission) {
        this.names = names;
        this.permission = permission;
        this.arg = arg;
    }

    public TabCompletion(List<String> names, int arg) {
        this.names = names;
        this.permission = null;
        this.arg = arg;
    }
}
