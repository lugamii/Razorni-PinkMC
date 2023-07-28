package net.minecraft.server;

import com.google.common.collect.Lists;
import eu.vortexdev.invictusspigot.util.java.WatchableArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataWatcher {
    
    private static final Object2IntOpenHashMap<Class<?>> classToId = new Object2IntOpenHashMap<>(10, 0.5f);
    
    private final Entity a;
    private boolean b = true;
    private final WatchableArrayMap dataValues = new WatchableArrayMap();
    private final Map<Integer, WatchableObject> d = dataValues; // Old plugins comp
    private boolean e;

    public DataWatcher(Entity entity) {
        this.a = entity;
    }

    public <T> void a(int i, T t0) {
        int integer = classToId.getInt(t0.getClass()); // Spigot

        if (integer == -1) { // Spigot
            throw new IllegalArgumentException("Unknown data type: " + t0.getClass());
        } else if (i > 31) {
            throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is " + 31 + ")");
        } else if (this.dataValues.containsKey(i)) { // Spigot
            throw new IllegalArgumentException("Duplicate id value for " + i + "!");
        } else {
            this.dataValues.put(i, new WatchableObject(integer, i, t0)); // Spigot
            this.b = false;
        }
    }

    public void add(int i, int j) {
        this.dataValues.put(i, new WatchableObject(j, i, null)); // Spigot
        this.b = false;
    }

    public byte getByte(int i) {
        return (byte) this.j(i).b();
    }

    public short getShort(int i) {
        return (short) this.j(i).b();
    }

    public int getInt(int i) {
        return (int) this.j(i).b();
    }

    public float getFloat(int i) {
        return (float) this.j(i).b();
    }

    public String getString(int i) {
        return (String) this.j(i).b();
    }

    public ItemStack getItemStack(int i) {
        return (ItemStack) this.j(i).b();
    }

    private WatchableObject j(int i) {
         return this.dataValues.get(i);
    }

    public Vector3f h(int i) {
        return (Vector3f) this.j(i).b();
    }

    public <T> void watch(int i, T t0) {
        WatchableObject datawatcher_watchableobject = this.j(i);
        if (ObjectUtils.notEqual(t0, datawatcher_watchableobject.b())) {
            datawatcher_watchableobject.a(t0);
            this.a.i(i);
            datawatcher_watchableobject.a(true);
            this.e = true;
        }
    }

    public void update(int i) {
        this.j(i).d = true;
        this.e = true;
    }

    public boolean a() {
        return this.e;
    }

    public static void a(List<WatchableObject> list, PacketDataSerializer packetdataserializer) throws IOException {
        if (list != null) {
            for (WatchableObject watchableObject : list) {
                a(packetdataserializer, watchableObject);
            }
        }

        packetdataserializer.writeByte(127);
    }

    public List<WatchableObject> b() {
        ArrayList<WatchableObject> arraylist = null;

        if (this.e) {
            for (WatchableObject datawatcher_watchableobject : this.dataValues.values()) {
                if (datawatcher_watchableobject.d()) {
                    datawatcher_watchableobject.a(false);
                    if (arraylist == null) {
                        arraylist = Lists.newArrayList();
                    }

                    // Spigot start - copy ItemStacks to prevent ConcurrentModificationExceptions
                    if (datawatcher_watchableobject.b() instanceof ItemStack) {
                        datawatcher_watchableobject = new WatchableObject(
                                datawatcher_watchableobject.c(),
                                datawatcher_watchableobject.a(),
                                ((ItemStack) datawatcher_watchableobject.b()).cloneItemStack()
                        );
                    }
                    // Spigot end

                    arraylist.add(datawatcher_watchableobject);
                }
            }
        }

        this.e = false;
        return arraylist;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        for (WatchableObject watchableObject : this.dataValues.values()) {
            a(packetdataserializer, watchableObject);
        }
        packetdataserializer.writeByte(127);
    }

    public List<WatchableObject> c() {
        ArrayList<WatchableObject> arraylist = Lists.newArrayList(); // Spigot
        arraylist.addAll(this.dataValues.values()); // Spigot
        // Spigot start - copy ItemStacks to prevent ConcurrentModificationExceptions
        for ( int i = 0; i < arraylist.size(); i++ )
        {
            WatchableObject watchableobject = arraylist.get( i );
            if ( watchableobject.b() instanceof ItemStack )
            {
                watchableobject = new WatchableObject(
                        watchableobject.c(),
                        watchableobject.a(),
                        ( (ItemStack) watchableobject.b() ).cloneItemStack()
                );
                arraylist.set( i, watchableobject );
            }
        }
        // Spigot end
        return arraylist;
    }

    private static void a(PacketDataSerializer packetdataserializer, WatchableObject datawatcher_watchableobject) {
        int i = (datawatcher_watchableobject.c() << 5 | datawatcher_watchableobject.a() & 31) & 255;

        packetdataserializer.writeByte(i);
        switch (datawatcher_watchableobject.c()) {
        case 0:
            packetdataserializer.writeByte((byte) datawatcher_watchableobject.b());
            break;

        case 1:
            packetdataserializer.writeShort((short)datawatcher_watchableobject.b());
            break;

        case 2:
            packetdataserializer.writeInt((int) datawatcher_watchableobject.b());
            break;

        case 3:
            packetdataserializer.writeFloat((float) datawatcher_watchableobject.b());
            break;

        case 4:
            packetdataserializer.a((String) datawatcher_watchableobject.b());
            break;

        case 5:
            packetdataserializer.a((ItemStack) datawatcher_watchableobject.b());
            break;

        case 6:
            BlockPosition blockposition = (BlockPosition) datawatcher_watchableobject.b();

            packetdataserializer.writeInt(blockposition.getX());
            packetdataserializer.writeInt(blockposition.getY());
            packetdataserializer.writeInt(blockposition.getZ());
            break;

        case 7:
            Vector3f vector3f = (Vector3f) datawatcher_watchableobject.b();

            packetdataserializer.writeFloat(vector3f.getX());
            packetdataserializer.writeFloat(vector3f.getY());
            packetdataserializer.writeFloat(vector3f.getZ());
        }

    }

    public static List<WatchableObject> b(PacketDataSerializer packetdataserializer) throws IOException {
        ArrayList<WatchableObject> arraylist = null;

        for (byte b0 = packetdataserializer.readByte(); b0 != 127; b0 = packetdataserializer.readByte()) {
            if (arraylist == null) {
                arraylist = Lists.newArrayList();
            }

            int i = (b0 & 224) >> 5;
            int j = b0 & 31;
            WatchableObject datawatcher_watchableobject = null;

            switch (i) {
            case 0:
                datawatcher_watchableobject = new WatchableObject(i, j, packetdataserializer.readByte());
                break;

            case 1:
                datawatcher_watchableobject = new WatchableObject(i, j, packetdataserializer.readShort());
                break;

            case 2:
                datawatcher_watchableobject = new WatchableObject(i, j, packetdataserializer.readInt());
                break;

            case 3:
                datawatcher_watchableobject = new WatchableObject(i, j, packetdataserializer.readFloat());
                break;

            case 4:
                datawatcher_watchableobject = new WatchableObject(i, j, packetdataserializer.c(32767));
                break;

            case 5:
                datawatcher_watchableobject = new WatchableObject(i, j, packetdataserializer.i());
                break;

            case 6:
                int k = packetdataserializer.readInt();
                int l = packetdataserializer.readInt();
                int i1 = packetdataserializer.readInt();

                datawatcher_watchableobject = new WatchableObject(i, j, new BlockPosition(k, l, i1));
                break;

            case 7:
                float f = packetdataserializer.readFloat();
                float f1 = packetdataserializer.readFloat();
                float f2 = packetdataserializer.readFloat();

                datawatcher_watchableobject = new WatchableObject(i, j, new Vector3f(f, f1, f2));
            }

            arraylist.add(datawatcher_watchableobject);
        }

        return arraylist;
    }

    public boolean d() {
        return this.b;
    }

    public void e() {
        this.e = false;
    }

    static {
        // Spigot Start - remove valueOf
        classToId.put(Byte.class, 0);
        classToId.put(Short.class, 1);
        classToId.put(Integer.class, 2);
        classToId.put(Float.class, 3);
        classToId.put(String.class, 4);
        classToId.put(ItemStack.class, 5);
        classToId.put(BlockPosition.class, 6);
        classToId.put(Vector3f.class, 7);
        // Spigot End
    }

    public static class WatchableObject {

        private final int a, b;
        private Object c;
        private boolean d;

        public WatchableObject(int i, int j, Object object) {
            this.b = j;
            this.c = object;
            this.a = i;
            this.d = true;
        }

        public int a() {
            return this.b;
        }

        public void a(Object object) {
            this.c = object;
        }

        public Object b() {
            return this.c;
        }

        public int c() {
            return this.a;
        }

        public boolean d() {
            return this.d;
        }

        public void a(boolean flag) {
            this.d = flag;
        }

        @Override
        public WatchableObject clone() {
            WatchableObject watchableObject = new WatchableObject(this.a, this.b, this.c);
            watchableObject.a(this.d);
            return watchableObject;
        }
    }

}
