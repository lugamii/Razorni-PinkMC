package net.minecraft.server;

import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Set;

public class AchievementSet extends ForwardingSet<String> implements IJsonStatistic {

    private final Set<String> a = Sets.newHashSet();

    public AchievementSet() {
    }

    public void a(JsonElement jsonelement) {
        if (jsonelement.isJsonArray()) {

            for (JsonElement jsonelement1 : jsonelement.getAsJsonArray()) {
                this.add(jsonelement1.getAsString());
            }
        }

    }

    public JsonElement a() {
        JsonArray jsonarray = new JsonArray();
        for (String s : this) {
            jsonarray.add(new JsonPrimitive(s));
        }

        return jsonarray;
    }

    protected Set<String> delegate() {
        return this.a;
    }

}
