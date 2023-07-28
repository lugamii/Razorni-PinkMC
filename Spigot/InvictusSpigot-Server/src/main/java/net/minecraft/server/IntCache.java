package net.minecraft.server;

import com.google.common.collect.Lists;
import org.spigotmc.SpigotConfig;

import java.util.List;

public class IntCache {
    private static int a = 256;

    private static final List<int[]> b = Lists.newArrayList();
    private static final List<int[]> c = Lists.newArrayList();
    private static final List<int[]> d = Lists.newArrayList();
    private static final List<int[]> e = Lists.newArrayList();

    public static synchronized int[] a(int i) {
        if (i <= 256) {
            if (b.isEmpty()) {
                int[] arrayOfInt1 = new int[256];
                if (c.size() < SpigotConfig.intCacheLimit)
                    c.add(arrayOfInt1);
                return arrayOfInt1;
            }
            int[] arrayOfInt = b.remove(b.size() - 1);
            if (c.size() < SpigotConfig.intCacheLimit)
                c.add(arrayOfInt);
            return arrayOfInt;
        }
        if (i > a) {
            a = i;
            d.clear();
            e.clear();
            int[] arrayOfInt = new int[a];
            if (0 < SpigotConfig.intCacheLimit)
                e.add(arrayOfInt);
            return arrayOfInt;
        }
        if (d.isEmpty()) {
            int[] arrayOfInt = new int[a];
            if (e.size() < SpigotConfig.intCacheLimit)
                e.add(arrayOfInt);
            return arrayOfInt;
        }
        int[] aint = d.remove(d.size() - 1);
        if (e.size() < SpigotConfig.intCacheLimit)
            e.add(aint);
        return aint;
    }

    public static synchronized void a() {
        if (!d.isEmpty())
            d.remove(d.size() - 1);
        if (!b.isEmpty())
            b.remove(b.size() - 1);
        d.addAll(e);
        b.addAll(c);
        e.clear();
        c.clear();
    }

    public static synchronized String b() {
        return "cache: " + d.size() + ", tcache: " + b.size() + ", allocated: " + e.size() + ", tallocated: " + c.size();
    }
}
