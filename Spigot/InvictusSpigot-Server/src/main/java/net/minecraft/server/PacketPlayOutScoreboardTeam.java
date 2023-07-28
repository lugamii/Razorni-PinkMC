package net.minecraft.server;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.Collection;

public class PacketPlayOutScoreboardTeam implements Packet<PacketListenerPlayOut> {
    private String a = "";
    private String b = "";
    private String c = "";
    private String d = "";
    private String e = ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS.e;
    private int f = -1;
    private Collection<String> g = Lists.newArrayList();
    private int h;
    private int i;

    public PacketPlayOutScoreboardTeam(ScoreboardTeam team, int action) {
        this.a = team.getName();
        this.h = action;
        if (action == 0 || action == 2) {
            this.b = team.getDisplayName();
            this.c = team.getPrefix();
            this.d = team.getSuffix();
            this.i = team.packOptionData();
            this.e = (team.getNameTagVisibility()).e;
            this.f = team.l().b();
        }
        if (action == 0)
            this.g.addAll(team.getPlayerNameSet());
    }

    public PacketPlayOutScoreboardTeam(ScoreboardTeam team, Collection<String> players, int action) {
        if (action != 3 && action != 4)
            throw new IllegalArgumentException("Method must be join or leave for player constructor");
        if (players == null || players.isEmpty())
            throw new IllegalArgumentException("Players cannot be null/empty");
        this.h = action;
        this.a = team.getName();
        this.g.addAll(players);
    }

    public void a(PacketDataSerializer serializer) throws IOException {
        this.a = serializer.c(16);
        this.h = serializer.readByte();
        if (this.h == 0 || this.h == 2) {
            this.b = serializer.c(32);
            this.c = serializer.c(16);
            this.d = serializer.c(16);
            this.i = serializer.readByte();
            this.e = serializer.c(32);
            this.f = serializer.readByte();
        }
        if (this.h == 0 || this.h == 3 || this.h == 4) {
            int j = serializer.e();
            for (byte b1 = 0; b1 < j; b1 = (byte)(b1 + 1))
                this.g.add(serializer.c(40));
        }
    }

    public void b(PacketDataSerializer serializer) throws IOException {
        serializer.a(this.a);
        serializer.writeByte(this.h);
        if (this.h == 0 || this.h == 2) {
            serializer.a(this.b);
            serializer.a(this.c);
            serializer.a(this.d);
            serializer.writeByte(this.i);
            serializer.a(this.e);
            serializer.writeByte(this.f);
        }
        if (this.h == 0 || this.h == 3 || this.h == 4) {
            serializer.b(this.g.size());
            for (String str : this.g)
                serializer.a(str);
        }
    }

    public void a(PacketListenerPlayOut listener) {
        listener.a(this);
    }

    public String getName() {
        return this.a;
    }

    public void setName(String name) {
        this.a = name;
    }

    public String getDisplayName() {
        return this.b;
    }

    public void setDisplayName(String displayName) {
        this.b = displayName;
    }

    public String getPrefix() {
        return this.c;
    }

    public void setPrefix(String prefix) {
        this.c = prefix;
    }

    public String getSuffix() {
        return this.d;
    }

    public void setSuffix(String suffix) {
        this.d = suffix;
    }

    public String getNameTagVisibility() {
        return this.e;
    }

    public void setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility visibility) {
        this.e = visibility.e;
    }

    public int getColor() {
        return this.f;
    }

    public void setColor(int color) {
        this.f = color;
    }

    public Collection<String> getPlayers() {
        return this.g;
    }

    public void addPlayers(Collection<String> players) {
        this.g.addAll(players);
    }

    public void setPlayers(Collection<String> players) {
        this.g = players;
    }

    public int getAction() {
        return this.h;
    }

    public void setAction(int action) {
        this.h = action;
    }

    public int getPackOptionData() {
        return this.i;
    }

    public void setPackOptionData(int packOptionData) {
        this.i = packOptionData;
    }

    public PacketPlayOutScoreboardTeam() {}
}
