package dev.razorni.core.profile.staffmode.menu.button;

import dev.razorni.core.Core;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.menu.Button;
import dev.razorni.hcfactions.HCF;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StaffButton extends Button {

    private final UUID wrapped;

    public StaffButton(UUID wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Player resolved = Core.getInstance().getServer().getPlayer(this.wrapped);
        player.teleport(resolved);
        player.sendMessage(CC.translate("&fYou have been teleported to &6" + resolved.getName()));
        player.closeInventory();
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        Player resolved = Core.getInstance().getServer().getPlayer(this.wrapped);
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(Profile.getByUuid(resolved.getUniqueId()).getColoredUsername());
        List<String> lore = new ArrayList<>();
        String staffchat = "";
        if (Profile.getByUuid(resolved.getUniqueId()).getStaffOptions().isStaffChat()) {
            staffchat = CC.translate("&cStaff");
        } else {
            staffchat = CC.translate("&aPublic");
        }
        String staffmod = "";
        String vanished = "";
        if (HCF.getPlugin().getStaffManager().isVanished(resolved)) {
            vanished = CC.translate(" &7(Vanished)");
        } else {
            vanished = CC.translate("");
        }
        if (HCF.getPlugin().getStaffManager().isStaffEnabled(resolved)) {
            staffmod = CC.translate("&a✔") + vanished;
        } else {
            staffmod = CC.translate("&c✘") + vanished;
        }
        lore.add(CC.translate(" "));
        lore.add(CC.translate("&dRank: " + Core.getInstance().getCoreAPI().getRankColor(resolved) + Core.getInstance().getCoreAPI().getRankName(resolved)));
        lore.add(CC.translate("&dChat: " + staffchat));
        lore.add(CC.translate("&dMod Mode: " + staffmod));
        lore.add(CC.translate(" "));
        lore.add(CC.translate("&aClick to teleport to the " + resolved.getName()));
        skull.setLore(lore);
        skull.setOwner(resolved.getName());
        item.setItemMeta(skull);
        return item;
    }

}
