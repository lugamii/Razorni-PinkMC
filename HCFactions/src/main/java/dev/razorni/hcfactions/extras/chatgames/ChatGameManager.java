package dev.razorni.hcfactions.extras.chatgames;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.chatgames.type.ChatMath;
import dev.razorni.hcfactions.extras.chatgames.type.ChatQuestion;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChatGameManager {

    @Getter
    private final List<ChatGame> chatGames;

    public ChatGameManager() {
        chatGames = new ArrayList<>();
        chatGames.add(new ChatQuestion());
        chatGames.add(new ChatMath());

        new BukkitRunnable() {
            @Override
            public void run() {
                ChatGame game = chatGames.get((new Random().nextInt(chatGames.size())));
                game.start();
            }
        }.runTaskTimerAsynchronously(HCF.getPlugin(), 4200, 4200);
    }

}