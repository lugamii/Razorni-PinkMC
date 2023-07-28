package dev.razorni.hcfactions.utils.menuapi;

import com.google.common.collect.Lists;
import dev.razorni.hcfactions.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemBuilder implements Listener {

	private ItemStack is;

	public ItemBuilder(Material mat) {
		is = new ItemStack(mat);
	}

	public ItemBuilder(ItemStack is) {
		this.is = is;
	}

	public ItemBuilder amount(int amount) {
		is.setAmount(amount);
		return this;
	}

	public ItemBuilder addToLore(String... parts) {
		List lore;
		ItemMeta meta = this.is.getItemMeta();
		if (meta == null) {
			meta = Bukkit.getItemFactory().getItemMeta(this.is.getType());
		}
		if ((lore = meta.getLore()) == null) {
			lore = Lists.newArrayList();
		}
		lore.addAll(Arrays.stream(parts).map(part -> ChatColor.translateAlternateColorCodes('&', part)).collect(Collectors.toList()));
		meta.setLore(lore);
		this.is.setItemMeta(meta);
		return this;
	}

	public ItemBuilder name(String name) {
		ItemMeta meta = is.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		is.setItemMeta(meta);
		return this;
	}

	public ItemBuilder lore(String name) {
		ItemMeta meta = is.getItemMeta();
		List<String> lore = meta.getLore();

		if (lore == null) {
			lore = new ArrayList<>();
		}

		lore.add(ChatColor.translateAlternateColorCodes('&', name));
		meta.setLore(lore);

		is.setItemMeta(meta);

		return this;
	}

	public ItemBuilder lore(List<String> lore) {
		List<String> toSet = new ArrayList<>();
		ItemMeta meta = is.getItemMeta();

		for (String string : lore) {
			toSet.add(ChatColor.translateAlternateColorCodes('&', string));
		}

		meta.setLore(toSet);
		is.setItemMeta(meta);

		return this;
	}

	public ItemBuilder durability(int durability) {
		is.setDurability((short) durability);
		return this;
	}

	public ItemBuilder enchantment(Enchantment enchantment, int level) {
		is.addUnsafeEnchantment(enchantment, level);
		return this;
	}

	public ItemBuilder enchantment(Enchantment enchantment) {
		is.addUnsafeEnchantment(enchantment, 1);
		return this;
	}

	public ItemBuilder type(Material material) {
		is.setType(material);
		return this;
	}

	public ItemBuilder clearLore() {
		ItemMeta meta = is.getItemMeta();

		meta.setLore(new ArrayList<>());
		is.setItemMeta(meta);

		return this;
	}

	public ItemBuilder skull(String owner) {

		SkullMeta meta = (SkullMeta) is.getItemMeta();
		meta.setOwner(owner);
		is.setItemMeta(meta);

		return this;
	}

	public ItemBuilder setGlowing(boolean glowing) {
		if (glowing) {
			this.enchantment(ItemUtils.FAKE_GLOW, 2);
		} else {
			this.clearEnchantments();
		}
		return this;
	}

	public ItemBuilder addCommand(String command) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
		return this;
	}

	public ItemBuilder clearEnchantments() {
		for (Enchantment e : is.getEnchantments().keySet()) {
			is.removeEnchantment(e);
		}

		return this;
	}

	public ItemStack build() {
		return is;
	}

}