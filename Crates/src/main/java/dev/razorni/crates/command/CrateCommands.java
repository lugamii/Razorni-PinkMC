package dev.razorni.crates.command;

import dev.razorni.crates.Crates;
import dev.razorni.crates.crate.Crate;
import dev.razorni.crates.crate.CrateManager;
import dev.razorni.crates.crate.menu.editor.CrateEditMenu;
import cc.invictusgames.ilib.command.annotation.Command;
import cc.invictusgames.ilib.command.annotation.Param;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class CrateCommands {

    private final Crates plugin;
    private final CrateManager crateManager;

    @Command(names = {"crate create", "cr create"},
            permission = "op",
            description = "Create a crate.")
    public boolean crateCreate(CommandSender commandSender, @Param(name = "name") String name) {
        if (crateManager.crateExists(name)) {
            commandSender.sendMessage(ChatColor.RED + "This create already exists.");
            return true;
        }

        Crate crate = crateManager.createCrate(name);
        commandSender.sendMessage(ChatColor.GREEN + "You have created a crate called "
                + ChatColor.BOLD + crate.getName() + ChatColor.GREEN + ".");
        return true;
    }

    @Command(names = "crate delete",
            permission = "op",
            description = "Delete a crate.")
    public boolean crateDelete(CommandSender commandSender, @Param(name = "name") Crate crate) {
        crateManager.deleteCrate(crate);
        //new LootBoxMenu().openMenu((Player) commandSender);
        commandSender.sendMessage(ChatColor.GREEN + "You have deleted the crate called "
                + ChatColor.BOLD + crate.getName() + ChatColor.GREEN + ".");
        return true;
    }

    @Command(names = "crate list",
            permission = "op",
            description = "List all available crates.")
    public boolean crateList(CommandSender commandSender) {
        List<String> names = new ArrayList<>();
        Map<UUID, Crate> crateMap = crateManager.getCrateMap();

        for (Crate crate : crateMap.values())
            names.add(crate.getName());

        commandSender.sendMessage(ChatColor.YELLOW + "Crates: " + ChatColor.RED + (crateMap.isEmpty() ? "None" :
                StringUtils.join(names, ", ")));
        return true;
    }

    @Command(names = "crate givekey",
            permission = "argon.command.crate.argument.givekey",
            description = "Give a player a key.")
    public boolean crateGivekey(CommandSender commandSender,
                                @Param(name = "player") Player player,
                                @Param(name = "name") Crate crate,
                                @Param(name = "amount", defaultValue = "1") int amount) {

        ItemStack key = crate.getKey().clone();
        key.setAmount(amount);
        player.getInventory().addItem(key).values().forEach(itemStack ->
                player.getWorld().dropItemNaturally(player.getLocation(), itemStack));
        IChatBaseComponent CT1 = ChatSerializer.a("{\"text\":\"" + "§d§lKEYS" + "\"}");
        IChatBaseComponent CS1 = ChatSerializer.a("{\"text\":\"" + "§fYou have received §dx" + amount + " " + crate.getDisplayName() + " §fkeys." + "\"}");
        PacketPlayOutTitle title1 = new PacketPlayOutTitle(EnumTitleAction.TITLE, CT1);
        PacketPlayOutTitle subtitle1 = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, CS1);
        PacketPlayOutTitle length1 = new PacketPlayOutTitle(10, 30, 10);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title1);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitle1);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length1);
        return true;
    }

    @Command(names = "crate giveallkey",
            permission = "argon.command.crate.argument.giveallkey",
            description = "Give all players a key.")
    public boolean crateGiveAllkey(CommandSender commandSender,
                                   @Param(name = "name") Crate crate,
                                   @Param(name = "amount", defaultValue = "1") int amount) {

        ItemStack key = crate.getKey().clone();
        key.setAmount(amount);
        Bukkit.getOnlinePlayers().forEach(player ->
                player.getInventory().addItem(key).values().forEach(itemStack ->
                        player.getWorld().dropItemNaturally(player.getLocation(), itemStack)));
        for (Player player : Bukkit.getOnlinePlayers()) {
            IChatBaseComponent CT1 = ChatSerializer.a("{\"text\":\"" + "§d§lKEYS" + "\"}");
            IChatBaseComponent CS1 = ChatSerializer.a("{\"text\":\"" + "§fYou have received §dx" + amount + " " + crate.getDisplayName() + " §fkeys." + "\"}");
            PacketPlayOutTitle title1 = new PacketPlayOutTitle(EnumTitleAction.TITLE, CT1);
            PacketPlayOutTitle subtitle1 = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, CS1);
            PacketPlayOutTitle length1 = new PacketPlayOutTitle(10, 30, 10);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title1);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitle1);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length1);
        }
        return true;
    }

    @Command(names = "crate edit",
            permission = "op",
            description = "Edit a crate.", playerOnly = true)
    public boolean crateEdit(Player player, @Param(name = "name") Crate crate) {
        new CrateEditMenu(crate, plugin).openMenu(player);

        player.sendMessage(ChatColor.GREEN + "You are now editing " + crate.getDisplayName()
                + ChatColor.GREEN + ".");
        return true;
    }

    @Command(names = "crate reload",
            permission = "op",
            description = "Reload all or one crate.", playerOnly = true)
    public boolean reloadCrate(Player player, @Param(name = "name", defaultValue = "all") String crateName) {
        if (crateName.equals("all")) {
            crateManager.getCrateMap().values().forEach(crate ->
                    plugin.getCrateManager().loadCrate(crate));
            return true;
        }

        crateManager.loadCrate(crateManager.getCrate(crateName));
        player.sendMessage(ChatColor.GREEN + "Crate(s) has been reloaded.");
        return true;
    }

    @Command(names = "crate setlocation",
            permission = "op",
            description = "Set a crate location", playerOnly = true)
    public boolean crateLocation(Player player, @Param(name = "name") Crate crate) {
        ItemStack itemStack = crate.buildCrate();
        player.getInventory().addItem(itemStack);
        return true;
    }

}
