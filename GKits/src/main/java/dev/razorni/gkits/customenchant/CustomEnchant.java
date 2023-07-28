package dev.razorni.gkits.customenchant;

import cc.invictusgames.ilib.builder.ItemBuilder;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.utils.callback.TypeCallable;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomEnchant {

    public static ChatColor COLOR = ChatColor.AQUA;

    public abstract String getName();

    public abstract String getDisplayName();

    public abstract void apply(Player player);

    public abstract void remove(Player player);

    public abstract String[] appliesTo();

    public abstract String getDescription();

    public String getStrippedName() {
        return ChatColor.stripColor(getName());
    }

    public boolean canApply(Player player, ItemStack itemStack, TypeCallable<String> stringTypeCallable) {
        String[] collect = appliesTo();

        if (itemStack == null ||
                itemStack.getType() == Material.AIR)
            return false;

        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()
                && itemStack.getItemMeta().getLore().contains(getDisplayName())) {
            stringTypeCallable.callback(ChatColor.RED + "This item already has this enchant.");
            return false;
        }

        for (String s : collect) {
            if (itemStack.getType().name()
                    .contains(s.toUpperCase()))
                return true;
        }

        stringTypeCallable.callback(ChatColor.RED + "You can't apply this enchant to this item.");
        return false;
    }

    public ItemStack getBook() {
        ItemBuilder itemBuilder = new ItemBuilder(Material.BOOK);
        itemBuilder.setDisplayName(getDisplayName());

        List<String> prettyAppliesTo = new ArrayList<>();
        for (String s : appliesTo())
            prettyAppliesTo.add(WordUtils.capitalizeFully(s));

        List<String> lore = new ArrayList<>();
        lore.add(CC.MENU_BAR);
        lore.add(CC.translate("&dWorks on &6»&f " + StringUtils.join(prettyAppliesTo, ", ")));
        lore.add(" ");
        lore.add(getDescription());
        lore.add(CC.MENU_BAR);

        itemBuilder.setLore(lore);
        return itemBuilder.build();
    }

    public String getBookClone() {
        List<String> prettyAppliesTo = new ArrayList<>();
        for (String s : appliesTo())
            prettyAppliesTo.add(WordUtils.capitalizeFully(s));
        return StringUtils.join(prettyAppliesTo, ", ");
    }

    public ItemStack addToLore(ItemStack itemStack) {
        ItemBuilder itemBuilder = new ItemBuilder(itemStack);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (!itemMeta.hasLore())
            itemMeta.setLore(new ArrayList<>());

        List<String> lore = itemMeta.getLore();
        lore.add(getDisplayName());
        itemBuilder.setLore(lore);

        return itemBuilder.build();
    }

}
