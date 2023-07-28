package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutScoreboardScore implements Packet<PacketListenerPlayOut> {
    private String a = "";
    private String b = "";
    private int c;
    private EnumScoreboardAction d;

    public PacketPlayOutScoreboardScore(ScoreboardScore score) {
        this.a = score.getPlayerName();
        this.b = score.getObjective().getName();
        this.c = score.getScore();
        this.d = EnumScoreboardAction.CHANGE;
    }

    public PacketPlayOutScoreboardScore(String player) {
        this.a = player;
        this.c = 0;
        this.d = EnumScoreboardAction.REMOVE;
    }

    public PacketPlayOutScoreboardScore(String player, ScoreboardObjective objective) {
        this.a = player;
        this.b = objective.getName();
        this.c = 0;
        this.d = EnumScoreboardAction.REMOVE;
    }

    public PacketPlayOutScoreboardScore() {}

    public String getPlayerName() {
        return this.a;
    }

    public void setPlayerName(String playerName) {
        this.a = playerName;
    }

    public String getObjective() {
        return this.b;
    }

    public void setObjective(String objective) {
        this.b = objective;
    }

    public int getScore() {
        return this.c;
    }

    public void setScore(int score) {
        this.c = score;
    }

    public EnumScoreboardAction getAction() {
        return this.d;
    }

    public void setAction(EnumScoreboardAction action) {
        this.d = action;
    }

    public void a(PacketDataSerializer serializer) throws IOException {
        this.a = serializer.c(40);
        this.d = serializer.<EnumScoreboardAction>a(EnumScoreboardAction.class);
        this.b = serializer.c(16);
        if (this.d != EnumScoreboardAction.REMOVE)
            this.c = serializer.e();
    }

    public void b(PacketDataSerializer serializer) throws IOException {
        serializer.a(this.a);
        serializer.a(this.d);
        serializer.a(this.b);
        if (this.d != EnumScoreboardAction.REMOVE)
            serializer.b(this.c);
    }

    public void a(PacketListenerPlayOut listener) {
        listener.a(this);
    }

    public enum EnumScoreboardAction {
        CHANGE, REMOVE;
    }
}
