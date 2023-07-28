package dev.razorni.hcfactions.signs.economy;

import dev.razorni.hcfactions.balance.BalanceManager;
import dev.razorni.hcfactions.signs.CustomSignManager;
import dev.razorni.hcfactions.utils.ItemUtils;
import lombok.Getter;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class EconomyBuySign extends EconomySign {
    private final int amountIndex;
    private final int priceIndex;
    private final int materialIndex;

    public EconomyBuySign(CustomSignManager manager) {
        super(manager, manager.getConfig().getStringList("SIGNS_CONFIG.BUY_SIGN.LINES"));
        this.materialIndex = this.getIndex("%material%");
        this.amountIndex = this.getIndex("%amount%");
        this.priceIndex = this.getIndex("%price%");
    }

    @Override
    public void onClick(Player player, Sign sign) {
        ItemStack stack = this.getItemStack(sign.getLine(this.materialIndex));
        BalanceManager manager = this.getInstance().getBalanceManager();
        int price = Integer.parseInt(sign.getLine(this.priceIndex).replaceAll("\\$", ""));
        int amount = Integer.parseInt(sign.getLine(this.amountIndex));
        if (!manager.hasBalance(player, price)) {
            String[] lines = sign.getLines().clone();
            lines[this.priceIndex] = this.getLanguageConfig().getString("CUSTOM_SIGNS.ECONOMY_SIGNS.INSUFFICIENT_MONEY");
            this.sendSignChange(player, sign, lines);
            return;
        }
        String[] lines = sign.getLines().clone();
        lines[this.materialIndex + 1] = this.getLanguageConfig().getString("CUSTOM_SIGNS.ECONOMY_SIGNS.PURCHASED");
        manager.takeBalance(player, price);
        stack.setAmount(amount);
        this.sendSignChange(player, sign, lines);
        ItemUtils.giveItem(player, stack, player.getLocation());
    }
}
