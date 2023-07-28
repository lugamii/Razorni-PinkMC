package dev.razorni.hcfactions.balance.type;

import dev.razorni.hcfactions.balance.BalanceManager;
import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.utils.CC;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.ServicePriority;

import java.util.ArrayList;
import java.util.List;

public class VaultBalance extends Module<BalanceManager> implements Economy {
    public VaultBalance(BalanceManager manager) {
        super(manager);
        Bukkit.getServicesManager().register((Class) Economy.class, this, this.instance, ServicePriority.Highest);
    }

    public boolean createPlayerAccount(OfflinePlayer player) {
        return false;
    }

    public boolean hasAccount(OfflinePlayer player) {
        return this.instance.getUserManager().getByUUID(player.getUniqueId()) != null;
    }

    public EconomyResponse depositPlayer(String player, double amount) {
        return this.depositPlayer(CC.getPlayer(player), amount);
    }

    public EconomyResponse depositPlayer(String player, String s, double amount) {
        return this.depositPlayer(player, amount);
    }

    public EconomyResponse deleteBank(String player) {
        return null;
    }

    public boolean createPlayerAccount(String player) {
        return false;
    }

    public boolean createPlayerAccount(OfflinePlayer player, String s) {
        return false;
    }

    public EconomyResponse isBankOwner(String player, String s) {
        return null;
    }

    public double getBalance(OfflinePlayer player) {
        return this.getInstance().getUserManager().getByUUID(player.getUniqueId()).getBalance();
    }

    public boolean hasBankSupport() {
        return false;
    }

    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        if (user == null) {
            return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "never joined!");
        }
        if (user.getBalance() <= 0) {
            return new EconomyResponse(0.0, user.getBalance(), EconomyResponse.ResponseType.FAILURE, "negative funds");
        }
        user.setBalance(user.getBalance() + (int) amount);
        return new EconomyResponse(amount, user.getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse bankWithdraw(String player, double amount) {
        return null;
    }

    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        if (user == null) {
            return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "never joined!");
        }
        if (user.getBalance() < 0) {
            return new EconomyResponse(0.0, user.getBalance(), EconomyResponse.ResponseType.FAILURE, "negative funds");
        }
        if (user.getBalance() < amount) {
            return new EconomyResponse(0.0, user.getBalance(), EconomyResponse.ResponseType.FAILURE, "insufficient funds");
        }
        user.setBalance(user.getBalance() - (int) amount);
        return new EconomyResponse(amount, user.getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    public boolean has(OfflinePlayer player, String s, double amount) {
        return this.has(player, amount);
    }

    public EconomyResponse withdrawPlayer(String player, double amount) {
        return this.withdrawPlayer(CC.getPlayer(player), amount);
    }

    public String getName() {
        return "Azurite";
    }

    public boolean has(String player, double amount) {
        return this.getBalance(player) >= amount;
    }

    public boolean createPlayerAccount(String player, String amount) {
        return false;
    }

    public EconomyResponse createBank(String player, String amount) {
        return null;
    }

    public EconomyResponse withdrawPlayer(String player, String s, double amount) {
        return this.withdrawPlayer(player, amount);
    }

    public EconomyResponse isBankMember(String s, OfflinePlayer player) {
        return null;
    }

    public EconomyResponse depositPlayer(OfflinePlayer player, String s, double amount) {
        return this.depositPlayer(player, amount);
    }

    public double getBalance(String player) {
        return this.getInstance().getUserManager().getByUUID(CC.getPlayer(player).getUniqueId()).getBalance();
    }

    public EconomyResponse bankHas(String player, double amount) {
        return null;
    }

    public String currencyNamePlural() {
        return "";
    }

    public boolean hasAccount(String player) {
        return this.hasAccount(CC.getPlayer(player));
    }

    public boolean has(OfflinePlayer player, double amount) {
        return this.getBalance(player) >= amount;
    }

    public EconomyResponse bankDeposit(String player, double amount) {
        return null;
    }

    public boolean has(String player, String s, double amount) {
        return this.has(player, amount);
    }

    public boolean hasAccount(OfflinePlayer player, String s) {
        return this.hasAccount(player);
    }

    public EconomyResponse isBankMember(String player, String s) {
        return null;
    }

    public EconomyResponse createBank(String s, OfflinePlayer player) {
        return null;
    }

    public String format(double amount) {
        return String.valueOf(amount);
    }

    public EconomyResponse bankBalance(String amount) {
        return null;
    }

    public double getBalance(String player, String s) {
        return this.getBalance(CC.getPlayer(player));
    }

    public EconomyResponse withdrawPlayer(OfflinePlayer player, String s, double amount) {
        return this.withdrawPlayer(player, amount);
    }

    public String currencyNameSingular() {
        return "";
    }

    public List<String> getBanks() {
        return new ArrayList<>();
    }

    public boolean hasAccount(String player, String s) {
        return this.hasAccount(player);
    }

    public int fractionalDigits() {
        return -1;
    }

    public EconomyResponse isBankOwner(String s, OfflinePlayer player) {
        return null;
    }

    public double getBalance(OfflinePlayer player, String s) {
        return this.getBalance(player);
    }

    public boolean isEnabled() {
        return this.instance.isEnabled();
    }
}
