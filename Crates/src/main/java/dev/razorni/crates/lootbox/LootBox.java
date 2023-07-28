package dev.razorni.crates.lootbox;

import dev.razorni.crates.Crates;
import cc.invictusgames.ilib.builder.ItemBuilder;
import cc.invictusgames.ilib.configuration.StaticConfiguration;
import cc.invictusgames.ilib.utils.CC;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Data
@NoArgsConstructor
public class LootBox implements StaticConfiguration {

    private UUID uuid;
    private String name;
    private String displayName;

    private final List<LootBoxItem> items = new ArrayList<>();
    private final List<LootBoxItem> finalItems = new ArrayList<>();

    public LootBox(String name) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.displayName = ChatColor.GREEN + name;
    }

    public ItemStack getLootBox(int amount) {
        List<String> lore = Arrays.asList(
                "&7Right click in the air to open this lootbox."
        );

        return new ItemBuilder(Material.ENDER_CHEST)
                .setDisplayName(displayName + ChatColor.WHITE + " Lootbox")
                .setLore(CC.translate(lore))
                .setAmount(amount)
                .build();
    }

    public LootBoxItem getItem(UUID uuid) {
        return items.stream()
                .filter(lootBoxItem -> lootBoxItem.getUuid().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    public LootBoxItem getRandomReward() {
        double max = 0.0;
        for (LootBoxItem reward : items) {
            if (reward.getPercentage() > max)
                max = reward.getPercentage();
        }

        String chanceNumberString = Crates.REWARD_FORMAT
                .format(0.0 + max * ThreadLocalRandom.current().nextDouble());
        double chanceNumber = Double.parseDouble(chanceNumberString);

        List<LootBoxItem> winnablePrizes = new ArrayList<>();
        items.forEach(prize2 -> {
            double chance = prize2.getPercentage();

            if (chanceNumber <= chance)
                winnablePrizes.add(prize2);
        });

        LootBoxItem reward;
        if (winnablePrizes.size() > 1) {
            int prizeToPick = ThreadLocalRandom.current().nextInt(winnablePrizes.size());
            reward = winnablePrizes.get(prizeToPick);
        } else reward = winnablePrizes.get(0);

        return reward;
    }

    public LootBoxItem getRandomFinalReward() {
        double max = 0.0;
        for (LootBoxItem reward : finalItems) {
            if (reward.getPercentage() > max)
                max = reward.getPercentage();
        }

        String chanceNumberString = Crates.REWARD_FORMAT
                .format(0.0 + max * ThreadLocalRandom.current().nextDouble());
        double chanceNumber = Double.parseDouble(chanceNumberString);

        List<LootBoxItem> winnablePrizes = new ArrayList<>();
        finalItems.forEach(prize2 -> {
            double chance = prize2.getPercentage();

            if (chanceNumber <= chance)
                winnablePrizes.add(prize2);
        });

        LootBoxItem reward;
        if (winnablePrizes.size() > 1) {
            int prizeToPick = ThreadLocalRandom.current().nextInt(winnablePrizes.size());
            reward = winnablePrizes.get(prizeToPick);
        } else reward = winnablePrizes.get(0);

        return reward;
    }

}
