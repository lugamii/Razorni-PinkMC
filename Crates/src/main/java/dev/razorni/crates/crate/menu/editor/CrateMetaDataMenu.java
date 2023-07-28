package dev.razorni.crates.crate.menu.editor;

import dev.razorni.crates.crate.Crate;
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
public class CrateMetaDataMenu extends Menu {

    private final Crate crate;

    @Override
    public String getTitle(Player player) {
        return "Editing " + crate.getDisplayName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();

        crate.getItems().forEach(crateItem -> buttonMap.put(crateItem.getSlot(), new Button() {
            @Override
            public ItemStack getItem(Player player) {
                ItemBuilder itemBuilder = new ItemBuilder(crateItem.getItemStack().clone());
                itemBuilder.addToLore(CC.translate("&6Percentage: &f"
                        + crateItem.getPercentage()));
                if (crateItem.getFakePercentage() > 0)
                    itemBuilder.addToLore(CC.translate("&6Fake Percentage: &f"
                            + crateItem.getFakePercentage()));

                if (!crateItem.getCommands().isEmpty()) {
                    itemBuilder.addToLore(" ");

                    crateItem.getCommands().forEach(s ->
                            itemBuilder.addToLore(ChatColor.WHITE + " - " + s));
                }

                itemBuilder.addToLore(" ");
                itemBuilder.addToLore(ChatColor.GRAY + ChatColor.ITALIC.toString()
                        + "Right Click to edit commands.");
                itemBuilder.addToLore(ChatColor.GRAY + ChatColor.ITALIC.toString()
                        + "Left Click to edit percentages.");
                itemBuilder.addToLore(ChatColor.GRAY + ChatColor.ITALIC.toString()
                        + "Shift Click to edit fake percentages.");
                return itemBuilder.build();
            }

            @Override
            public void click(Player whoClicked, int slot, ClickType clickType, int hotbarButton) {

                if (clickType.isShiftClick()) {
                    whoClicked.closeInventory();
                    new ChatInput<Double>(Double.class)
                            .text(ChatColor.GREEN + "Please enter a fake percentage.")
                            .accept((player, percentage) -> {
                                crateItem.setFakePercentage(percentage);
                                player.sendMessage(ChatColor.GREEN + "Successfully updated fake percentages.");
                                new CrateMetaDataMenu(crate).openMenu(player);
                                return true;
                            }).send(whoClicked);
                    return;
                }

                if (clickType.isLeftClick()) {
                    whoClicked.closeInventory();
                    new ChatInput<Double>(Double.class)
                            .text(ChatColor.GREEN + "Please enter a percentage.")
                            .accept((player, percentage) -> {
                                crateItem.setPercentage(percentage);
                                player.sendMessage(ChatColor.GREEN + "Successfully updated percentages.");
                                new CrateMetaDataMenu(crate).openMenu(player);
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
                                crateItem.getCommands().add(command);
                                player.sendMessage(ChatColor.GREEN + "Successfully updated commands.");
                                return true;
                            }).send(whoClicked);
                }
            }
        }));

        return buttonMap;
    }
}
