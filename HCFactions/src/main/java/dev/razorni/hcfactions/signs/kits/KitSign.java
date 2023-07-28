package dev.razorni.hcfactions.signs.kits;

import dev.razorni.hcfactions.kits.Kit;
import dev.razorni.hcfactions.signs.CustomSign;
import dev.razorni.hcfactions.signs.CustomSignManager;
import lombok.Getter;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

@Getter
public class KitSign extends CustomSign {
    private final int kitTypeIndex;
    private final int kitIndex;

    public KitSign(CustomSignManager manager) {
        super(manager, manager.getConfig().getStringList("SIGNS_CONFIG.KIT_SIGN.LINES"));
        this.kitIndex = this.getIndex("kit");
        this.kitTypeIndex = this.getIndex("%kit%");
    }

    @Override
    public void onClick(Player player, Sign sign) {
        Kit kit = this.getInstance().getKitManager().getKit(sign.getLine(this.kitTypeIndex));
        if (kit == null) {
            player.sendMessage(this.getLanguageConfig().getString("CUSTOM_SIGNS.KIT_SIGNS.KIT_NOT_FOUND"));
            return;
        }
        if (kit.getCooldown().hasCooldown(player)) {
            player.sendMessage(this.getLanguageConfig().getString("KIT_COMMAND.ON_COOLDOWN").replaceAll("%seconds%", kit.getCooldown().getRemaining(player)));
            return;
        }
        kit.equip(player);
        player.sendMessage(this.getLanguageConfig().getString("CUSTOM_SIGNS.KIT_SIGNS.EQUIPPED").replaceAll("%kit%", kit.getName()));
    }
}
