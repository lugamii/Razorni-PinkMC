package dev.razorni.crates.lootbox;

import cc.invictusgames.ilib.configuration.StaticConfiguration;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Data
public class LootBoxItem implements StaticConfiguration {

    private transient UUID uuid = UUID.randomUUID();
    private ItemStack itemStack;
    private double percentage = -1;
    private List<String> commands = new ArrayList<>();

    public LootBoxItem(ItemStack itemStack, double percentage) {
        this.itemStack = itemStack;
        this.percentage = percentage;
    }
}
