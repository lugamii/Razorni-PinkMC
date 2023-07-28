package net.minecraft.server;

import com.eatthepath.uuid.FastUUID;
import com.google.common.base.Charsets;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.*;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserCache {

    public static final SimpleDateFormat a = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private final Map<String, UserCacheEntry> c = Maps.newHashMap();
    private final Map<UUID, UserCacheEntry> d = Maps.newHashMap();
    private final java.util.Deque<GameProfile> e = new java.util.concurrent.LinkedBlockingDeque<>(); // CraftBukkit
    private final MinecraftServer f;
    protected final Gson b;
    private final File g;
    private static final ParameterizedType h = new ParameterizedType() {
        public Type[] getActualTypeArguments() {
            return new Type[] { UserCacheEntry.class};
        }

        public Type getRawType() {
            return List.class;
        }

        public Type getOwnerType() {
            return null;
        }
    };

    public UserCache(MinecraftServer minecraftserver, File file) {
        this.f = minecraftserver;
        this.g = file;
        GsonBuilder gsonbuilder = new GsonBuilder();

        gsonbuilder.registerTypeHierarchyAdapter(UserCacheEntry.class, new BanEntrySerializer(null));
        this.b = gsonbuilder.create();
        this.b();
    }

    private static GameProfile a(MinecraftServer minecraftserver, String s) {
        final GameProfile[] agameprofile = new GameProfile[1];
        ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
            public void onProfileLookupSucceeded(GameProfile gameprofile) {
                agameprofile[0] = gameprofile;
            }

            public void onProfileLookupFailed(GameProfile gameprofile, Exception exception) {
                agameprofile[0] = null;
            }
        };

        minecraftserver.getGameProfileRepository().findProfilesByNames(new String[] { s}, Agent.MINECRAFT, profilelookupcallback);
        if (!minecraftserver.getOnlineMode() && agameprofile[0] == null) {
            profilelookupcallback.onProfileLookupSucceeded(new GameProfile(EntityHuman.a(new GameProfile(null, s)), s));
        }

        return agameprofile[0];
    }

    public void a(GameProfile gameprofile) {
        this.a(gameprofile, null);
    }

    private void a(GameProfile gameprofile, Date date) {
        UUID uuid = gameprofile.getId();

        if (date == null) {
            Calendar calendar = Calendar.getInstance();

            calendar.setTime(new Date());
            calendar.add(Calendar.MONTH, 1);
            date = calendar.getTime();
        }

        UserCacheEntry usercache_usercacheentry = new UserCacheEntry(gameprofile, date, null);

        if (this.d.containsKey(uuid)) {
            UserCacheEntry usercache_usercacheentry1 = this.d.get(uuid);

            this.c.remove(usercache_usercacheentry1.a().getName().toLowerCase(Locale.ROOT));
            this.e.remove(gameprofile);
        }

        this.c.put(gameprofile.getName().toLowerCase(Locale.ROOT), usercache_usercacheentry);
        this.d.put(uuid, usercache_usercacheentry);
        this.e.addFirst(gameprofile);
        if( !org.spigotmc.SpigotConfig.saveUserCacheOnStopOnly ) this.c(); // Spigot - skip saving if disabled
    }

    public GameProfile getProfile(String s) {
        String s1 = s.toLowerCase(Locale.ROOT);
        UserCacheEntry usercache_usercacheentry = this.c.get(s1);

        if (usercache_usercacheentry != null && (new Date()).getTime() >= usercache_usercacheentry.c.getTime()) {
            this.d.remove(usercache_usercacheentry.a().getId());
            this.c.remove(usercache_usercacheentry.a().getName().toLowerCase(Locale.ROOT));
            this.e.remove(usercache_usercacheentry.a());
            usercache_usercacheentry = null;
        }

        GameProfile gameprofile;

        if (usercache_usercacheentry != null) {
            gameprofile = usercache_usercacheentry.a();
            this.e.remove(gameprofile);
            this.e.addFirst(gameprofile);
        } else {
            gameprofile = a(this.f, s); // Spigot - use correct case for offline players
            if (gameprofile != null) {
                this.a(gameprofile);
                usercache_usercacheentry = this.c.get(s1);
            }
        }

        if( !org.spigotmc.SpigotConfig.saveUserCacheOnStopOnly ) this.c(); // Spigot - skip saving if disabled
        return usercache_usercacheentry == null ? null : usercache_usercacheentry.a();
    }

    public String[] a() {
        ArrayList<String> arraylist = Lists.newArrayList(this.c.keySet());
        return arraylist.toArray(new String[0]);
    }

    public GameProfile a(UUID uuid) {
        UserCacheEntry usercache_usercacheentry = this.d.get(uuid);

        return usercache_usercacheentry == null ? null : usercache_usercacheentry.a();
    }

    private UserCacheEntry b(UUID uuid) {
        UserCacheEntry usercache_usercacheentry = this.d.get(uuid);

        if (usercache_usercacheentry != null) {
            GameProfile gameprofile = usercache_usercacheentry.a();

            this.e.remove(gameprofile);
            this.e.addFirst(gameprofile);
        }

        return usercache_usercacheentry;
    }

    public void b() {
        BufferedReader bufferedreader = null;

        try {
            bufferedreader = Files.newReader(this.g, Charsets.UTF_8);
            List<UserCacheEntry> list = this.b.fromJson(bufferedreader, UserCache.h);

            this.c.clear();
            this.d.clear();
            this.e.clear();

            for (UserCacheEntry usercache_usercacheentry : Lists.reverse(list)) {
                if (usercache_usercacheentry != null) {
                    this.a(usercache_usercacheentry.a(), usercache_usercacheentry.b());
                }
            }
        } catch (FileNotFoundException filenotfoundexception) {
            // Spigot Start
        } catch (com.google.gson.JsonSyntaxException ex) {
            JsonList.a.warn( "Usercache.json is corrupted or has bad formatting. Deleting it to prevent further issues." );
            this.g.delete();
        // Spigot End
        } catch (JsonParseException ignored) {
        } finally {
            IOUtils.closeQuietly(bufferedreader);
        }

    }

    public void c() {
        String s = this.b.toJson(this.a(org.spigotmc.SpigotConfig.userCacheCap));
        BufferedWriter bufferedwriter = null;

        try {
            bufferedwriter = Files.newWriter(this.g, Charsets.UTF_8);
            bufferedwriter.write(s);
        } catch (IOException ignored) {
        } finally {
            IOUtils.closeQuietly(bufferedwriter);
        }

    }

    private List<UserCacheEntry> a(int i) {
        ArrayList<UserCacheEntry> arraylist = Lists.newArrayList();
        ArrayList<GameProfile> arraylist1 = Lists.newArrayList(Iterators.limit(this.e.iterator(), i));

        for (GameProfile gameProfile : arraylist1) {
            UserCacheEntry usercache_usercacheentry = this.b(gameProfile.getId());

            if (usercache_usercacheentry != null) {
                arraylist.add(usercache_usercacheentry);
            }
        }

        return arraylist;
    }

    static class UserCacheEntry {

        private final GameProfile b;
        private final Date c;

        private UserCacheEntry(GameProfile gameprofile, Date date) {
            this.b = gameprofile;
            this.c = date;
        }

        public GameProfile a() {
            return this.b;
        }

        public Date b() {
            return this.c;
        }

        UserCacheEntry(GameProfile gameprofile, Date date, Object object) {
            this(gameprofile, date);
        }
    }

    static class BanEntrySerializer implements JsonDeserializer<UserCacheEntry>, JsonSerializer<UserCacheEntry> {

        private BanEntrySerializer() {}

        public JsonElement a(UserCacheEntry usercache_usercacheentry, Type type, JsonSerializationContext jsonserializationcontext) {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("name", usercache_usercacheentry.a().getName());
            UUID uuid = usercache_usercacheentry.a().getId();

            jsonobject.addProperty("uuid", uuid == null ? "" : FastUUID.toString(uuid));
            jsonobject.addProperty("expiresOn", UserCache.a.format(usercache_usercacheentry.b()));
            return jsonobject;
        }

        public UserCacheEntry a(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            if (jsonelement.isJsonObject()) {
                JsonObject jsonobject = jsonelement.getAsJsonObject();
                JsonElement jsonelement1 = jsonobject.get("name");
                JsonElement jsonelement2 = jsonobject.get("uuid");
                JsonElement jsonelement3 = jsonobject.get("expiresOn");

                if (jsonelement1 != null && jsonelement2 != null) {
                    String s = jsonelement2.getAsString();
                    String s1 = jsonelement1.getAsString();
                    Date date = null;

                    if (jsonelement3 != null) {
                        try {
                            date = UserCache.a.parse(jsonelement3.getAsString());
                        } catch (ParseException ignored) {
                        }
                    }

                    if (s1 != null && s != null) {
                        UUID uuid;

                        try {
                            uuid = FastUUID.parseUUID(s);
                        } catch (Throwable throwable) {
                            return null;
                        }

                        return new UserCacheEntry(new GameProfile(uuid, s1), date, null);
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

        public JsonElement serialize(UserCacheEntry object, Type type, JsonSerializationContext jsonserializationcontext) { // CraftBukkit - decompile error
            return this.a(object, type, jsonserializationcontext);
        }

        public UserCacheEntry deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException { // CraftBukkit - decompile error
            return this.a(jsonelement, type, jsondeserializationcontext);
        }

        BanEntrySerializer(Object object) {
            this();
        }
    }
}
