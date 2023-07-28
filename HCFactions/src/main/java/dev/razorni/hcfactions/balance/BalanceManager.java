package dev.razorni.hcfactions.balance;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.balance.type.VaultBalance;
import dev.razorni.hcfactions.extras.framework.Manager;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.utils.Utils;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BalanceManager extends Manager {
    public BalanceManager(HCF plugin) {
        super(plugin);
        if (Utils.verifyPlugin("Vault", plugin)) {
            new VaultBalance(this);
        }
    }

    public boolean hasBalance(Player player, int amount) {
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        return user.getBalance() >= amount;
    }

    public void takeBalance(Player player, int amount) {
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        user.setBalance(Math.max(user.getBalance() - amount, 0));
        user.save();
    }

    public int getBalance(UUID uuid) {
        User user = this.getInstance().getUserManager().getByUUID(uuid);
        return user.getBalance();
    }

    public void giveBalance(Player player, int amount) {
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        user.setBalance(Math.max(user.getBalance() + amount, 0));
        user.save();
    }

    public void setBalance(Player player, int balance) {
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        user.setBalance(Math.max(balance, 0));
        user.save();
    }
}
