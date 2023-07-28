package dev.razorni.hcfactions.staff;

import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Staff {
    private final List<PotionEffect> effects;
    private final ItemStack[] armorContents;
    private final GameMode gameMode;
    private final ItemStack[] contents;

    public Staff(PlayerInventory inventory, GameMode gameMode) {
        this.contents = inventory.getContents();
        this.armorContents = inventory.getArmorContents();
        this.gameMode = gameMode;
        this.effects = new ArrayList<>();
    }
}