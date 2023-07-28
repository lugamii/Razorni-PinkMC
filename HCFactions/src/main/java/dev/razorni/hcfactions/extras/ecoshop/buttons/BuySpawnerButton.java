package dev.razorni.hcfactions.extras.ecoshop.buttons;

import com.google.common.collect.Lists;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.ecoshop.menu.CategorySelectorMenu;
import dev.razorni.hcfactions.extras.ecoshop.menu.ItemsMenu;
import dev.razorni.hcfactions.extras.ecoshop.menu.PotionMenu;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.utils.menuapi.ItemBuilder;
import dev.razorni.hcfactions.utils.menuapi.menu.Button;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@RequiredArgsConstructor
public class BuySpawnerButton extends Button {

    private final String name;
    private final int cost;
    private final int amount;
    private final ItemStack itemStack;
    private String command;

    public BuySpawnerButton(String name, int cost,int amount, ItemStack itemStack, String command) {
        this.name = name;
        this.cost = cost;
        this.amount = amount;
        this.itemStack = itemStack;
        this.command = command;
    }

    public String getName(Player player) {
        return ChatColor.LIGHT_PURPLE + name;
    }

    public List<String> getDescription(Player player) {
        return Lists.newArrayList(
                "&fPrice: &d$" + cost
        );
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(itemStack.clone()).name(getName(player))
                .lore(getDescription(player)).amount(amount).build();
    }

    public byte getDamageValue(Player player) {
        return (byte) itemStack.getDurability();
    }

    public Material getMaterial(Player player) {
        return itemStack.getType();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatColor.RED + "You don't have enough space in your inventory!");
            Button.playFail(player);
            return;
        }

        User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());

        if (profile.getBalance() < cost) {
            player.sendMessage(ChatColor.RED + "You don't have enough money!");
            Button.playFail(player);
            return;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

        profile.setBalance(profile.getBalance() - cost);
        profile.save();


        player.sendMessage(ChatColor.GREEN + "You have bought " + amount + " " + name + " for $"
                + cost);
        if (player.getOpenInventory().getTitle().contains("Spawners")) {
            new CategorySelectorMenu().openMenu(player);
            new PotionMenu().openMenu(player);
        } else {
            new CategorySelectorMenu().openMenu(player);
            new ItemsMenu().openMenu(player);
        }

        Button.playSuccess(player);
    }

}
