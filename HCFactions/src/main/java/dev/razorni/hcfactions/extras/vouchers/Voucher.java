package dev.razorni.hcfactions.extras.vouchers;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public class Voucher {

    @Getter
    public static final Map<Integer, Voucher> vouchers = new HashMap<>();
    @Getter
    public static final Map<String, Voucher> voucherNames = new HashMap<>();

    public String name;
    public List<String> commands;
    public ItemStack item;

    public Voucher(String name, List<String> commands, ItemStack item) {
        this.name = name;
        this.commands = commands;
        this.item = item;
        loadVoucher(this);
    }

    public static Voucher getByName(String name) {
        return voucherNames.get(name.toLowerCase());
    }

    public static int calculateItemHash(ItemMeta itemMeta) {
        return Objects.hash(itemMeta.getDisplayName(), itemMeta.getLore());
    }

    public static ItemStack getVoucher(Voucher voucher, int amount) {
        ItemStack itemStack = voucher.getItem().clone();
        itemStack.setAmount(amount);
        return itemStack;
    }

    private void loadVoucher(Voucher voucher) {
        Integer itemHash = calculateItemHash(voucher.getItem().getItemMeta());
        vouchers.put(itemHash, voucher);
        voucherNames.put(voucher.getName().toLowerCase(), voucher);
    }
}