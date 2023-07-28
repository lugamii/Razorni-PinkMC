package dev.razorni.hcfactions.teams.claims;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.TeamManager;
import dev.razorni.hcfactions.teams.type.WarzoneTeam;
import dev.razorni.hcfactions.teams.type.WildernessTeam;
import dev.razorni.hcfactions.utils.Utils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

public class ClaimManager extends Module<TeamManager> {
    private final Table<Integer, Integer, Long> hashCache;
    private final WildernessTeam wildernessTeam;
    private final Table<String, Long, Claim> claims;
    private final WarzoneTeam warzoneTeam;

    public ClaimManager(TeamManager manager) {
        super(manager);
        this.hashCache = HashBasedTable.create();
        this.claims = HashBasedTable.create();
        this.wildernessTeam = new WildernessTeam(manager);
        this.warzoneTeam = new WarzoneTeam(manager);
    }

    public void saveClaim(Claim claim) {
        for (int i = claim.getMinimumX(); i <= claim.getMaximumX(); ++i) {
            for (int f = claim.getMinimumZ(); f <= claim.getMaximumZ(); ++f) {
                Long cache = this.hashCache.get(i, f);
                String worldName = claim.getWorldName();
                if (cache == null) {
                    cache = this.toLong(i, f);
                    this.hashCache.put(i, f, cache);
                }
                if (!this.claims.contains(worldName, cache)) {
                    this.claims.put(worldName, cache, claim);
                }
            }
        }
    }

    public Claim getClaim(Location location) {
        int x = location.getBlockX();
        int z = location.getBlockZ();
        return this.getClaim(location.getWorld().getName(), x, z);
    }

    public WarzoneTeam getWarzoneTeam() {
        return this.warzoneTeam;
    }

    public Team getTeam(World world, int x, int z) {
        int normal = this.getTeamConfig().getInt("WARZONE.WARZONE_NORMAL");
        int nether = this.getTeamConfig().getInt("WARZONE.WARZONE_NETHER");
        Claim claim = this.getClaim(world.getName(), x, z);
        if (claim != null) {
            return this.getManager().getTeam(claim.getTeam());
        }
        if (world.getEnvironment() == World.Environment.NORMAL) {
            if (x <= normal && x >= -normal && z <= normal && z >= -normal) {
                return this.warzoneTeam;
            }
        } else if (x <= nether && x >= -nether && z <= nether && z >= -nether) {
            return this.warzoneTeam;
        }
        return this.wildernessTeam;
    }

    public WildernessTeam getWildernessTeam() {
        return this.wildernessTeam;
    }

    private long toLong(int i1, int i2) {
        return ((long) i1 << 32) + (long) i2 - Integer.MIN_VALUE;
    }

    public Team getTeam(Location location) {
        return this.getTeam(location.getWorld(), location.getBlockX(), location.getBlockZ());
    }

    public Claim getClaim(String name, int x, int z) {
        Long cache = this.hashCache.get(x, z);
        if (cache == null) {
            cache = this.toLong(x, z);
            this.hashCache.put(x, z, cache);
        }
        return this.claims.get(name, cache);
    }

    public void deleteClaim(Claim claim) {
        for (int i = claim.getMinimumX(); i <= claim.getMaximumX(); ++i) {
            for (int f = claim.getMinimumZ(); f <= claim.getMaximumZ(); ++f) {
                Long cache = this.hashCache.get(i, f);
                String worldName = claim.getWorldName();
                if (cache == null) {
                    cache = this.toLong(i, f);
                    this.hashCache.put(i, f, cache);
                }
                if (this.claims.contains(worldName, cache)) {
                    this.claims.remove(worldName, cache);
                }
            }
        }
    }

    public Table<String, Long, Claim> getClaims() {
        return this.claims;
    }

    public int getPrice(Claim claim, boolean b) {
        int area = this.getTeamConfig().getInt("CLAIMING.MULTIPLIER_AREA");
        int c = this.getTeamConfig().getInt("CLAIMING.MULTIPLIER_CLAIM");
        double pricePerBlock = this.getTeamConfig().getDouble("CLAIMING.PRICE_PER_BLOCK");
        double multiplier = this.getTeamConfig().getDouble("CLAIMING.SELL_MULTIPLIER");
        int claimNumber = this.getManager().getPlayerTeam(claim.getTeam()).getClaims().size();
        int i = 1;
        int arenas = claim.getArea();
        double price = 0.0;
        while (arenas > 0) {
            if (--arenas % area == 0) {
                ++i;
            }
            price += pricePerBlock * i;
        }
        if (claimNumber != 0) {
            claimNumber = Math.max(claimNumber + (b ? -1 : 0), 0);
            price += claimNumber * c;
        }
        if (b) {
            price *= multiplier;
        }
        return (int) price;
    }

    private boolean canTeleport(Location location) {
        Block block = location.getBlock();
        for (BlockFace face : BlockFace.values()) {
            if (face == BlockFace.DOWN || face == BlockFace.UP || this.getClaim(block.getRelative(face).getLocation()) == null)
                continue;
            return false;
        }
        return true;
    }

    public List<Claim> getNearbyCuboids(Location location, int i) {
        List<Claim> claims = new ArrayList<>();
        String name = location.getWorld().getName();
        int x = location.getBlockX();
        int z = location.getBlockZ();
        for (int f = -i; f <= i; ++f) {
            for (int t = -i; t <= i; ++t) {
                Claim claim = this.getClaim(name, x + f, z + t);
                if (claim != null) {
                    if (!claims.contains(claim)) {
                        claims.add(claim);
                    }
                }
            }
        }
        return claims;
    }

    public Table<Integer, Integer, Long> getHashCache() {
        return this.hashCache;
    }

    public void teleportSafe(Player player) {
        Location location = player.getLocation();
        boolean b = false;
        if (this.getClaim(location) == null) {
            player.teleport(Utils.getActualHighestBlock(location.getBlock()).getLocation().add(0.5, 1.0, 0.5));
            return;
        }
        for (int i = 2, f = -2; i < 250; i += 2, f -= 2) {
            for (int t = 2, j = -2; t < 250; t += 2, j -= 2) {
                Location loc = location.clone().add(i, 0.0, t);
                if (this.getClaim(loc) == null && this.canTeleport(loc)) {
                    b = true;
                    player.teleport(Utils.getActualHighestBlock(loc.getBlock()).getLocation().add(0.5, 1.0, 0.5));
                    break;
                }
                Location locClone = location.clone().add(f, 0.0, j);
                if (this.getClaim(locClone) == null && this.canTeleport(locClone)) {
                    b = true;
                    player.teleport(Utils.getActualHighestBlock(locClone.getBlock()).getLocation().add(0.5, 1.0, 0.5));
                    break;
                }
            }
        }
        if (!b) {
            player.setMetadata("loggedout", new FixedMetadataValue(this.instance, true));
            player.kickPlayer(this.getLanguageConfig().getString("STUCK_TIMER.NO_SAFE_LOC"));
        }
    }
}
