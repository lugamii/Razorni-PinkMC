package dev.razorni.hcfactions.utils.hooks.ranks;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.framework.Manager;
import dev.razorni.hcfactions.utils.hooks.ranks.type.*;
import dev.razorni.hcfactions.utils.Utils;
import org.bukkit.entity.Player;

public class RankManager extends Manager implements Rank {
    private Rank rank;

    public RankManager(HCF plugin) {
        super(plugin);
        this.load();
    }

    private void load() {
        if (Utils.verifyPlugin("Core", this.getInstance())) {
            this.rank = new AquaCoreRank();
        } else if (Utils.verifyPlugin("Zoot", this.getInstance())) {
            this.rank = new ZootRank();
        } else if (Utils.verifyPlugin("Zoom", this.getInstance())) {
            this.rank = new ZoomRank();
        } else if (Utils.verifyPlugin("Mizu", this.getInstance())) {
            this.rank = new MizuRank();
        } else if (Utils.verifyPlugin("Atom", this.getInstance())) {
            this.rank = new AtomRank();
        } else if (Utils.verifyPlugin("Basic", this.getInstance())) {
            this.rank = new CoreRank();
        } else if (Utils.verifyPlugin("ZPermissions", this.getInstance())) {
            this.rank = new ZPermissionRank();
        } else if (Utils.verifyPlugin("Vault", this.getInstance())) {
            this.rank = new VaultRank();
        } else {
            this.rank = new NoneRank();
        }
    }

    @Override
    public String getRankColor(Player player) {
        return this.rank.getRankColor(player);
    }

    @Override
    public String getRankName(Player player) {
        return this.rank.getRankName(player);
    }

    @Override
    public String getRankSuffix(Player player) {
        return this.rank.getRankSuffix(player);
    }

    @Override
    public String getRankPrefix(Player player) {
        return this.rank.getRankPrefix(player);
    }
}
