package dev.razorni.gkits.gkit.command;

import dev.razorni.gkits.GKits;
import dev.razorni.gkits.gkit.GKit;
import dev.razorni.gkits.gkit.GKitCooldown;
import dev.razorni.gkits.gkit.GKitManager;
import dev.razorni.gkits.gkit.menu.impl.GKitMenu;
import dev.razorni.gkits.profile.Profile;
import cc.invictusgames.ilib.command.annotation.Command;
import cc.invictusgames.ilib.command.annotation.Param;
import cc.invictusgames.ilib.command.parameter.defaults.Duration;
import cc.invictusgames.ilib.menu.menu.TextEditMenu;
import cc.invictusgames.ilib.utils.CC;
import com.mongodb.Block;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;


@RequiredArgsConstructor
public class GKitCommands {

    private final GKits plugin;
    private final GKitManager gKitManager;

    @Command(names = {"gkit", "gkits", "kits", "kit"}, playerOnly = true)
    public void gkit(Player player) {
        if (!gKitManager.canUse(player))
            return;

        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        new GKitMenu(plugin, profile).openMenu(player);
    }

    @Command(names = {"gkm apply"}, permission = "arsenic.gkit.apply", description = "Apply a GKIT to a player.")
    public void applyGKit(CommandSender sender, @Param(name = "target") Player target, @Param(name = "gkit") String name) {
        if (!gKitManager.doesGKitExist(name)) {
            sender.sendMessage(CC.RED + "This gkit does not exist.");
            return;
        }

        GKit gkit = gKitManager.getGKit(name);
        gkit.apply(target);

        sender.sendMessage(gkit.getDisplayName() + CC.GREEN + " applied to " + target.getDisplayName() + CC.GREEN + ".");
        if (target.hasPermission("arsenic.gkit.apply.notify")) {
            target.sendMessage(sender.getName() + CC.GREEN + " applied " + gkit.getDisplayName() + CC.GREEN + " to you.");
        }
    }

    @Command(names = {"gkm create", "gkitmanager create"}, permission = "op", description = "Create a gkit.")
    public boolean createGKit(CommandSender commandSender, @Param(name = "name") String name) {
        if (gKitManager.doesGKitExist(name)) {
            commandSender.sendMessage(ChatColor.RED + "This gkit already exists.");
            return true;
        }

        GKit gkit = gKitManager.createGkit(name);
        commandSender.sendMessage(ChatColor.GREEN + "You have created a gkit called " + gkit.getName() + ".");
        return true;
    }

    @Command(names = "gkm menu", permission = "op", description = "Open the menu editor.", playerOnly = true)
    public boolean menuGkit(Player player) {

        Inventory inventory = Bukkit.createInventory(null, 54, "Gkit Menu");
        plugin.getGKitMenuConfig().getKitMenuItemList().forEach(gKitMenuItem ->
                inventory.setItem(gKitMenuItem.getSlot(), gKitMenuItem.getItemStack()));
        player.openInventory(inventory);

        gKitManager.getIsEditing().add(player.getUniqueId());
        return true;
    }

    @Command(names = "gkm setinventorysize", permission = "op", description = "Set the size of the gkit menu.")
    public boolean setInventorySizeGkit(CommandSender commandSender, @Param(name = "size") int size) {
        plugin.getGKitConfig().setInventorySize(size);
        plugin.getGKitConfig().saveConfig();
        commandSender.sendMessage(ChatColor.GREEN + "You have set the size to " + size + ".");
        return true;
    }

    @Command(names = "gkm delete", permission = "op", description = "Delete a gkit.")
    public boolean deleteGKit(CommandSender commandSender, @Param(name = "name") GKit gKit) {
        commandSender.sendMessage(ChatColor.GREEN + "You have deleted the gkit called " + gKit.getName() + ".");
        gKitManager.deleteGkit(gKit, true);
        return true;
    }

    @Command(names = "gkm seticon", permission = "op",
            description = "Set the icon for a gkit.",
            playerOnly = true)
    public boolean setIconGkit(Player player, @Param(name = "name") GKit gKit) {
        gKit.setIcon(player.getItemInHand());

        gKitManager.saveGKit(gKit, true);
        player.sendMessage(ChatColor.GREEN + "You have set the icon for the gkit called " + gKit.getName() + ".");
        return true;
    }

    @Command(names = "gkm setslot", permission = "op",
            description = "Set the slot for a gkit.",
            playerOnly = true)
    public boolean setSlotGkit(Player player, @Param(name = "name") GKit gKit,
                               @Param(name = "slot") int slot) {
        gKit.setSlot(slot);
        plugin.getGKitManager().saveGKit(gKit, true);

        player.sendMessage(ChatColor.GREEN + "You have set the slot " +
                "for the gkit called " + gKit.getName() + ".");
        return true;
    }


    @Command(names = "gkm setcooldown", permission = "op",
            description = "Set the cooldown for a gkit.",
            playerOnly = true)
    public boolean setCooldownGKit(Player player, @Param(name = "name") GKit gKit,
                                   @Param(name = "cooldown") Duration duration) {
        gKit.setCoolDown(duration.getDuration());

        player.sendMessage(ChatColor.GREEN + "You have set the cooldown " +
                "for the gkit called " + gKit.getName() + ".");
        return true;
    }

    @Command(names = "gkm description clear", permission = "op",
            description = "Clear the description for a gkit.",
            playerOnly = true)
    public boolean clearGkit(Player player, @Param(name = "name") GKit gKit) {
        gKit.clearDescription();
        player.sendMessage(ChatColor.GREEN + "You have cleared the description for the gkit called "
                + gKit.getName() + ".");
        return true;
    }

    @Command(names = "gkm description setline", permission = "op",
            description = "Set the description line for a gkit.",
            playerOnly = true)
    public boolean setLineGkit(Player player, @Param(name = "name") GKit gKit, @Param(name = "line") int line,
                               @Param(name = "line", wildcard = true) String string) {

        gKit.getDescription().set(line, string);
        player.sendMessage(ChatColor.GREEN + "You have set a line for the gkit " + gKit.getName() + ".");
        return true;
    }

    @Command(names = "gkm description addline", permission = "op",
            description = "Add a description line for a gkit.",
            playerOnly = true)
    public boolean addLineGkit(Player player, @Param(name = "name") GKit gKit,
                               @Param(name = "line", wildcard = true) String line) {

        gKit.getDescription().add(line);
        player.sendMessage(ChatColor.GREEN + "You have added a line to the gkit called " + gKit.getName() + ".");
        return true;
    }

    @Command(names = {"gkm description edit"},
             permission = "op",
             description = "Open the description editor for a gkit",
             playerOnly = true)
    public boolean descriptionEdit(Player sender, @Param(name = "name") GKit gKit) {
        new TextEditMenu(
                gKit.getDescription(),
                gKit::setDescription,
                "Editing description: " + gKit.getDisplayName()
        ).openMenu(sender);
        return true;
    }

    @Command(names = "gkm applyarmor", permission = "op",
            description = "Automatically apply armor of a gkit.",
            playerOnly = true)
    public boolean applyArmor(Player player, @Param(name = "name") GKit gKit) {
        boolean applyArmor = !gKit.isApplyArmor();

        gKit.setApplyArmor(applyArmor);

        player.sendMessage(gKit.getDisplayName() + CC.GREEN + " is "
                + (applyArmor ? "now" : "no longer") + " applying armor.");
        return true;
    }

    @Command(names = "gkm setdisplayname", permission = "op",
            description = "Set the displayname for a gkit.",
            playerOnly = true)
    public boolean setDisplayNameGkit(Player player, @Param(name = "name") GKit gKit,
                                      @Param(name = "displayname", wildcard = true) String displayName) {
        gKit.setDisplayName(displayName);

        player.sendMessage(ChatColor.GREEN + "You have set the displayname " +
                "for the gkit called " + gKit.getName() + ".");
        return true;
    }

    @Command(names = "gkm cooldown reset", permission = "op",
            description = "Reset a cooldown for a specific gkit for a specific player.",
            async = true)
    public boolean resetCooldownsGkit(CommandSender commandSender,
                                      @Param(name = "target") Player player,
                                      @Param(name = "gkit") GKit gkit) {

        plugin.getProfileManager().loadOrCreate(player.getUniqueId(), profile -> {
            GKitCooldown gKitCooldown = profile.getCooldownFor(gkit);
            gKitCooldown.setRemaining(1L);
            profile.save(false);
        }, true);

        commandSender.sendMessage(ChatColor.GREEN + "You have reset the " + gkit.getName() + " cooldown for "
                + player.getName() + ".");
        return true;
    }

    @Command(names = "gkm resetallcooldowns", permission = "console",
            description = "Reset all cooldowns.", async = true, hidden = true)
    public boolean resetCooldownsGkit(CommandSender commandSender) {
        plugin.getMongoManager().getProfiles().find().forEach((Block<? super Document>) document ->
                plugin.getProfileManager().loadOrCreate(UUID.fromString(document.getString("uuid")), profile -> {
                    profile.resetCooldowns();
                    profile.save(false);
                }, false));

        commandSender.sendMessage(ChatColor.GREEN + "ALL COOLDOWNS HAS BEEN RESET.");
        return true;
    }

    @Command(names = "gkm setitems", permission = "op",
            description = "Set items for a gkit.",
            playerOnly = true)
    public boolean setItemsGkit(Player player, @Param(name = "name") GKit gKit) {
        gKit.setArmor(player.getInventory().getArmorContents());
        gKit.setContents(player.getInventory().getContents());

        gKitManager.saveGKit(gKit, true);
        player.sendMessage(ChatColor.GREEN + "You have set the items for the gkit called " + gKit.getName() + ".");
        return true;
    }

}
