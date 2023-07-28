package net.minecraft.server;

import com.eatthepath.uuid.FastUUID;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;

import java.io.File;
import java.util.Iterator;

public class GameProfileBanList extends JsonList<GameProfile, GameProfileBanEntry> {

    public GameProfileBanList(File file) {
        super(file);
    }

    protected JsonListEntry<GameProfile> a(JsonObject jsonobject) {
        return new GameProfileBanEntry(jsonobject);
    }

    public boolean isBanned(GameProfile gameprofile) {
        return this.d(gameprofile);
    }

    public String[] getEntries() {
        String[] astring = new String[this.e().size()];
        int i = 0;

        GameProfileBanEntry gameprofilebanentry;

        for (Iterator<GameProfileBanEntry> iterator = this.e().values().iterator(); iterator.hasNext(); astring[i++] = gameprofilebanentry.getKey().getName()) {
            gameprofilebanentry = iterator.next();
        }

        return astring;
    }

    protected String b(GameProfile gameprofile) {
        return FastUUID.toString(gameprofile.getId());
    }

    public GameProfile a(String s) {
        Iterator<GameProfileBanEntry> iterator = this.e().values().iterator();

        GameProfileBanEntry gameprofilebanentry;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            gameprofilebanentry = iterator.next();
        } while (!s.equalsIgnoreCase(gameprofilebanentry.getKey().getName()));

        return gameprofilebanentry.getKey();
    }

    protected String a(GameProfile object) {
        return this.b(object);
    }
}
