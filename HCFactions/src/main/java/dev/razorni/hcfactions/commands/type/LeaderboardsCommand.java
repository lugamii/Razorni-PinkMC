package dev.razorni.hcfactions.commands.type;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.extras.framework.menu.Menu;
import dev.razorni.hcfactions.extras.framework.menu.MenuManager;
import dev.razorni.hcfactions.extras.framework.menu.button.Button;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.utils.ItemBuilder;
import dev.razorni.hcfactions.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardsCommand extends Command {
    public LeaderboardsCommand(CommandManager manager) {
        super(manager, "leaderboards");
        this.setAsync(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player) sender;
        new LeaderboardsMenu(this.getInstance().getMenuManager(), player).open();
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("leaderboard");
    }

    private static class LeaderboardsMenu extends Menu {
        public LeaderboardsMenu(MenuManager manager, Player player) {
            super(manager, player, manager.getLanguageConfig().getString("LEADERBOARDS_COMMAND.TITLE"), manager.getLanguageConfig().getInt("LEADERBOARDS_COMMAND.SIZE"), false);
        }

        @Override
        public Map<Integer, Button> getButtons(Player player) {
            Map<Integer, Button> buttons = new HashMap<>();
            List<PlayerTeam> topTeams = this.getInstance().getTeamManager().getTeamSorting().getTeamTop();
            List<User> topKills = this.getInstance().getUserManager().getTopKills();
            List<User> topDeaths = this.getInstance().getUserManager().getTopDeaths();
            List<User> topKDR = this.getInstance().getUserManager().getTopKDR();
            List<User> topKillStreak = this.getInstance().getUserManager().getTopKillStreaks();
            for (String s : this.getLanguageConfig().getConfigurationSection("LEADERBOARDS_COMMAND.ITEMS").getKeys(false)) {
                String ss = "LEADERBOARDS_COMMAND.ITEMS." + s + ".";
                buttons.put(this.getLanguageConfig().getInt(ss + "SLOT"), new Button() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        event.setCancelled(true);
                    }

                    @Override
                    public ItemStack getItemStack() {
                        ItemBuilder builder = new ItemBuilder(ItemUtils.getMat(getLanguageConfig().getString(ss + "MATERIAL")));
                        builder.setName(getLanguageConfig().getString(ss + "NAME"));
                        List<String> lore = getLanguageConfig().getStringList(ss + "LORE");
                        lore.replaceAll(s -> {
                            for (int i = 0; i < topTeams.size() && i != 10; ++i) {
                                PlayerTeam team = topTeams.get(i);
                                s = s.replaceAll("%team_top" + (i + 1) + "%", getLanguageConfig().getString(ss + "FORMAT").replaceAll("%team%", team.getName()).replaceAll("%points%", String.valueOf(team.getPoints())));
                            }
                            for (int i = 0; i < topKills.size() && i != 10; ++i) {
                                User user = topKills.get(i);
                                s = s.replaceAll("%kills_top" + (i + 1) + "%", getLanguageConfig().getString(ss + "FORMAT").replaceAll("%player%", Bukkit.getOfflinePlayer(user.getUniqueID()).getName()).replaceAll("%kills%", String.valueOf(user.getKills())));
                            }
                            for (int i = 0; i < topDeaths.size() && i != 10; ++i) {
                                User user = topDeaths.get(i);
                                s = s.replaceAll("%deaths_top" + (i + 1) + "%", getLanguageConfig().getString(ss + "FORMAT").replaceAll("%player%", Bukkit.getOfflinePlayer(user.getUniqueID()).getName()).replaceAll("%deaths%", String.valueOf(user.getDeaths())));
                            }
                            for (int i = 0; i < topKDR.size() && i != 10; ++i) {
                                User user = topKDR.get(i);
                                s = s.replaceAll("%kdr_top" + (i + 1) + "%", getLanguageConfig().getString(ss + "FORMAT").replaceAll("%player%", Bukkit.getOfflinePlayer(user.getUniqueID()).getName()).replaceAll("%kdr%", user.getKDRString()));
                            }
                            for (int i = 0; i < topKillStreak.size() && i != 10; ++i) {
                                User user = topKillStreak.get(i);
                                s = s.replaceAll("%killstreaks_top" + (i + 1) + "%", getLanguageConfig().getString(String.valueOf(new StringBuilder().append(ss).append("FORMAT"))).replaceAll("%player%", Bukkit.getOfflinePlayer(user.getUniqueID()).getName()).replaceAll("%killstreak%", String.valueOf(user.getKillstreak())));
                            }
                            if (s.contains("%deaths_top") || s.contains("%kills_top") || s.contains("%team_top") || s.contains("%killstreaks_top") || s.contains("%kdr_top")) {
                                s = getLanguageConfig().getString("LEADERBOARDS_COMMAND.NONE_MESSAGE");
                            }
                            return s;
                        });
                        builder.setLore(lore);
                        return builder.toItemStack();
                    }
                });
            }
            return buttons;
        }
    }
}