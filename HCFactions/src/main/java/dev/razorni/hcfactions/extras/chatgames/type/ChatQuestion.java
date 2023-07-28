package dev.razorni.hcfactions.extras.chatgames.type;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.chatgames.ChatGame;
import dev.razorni.hcfactions.utils.menuapi.CC;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ChatQuestion extends ChatGame {

    private final List<Question> questions = Arrays.asList(
            new Question("Who is Developer?", Arrays.asList("Razorni", "DevRazorni")),
            new Question("What is 12. letter in the alphabet?", Arrays.asList("l", "L")),
            new Question("What is Green + Red mixed together?", Arrays.asList("yellow", "Yellow")),
            new Question("Who is the main owner of the Network?", Arrays.asList("consealment", "Consealment")),
            new Question("What is German word for Hey?", Arrays.asList("guten tag", "Guten Tag"))
    );
    private Question pickedQuestion = null;
    private double tickedTime;

    @Override
    public String name() {
        return "QUIZ";
    }

    @Override
    public void start() {
        this.started = true;
        this.tickedTime = 0;

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

        Question picked = questions.get((new Random().nextInt(questions.size())));

        this.pickedQuestion = picked;

        List<String> format = Arrays.asList(
                " ",
                "&6[Quiz&6] &eAnswer the question to win unique rewards.",
                "     &7➥ &eQuestion: &9" + picked.getQuestion(),
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
            if (this.pickedQuestion != null) {
                if (this.pickedQuestion.getAnswers().contains(event.getMessage())) {
                    this.started = false;
                    event.setCancelled(true);

                    List<String> winMessage = Arrays.asList(
                            "",
                            "&6[Quiz&6] &eCongrats " + HCF.getPlugin().getRankManager().getRankColor(event.getPlayer()) + event.getPlayer().getName() + " &eon resolving this quiz.",
                            "     &7➥ &eAnswer: &9" + event.getMessage(),
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
            }
        }
    }

    private void reward(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " 2023 1");
    }

    @AllArgsConstructor
    @Data
    public static class Question {
        private final String question;
        private final List<String> answers;
    }

}
