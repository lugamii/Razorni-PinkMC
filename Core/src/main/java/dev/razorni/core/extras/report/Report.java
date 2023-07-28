package dev.razorni.core.extras.report;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Getter
@Setter
@EqualsAndHashCode
public class Report {

    private String name;
    private String message;
    private String sender;
    private String server;
    private long createdAt;
    private boolean report;
    private boolean resolved = false;
    private long resolvedAt = 0;
    private String resolvedBy = "";
    private String reported = "";

    public Report(String name, String message, String sender, long createdAt, boolean report) {
        this.name = name;
        this.message = message;
        this.createdAt = createdAt;
        this.report = report;
        this.sender = sender;
    }

    public String getCreatedDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("AEST"));
        return simpleDateFormat.format(new Date(createdAt));
    }

    public String getRemovedAtDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("AEST"));
        return simpleDateFormat.format(new Date(resolvedAt));
    }

}
