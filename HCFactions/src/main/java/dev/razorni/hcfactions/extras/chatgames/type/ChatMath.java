package dev.razorni.hcfactions.extras.chatgames.type;


import dev.razorni.hcfactions.utils.menuapi.CC;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.chatgames.ChatGame;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ChatMath extends ChatGame {

    public static DecimalFormat DTR_FORMAT2 = new DecimalFormat("0.0");
    private final List<Equation> equations = new ArrayList<>();
    private Equation pickedEquation = null;
    private double tickedTime;

    public ChatMath() {
        for (int i = 0; i < 50; i++) {
            int first = new Random().nextInt(50);
            int second = new Random().nextInt(100);
            if (i <= 15) {
                equations.add(new Equation(first, second, "*"));
            } else if (i <= 30) {
                equations.add(new Equation(first, second, "+"));
            } else if (i <= 40) {
                equations.add(new Equation(first, second, "-"));
            } else if (i <= 45) {
                equations.add(new Equation(first, second, "/"));
            }
        }
    }

    @Override
    public String name() {
        return "MATH";
    }

    @Override
    public void start() {
        this.started = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!started) {
                    cancel();
                    return;
                }
                tickedTime = tickedTime + 0.1;
            }
        }.runTaskTimer(HCF.getPlugin(), 5, 5);

        Equation picked = equations.get((new Random().nextInt(equations.size())));

        this.pickedEquation = picked;

        List<String> format = Arrays.asList(
                " ",
                "&6[Quiz&6] &eAnswer the math to win unique rewards.",
                "     &7➥ &eMath: &9" + picked.getFirstNumber() + " " + picked.getEquationType() + " " + picked.getSecondNumber() + "?",
                " "
        );

        format.forEach(s -> {
            Bukkit.broadcastMessage(CC.translate(s));
        });

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!started)
                    return;
                end();
            }
        }.runTaskLater(HCF.getPlugin(), 20 * 15);

    }

    @Override
    public void end() {

        this.started = false;

        Bukkit.broadcastMessage(CC.translate(" "));
        Bukkit.broadcastMessage(CC.translate("&6[Quiz&6] &eQuiz has end, no one gave right answer."));
        Bukkit.broadcastMessage(CC.translate(" "));

    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (this.started) {
            if (this.pickedEquation != null) {
                try {
                    if (this.pickedEquation.getTotal() == Integer.parseInt(event.getMessage())) {
                        this.started = false;
                        event.setCancelled(true);

                        List<String> winMessage = Arrays.asList(
                                "",
                                "&6[Quiz&6] &eCongrats " + HCF.getPlugin().getRankManager().getRankColor(event.getPlayer()) + event.getPlayer().getName() + " &eon resolving this quiz.",
                                "     &7➥ &eAnswer: &9" + this.pickedEquation.getTotal(),
                                ""
                        );

                        winMessage.forEach(s -> {
                            Bukkit.broadcastMessage(CC.translate(s));
                        });

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                reward(event.getPlayer());
                            }
                        }.runTask(HCF.getPlugin());

                    }
                } catch (NumberFormatException ignored) {

                }
            }
        }
    }

    private void reward(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " 2023 1");
    }

    @AllArgsConstructor
    @Data
    public static class Equation {
        private final int firstNumber;
        private final int secondNumber;
        private final String equationType;

        public int getTotal() {
            if (this.equationType.equalsIgnoreCase("*")) {
                return firstNumber * secondNumber;
            } else if (this.equationType.equalsIgnoreCase("+")) {
                return firstNumber + secondNumber;
            } else if (this.equationType.equalsIgnoreCase("/")) {
                return firstNumber / secondNumber;
            } else if (this.equationType.equalsIgnoreCase("-")) {
                return firstNumber - secondNumber;
            } else {
                return firstNumber * secondNumber;
            }
        }

    }

}