package dev.razorni.crates.lootbox.menu.editor;

import dev.razorni.crates.lootbox.LootBox;
import cc.invictusgames.ilib.builder.ItemBuilder;
import cc.invictusgames.ilib.chatinput.ChatInput;
import cc.invictusgames.ilib.menu.Button;
import cc.invictusgames.ilib.menu.Menu;
import cc.invictusgames.ilib.utils.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class LootBoxMetaDataMenu extends Menu {

    private final LootBox lootBox;

    @Override
    public String getTitle(Player player) {
        return "Editing " + lootBox.getDisplayName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();

        lootBox.getItems().forEach(lootBoxItem -> buttonMap.put(buttonMap.size(), new Button() {
            @Override
            public ItemStack getItem(Player player) {
                ItemBuilder itemBuilder = new ItemBuilder(lootBoxItem.getItemStack().clone());
                itemBuilder.addToLore(CC.translate("&6Percentage: &f"
                        + lootBoxItem.getPercentage()));

                if (!lootBoxItem.getCommands().isEmpty()) {
                    itemBuilder.addToLore(" ");

                    lootBoxItem.getCommands().forEach(s ->
                            itemBuilder.addToLore(ChatColor.WHITE + " - " + s));
                }

                itemBuilder.addToLore(" ");
                itemBuilder.addToLore(ChatColor.GRAY + ChatColor.ITALIC.toString()
                        + "Right Click to edit commands.");
                itemBuilder.addToLore(ChatColor.GRAY + ChatColor.ITALIC.toString()
                        + "Left Click to edit percentages.");
                return itemBuilder.build();
            }

            @Override
            public void click(Player whoClicked, int slot, ClickType clickType, int hotbarButton) {
                if (clickType.isLeftClick()) {
                    whoClicked.closeInventory();
                    new ChatInput<Double>(Double.class)
                            .text(ChatColor.GREEN + "Please enter a percentage.")
                            .accept((player, percentage) -> {
                                lootBoxItem.setPercentage(percentage);
                                player.sendMessage(ChatColor.GREEN + "Successfully updated percentages.");
                                new LootBoxMetaDataMenu(lootBox).openMenu(player);
                                return true;
                            }).send(whoClicked);
                    return;
                }

                if (clickType.isRightClick()) {
                    whoClicked.sendMessage(ChatColor.GREEN + "Please enter a command.");
                    whoClicked.closeInventory();
                    new ChatInput<String>(String.class)
                            .text(ChatColor.GREEN + "Please enter a command.")
                            .accept((player, command) -> {
                                lootBoxItem.getCommands().add(command);
                                player.sendMessage(ChatColor.GREEN + "Successfully updated commands.");
                                return true;
                            }).send(whoClicked);
                }
            }
        }));

        return buttonMap;
    }
}
