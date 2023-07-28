package eu.vortexdev.invictusspigot.command;

import eu.vortexdev.api.knockback.KnockbackProfile;
import eu.vortexdev.api.knockback.KnockbackValue;
import eu.vortexdev.invictusspigot.InvictusSpigot;
import eu.vortexdev.invictusspigot.config.InvictusConfig;
import eu.vortexdev.invictusspigot.knockback.CraftKnockbackProfile;
import eu.vortexdev.invictusspigot.knockback.KnockbackManager;
import eu.vortexdev.invictusspigot.util.BukkitUtil;
import eu.vortexdev.invictusspigot.util.JavaUtil;
import joptsimple.internal.Strings;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class KnockbackCommand extends Command {

    public KnockbackCommand() {
        super("kb");
        setAliases(Arrays.asList("kb", "knockback"));
        setPermission("invictusspigot.knockback");
        setDescription("Set knockback values");
    }

    public boolean execute(CommandSender s, String currentAlias, String[] args) {
        if (testPermission(s)) {
            if (args.length == 0) {
                helpMessage(s);
                return true;
            }
            KnockbackManager manager = InvictusSpigot.INSTANCE.getKnockbackManager();
            switch (args[0].toLowerCase()) {
                case "list": {
                    s.sendMessage("");
                    s.sendMessage(InvictusConfig.mainColor.toString() + ChatColor.ITALIC + "Knockback Profiles:");
                    for (KnockbackProfile pf : manager.getProfiles())
                        s.sendMessage(ChatColor.GRAY + " * " + ChatColor.WHITE + pf.getName());
                    s.sendMessage("");
                    s.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + " Use /kb <profile> to view values.");
                    s.sendMessage("");
                    break;
                }
                case "reload": {
                    manager.reloadProfiles();
                    s.sendMessage(InvictusConfig.prefix + ChatColor.WHITE + "Reloaded profiles from config");
                    break;
                }
                case "add": {
                    if (args.length < 2) {
                        s.sendMessage(InvictusConfig.prefix + ChatColor.RED + "Usage: /kb add <name>");
                        return true;
                    }
                    String name = args[1];
                    if (manager.getProfile(name) != null) {
                        s.sendMessage(InvictusConfig.prefix + ChatColor.RED + "This profile already exists!");
                        return true;
                    }
                    manager.addProfile(new CraftKnockbackProfile(name));
                    manager.saveProfiles();
                    s.sendMessage(InvictusConfig.prefix + ChatColor.WHITE + "You've created profile: " + ChatColor.GRAY + name);
                    break;
                }
                case "remove": {
                    if (args.length < 2) {
                        s.sendMessage(InvictusConfig.prefix + ChatColor.RED + "Usage: /kb remove <name>");
                        return true;
                    }
                    String name = args[1];
                    KnockbackProfile profile = manager.getProfile(args[1]);
                    if (profile == null) {
                        s.sendMessage(InvictusConfig.prefix + ChatColor.RED + "Profile " + args[1] + " does not exist!");
                        return true;
                    }
                    manager.removeProfile(profile);
                    manager.saveProfiles();
                    s.sendMessage(InvictusConfig.prefix + ChatColor.WHITE + "You've removed profile: " + ChatColor.GRAY + name);
                    break;
                }
                case "set": {
                    if (args.length < 3) {
                        s.sendMessage(ChatColor.RED + "Usage: /kb set <playerName> <profileName>");
                        return true;
                    }
                    String name = args[1];
                    Player player = Bukkit.getPlayerExact(name);
                    if (player == null) {
                        s.sendMessage(InvictusConfig.prefix + ChatColor.RED + "Player " + name + " cannot be found!");
                        return true;
                    }
                    KnockbackProfile profile = manager.getProfile(args[2]);
                    if (profile == null) {
                        s.sendMessage(InvictusConfig.prefix + ChatColor.RED + "Profile " + args[2] + " does not exist!");
                        return true;
                    }
                    player.setKnockbackProfile(profile);
                    if (s instanceof Player)
                        s.sendMessage(InvictusConfig.prefix + ChatColor.WHITE + "You've set profile " + args[2] + " for " + ChatColor.GRAY + name);
                    break;
                }
                case "setall": {
                    if (args.length < 2) {
                        s.sendMessage(ChatColor.RED + "Usage: /kb setall <profileName>");
                        return true;
                    }
                    String name = args[1];
                    KnockbackProfile profile = manager.getProfile(name);
                    if (profile == null) {
                        s.sendMessage(InvictusConfig.prefix + ChatColor.RED + "Profile " + name + " does not exist!");
                        return true;
                    }
                    for(Player player : Bukkit.getOnlinePlayers())
                        player.setKnockbackProfile(profile);
                    if (s instanceof Player)
                        s.sendMessage(InvictusConfig.prefix + ChatColor.WHITE + "You've set profile " + name + " for " + ChatColor.GRAY + "all online players");
                    break;
                }
                case "edit": {
                    if (args.length == 4) {
                        KnockbackProfile profile = manager.getProfile(args[1]);
                        if (profile == null) {
                            s.sendMessage(InvictusConfig.prefix + ChatColor.RED + "Profile " + args[1] + " does not exist!");
                            return true;
                        }

                        KnockbackValue setting = profile.getSetting(args[2]);
                        if (setting == null) {
                            s.sendMessage(InvictusConfig.prefix + ChatColor.RED + "Setting " + args[2] + " does not exist!");
                            return true;
                        }

                        String input = args[3];
                        ChatColor color = ChatColor.GRAY;
                        Object value = setting.getValue();
                        if (value instanceof Integer) {
                            value = JavaUtil.tryParseInteger(input);
                        } else if (value instanceof Double) {
                            value = JavaUtil.tryParseDouble(input);
                        } else if (value instanceof Boolean) {
                            value = Boolean.parseBoolean(input);
                            color = (Boolean) value ? ChatColor.GREEN : ChatColor.RED;
                        }

                        if (value == null) {
                            s.sendMessage(InvictusConfig.prefix + ChatColor.RED + "Value " + input + " is not valid.");
                            return true;
                        }

                        setting.setValue(value);
                        manager.saveProfiles();

                        s.sendMessage(InvictusConfig.prefix + ChatColor.WHITE + "You've set the " + ChatColor.GRAY + WordUtils.capitalizeFully(Strings.join(setting.getKey().split("(?=[A-Z])"), " ")) + ChatColor.WHITE + " value of " + ChatColor.GRAY + profile.getName() + ChatColor.WHITE + " to " + color + value);
                    } else {
                        s.sendMessage(InvictusConfig.prefix + ChatColor.RED + "Usage: /kb edit <profile> <setting> <value>");
                    }
                    break;
                }
                default: {
                    KnockbackProfile profile = manager.getProfile(args[0]);
                    if (profile == null) {
                        s.sendMessage(InvictusConfig.prefix + ChatColor.RED + "Profile " + args[0] + " does not exist!");
                        return true;
                    }

                    s.sendMessage(BukkitUtil.LINE);
                    s.sendMessage(InvictusConfig.mainColor.toString() + ChatColor.ITALIC + profile.getName() + " Values:");
                    for(KnockbackValue setting : profile.getSettings()) {
                        s.sendMessage(ChatColor.GRAY + " " + setting.getKey() + ": " + ChatColor.WHITE + setting.getValue());
                    }
                    s.sendMessage(BukkitUtil.LINE);
                }
            }
        }
        return true;
    }

    public void helpMessage(CommandSender s) {
        s.sendMessage(BukkitUtil.LINE);
        s.sendMessage(InvictusConfig.mainColor.toString() + ChatColor.ITALIC + "Knockback Help:");
        s.sendMessage("/kb <profile>");
        s.sendMessage("/kb list");
        s.sendMessage("/kb reload");
        s.sendMessage("/kb add <profile>");
        s.sendMessage("/kb remove <profile>");
        s.sendMessage("/kb set <player> <profile>");
        s.sendMessage("/kb setall <profile>");
        s.sendMessage("/kb edit <profile> <setting> <value>");
        s.sendMessage(BukkitUtil.LINE);
    }

}
