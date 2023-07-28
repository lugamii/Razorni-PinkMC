package dev.razorni.core.commands.staff;

import dev.razorni.core.util.CC;
import dev.razorni.core.util.command.Command;
import dev.razorni.core.util.command.Param;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 02/08/2021 / 9:13 AM
 * potpvp-si / net.frozenorb.potpvp.command
 */
public class EnchantCommand {

	@Command(names = "enchant", permission = "op")
	public static void enchant(Player sender, @Param(name = "enchant") String enchant, @Param(name = "power") int power) {

		Enchantment enchantment = getEnchantmentByName(enchant);

		if (enchantment == null) {
			sender.sendMessage(CC.translate("&cInvalid enchantment."));
			return;
		}

		sender.getItemInHand().addUnsafeEnchantment(enchantment, power);
	}

	public static Enchantment getEnchantmentByName(Object object) {
		String value = object.toString().replace("_", "").trim();

		for(Enchantment enchant : Enchantment.values()) {
			if(value.equals(String.valueOf(enchant.getId()))
					|| value.equalsIgnoreCase(enchant.getName().replace("_", ""))
					|| value.equalsIgnoreCase(enchant.getName())) {
				return enchant;
			}
		}

		switch (value.toUpperCase()) {
			case "PROT":
			case "PROTECTION": return Enchantment.PROTECTION_ENVIRONMENTAL;
			case "UNB":
			case "UNBREAKING": return Enchantment.DURABILITY;
			case "FIREP":
			case "FP":
			case "FIREPROTECTION": return Enchantment.PROTECTION_FIRE;
			case "FEATHERF":
			case "FL":
			case "FEATHERFALLING": return Enchantment.PROTECTION_FALL;
			case "BLASTP":
			case "BP":
			case "BLASTPROTECTION": return Enchantment.PROTECTION_EXPLOSIONS;
			case "SHARP":
			case "SHARPNESS": return Enchantment.DAMAGE_ALL;
			case "KNOCK":
			case "KNOCKBACK": return Enchantment.KNOCKBACK;
			case "FIREA":
			case "FA":
			case "FIRE":
			case "FIREASPECT": return Enchantment.FIRE_ASPECT;
			case "L":
			case "LOOT":
			case "LOOTING": return Enchantment.LOOT_BONUS_MOBS;
			case "F":
			case "FORT":
			case "FORTUNE": return Enchantment.LOOT_BONUS_BLOCKS;
			case "ST":
			case "SILK":
			case "SILKTOUCH": return Enchantment.SILK_TOUCH;
			case "EFF":
			case "EFFICIENCY": return Enchantment.DIG_SPEED;
			case "SM":
			case "SMITE": return Enchantment.DAMAGE_UNDEAD;
			case "INF":
			case "INFINITY": return Enchantment.ARROW_INFINITE;
			case "FLA":
			case "FLAME": return Enchantment.ARROW_FIRE;
			case "PUNCH": return Enchantment.ARROW_KNOCKBACK;
			case "POWER": return Enchantment.ARROW_DAMAGE;
			default: return null;
		}
	}
}
