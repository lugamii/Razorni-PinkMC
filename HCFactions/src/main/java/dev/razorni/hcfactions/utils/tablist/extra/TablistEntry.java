package dev.razorni.hcfactions.utils.tablist.extra;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TablistEntry {
    private String text;
    private int ping;

    public TablistEntry(String text, int ping) {
        this.text = text;
        this.ping = ping;
    }
}