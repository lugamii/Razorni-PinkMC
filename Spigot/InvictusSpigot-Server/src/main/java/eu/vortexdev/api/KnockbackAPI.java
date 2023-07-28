package eu.vortexdev.api;

import eu.vortexdev.api.knockback.KnockbackProfile;
import eu.vortexdev.invictusspigot.InvictusSpigot;
import org.bukkit.entity.Player;

import java.util.Collection;

public class KnockbackAPI {

    /**
     * Get a {@link KnockbackProfile} by its name
     *
     * @param name the name of the profile
     * @return     {@link KnockbackProfile}
     */
    public static KnockbackProfile getByName(String name) {
       return InvictusSpigot.INSTANCE.getKnockbackManager().getProfile(name);
    }

    /**
     * Apply a {@link KnockbackProfile} to the given player
     *
     * @param profile the knockback profile
     * @param player  the player
     */
    public static void applyKnockback(KnockbackProfile profile, Player player) {
        player.setKnockbackProfile(profile);
    }

    /**
     * Get the default {@link KnockbackProfile}
     *
     * @return the default knockback
     */
    public static KnockbackProfile getDefault() {
        return InvictusSpigot.INSTANCE.getKnockbackManager().getProfile("Default");
    }

    /**
     * Add a profile {@link KnockbackProfile}
     *
     * @param profile the knockback profile
     */
    public static void addProfile(KnockbackProfile profile) {
        InvictusSpigot.INSTANCE.getKnockbackManager().addProfile(profile);
    }

    /**
     * Remove a profile {@link KnockbackProfile}
     *
     * @param profile the knockback profile
     */
    public static void removeProfile(KnockbackProfile profile) {
        InvictusSpigot.INSTANCE.getKnockbackManager().removeProfile(profile);
    }

    /**
     * Saves all profiles to knockback.yml
     */
    public static void saveProfiles() {
        InvictusSpigot.INSTANCE.getKnockbackManager().saveProfiles();
    }

    /**
     * Reloads all profiles from knockback.yml
     */
    public static void reloadProfiles() {
        InvictusSpigot.INSTANCE.getKnockbackManager().reloadProfiles();
    }

    /**
     * Get collection of all profiles {@link KnockbackProfile}
     *
     * @return the collection of profiles
     */
    public static Collection<KnockbackProfile> getProfiles() {
        return InvictusSpigot.INSTANCE.getKnockbackManager().getProfiles();
    }
}
