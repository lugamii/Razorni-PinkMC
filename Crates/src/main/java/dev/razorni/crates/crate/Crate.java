package dev.razorni.crates.crate;

import dev.razorni.crates.Crates;
import dev.razorni.crates.crate.effect.CrateEffect;
import cc.invictusgames.ilib.builder.ItemBuilder;
import cc.invictusgames.ilib.configuration.StaticConfiguration;
import cc.invictusgames.ilib.hologram.HologramBuilder;
import cc.invictusgames.ilib.hologram.HologramService;
import cc.invictusgames.ilib.hologram.statics.StaticHologram;
import cc.invictusgames.ilib.utils.CC;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Data
@NoArgsConstructor
public class Crate implements StaticConfiguration {

    private UUID uuid;
    private String name;
    private String displayName;
    private Location location;
    private boolean broadcast;
    private MaterialData keyItem;
    private int rewardAmount = 5;
    private boolean showPercentage = true;

    private final List<CrateItem> items = new ArrayList<>();

    private boolean hologramEnabled = true;
    private List<String> hologramLines;

    private transient StaticHologram hologram;
    private CrateEffect effect;

    private Material locationItem = Material.CHEST;

    public Crate(String name) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.displayName = ChatColor.GREEN + name;
        this.hologramLines = Arrays.asList(
                displayName + ChatColor.GRAY + " Crate",
                ChatColor.GRAY + ChatColor.ITALIC.toString() + "store.hcfactions.net"
        );
        this.effect = CrateEffect.LAVA_RINGS;
        this.keyItem = Material.TRIPWIRE_HOOK.getNewData((byte) 0);
    }

    public CrateItem getItem(UUID uuid) {
        for (CrateItem item : items) {
            if (item.getUuid().equals(uuid))
                return item;
        }
        return null;
    }

    public ItemStack getKey() {
        if (keyItem.getItemType() == null)
            return null;

        return new ItemBuilder(keyItem.getItemType(), keyItem.getData())
                .setDisplayName(displayName + ChatColor.GRAY + " Key")
                .setLore(ChatColor.GRAY + "Open this key at crates zone right clicking " + displayName + ChatColor.GRAY + " chest.")
                .build();
    }

    public void updateHologram() {
        if (!hologramEnabled)
            return;

        if (hologram == null) {
            hologram = new HologramBuilder().at(location.clone().add(0.5, 0.5, 0.5))
                    .staticHologram()
                    .addLines(hologramLines)
                    .build();
            hologram.spawn();
            return;
        }

          hologram.setLines(CC.translate(hologramLines));
    }

    public ItemStack buildCrate() {
        ItemBuilder itemBuilder = new ItemBuilder(locationItem);
        itemBuilder.setDisplayName(displayName + CC.YELLOW + " location");

        itemBuilder.setLore(Collections.singletonList(CC.GRAY + "Place this to set the crate location."));

        return itemBuilder.build();
    }

    public void deleteHologram() {
        if (hologram != null) {
            hologram.destroy();
            HologramService.unregisterHologram(hologram.getId());
            hologram = null;
        }
    }

    public CrateItem getRandomReward() {
        double max = 0.0;
        for (CrateItem reward : items) {
            if (reward.getPercentage() > max)
                max = reward.getPercentage();
        }

        String chanceNumberString = Crates.REWARD_FORMAT.format(0.0 + max *
                ThreadLocalRandom.current().nextDouble());
        double chanceNumber = Double.parseDouble(chanceNumberString);

        List<CrateItem> winnablePrizes = new ArrayList<>();
        items.forEach(prize2 -> {
            double chance = prize2.getPercentage();

            if (chanceNumber <= chance)
                winnablePrizes.add(prize2);
        });

        CrateItem reward;
        if (winnablePrizes.size() > 1) {
            int prizeToPick = ThreadLocalRandom.current().nextInt(winnablePrizes.size());
            reward = winnablePrizes.get(prizeToPick);
        } else reward = winnablePrizes.get(0);

        return reward;
    }
}
