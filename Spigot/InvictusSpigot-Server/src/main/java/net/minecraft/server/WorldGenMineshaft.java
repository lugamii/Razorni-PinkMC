package net.minecraft.server;

import java.util.Map;
import java.util.Map.Entry;

public class WorldGenMineshaft extends StructureGenerator {

    private double d = 0.004D;

    public WorldGenMineshaft() {
    }

    public String a() {
        return "Mineshaft";
    }

    public WorldGenMineshaft(Map<String, String> map) {

        for (Entry<String, String> stringStringEntry : map.entrySet()) {

            if (stringStringEntry.getKey().equals("chance")) {
                this.d = MathHelper.a(stringStringEntry.getValue(), this.d);
            }
        }

    }

    protected boolean a(int i, int j) {
        return this.b.nextDouble() < this.d && this.b.nextInt(80) < Math.max(Math.abs(i), Math.abs(j));
    }

    protected StructureStart b(int i, int j) {
        return new WorldGenMineshaftStart(this.c, this.b, i, j);
    }
}
