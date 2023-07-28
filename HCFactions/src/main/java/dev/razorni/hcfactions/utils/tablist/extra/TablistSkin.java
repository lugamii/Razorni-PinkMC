package dev.razorni.hcfactions.utils.tablist.extra;

import lombok.Getter;

@Getter
public class TablistSkin {
    private final String signature;
    private final String value;

    public TablistSkin(String value, String signature) {
        this.value = value;
        this.signature = signature;
    }


}