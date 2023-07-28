package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ServerStatisticManager extends StatisticManager {

    private static final Logger b = LogManager.getLogger();
    private final MinecraftServer c;
    private final File d;
    private final Set<Statistic> e = Sets.newHashSet();
    private int f = -300;
    private boolean g = false;

    public ServerStatisticManager(MinecraftServer minecraftserver, File file) {
        this.c = minecraftserver;
        this.d = file;
        // Spigot start
        for ( String name : org.spigotmc.SpigotConfig.forcedStats.keySet() )
        {
            StatisticWrapper wrapper = new StatisticWrapper();
            wrapper.a( org.spigotmc.SpigotConfig.forcedStats.get( name ) );
            a.put( StatisticList.getStatistic( name ), wrapper );
        }
        // Spigot end
    }

    public void a() {
        if (this.d.isFile()) {
            try {
                this.a.clear();
                this.a.putAll(this.a(FileUtils.readFileToString(this.d)));
            } catch (IOException ioexception) {
                ServerStatisticManager.b.error("Couldn't read statistics file " + this.d, ioexception);
            } catch (JsonParseException jsonparseexception) {
                ServerStatisticManager.b.error("Couldn't parse statistics file " + this.d, jsonparseexception);
            }
        }

    }

    public void b() {
        if ( org.spigotmc.SpigotConfig.disableStatSaving ) return; // Spigot
        try {
            FileUtils.writeStringToFile(this.d, a(this.a));
        } catch (IOException ioexception) {
            ServerStatisticManager.b.error("Couldn't save stats", ioexception);
        }

    }

    public void setStatistic(EntityHuman entityhuman, Statistic statistic, int i) {
        if ( org.spigotmc.SpigotConfig.disableStatSaving ) return; // Spigot
        int j = statistic.d() ? this.getStatisticValue(statistic) : 0;

        super.setStatistic(entityhuman, statistic, i);
        this.e.add(statistic);
        if (statistic.d() && j == 0 && i > 0) {
            this.g = true;
            if (this.c.aB()) {
                this.c.getPlayerList().sendMessage(new ChatMessage("chat.type.achievement", entityhuman.getScoreboardDisplayName(), statistic.j()));
            }
        }

        if (statistic.d() && j > 0 && i == 0) {
            this.g = true;
            if (this.c.aB()) {
                this.c.getPlayerList().sendMessage(new ChatMessage("chat.type.achievement.taken", entityhuman.getScoreboardDisplayName(), statistic.j()));
            }
        }

    }

    public Set<Statistic> c() {
        HashSet<Statistic> hashset = Sets.newHashSet(this.e);

        this.e.clear();
        this.g = false;
        return hashset;
    }

    public Map<Statistic, StatisticWrapper> a(String s) {
        JsonElement jsonelement = (new JsonParser()).parse(s);

        if (!jsonelement.isJsonObject()) {
            return Maps.newHashMap();
        } else {
            JsonObject jsonobject = jsonelement.getAsJsonObject();
            HashMap<Statistic, StatisticWrapper> hashmap = Maps.newHashMap();

            for (Entry<String, JsonElement> entry : jsonobject.entrySet()) {
                Statistic statistic = StatisticList.getStatistic(entry.getKey());

                if (statistic != null) {
                    StatisticWrapper statisticwrapper = new StatisticWrapper();

                    JsonElement element = entry.getValue();
                    if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
                        statisticwrapper.a(element.getAsInt());
                    } else if (element.isJsonObject()) {
                        JsonObject jsonobject1 = element.getAsJsonObject();

                        if (jsonobject1.has("value") && jsonobject1.get("value").isJsonPrimitive() && jsonobject1.get("value").getAsJsonPrimitive().isNumber()) {
                            statisticwrapper.a(jsonobject1.getAsJsonPrimitive("value").getAsInt());
                        }

                        if (jsonobject1.has("progress") && statistic.l() != null) {
                            try {
                                Constructor<? extends IJsonStatistic> constructor = statistic.l().getConstructor();
                                IJsonStatistic ijsonstatistic = constructor.newInstance();

                                ijsonstatistic.a(jsonobject1.get("progress"));
                                statisticwrapper.a(ijsonstatistic);
                            } catch (Throwable throwable) {
                                ServerStatisticManager.b.warn("Invalid statistic progress in " + this.d, throwable);
                            }
                        }
                    }

                    hashmap.put(statistic, statisticwrapper);
                } else {
                    ServerStatisticManager.b.warn("Invalid statistic in " + this.d + ": Don't know what " + entry.getKey() + " is");
                }
            }

            return hashmap;
        }
    }

    public static String a(Map<Statistic, StatisticWrapper> map) {
        JsonObject jsonobject = new JsonObject();

        for (Entry<Statistic, StatisticWrapper> entry : map.entrySet()) {
            if (entry.getValue().b() != null) {
                JsonObject jsonobject1 = new JsonObject();

                jsonobject1.addProperty("value", entry.getValue().a());

                try {
                    jsonobject1.add("progress", entry.getValue().b().a());
                } catch (Throwable throwable) {
                    ServerStatisticManager.b.warn("Couldn't save statistic " + entry.getKey().e() + ": error serializing progress", throwable);
                }

                jsonobject.add(entry.getKey().name, jsonobject1);
            } else {
                jsonobject.addProperty(entry.getKey().name, entry.getValue().a());
            }
        }

        return jsonobject.toString();
    }

    public void d() {

        this.e.addAll(this.a.keySet());

    }

    public void a(EntityPlayer entityplayer) {
        int i = this.c.at();
        HashMap<Statistic, Integer> hashmap = Maps.newHashMap();

        if (this.g || i - this.f > 300) {
            this.f = i;

            for (Statistic statistic : this.c()) {
                hashmap.put(statistic, this.getStatisticValue(statistic));
            }
        }

        entityplayer.playerConnection.sendPacket(new PacketPlayOutStatistic(hashmap));
    }

    public void updateStatistics(EntityPlayer entityplayer) {
        HashMap<Statistic, Integer> hashmap = Maps.newHashMap();

        for (Achievement achievement : AchievementList.e) {
            if (this.hasAchievement(achievement)) {
                hashmap.put(achievement, this.getStatisticValue(achievement));
                this.e.remove(achievement);
            }
        }

        entityplayer.playerConnection.sendPacket(new PacketPlayOutStatistic(hashmap));
    }

    public boolean e() {
        return this.g;
    }
}
