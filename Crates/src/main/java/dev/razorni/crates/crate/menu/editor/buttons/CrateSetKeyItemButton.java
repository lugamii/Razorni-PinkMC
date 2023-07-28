package dev.razorni.crates.crate.menu.editor.buttons;

import dev.razorni.crates.Crates;
import dev.razorni.crates.crate.Crate;
import cc.invictusgames.ilib.builder.ItemBuilder;
import cc.invictusgames.ilib.chatinput.ChatInput;
import cc.invictusgames.ilib.menu.Button;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.utils.ItemUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@RequiredArgsConstructor
public class CrateSetKeyItemButton extends Button {

    private final Crate crate;

    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder(crate.getKeyItem().getItemType(), (byte) crate.getKeyItem().getData())
                .setDisplayName(ChatColor.YELLOW + "Key Item")
                .setLore(Arrays.asList(
                        CC.MENU_BAR,
                        ChatColor.WHITE + "Click to change the key item of the crate.",
                        CC.MENU_BAR)
                ).build();
    }

    @Override
    public void click(Player whoClicked, int slot, ClickType clickType, int hotbarButton) {
        whoClicked.closeInventory();

        new ChatInput<String>(String.class)
                .text(ChatColor.GREEN + "Insert a new key item.")
                .accept((player, input) -> {
                    ItemStack itemStack = ItemUtils.get(input);

                    if (input.equalsIgnoreCase("hand")) {
                        ItemStack itemInHand = player.getItemInHand().clone();

                        if (itemInHand == null) {
                            player.sendMessage(CC.RED + "You do not have an item in your hand.");
                            return false;
                        }

                        itemStack = itemInHand;
                    }

                    if (itemStack == null) {
                        player.sendMessage(CC.RED + "This item does not exist in the database.");
                        return false;
                    }

                    itemStack.setAmount(1);
                    crate.setKeyItem(itemStack.getData());
                    Crates.get().getCrateManager().saveCrate(crate, true);

                    player.sendMessage(ChatColor.GREEN + "You set the key item to "
                            + ItemUtils.getName(itemStack) + ChatColor.GREEN + ".");
                    return true;
                }).send(whoClicked);
    }
}
