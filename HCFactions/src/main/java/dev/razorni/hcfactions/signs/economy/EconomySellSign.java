package dev.razorni.hcfactions.signs.economy;

import dev.razorni.hcfactions.balance.BalanceManager;
import dev.razorni.hcfactions.signs.CustomSignManager;
import dev.razorni.hcfactions.utils.Utils;
import lombok.Getter;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class EconomySellSign extends EconomySign {
    private final int priceIndex;
    private final int materialIndex;
    private final int amountIndex;

    public EconomySellSign(CustomSignManager manager) {
        super(manager, manager.getConfig().getStringList("SIGNS_CONFIG.SELL_SIGN.LINES"));
        this.materialIndex = this.getIndex("%material%");
        this.amountIndex = this.getIndex("%amount%");
        this.priceIndex = this.getIndex("%price%");
    }

    @Override
    public void onClick(Player player, Sign sign) {
        ItemStack stack = this.getItemStack(sign.getLine(this.materialIndex));
        BalanceManager manager = this.getInstance().getBalanceManager();
        int amount = Utils.getAmountItems(this.getManager(), player, stack);
        int price = Integer.parseInt(sign.getLine(this.priceIndex).replaceAll("\\$", ""));
        int amountIndex = Integer.parseInt(sign.getLine(this.amountIndex));
        if (amount < amountIndex) {
            String[] lines = sign.getLines().clone();
            lines[this.priceIndex] = this.getLanguageConfig().getString("CUSTOM_SIGNS.ECONOMY_SIGNS.INSUFFICIENT_BLOCKS");
            this.sendSignChange(player, sign, lines);
            return;
        }
        String[] lines = sign.getLines().clone();
        lines[this.materialIndex + 1] = this.getLanguageConfig().getString("CUSTOM_SIGNS.ECONOMY_SIGNS.SOLD");
        manager.giveBalance(player, price);
        this.sendSignChange(player, sign, lines);
        Utils.takeItems(this.getManager(), player, stack, amountIndex);
    }

}