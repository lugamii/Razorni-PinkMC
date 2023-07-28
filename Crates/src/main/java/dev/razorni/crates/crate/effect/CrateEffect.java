package dev.razorni.crates.crate.effect;

import lombok.RequiredArgsConstructor;
import org.bukkit.Effect;
import org.bukkit.Location;

@RequiredArgsConstructor
public enum CrateEffect {

    BLOOD_HELIX("Blood Helix", Effect.COLOURED_DUST) {
        @Override
        public void tick(Location location) {

        }

    },

    LAVA_RINGS("Lava Rings", Effect.LAVADRIP) {
        @Override
        public void tick(Location location) {

            for (int i = 0; i < 360; i += 360 / 4) {
                double angle = (i * Math.PI / 180);
                double x = 0.4 * Math.cos(angle);
                double z = 0.4 * Math.sin(angle);

                location.add(x, 0, z);
                location.getWorld().playEffect(location, Effect.LAVADRIP, 50);
                location.subtract(x, 0, z);
            }
        }

    };

    public final String name;
    public final Effect effect;

    public abstract void tick(Location location);
}
