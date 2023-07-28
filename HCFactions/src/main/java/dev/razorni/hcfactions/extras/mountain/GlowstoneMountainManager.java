package dev.razorni.hcfactions.extras.mountain;

import cc.invictusgames.ilib.configuration.StaticConfiguration;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.teams.claims.Claim;
import dev.razorni.hcfactions.teams.type.GlowstoneMountainTeam;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor
public class GlowstoneMountainManager implements StaticConfiguration {

    private transient static final String GLOWSTONE_TEAM_NAME = "Glowstone";

    private transient static final long RESET_INTERVAL = TimeUnit.MINUTES.toSeconds(30L);

    private final Set<Location> glowstone = new HashSet<>();

    @Getter
    private transient long nextReset = System.currentTimeMillis();

    public void setupTask() {
        Bukkit.getServer().getScheduler().runTaskTimer(HCF.getPlugin(), () -> {
            reset();
            nextReset = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(RESET_INTERVAL);

            Bukkit.broadcastMessage(ChatColor.GOLD + "[Glowstone Mountain] "
                    + ChatColor.GREEN + "Glowstone Mountain has been reset.");
        }, 0, RESET_INTERVAL * 20);
    }

    public void scanGlowStone() {
        glowstone.clear();

        GlowstoneMountainTeam faction =
                (GlowstoneMountainTeam) HCF.getPlugin().getTeamManager().getTeam(GLOWSTONE_TEAM_NAME);

        if (faction == null)
            return;

        faction.getClaims().forEach(claim -> {
            for (int x = claim.getMinimumX(); x < claim.getMaximumX(); x++) {
                for (int y = 0; y < claim.getWorld().getMaxHeight(); y++) {
                    for (int z = claim.getMinimumZ(); z < claim.getMaximumZ(); z++) {
                        Block block = claim.getWorld().getBlockAt(x, y, z);

                        if (block.getType() == Material.GLOWSTONE)
                            glowstone.add(block.getLocation().clone());
                    }
                }
            }
        });
    }

    public void reset() {
        glowstone.forEach(location -> location.getBlock().setType(Material.GLOWSTONE));
    }

    public void saveConfig() {
        try {
            HCF.getPlugin().getConfigurationService().saveConfiguration(this,
                    new File(HCF.getPlugin().getDataFolder(), "glowstone.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Claim> getClaims() {
        return ((GlowstoneMountainTeam) HCF.getPlugin().getTeamManager()
                .getTeam(GLOWSTONE_TEAM_NAME)).getClaims();
    }

    public Claim getClaim() {
        return ((GlowstoneMountainTeam) HCF.getPlugin().getTeamManager()
                .getTeam(GLOWSTONE_TEAM_NAME)).getClaims().get(0);
    }
}
