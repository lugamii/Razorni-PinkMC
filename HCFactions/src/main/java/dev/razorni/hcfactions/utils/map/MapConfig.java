package dev.razorni.hcfactions.utils.map;

import cc.invictusgames.ilib.configuration.StaticConfiguration;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;

@Data
@NoArgsConstructor
public class MapConfig implements StaticConfiguration {

    private Location endSpawn = null;

}
