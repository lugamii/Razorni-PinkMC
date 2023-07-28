package dev.razorni.hcfactions.deathban;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.utils.Formatter;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class Deathban extends Module<DeathbanManager> {
    private long time;
    private String reason;
    private UUID uniqueID;
    private Location location;
    private Date date;

    public Deathban(DeathbanManager manager, UUID uuid, long time, String reason, Location location) {
        super(manager);
        this.uniqueID = uuid;
        this.reason = reason;
        this.location = location;
        this.date = new Date();
        this.time = System.currentTimeMillis() + time;
    }

    public boolean isExpired() {
        return this.getTime() < 0L;
    }

    public String getDateFormatted() {
        return Formatter.DATE_FORMAT.format(this.date);
    }

    public long getTime() {
        long t = this.time - System.currentTimeMillis();
        if (t < 0L) {
            Player player = Bukkit.getPlayer(this.uniqueID);
            if (player != null) {
                this.getManager().removeDeathban(player);
            }
        }
        return t;
    }
}
