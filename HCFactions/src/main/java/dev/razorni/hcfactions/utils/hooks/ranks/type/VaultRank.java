package dev.razorni.hcfactions.utils.hooks.ranks.type;

import dev.razorni.hcfactions.utils.hooks.ranks.Rank;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VaultRank implements Rank {
    private final Chat chat;

    public VaultRank() {
        this.chat = Bukkit.getServer().getServicesManager().getRegistration(Chat.class).getProvider();
    }

    @Override
    public String getRankSuffix(Player player) {
        return this.chat.getPlayerSuffix(player);
    }

    @Override
    public String getRankColor(Player player) {
        return "";
    }

    @Override
    public String getRankName(Player player) {
        return this.chat.getPrimaryGroup(player);
    }

    @Override
    public String getRankPrefix(Player player) {
        return this.chat.getPlayerPrefix(player);
    }
}
