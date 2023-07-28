package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutAttachEntity implements Packet<PacketListenerPlayOut> {
    public int a, b, c;

    public PacketPlayOutAttachEntity(int leash, Entity entity, Entity vehicle) {
        this.a = leash;
        this.b = entity.getId();
        this.c = (vehicle != null) ? vehicle.getId() : -1;
    }

    public PacketPlayOutAttachEntity() {
    }

    public int getLeash() {
        return this.a;
    }

    public int getEntityId() {
        return this.b;
    }

    public int getVehicle() {
        return this.c;
    }

    public void a(PacketDataSerializer serializer) throws IOException {
        this.b = serializer.readInt();
        this.c = serializer.readInt();
        this.a = serializer.readUnsignedByte();
    }

    public void b(PacketDataSerializer serializer) throws IOException {
        serializer.writeInt(this.b);
        serializer.writeInt(this.c);
        serializer.writeByte(this.a);
    }

    public void a(PacketListenerPlayOut listener) {
        listener.a(this);
    }
}
