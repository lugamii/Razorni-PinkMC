package dev.razorni.hub.versions;

import org.bukkit.Location;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Version {

    void hideArmor(Player player);

    boolean isNotGapple(ItemStack stack);

    void playEffect(Location location, String p1, Object p2);

    CommandMap getCommandMap();

    ItemStack getItemInHand(Player player);

    String getTPSColored();

    void showArmor(Player player);

    int getPing(Player player);

    void setItemInHand(Player player, ItemStack stack);
}