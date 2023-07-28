package dev.razorni.hcfactions.extras.shop.command;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OpenShopCommand extends Command {

    public OpenShopCommand(CommandManager manager) {
        super(manager, "openshop");
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        if (args.length == 0) {
            Player player = (Player) sender;
            /* inventory */
            int size = HCF.getPlugin().getConfig().getInt("SHOP.MAIN-MENU.SIZE");
            Inventory inv = Bukkit.createInventory(null, size, ChatColor.translateAlternateColorCodes('&', HCF.getPlugin().getConfig().getString("SHOP.MAIN-MENU.INVENTORY-NAME")));
            Material glassmaterial = Material.valueOf(HCF.getPlugin().getConfig().getString("SHOP.MAIN-MENU.MATERIAL"));
            int glassdata = HCF.getPlugin().getConfig().getInt("SHOP.MAIN-MENU.DATA");
            ItemStack glasspane = new ItemStack(glassmaterial, 1, (short) glassdata);
            ItemMeta glassmeta = glasspane.getItemMeta();
            glassmeta.setDisplayName(" ");
            glassmeta.addEnchant(Enchantment.DURABILITY, 1, true);
            glasspane.setItemMeta(glassmeta);
            if (HCF.getPlugin().getConfig().getBoolean("SHOP.MAIN-MENU.FILL-CORNERS")) {
                inv.setItem(0, glasspane);
                inv.setItem(1, glasspane);
                inv.setItem(7, glasspane);
                inv.setItem(8, glasspane);
                inv.setItem(9, glasspane);
                inv.setItem(17, glasspane);
                inv.setItem(27, glasspane);
                inv.setItem(35, glasspane);
                inv.setItem(36, glasspane);
                inv.setItem(37, glasspane);
                inv.setItem(43, glasspane);
                inv.setItem(44, glasspane);
            }
            /* inventory end */
            Material buymaterial = Material.valueOf(HCF.getPlugin().getConfig().getString("SHOP.ITEMS.BUY-SHOP.MATERIAL"));
            int buyamount = HCF.getPlugin().getConfig().getInt("SHOP.ITEMS.BUY-SHOP.AMOUNT");
            int buydata = HCF.getPlugin().getConfig().getInt("SHOP.ITEMS.BUY-SHOP.DATA");
            ItemStack buyshop = new ItemStack(buymaterial, buyamount, (short) buydata);
            ItemMeta buymeta = buyshop.getItemMeta();
            List<String> buylore = new ArrayList<String>();
            buymeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', HCF.getPlugin().getConfig().getString("SHOP.ITEMS.BUY-SHOP.NAME")));
            for (String lore : HCF.getPlugin().getConfig().getStringList("SHOP.ITEMS.BUY-SHOP.LORE")) {
                buylore.add(ChatColor.translateAlternateColorCodes('&', lore));
            }
            buymeta.setLore(buylore);
            buyshop.setItemMeta(buymeta);
            if (HCF.getPlugin().getConfig().getBoolean("SHOP.ITEMS.BUY-SHOP.ENABLED")) {
                inv.setItem(HCF.getPlugin().getConfig().getInt("SHOP.ITEMS.BUY-SHOP.SLOT"), buyshop);
            }
            Material sellmaterial = Material.valueOf(HCF.getPlugin().getConfig().getString("SHOP.ITEMS.SELL-SHOP.MATERIAL"));
            int sellamount = HCF.getPlugin().getConfig().getInt("SHOP.ITEMS.SELL-SHOP.AMOUNT");
            int selldata = HCF.getPlugin().getConfig().getInt("SHOP.ITEMS.SELL-SHOP.DATA");
            ItemStack sellshop = new ItemStack(sellmaterial, sellamount, (short) selldata);
            ItemMeta sellmeta = sellshop.getItemMeta();
            List<String> selllore = new ArrayList<String>();
            sellmeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', HCF.getPlugin().getConfig().getString("SHOP.ITEMS.SELL-SHOP.NAME")));
            for (String selllore2 : HCF.getPlugin().getConfig().getStringList("SHOP.ITEMS.SELL-SHOP.LORE")) {
                selllore.add(ChatColor.translateAlternateColorCodes('&', selllore2));
            }
            sellmeta.setLore(selllore);
            sellshop.setItemMeta(sellmeta);
            if (HCF.getPlugin().getConfig().getBoolean("SHOP.ITEMS.SELL-SHOP.ENABLED")) {
                inv.setItem(HCF.getPlugin().getConfig().getInt("SHOP.ITEMS.SELL-SHOP.SLOT"), sellshop);
            }
            Material spawnermaterial = Material.valueOf(HCF.getPlugin().getConfig().getString("SHOP.ITEMS.SPAWNER-SHOP.MATERIAL"));
            int spawneramount = HCF.getPlugin().getConfig().getInt("SHOP.ITEMS.SPAWNER-SHOP.AMOUNT");
            int spawnerdata = HCF.getPlugin().getConfig().getInt("SHOP.ITEMS.SPAWNER-SHOP.DATA");
            ItemStack spawnershop = new ItemStack(spawnermaterial, spawneramount, (short) spawnerdata);
            ItemMeta spawnermeta = spawnershop.getItemMeta();
            List<String> spawnerlore = new ArrayList<String>();
            spawnermeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', HCF.getPlugin().getConfig().getString("SHOP.ITEMS.SPAWNER-SHOP.NAME")));
            for (String spawnerlore2 : HCF.getPlugin().getConfig().getStringList("SHOP.ITEMS.SPAWNER-SHOP.LORE")) {
                spawnerlore.add(ChatColor.translateAlternateColorCodes('&', spawnerlore2));
            }
            spawnermeta.setLore(spawnerlore);
            spawnershop.setItemMeta(spawnermeta);
            if (HCF.getPlugin().getConfig().getBoolean("SHOP.ITEMS.SPAWNER-SHOP.ENABLED")) {
                inv.setItem(HCF.getPlugin().getConfig().getInt("SHOP.ITEMS.SPAWNER-SHOP.SLOT"), spawnershop);
            }
            player.openInventory(inv);
        }
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }
}
