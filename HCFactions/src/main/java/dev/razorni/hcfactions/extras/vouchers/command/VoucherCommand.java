package dev.razorni.hcfactions.extras.vouchers.command;

import cc.invictusgames.ilib.utils.CC;
import dev.razorni.hcfactions.extras.vouchers.Voucher;
import dev.razorni.hcfactions.utils.commandapi.command.Command;
import dev.razorni.hcfactions.utils.commandapi.command.Param;
import dev.razorni.hcfactions.utils.menuapi.Clickable;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class VoucherCommand {

    @Command(names = {"voucher list", "vouchers list"}, permission = "op")
    public static void list(Player sender) {
        sender.sendMessage(CC.translate("&7&m--------------------------"));
        sender.sendMessage(CC.translate("&c&lVoucher List"));
        for (Voucher voucher : Voucher.getVouchers().values()) {
            Clickable clickable = new Clickable(CC.GRAY + " » " + CC.RED + voucher.getName() + CC.GRAY
                      + " - " + CC.WHITE + "Click here to receive this voucher", "&aClick to receive &lx1 " + voucher.getName(), "/voucher give " + voucher.getName() + " " + sender.getName() + " 1");
            clickable.sendToPlayer(sender);
        }
        sender.sendMessage(CC.translate("&7&m--------------------------"));
    }

    @Command(names = {"voucher give", "vouchers give"}, permission = "op")
    public static void give(CommandSender sender, @Param(name = "voucher") String vouchername, @Param(name = "target") Player target, @Param(name = "amount") int amount) {
        Voucher voucher = Voucher.getByName(vouchername);
        if (voucher == null) {
            sender.sendMessage(ChatColor.RED + "There is not a voucher named " + vouchername.toLowerCase() + ".");
            return;
        }
        if (sender instanceof Player)
            sender.sendMessage(CC.GREEN + "You have given " + target.getName() + " x" + amount + " " + voucher.getName() + " voucher(s).");
        target.sendMessage(CC.translate("&fYou have received &cx" + amount + " " + voucher.getName() + " &fvoucher(s)."));
        target.getInventory().addItem(Voucher.getVoucher(voucher, amount));
        IChatBaseComponent CT1 = IChatBaseComponent.ChatSerializer.a("{\"text\": \"§c§lVOUCHER\"}");
        IChatBaseComponent CS1 = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§fYou have received §cx" + amount + " " + vouchername + " §fvoucher(s)." + "\"}");
        PacketPlayOutTitle title1 = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, CT1);
        PacketPlayOutTitle subtitle1 = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, CS1);
        PacketPlayOutTitle length1 = new PacketPlayOutTitle(10, 30, 10);
        ((CraftPlayer) target).getHandle().playerConnection.sendPacket(title1);
        ((CraftPlayer) target).getHandle().playerConnection.sendPacket(subtitle1);
        ((CraftPlayer) target).getHandle().playerConnection.sendPacket(length1);
    }

    @Command(names = {"voucher giveall", "vouchers giveall"}, permission = "op")
    public static void give(CommandSender sender, @Param(name = "voucher") String vouchername, @Param(name = "amount") int amount) {
        Voucher voucher = Voucher.getByName(vouchername);
        if (voucher == null) {
            sender.sendMessage(ChatColor.RED + "There is not a voucher named " + vouchername.toLowerCase() + ".");
            return;
        }
        for (Player target : Bukkit.getOnlinePlayers()){
            target.sendMessage(CC.translate("&fYou have received &cx" + amount + " " + voucher.getName() + " &fvoucher(s)."));
            target.getInventory().addItem(Voucher.getVoucher(voucher, amount));

            IChatBaseComponent CT1 = IChatBaseComponent.ChatSerializer.a("{\"text\": \"§c§lVOUCHER\"}");
            IChatBaseComponent CS1 = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§fYou have received §cx" + amount + " " + vouchername + " §fvoucher(s)." + "\"}");
            PacketPlayOutTitle title1 = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, CT1);
            PacketPlayOutTitle subtitle1 = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, CS1);
            PacketPlayOutTitle length1 = new PacketPlayOutTitle(10, 30, 10);
            ((CraftPlayer) target).getHandle().playerConnection.sendPacket(title1);
            ((CraftPlayer) target).getHandle().playerConnection.sendPacket(subtitle1);
            ((CraftPlayer) target).getHandle().playerConnection.sendPacket(length1);
        }

        sender.sendMessage(CC.GREEN + "You have given all players x" + amount + " " + voucher.getName() + " voucher(s).");
    }

}