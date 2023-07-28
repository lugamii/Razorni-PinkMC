package dev.razorni.hcfactions.signs;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.framework.Manager;
import dev.razorni.hcfactions.signs.economy.EconomyBuySign;
import dev.razorni.hcfactions.signs.economy.EconomySellSign;
import dev.razorni.hcfactions.signs.elevators.ElevatorDownSign;
import dev.razorni.hcfactions.signs.elevators.ElevatorUpSign;
import dev.razorni.hcfactions.signs.kits.KitSign;
import dev.razorni.hcfactions.signs.listener.CustomSignListener;
import dev.razorni.hcfactions.signs.subclaim.SubclaimSign;
import lombok.Getter;

@Getter
public class CustomSignManager extends Manager {
    private EconomySellSign sellSign;
    private ElevatorUpSign upSign;
    private EconomyBuySign buySign;
    private ElevatorDownSign downSign;
    private KitSign kitSign;
    private SubclaimSign subclaimSign;

    public CustomSignManager(HCF plugin) {
        super(plugin);
        new CustomSignListener(this);
        if (this.getConfig().getBoolean("SIGNS_CONFIG.UP_SIGN.ENABLED")) {
            this.upSign = new ElevatorUpSign(this);
        }
        if (this.getConfig().getBoolean("SIGNS_CONFIG.DOWN_SIGN.ENABLED")) {
            this.downSign = new ElevatorDownSign(this);
        }
        if (this.getConfig().getBoolean("SIGNS_CONFIG.KIT_SIGN.ENABLED")) {
            this.kitSign = new KitSign(this);
        }
        if (this.getConfig().getBoolean("SIGNS_CONFIG.BUY_SIGN.ENABLED")) {
            this.buySign = new EconomyBuySign(this);
        }
        if (this.getConfig().getBoolean("SIGNS_CONFIG.SELL_SIGN.ENABLED")) {
            this.sellSign = new EconomySellSign(this);
        }
        if (this.getConfig().getBoolean("SIGNS_CONFIG.SUBCLAIM_SIGN.ENABLED")) {
            this.subclaimSign = new SubclaimSign(this);
        }
    }

    public CustomSign getSign(String[] lines) {
        if (lines[this.kitSign.getKitIndex()].toLowerCase().contains("kit")) {
            return this.kitSign;
        }
        if (lines[0].toLowerCase().contains("buy")) {
            return this.buySign;
        }
        if (lines[0].toLowerCase().contains("sell")) {
            return this.sellSign;
        }
        if (lines[this.upSign.getElevatorIndex()].toLowerCase().contains("elevator")) {
            if (lines[this.upSign.getUpIndex()].toLowerCase().contains("up")) {
                return this.upSign;
            }
            if (lines[this.downSign.getDownIndex()].toLowerCase().contains("down")) {
                return this.downSign;
            }
        }
        return null;
    }
}