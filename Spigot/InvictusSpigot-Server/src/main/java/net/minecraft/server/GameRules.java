package net.minecraft.server;

import eu.vortexdev.invictusspigot.config.InvictusConfig;

import java.util.Set;
import java.util.TreeMap;

public class GameRules {
    private final TreeMap<String, GameRuleValue> values = new TreeMap<>();

    public GameRules() {
        a("doFireTick", "true", EnumGameRuleType.BOOLEAN_VALUE);
        a("mobGriefing", "true", EnumGameRuleType.BOOLEAN_VALUE);
        a("keepInventory", "false", EnumGameRuleType.BOOLEAN_VALUE);
        a("doMobSpawning", Boolean.toString(InvictusConfig.mobSpawn), EnumGameRuleType.BOOLEAN_VALUE);
        a("doMobLoot", "true", EnumGameRuleType.BOOLEAN_VALUE);
        a("doTileDrops", "true", EnumGameRuleType.BOOLEAN_VALUE);
        a("doEntityDrops", "true", EnumGameRuleType.BOOLEAN_VALUE);
        a("commandBlockOutput", "true", EnumGameRuleType.BOOLEAN_VALUE);
        a("naturalRegeneration", "true", EnumGameRuleType.BOOLEAN_VALUE);
        a("doDaylightCycle", "true", EnumGameRuleType.BOOLEAN_VALUE);
        a("logAdminCommands", "true", EnumGameRuleType.BOOLEAN_VALUE);
        a("showDeathMessages", "true", EnumGameRuleType.BOOLEAN_VALUE);
        a("randomTickSpeed", Integer.toString(InvictusConfig.randomTickSpeed), EnumGameRuleType.NUMERICAL_VALUE);
        a("sendCommandFeedback", "true", EnumGameRuleType.BOOLEAN_VALUE);
        a("reducedDebugInfo", "false", EnumGameRuleType.BOOLEAN_VALUE);
        a("doWeatherCycle", "true", EnumGameRuleType.BOOLEAN_VALUE);
    }

    public void a(String name, String value, EnumGameRuleType type) {
        this.values.put(name, new GameRuleValue(value, type));
    }

    public void set(String name, String defaultValue) {
        GameRuleValue value = this.values.get(name);
        if (value != null) {
            value.a(defaultValue);
        } else {
            a(name, defaultValue, EnumGameRuleType.ANY_VALUE);
        }
    }

    public String get(String name) {
        GameRuleValue value = this.values.get(name);
        return (value != null) ? value.a() : "";
    }

    public boolean getBoolean(String name) {
        GameRuleValue value = this.values.get(name);
        return value != null && value.b();
    }

    public int c(String name) {
        GameRuleValue gameRuleValue = this.values.get(name);
        if (gameRuleValue != null)
            return gameRuleValue.c();
        return 0;
    }

    public NBTTagCompound a() {
        NBTTagCompound compound = new NBTTagCompound();
        for (String name : this.values.keySet())
            compound.setString(name, this.values.get(name).a());
        return compound;
    }

    public void a(NBTTagCompound compound) {
        for (String str1 : compound.c())
            set(str1, compound.getString(str1));
    }

    public String[] getGameRules() {
        Set<String> set = this.values.keySet();
        return set.toArray(new String[0]);
    }

    public boolean contains(String name) {
        return this.values.containsKey(name);
    }

    public boolean a(String name, EnumGameRuleType type) {
        GameRuleValue value = this.values.get(name);
        return (value != null && (value.e() == type || type == EnumGameRuleType.ANY_VALUE));
    }

    public enum EnumGameRuleType {
        ANY_VALUE, BOOLEAN_VALUE, NUMERICAL_VALUE
    }

    static class GameRuleValue {
        private final GameRules.EnumGameRuleType e;
        private String a;
        private boolean b;

        private int c;

        public GameRuleValue(String value, GameRules.EnumGameRuleType type) {
            this.e = type;
            a(value);
        }

        public void a(String value) {
            this.a = value;
            this.b = Boolean.parseBoolean(value);
            this.c = this.b ? 1 : 0;
            try {
                this.c = Integer.parseInt(value);
            } catch (NumberFormatException ignored) {
            }
        }

        public String a() {
            return this.a;
        }

        public boolean b() {
            return this.b;
        }

        public int c() {
            return this.c;
        }

        public GameRules.EnumGameRuleType e() {
            return this.e;
        }
    }
}
