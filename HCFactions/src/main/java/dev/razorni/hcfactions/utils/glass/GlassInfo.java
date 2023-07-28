package dev.razorni.hcfactions.utils.glass;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;

@Getter
@AllArgsConstructor
public class GlassInfo {

    private final GlassManager.GlassType type;
    private final Location location;

    private final Material material;
    private final byte data;
}