package net.minecraft.server;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class PacketPlayOutEntityMetadata implements Packet<PacketListenerPlayOut> {
    private int a;
    private List<DataWatcher.WatchableObject> b;
    private boolean found = false;

    public PacketPlayOutEntityMetadata(int entityId, DataWatcher watcher, boolean flag) {
        this.a = entityId;
        this.b = flag ? watcher.c() : watcher.b();
    }

    public PacketPlayOutEntityMetadata() {
    }

    // Constructor accepting List of change metadata
    public PacketPlayOutEntityMetadata(int i, List<DataWatcher.WatchableObject> list, boolean flag) {
        this.a = i;
        this.b = list;
    }

    // replaces health with 1.0F
    public PacketPlayOutEntityMetadata obfuscateHealth() {
        Iterator<DataWatcher.WatchableObject> iter = b.iterator();
        found = false;
        while (iter.hasNext()) {
            DataWatcher.WatchableObject watchable = iter.next();
            if (watchable.a() == 6) {
                iter.remove();
                found = true;
            }
        }

        if (found) b.add(new DataWatcher.WatchableObject(3, 6, 1.0F));
        return this;
    }

    public void a(PacketDataSerializer serializer) throws IOException {
        this.a = serializer.e();
        this.b = DataWatcher.b(serializer);
    }

    public void b(PacketDataSerializer serializer) throws IOException {
        serializer.b(this.a);
        DataWatcher.a(this.b, serializer);
    }

    public void a(PacketListenerPlayOut listener) {
        listener.a(this);
    }

    public int getEntityId() {
        return this.a;
    }

    public void setEntityId(int id) {
        this.a = id;
    }

    public List<DataWatcher.WatchableObject> getData() {
        return this.b;
    }

    public boolean didFindHealth() {
        return found;
    }

    public void setData(List<DataWatcher.WatchableObject> data) {
        this.b = data;
    }
}
