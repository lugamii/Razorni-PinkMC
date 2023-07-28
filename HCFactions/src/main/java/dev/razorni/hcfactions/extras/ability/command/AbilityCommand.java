package dev.razorni.hcfactions.extras.ability.command;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.ability.Ability;
import dev.razorni.hcfactions.extras.ability.menu.AbilityToggleMenu;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.extras.framework.commands.extra.TabCompletion;
import dev.razorni.hcfactions.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AbilityCommand extends Command {

    public AbilityCommand(CommandManager commandManager) {
        super(commandManager, "ability");
        setPermissible("azurite.ability");
        this.completions.add(new TabCompletion(Arrays.asList("give", "list", "toggle", "getall"), 0));
    }

    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 3) {
            String b = args[args.length - 1];
            return getInstance().getAbilityManager().getAbilities().keySet().stream().filter(s -> s.regionMatches(true, 0, b, 0, b.length())).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        switch (args[0].toLowerCase()) {
            case "give": {
                if (args.length < 4) {
                    this.sendMessage(sender, this.getLanguageConfig().getString("ABILITY_COMMAND.ABILITY_GIVE.USAGE"));
                    return;
                }
                Player target = Bukkit.getPlayer(args[1]);
                Ability ability = this.getInstance().getAbilityManager().getAbility(args[2]);
                Integer amount = this.getInt(args[3]);
                if (target == null) {
                    this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[1]));
                    return;
                }
                if (ability == null) {
                    this.sendMessage(sender, this.getLanguageConfig().getString("ABILITY_COMMAND.ABILITY_GIVE.NOT_FOUND").replaceAll("%ability%", args[1]));
                    return;
                }
                if (amount == null) {
                    this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[3]));
                    return;
                }
                ItemStack stack = ability.getItem().clone();
                stack.setAmount(amount);
                ItemUtils.giveItem(target, stack, target.getLocation());
                this.sendMessage(sender, this.getLanguageConfig().getString("ABILITY_COMMAND.ABILITY_GIVE.GAVE").replaceAll("%player%", target.getName()).replaceAll("%amount%", String.valueOf(amount)).replaceAll("%ability%", ability.getName()));
                return;
            }
            case "list": {
                for (String s : this.getLanguageConfig().getStringList("ABILITY_COMMAND.ABILITY_LIST.ABILITIES")) {
                    if (!s.equalsIgnoreCase("%abilities%")) {
                        this.sendMessage(sender, s);
                        continue;
                    }
                    for (Ability ability : this.getInstance().getAbilityManager().getAbilities().values()) {
                        this.sendMessage(sender, this.getLanguageConfig().getString("ABILITY_COMMAND.ABILITY_LIST.ABILITY_FORMAT").replaceAll("%ability%", ability.getName().replaceAll(" ", "")).replaceAll("%cooldown%", String.valueOf(ability.getAbilityCooldown().getSeconds())));
                    }
                }
                return;
            }
            case "toggle": {
                if (!(sender instanceof Player)) {
                    this.sendMessage(sender, Config.PLAYER_ONLY);
                    return;
                }
                Player player = (Player) sender;
                new AbilityToggleMenu(this.getInstance().getMenuManager(), player).open();
                return;
            }
            case "getall": {
                if (!(sender instanceof Player)) {
                    this.sendMessage(sender, Config.PLAYER_ONLY);
                    return;
                }
                for (Ability ability : this.getInstance().getAbilityManager().getAbilities().values()) {
                    ((Player) sender).getInventory().addItem(ability.getItem());
                }
                return;
            }
        }
        this.sendUsage(sender);
    }

    public List<String> usage() {
        return getLanguageConfig().getStringList("ABILITY_COMMAND.USAGE");
    }
}
