package dev.razorni.core.util;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public class Request {

    private final Clickable requestMessage;

    private static final List<Request> requests = Lists.newArrayList();
    private final UUID requester;
    private final UUID requested;
    private final long endAt;
    private Consumer<List<Player>> action;

    public boolean isExpired() {
        return endAt < System.currentTimeMillis();
    }

    public void addAction(Consumer<List<Player>> action) {
        this.action = action;
    }

    public void execute() {

        requests.remove(this);
        if (isExpired()) {
            getRequesterPlayer().sendMessage("&cThe request has expired.");
            return;
        }

        Player requester = getRequesterPlayer();
        Player requested = getRequestedPlayer();

        if (requester == null || requested == null) {
            return;
        }

        action.accept(Lists.newArrayList(requested, requester));
    }

    public Player getRequesterPlayer() {
        return Bukkit.getPlayer(requester);
    }

    public Player getRequestedPlayer() {
        return Bukkit.getPlayer(requested);
    }

    public static boolean hasRequest(Player player) {
        return requests.stream()
                .anyMatch(
                        request -> request.getRequested() == player.getUniqueId() && !request.isExpired());
    }

    public static boolean hasRequested(Player player) {
        return requests.stream()
                .anyMatch(
                        request -> request.getRequester() == player.getUniqueId() && !request.isExpired());
    }

    public static Request getRequest(Player player) {
        Request requestFound = requests.stream()
                .filter(
                        request -> request.getRequested() == player.getUniqueId())
                .findFirst()
                .orElse(null);

        if (requestFound != null && requestFound.isExpired()) {
            requests.remove(requestFound);
            requestFound = null;
        }

        return requestFound;
    }

    public void send() {
        requests.add(this);
        if (this.getRequestMessage() != null) {
            this.getRequestMessage().sendToPlayer(this.getRequestedPlayer());
            this.getRequestedPlayer()
                    .playSound(this.getRequestedPlayer().getLocation(), Sound.NOTE_PLING, 1, 1);
        }
    }

}