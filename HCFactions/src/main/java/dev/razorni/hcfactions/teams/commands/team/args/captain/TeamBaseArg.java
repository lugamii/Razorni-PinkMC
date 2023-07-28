package dev.razorni.hcfactions.teams.commands.team.args.captain;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.extras.workload.type.TeamWorkdLoadType;
import dev.razorni.hcfactions.teams.player.Role;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.utils.CC;
import dev.razorni.hcfactions.utils.ItemBuilder;
import dev.razorni.hcfactions.utils.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class TeamBaseArg extends Argument {

    private final ItemStack baseWand;

    public TeamBaseArg(CommandManager manager) {
        super(manager, Collections.singletonList("base"));
        this.baseWand = new ItemBuilder(ItemUtils.getMat(manager.getTeamConfig().getString("CLAIMING.BASE_WAND.TYPE"))).setName(manager.getTeamConfig().getString("CLAIMING.BASE_WAND.NAME")).setLore(manager.getTeamConfig().getStringList("CLAIMING.BASE_WAND.LORE")).toItemStack();

    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player) sender;
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        if (team == null) {
            this.sendMessage(sender, CC.translate(Config.NOT_IN_TEAM));
            return;
        }

        if (!team.checkRole(player, Role.CAPTAIN)) {
            this.sendMessage(sender, Config.INSUFFICIENT_ROLE.replaceAll("%role%", Role.CAPTAIN.getName()));
            return;
        }

        if (team.isRaidable()) {
            this.sendMessage(sender, ChatColor.RED + ("You may not claim land while your faction is raidable!"));
            return;
        }

        int slot = -1;

        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getItem(i) == null) {
                slot = i;
                break;
            }
        }

        if (slot == -1) {
            player.sendMessage(ChatColor.RED + "You don't have space in your hotbar for the claim wand!");
            return;
        }

        if (team.getWorkloadRunnables().containsKey(TeamWorkdLoadType.BASE)){
            sender.sendMessage(CC.translate("&cYour base is already in progress!"));
            return;
        }

        if (team.isUseBase()){
            sender.sendMessage(CC.translate("&cYour base is already created!"));
            return;
        }

    }

    @Override
    public String usage() {
        return null;
    }

}
