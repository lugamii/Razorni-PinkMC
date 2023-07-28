package dev.razorni.hcfactions.extras.workload.commands;

import dev.razorni.hcfactions.utils.menuapi.CC;
import dev.razorni.hcfactions.utils.commandapi.command.Command;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.extras.workload.ScheduleWorkLoad;
import dev.razorni.hcfactions.extras.workload.TeamWorkload;
import dev.razorni.hcfactions.extras.workload.WorkLoadQueue;
import dev.razorni.hcfactions.extras.workload.type.TeamWorkdLoadType;
import org.bukkit.entity.Player;

import java.util.List;

public class WorkLoadCommands {

    private static final WorkLoadQueue queue = HCF.getPlugin().getWorkLoadQueue();

    @Command(names = {"workload pause", "workload resume"}, permission = "op")
    public static void pause(Player player){
        queue.setPaused(!queue.isPaused());

        if (queue.isPaused()){
            queue.getCurrentWorkloads().forEach(ScheduleWorkLoad::pause);
        } else {
            queue.getCurrentWorkloads().forEach(ScheduleWorkLoad::resume);
        }

        player.sendMessage(CC.translate("&eWork load queue " + (queue.isPaused() ? "&cpaused" : "&aresumed")));
    }

    @Command(names = {"queue base"}, permission = "")
    public static void base(Player player){
        PlayerTeam team = HCF.getPlugin().getTeamManager().getByPlayer(player.getUniqueId());

        if (team != null) {

            if (team.getWorkloadRunnables().containsKey(TeamWorkdLoadType.BASE)){
                TeamWorkload runnable = team.getWorkloadRunnables().get(TeamWorkdLoadType.BASE);

                if (HCF.getPlugin().getWorkLoadQueue().getCurrentWorkloads().contains(runnable)){
                    player.sendMessage(CC.translate("&aYour base is building!"));
                }else{
                    player.sendMessage(CC.translate("&aYour are in position &e" + HCF.getPlugin().getWorkLoadQueue().getQueuePosition(runnable) + " &aof the queue!"));
                }
            }

        }else{
            player.sendMessage(CC.translate("&cYou are not in a team!"));
        }
    }

    @Command(names = {"queue falltrap"}, permission = "")
    public static void falltrap(Player player){
        PlayerTeam team = HCF.getPlugin().getTeamManager().getByPlayer(player.getUniqueId());

        if (team != null) {

            if (team.getWorkloadRunnables().containsKey(TeamWorkdLoadType.FALL_TRAP)){
                TeamWorkload runnable = team.getWorkloadRunnables().get(TeamWorkdLoadType.FALL_TRAP);

                if (HCF.getPlugin().getWorkLoadQueue().getCurrentWorkloads().contains(runnable)){
                    player.sendMessage(CC.translate("&aYour falltrap is building!"));
                }else{
                    player.sendMessage(CC.translate("&aYour are in position &e" + HCF.getPlugin().getWorkLoadQueue().getQueuePosition(runnable) + " &aof the queue!"));
                }
            }

        }else{
            player.sendMessage(CC.translate("&cYou are not in a team!"));
        }
    }

    @Command(names = {"queue size"}, permission = "op")
    public static void size(Player player){
        player.sendMessage(CC.translate("&aThere are &e" + HCF.getPlugin().getWorkLoadQueue().getQueueWorkLoads().size() + " &aitems in the queue!"));
        player.sendMessage(CC.translate("&aThere are &e" + HCF.getPlugin().getWorkLoadQueue().getCurrentWorkloads().size() + " &aitems in the current queue!"));
    }

    @Command(names = {"queue view"}, permission = "op")
    public static void view(Player player){
        List<ScheduleWorkLoad> currentWorkload = HCF.getPlugin().getWorkLoadQueue().getCurrentWorkloads();

        player.sendMessage(CC.translate("&fTeams in currently &aactive: "));

        for (ScheduleWorkLoad scheduleWorkLoad : currentWorkload) {
            if (scheduleWorkLoad instanceof TeamWorkload){
                TeamWorkload teamWorkload = (TeamWorkload) scheduleWorkLoad;

                player.sendMessage(CC.translate("&6&l┃ &f" + teamWorkload.getTeam().getName()));
            }
        }
    }

    @Command(names = {"queue cancel", "base cancel", "falltrap cancel", "queue leave"}, permission = "")
    public static void cancel(Player player){
        PlayerTeam team = HCF.getPlugin().getTeamManager().getByPlayer(player.getUniqueId());

        if (team != null) {

            TeamWorkload runnable;
            if (team.getWorkloadRunnables().containsKey(TeamWorkdLoadType.FALL_TRAP)){
                runnable = team.getWorkloadRunnables().get(TeamWorkdLoadType.FALL_TRAP);
            }else if (team.getWorkloadRunnables().containsKey(TeamWorkdLoadType.BASE)) {
                runnable = team.getWorkloadRunnables().get(TeamWorkdLoadType.BASE);
            }else{
                if (queue.getCurrentWorkloads().contains(team.getWorkloadRunnables().get(TeamWorkdLoadType.BASE))) {
                    player.sendMessage(CC.translate("&aYou base is currently being built!"));
                    return;
                }else if (queue.getCurrentWorkloads().contains(team.getWorkloadRunnables().get(TeamWorkdLoadType.FALL_TRAP))) {
                    player.sendMessage(CC.translate("&aYou falltrap is currently being built!"));
                    return;
                }

                player.sendMessage(CC.translate("&cYou are not in a queue!"));
                return;
            }

            if (HCF.getPlugin().getWorkLoadQueue().getQueueWorkLoads().contains(runnable)) {
                HCF.getPlugin().getWorkLoadQueue().getQueueWorkLoads().remove(runnable);
                team.getWorkloadRunnables().remove(runnable.getType());
                player.sendMessage(CC.translate("&aYou have left the queue!"));
            }

        }else{
            player.sendMessage(CC.translate("&cYou are not in a team!"));
        }

    }

}
