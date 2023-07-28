package dev.razorni.core.extras.tag.menu.buttons;

import dev.razorni.core.profile.Profile;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import org.bukkit.inventory.ItemStack;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.menu.Button;

@AllArgsConstructor
public class ResetTagButton extends Button {


    public String getName(Player player) {
        return CC.translate("&cReset your tag");
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile == null) {
            player.closeInventory();
            return;
        }

        if (profile.getTag() == null) {
            player.sendMessage(CC.translate("&4✘ &cYour tag is already reset."));
            player.closeInventory();
            return;
        }

        player.closeInventory();
        profile.setTag(null);
        profile.save();

        player.sendMessage(CC.GREEN + "Your tag has been reset.");

    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.ANVIL).name(getName(player)).lore(CC.GRAY + "Click to reset your tag!").build();
    }

}

