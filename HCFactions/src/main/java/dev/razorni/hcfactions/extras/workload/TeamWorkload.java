package dev.razorni.hcfactions.extras.workload;

import cc.invictusgames.ilib.hologram.HologramBuilder;
import cc.invictusgames.ilib.hologram.HologramService;
import cc.invictusgames.ilib.hologram.updating.UpdatingHologram;
import com.google.common.collect.Lists;
import dev.razorni.hcfactions.utils.menuapi.CC;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.extras.workload.type.TeamWorkdLoadType;
import lombok.Getter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This is the basic implementation of a WorkloadRunnable.
 * It processes as many Workloads every tick as the given
 * field MAX_MILLIS_PER_TICK allows.
 */
@Getter
public class TeamWorkload extends ScheduleWorkLoad {

    private static final double MAX_MILLIS_PER_TICK = 2.5;
    private static final int MAX_NANOS_PER_TICK = (int) (MAX_MILLIS_PER_TICK * 1E6);
    private final PlayerTeam team;
    private final TeamWorkdLoadType type;
    private final Consumer<PlayerTeam> onStart;
    private final Consumer<PlayerTeam> onFinish;
    private final LinkedList<Workload> workloadDeque = Lists.newLinkedList();
    private final List<Workload> completeWorkloads = new ArrayList<>();
    private boolean paused = false;
    private final HologramService holograms;

    public TeamWorkload(PlayerTeam team, TeamWorkdLoadType type, Location location, HologramService holograms) {
        this.team = team;
        this.type = type;
        this.holograms = holograms;

        onStart = (t) -> {
            if (type == TeamWorkdLoadType.BASE) {
                team.broadcast(CC.translate("&fStarting to create your &6base"));

                if (location != null) {
                    UpdatingHologram hologram = new HologramBuilder()
                            .at(location.add(0.5, 1, 0.5))
                            .staticHologram()
                            .addLines(status())
                            .updating()
                            .build();


                    hologram.start();
                    hologram.spawn();

                    holograms.save();

                    team.setRegenBaseHologram(hologram);
                }
            }  else if (type == TeamWorkdLoadType.FALL_TRAP) {
                team.broadcast(CC.translate("&eStarting to create your falltrap"));

                if (location != null) {
                    UpdatingHologram hologram = new HologramBuilder()
                            .at(location.add(0.5, 1, 0.5))
                            .staticHologram()
                            .addLines(status())
                            .updating()
                            .build();


                    hologram.start();
                    hologram.spawn();
                    hologram.setId(1);

                    holograms.save();

                    team.setRegenBaseHologram(hologram);
                }
            }
        };

        onFinish = (t) -> {
            if (type == TeamWorkdLoadType.BASE) {
                team.broadcast(CC.translate("&eYour team base has been created"));
                team.getRegenBaseHologram().cancel();
                team.setRegenBaseHologram(null);
            } else if (type == TeamWorkdLoadType.FALL_TRAP) {
                team.broadcast(CC.translate("&eYour falltrap has been created"));
                team.getRegenBaseHologram().cancel();
                team.setRegenBaseHologram(null);
            }
            team.getWorkloadRunnables().remove(type);
        };
    }

    public String status() {
        if (workloadDeque.isEmpty()) {
            return CC.translate("&eBase Progress:&6 Complete");
        }
        return CC.translate("&eFallTrap Progress:&6 " + getPercentage());
    }

    public void addWorkload(Workload workload) {
        this.workloadDeque.add(workload);
    }

    @Override
    public void run() {

        if (paused) {
            return;
        }

        for (int i = 0; i < 3; i++) {

            if (workloadDeque.isEmpty()) {
                break;
            }

            Workload workload = workloadDeque.poll();

            if (workload != null) {
                workload.compute();
                completeWorkloads.add(workload);
            }
        }
    }

    @Override
    public void compute() {
        runTaskTimer(HCF.getPlugin(), 0, 1L);

        onStart.accept(team);
    }

    @Override
    public boolean isFinished() {

        if (workloadDeque.isEmpty()) {
            onFinish.accept(team);

            cancel();

            completeWorkloads.clear();
            return true;
        }

        return false;
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    public String getPercentage() {
        int total = workloadDeque.size() + completeWorkloads.size();

        int percentaje = ((100 * completeWorkloads.size()) / total);

        return percentaje + "%";
    }
}