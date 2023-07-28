package dev.razorni.hcfactions.commands.type.essential;

import dev.razorni.hcfactions.HCF;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantCommand implements CommandExecutor {

    HCF plugin;
    String title;

    public EnchantCommand(HCF plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("enchant")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
                return true;
            }
            Player player = (Player) sender;
            if (!player.hasPermission("enchants.enchant")) {
                player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                return true;
            }
            if (args.length <= 0) {
                if (player.getItemInHand().getType() == Material.AIR) {
                    player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                    return true;
                }
                player.sendMessage(ChatColor.RED + "Usage: /enchant <enchantment> <level>");
                return true;
            }
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("sharpness") || args[0].equals("16") || args[0].equalsIgnoreCase("minecraft:sharpness")) {
                    if (!player.hasPermission("enchants.enchant.sharpness")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.sharpness");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.DAMAGE_ALL, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("protection") || args[0].equals("0") || args[0].equalsIgnoreCase("minecraft:protection")) {
                    if (!player.hasPermission("enchants.enchant.protection")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.protection");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("fireprotection") || args[0].equals("1") || args[0].equalsIgnoreCase("minecraft:fire_protection")) {
                    if (!player.hasPermission("enchants.enchant.fireprotection")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.fireprotection");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.PROTECTION_FIRE, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("featherfalling") || args[0].equals("2") || args[0].equalsIgnoreCase("minecraft:feather_falling")) {
                    if (!player.hasPermission("enchants.enchant.featherfalling")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.featherfalling");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.PROTECTION_FALL, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("blastprotection") || args[0].equals("3") || args[0].equalsIgnoreCase("minecraft:blast_protection")) {
                    if (!player.hasPermission("enchants.enchant.blastprotection")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.blastprotection");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("projectileprotection") || args[0].equals("4") || args[0].equalsIgnoreCase("minecraft:projectile_protection")) {
                    if (!player.hasPermission("enchants.enchant.projectileprotection")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.projectileprotection");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("respiration") || args[0].equals("5") || args[0].equalsIgnoreCase("minecraft:respiration")) {
                    if (!player.hasPermission("enchants.enchant.respiration")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.respiration");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.OXYGEN, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.OXYGEN, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("aquaaffinity") || args[0].equals("6") || args[0].equalsIgnoreCase("minecraft:aqua_affinity")) {
                    if (!player.hasPermission("enchants.enchant.aquaaffinity")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.aquaaffinity");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.WATER_WORKER, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.WATER_WORKER, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("thorns") || args[0].equals("7") || args[0].equalsIgnoreCase("minecraft:thorns")) {
                    if (!player.hasPermission("enchants.enchant.thorns")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.thorns");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.THORNS, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.THORNS, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("depthstrider") || args[0].equals("8") || args[0].equalsIgnoreCase("minecraft:depth_strider")) {
                    if (!player.hasPermission("enchants.enchant.depthstrider")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.depthstrider");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.DEPTH_STRIDER, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("smite") || args[0].equals("17") || args[0].equalsIgnoreCase("minecraft:smite")) {
                    if (!player.hasPermission("enchants.enchant.smite")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.smite");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.DAMAGE_UNDEAD, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("baneofarthropods") || args[0].equals("18") || args[0].equalsIgnoreCase("minecraft:bane_of_arthropods")) {
                    if (!player.hasPermission("enchants.enchant.baneofarthropods")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.baneofarthropods");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.DAMAGE_ARTHROPODS, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("knockback") || args[0].equals("19") || args[0].equalsIgnoreCase("minecraft:knockback")) {
                    if (!player.hasPermission("enchants.enchant.knockback")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.knockback");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.KNOCKBACK, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.KNOCKBACK, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("fireaspect") || args[0].equals("20") || args[0].equalsIgnoreCase("minecraft:fire_aspect")) {
                    if (!player.hasPermission("enchants.enchant.fireaspect")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.fireaspect");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.FIRE_ASPECT, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("looting") || args[0].equals("21") || args[0].equalsIgnoreCase("minecraft:looting")) {
                    if (!player.hasPermission("enchants.enchant.looting")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.looting");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.LOOT_BONUS_MOBS, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("efficiency") || args[0].equals("32") || args[0].equalsIgnoreCase("minecraft:efficiency")) {
                    if (!player.hasPermission("enchants.enchant.efficiency")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.efficiency");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.DIG_SPEED, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.DIG_SPEED, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("silktouch") || args[0].equals("33") || args[0].equalsIgnoreCase("minecraft:silk_touch")) {
                    if (!player.hasPermission("enchants.enchant.silktouch")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.silktouch");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.SILK_TOUCH, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.SILK_TOUCH, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("unbreaking") || args[0].equals("34") || args[0].equalsIgnoreCase("minecraft:unbreaking")) {
                    if (!player.hasPermission("enchants.enchant.unbreaking")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.unbreaking");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.DURABILITY, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.DURABILITY, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("fortune") || args[0].equals("35") || args[0].equalsIgnoreCase("minecraft:fortune")) {
                    if (!player.hasPermission("enchants.enchant.fortune")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.fortune");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("power") || args[0].equals("48") || args[0].equalsIgnoreCase("minecraft:power")) {
                    if (!player.hasPermission("enchants.enchant.power")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.power");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.ARROW_DAMAGE, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("punch") || args[0].equals("49") || args[0].equalsIgnoreCase("minecraft:punch")) {
                    if (!player.hasPermission("enchants.enchant.punch")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.punch");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.ARROW_KNOCKBACK, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("flame") || args[0].equals("50") || args[0].equalsIgnoreCase("minecraft:flame")) {
                    if (!player.hasPermission("enchants.enchant.flame")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.flame");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.ARROW_FIRE, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.ARROW_FIRE, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("infinity") || args[0].equals("51") || args[0].equalsIgnoreCase("minecraft:infinity")) {
                    if (!player.hasPermission("enchants.enchant.infinity")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.infinity");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.ARROW_INFINITE, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("luckofthesea") || args[0].equals("61") || args[0].equalsIgnoreCase("minecraft:luck_of_the_sea")) {
                    if (!player.hasPermission("enchants.enchant.luckofthesea")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.luckofthesea");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.LUCK, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.LUCK, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("lure") || args[0].equals("62") || args[0].equalsIgnoreCase("minecraft:lure")) {
                    if (!player.hasPermission("enchants.enchant.lure")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.BLUE + "enchants.enchant.lure");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        ItemStack IS = player.getItemInHand();
                        int a = 0;
                        a = Integer.parseInt(args[1]);
                        try {
                            if (a > 10000) {
                                IS.addEnchantment(Enchantment.LURE, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.LURE, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(ChatColor.RED + "Error: enchantment must be at max 10000.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Please specify a level to enchant this item to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("*")) {
                    if (!player.hasPermission("enchants.enchant.all")) {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                        return true;
                    }
                    if (player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You cannot enchant this item.");
                        return true;
                    }
                    try {
                        if (args[1].equalsIgnoreCase("*") || args[1].equalsIgnoreCase("max")) {
                            ItemStack IS = player.getItemInHand();
                            IS.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 10000);
                            IS.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 10000);
                            IS.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10000);
                            IS.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 10000);
                            IS.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 10000);
                            IS.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 10000);
                            IS.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 10000);
                            IS.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, 10000);
                            IS.addUnsafeEnchantment(Enchantment.DIG_SPEED, 10000);
                            IS.addUnsafeEnchantment(Enchantment.DURABILITY, 10000);
                            IS.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 10000);
                            IS.addUnsafeEnchantment(Enchantment.KNOCKBACK, 10000);
                            IS.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 10000);
                            IS.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 10000);
                            IS.addUnsafeEnchantment(Enchantment.LUCK, 10000);
                            IS.addUnsafeEnchantment(Enchantment.LURE, 10000);
                            IS.addUnsafeEnchantment(Enchantment.OXYGEN, 10000);
                            IS.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 10000);
                            IS.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 10000);
                            IS.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 10000);
                            IS.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 10000);
                            IS.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 10000);
                            IS.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 10000);
                            IS.addUnsafeEnchantment(Enchantment.THORNS, 10000);
                            IS.addUnsafeEnchantment(Enchantment.WATER_WORKER, 10000);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        }
                        try {
                            ItemStack IS = player.getItemInHand();
                            int a = 0;
                            a = Integer.parseInt(args[1]);
                            if (a > 10000) {
                                IS.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 10000);
                                IS.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 10000);
                                IS.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10000);
                                IS.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 10000);
                                IS.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 10000);
                                IS.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 10000);
                                IS.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 10000);
                                IS.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, 10000);
                                IS.addUnsafeEnchantment(Enchantment.DIG_SPEED, 10000);
                                IS.addUnsafeEnchantment(Enchantment.DURABILITY, 10000);
                                IS.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 10000);
                                IS.addUnsafeEnchantment(Enchantment.KNOCKBACK, 10000);
                                IS.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 10000);
                                IS.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 10000);
                                IS.addUnsafeEnchantment(Enchantment.LUCK, 10000);
                                IS.addUnsafeEnchantment(Enchantment.LURE, 10000);
                                IS.addUnsafeEnchantment(Enchantment.OXYGEN, 10000);
                                IS.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 10000);
                                IS.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 10000);
                                IS.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 10000);
                                IS.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 10000);
                                IS.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 10000);
                                IS.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 10000);
                                IS.addUnsafeEnchantment(Enchantment.THORNS, 10000);
                                IS.addUnsafeEnchantment(Enchantment.WATER_WORKER, 10000);
                                player.sendMessage(ChatColor.BLUE + "There ya' go!");
                                return true;
                            }
                            IS.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, a);
                            IS.addUnsafeEnchantment(Enchantment.ARROW_FIRE, a);
                            IS.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, a);
                            IS.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, a);
                            IS.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, a);
                            IS.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, a);
                            IS.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, a);
                            IS.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, a);
                            IS.addUnsafeEnchantment(Enchantment.DIG_SPEED, a);
                            IS.addUnsafeEnchantment(Enchantment.DURABILITY, a);
                            IS.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, a);
                            IS.addUnsafeEnchantment(Enchantment.KNOCKBACK, a);
                            IS.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, a);
                            IS.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, a);
                            IS.addUnsafeEnchantment(Enchantment.LUCK, a);
                            IS.addUnsafeEnchantment(Enchantment.LURE, a);
                            IS.addUnsafeEnchantment(Enchantment.OXYGEN, a);
                            IS.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, a);
                            IS.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, a);
                            IS.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, a);
                            IS.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, a);
                            IS.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, a);
                            IS.addUnsafeEnchantment(Enchantment.SILK_TOUCH, a);
                            IS.addUnsafeEnchantment(Enchantment.THORNS, a);
                            IS.addUnsafeEnchantment(Enchantment.WATER_WORKER, a);
                            player.sendMessage(ChatColor.BLUE + "There ya' go!");
                            return true;
                        } catch (NumberFormatException ex) {
                            player.sendMessage(ChatColor.DARK_RED + args[1] + ChatColor.RED + " is not a valid input.");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(ChatColor.RED + "Use /enchant <*|all> [level|*|max].");
                        return true;
                    }
                }
                player.sendMessage(ChatColor.RED + "\"" + ChatColor.DARK_RED + args[0] + ChatColor.RED + "\" is not a valid argument.");
                return true;
            }
        }
        return true;
    }
}
