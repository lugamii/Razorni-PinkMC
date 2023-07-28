package dev.razorni.gkits.customenchant.command;

import dev.razorni.gkits.GKits;
import dev.razorni.gkits.customenchant.CustomEnchant;
import dev.razorni.gkits.customenchant.menu.ShopMenu;
import cc.invictusgames.ilib.command.annotation.Command;
import cc.invictusgames.ilib.command.annotation.Param;
import cc.invictusgames.ilib.utils.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class CustomEnchantCommands {

    private final GKits arsenic;

    @Command(names =  {"customenchant", "ce"},
             description = "Open the custom enchant shop",
             playerOnly = true)
    public boolean customenchant(Player sender) {
        return shop(sender);
    }

    @Command(names = {"customenchant give"},
             permission = "arsenic.command.customenchant.give",
             description = "Give a custom enchant book to a player")
    public boolean giveBook(CommandSender commandSender,
                            @Param(name = "target") Player player,
                            @Param(name = "enchant") CustomEnchant customEnchant) {
        player.getInventory().addItem(customEnchant.getBook());
        return true;
    }

    @Command(names = {"customenchant shop"},
             description = "Open the custom enchant shop",
             playerOnly = true)
    public boolean shop(Player player) {
        new ShopMenu(arsenic).openMenu(player);
        return true;
    }

    @Command(names = {"customenchant setprice"},
             permission = "arsenic.command.customenchant.setprice",
             description = "Set the price of an custom enchant",
             async = true)
    public boolean setPrice(CommandSender sender,
                            @Param(name = "enchant") CustomEnchant enchant,
                            @Param(name = "price") int price) {
        if (price < 0) {
            sender.sendMessage(ChatColor.RED + "Price cannot be negative.");
            return false;
        }

        String name = enchant.getName().replace(" ", "");
        arsenic.getGKitConfig().getEnchantPrices().put(name.toLowerCase(), price);
        arsenic.getGKitConfig().saveConfig();
        sender.sendMessage(CC.format("&aSet price of %s &ato &e$%d&a.",
                enchant.getDisplayName(), price));
        return true;
    }

}
