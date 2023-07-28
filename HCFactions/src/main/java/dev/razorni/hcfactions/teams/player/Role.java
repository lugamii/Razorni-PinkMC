package dev.razorni.hcfactions.teams.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    CAPTAIN("Captain"),
    CO_LEADER("Co-Leader"),
    MEMBER("Member"),
    LEADER("Leader");

    private final String name;
}