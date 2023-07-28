package dev.razorni.hcfactions.users;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class PlaySession {

    private long startTime;
    private long endTime;

    public PlaySession() {
        this.startTime = System.currentTimeMillis();
    }

    public long getTime() {
        return endTime - startTime;
    }

    public long getCurrentSession() {
        return System.currentTimeMillis() - startTime;
    }

}
