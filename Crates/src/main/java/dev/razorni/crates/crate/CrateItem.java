package dev.razorni.crates.crate;

import cc.invictusgames.ilib.configuration.StaticConfiguration;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Data
public class CrateItem implements StaticConfiguration {

    private transient UUID uuid = UUID.randomUUID();
    private ItemStack itemStack;
    private double percentage = -1;
    private double fakePercentage = -1;
    private List<String> commands = new ArrayList<>();
    private int slot;

    public CrateItem(ItemStack itemStack, double percentage, int slot) {
        this.itemStack = itemStack;
        this.percentage = percentage;
        this.slot = slot;
    }
}
