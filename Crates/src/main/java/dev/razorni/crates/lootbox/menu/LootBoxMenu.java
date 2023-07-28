package dev.razorni.crates.lootbox.menu;

import dev.razorni.crates.Crates;
import dev.razorni.crates.lootbox.LootBox;
import dev.razorni.crates.lootbox.LootBoxItem;
import dev.razorni.crates.lootbox.profile.LootBoxProfile;
import cc.invictusgames.ilib.builder.ItemBuilder;
import cc.invictusgames.ilib.menu.Button;
import cc.invictusgames.ilib.menu.Menu;
import cc.invictusgames.ilib.menu.fill.FillTemplate;
import cc.invictusgames.ilib.placeholder.PlaceholderService;
import cc.invictusgames.ilib.utils.ItemNbtUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class LootBoxMenu extends Menu {

    private static final List<String> CHEST_LORE =
            Stream.of("", "&7&oClick to open.", "")
                    .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                    .collect(Collectors.toList());


    private static final List<String> UNABLE_CHEST_LORE =
            Stream.of("", "&cOpen other chests first.", "")
                    .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                    .collect(Collectors.toList());

    private static final String CHEST_NAME = ChatColor.YELLOW + "???";
    private static final int[] CHEST_SPOTS = new int[]{
            12, 13, 14,
            21, 22, 23,
            30, 31, 32};

    private final Map<Integer, ItemStack> openedChests = new HashMap<>();

    private final LootBox lootBox;
    private final Player player;
    private final Crates plugin;

    private boolean doneOpening = false;

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    public String getTitle(Player player) {
        return lootBox.getDisplayName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();

        Arrays.stream(CHEST_SPOTS).forEach(value ->
                buttonMap.put(value, new LootBoxChestButton(value)));

        buttonMap.put(40, new LootBoxFinalChestButton(40));

        return buttonMap;
    }

    @Override
    public void onClose(Player player) {
        if (!doneOpening) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> openMenu(player), 1L);
            return;
        }

        LootBoxProfile lootBoxProfile = LootBoxProfile.getLootBoxProfile(player.getUniqueId());
        lootBoxProfile.setOpening(false);
    }

    @Override
    public FillTemplate getFillTemplate() {
        return FillTemplate.FILL;
    }

    @Override
    public boolean isClickUpdate() {
        return true;
    }

    @Override
    public ItemStack getPlaceholderItem(Player player) {
        return Button.createPlaceholder(Material.STAINED_GLASS_PANE,
                        (short) ThreadLocalRandom.current().nextInt(15))
                .getItem(player);
    }

    @RequiredArgsConstructor
    private class LootBoxChestButton extends Button {

        private final int slot;

        @Override
        public ItemStack getItem(Player player) {
            if (openedChests.containsKey(slot)) {
                ItemStack itemStack = openedChests.get(slot);
                if (itemStack != null)
                    return itemStack;
            }

            return new ItemBuilder(Material.CHEST)
                    .setDisplayName(CHEST_NAME)
                    .setLore(CHEST_LORE)
                    .build();
        }

        @Override
        public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
            if (openedChests.containsKey(slot)) {
                ItemStack itemStack = openedChests.get(slot);
                if (itemStack != null)
                    return;
            }

            LootBoxItem reward = lootBox.getRandomReward();
            if (reward == null) {
                player.sendMessage(ChatColor.RED + "Could not get random reward.");
                openedChests.put(slot, new ItemStack(Material.AIR));
                return;
            }

            ItemStack clone = reward.getItemStack().clone();
            ItemNbtUtil.remove(clone, "uuid");
            openedChests.put(slot, clone);

            if (!reward.getCommands().isEmpty())
                reward.getCommands().forEach(command -> Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        PlaceholderService.replace(player, command)
                ));
            else player.getInventory().addItem(clone).values()
                    .forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
            player.playSound(player.getLocation(), Sound.HORSE_ARMOR, 1.0F, 1.25F);
        }
    }

    @RequiredArgsConstructor
    private class LootBoxFinalChestButton extends Button {

        private final int slot;

        @Override
        public ItemStack getItem(Player player) {
            if (openedChests.containsKey(slot)) {
                ItemStack itemStack = openedChests.get(slot);
                if (itemStack != null)
                    return itemStack;
            }

            return new ItemBuilder(Material.ENDER_CHEST)
                    .setDisplayName(ChatColor.RED + "???")
                    .setLore(openedChests.size() < 9 ? UNABLE_CHEST_LORE : CHEST_LORE).build();
        }

        @Override
        public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
            if (openedChests.containsKey(slot)) {
                ItemStack itemStack = openedChests.get(slot);
                if (itemStack != null)
                    return;
            }

            if (openedChests.size() < 9) {
                player.sendMessage(ChatColor.RED + "You have to open the other chests first.");
                return;
            }

            LootBoxItem reward = lootBox.getRandomFinalReward();
            if (reward == null) {
                player.sendMessage(ChatColor.RED + "Could not get random final reward.");
                openedChests.put(slot, new ItemStack(Material.AIR));
                return;
            }

            ItemStack clone = reward.getItemStack().clone();
            ItemNbtUtil.remove(clone, "uuid");
            openedChests.put(slot, clone);

            if (!reward.getCommands().isEmpty())
                reward.getCommands().forEach(command -> Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        PlaceholderService.replace(player, command)
                ));
            else player.getInventory().addItem(clone).values()
                    .forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
            player.playSound(player.getLocation(), Sound.HORSE_ARMOR, 1.0F, 1.25F);
            Bukkit.getScheduler().runTaskLater(plugin, player::closeInventory, 60L);
            doneOpening = true;
        }
    }

}
