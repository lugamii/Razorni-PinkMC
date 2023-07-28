package net.minecraft.server;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

public class PacketPlayOutPosition implements Packet<PacketListenerPlayOut> {
    private double a;
    private double b;
    private double c;
    private float d;
    private float e;
    private Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> f;

    public PacketPlayOutPosition() {
    }

    public PacketPlayOutPosition(double var1, double var3, double var5, float var7, float var8, Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> var9) {
        this.a = var1;
        this.b = var3;
        this.c = var5;
        this.d = var7;
        this.e = var8;
        this.f = var9;
    }

    public void a(PacketDataSerializer var1) throws IOException {
        this.a = var1.readDouble();
        this.b = var1.readDouble();
        this.c = var1.readDouble();
        this.d = var1.readFloat();
        this.e = var1.readFloat();
        this.f = PacketPlayOutPosition.EnumPlayerTeleportFlags.a(var1.readUnsignedByte());
    }

    public void b(PacketDataSerializer var1) throws IOException {
        var1.writeDouble(this.a);
        var1.writeDouble(this.b);
        var1.writeDouble(this.c);
        var1.writeFloat(this.d);
        var1.writeFloat(this.e);
        var1.writeByte(PacketPlayOutPosition.EnumPlayerTeleportFlags.a(this.f));
    }

    public void a(PacketListenerPlayOut var1) {
        var1.a(this);
    }

    public static enum EnumPlayerTeleportFlags {
        X(0),
        Y(1),
        Z(2),
        Y_ROT(3),
        X_ROT(4);

        private final int f;

        EnumPlayerTeleportFlags(int var3) {
            this.f = var3;
        }

        public static Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> a(int var0) {
            EnumSet<EnumPlayerTeleportFlags> var1 = EnumSet.noneOf(PacketPlayOutPosition.EnumPlayerTeleportFlags.class);
            for (EnumPlayerTeleportFlags var5 : values()) {
                if (var5.b(var0)) {
                    var1.add(var5);
                }
            }

            return var1;
        }

        public static int a(Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> var0) {
            int var1 = 0;

            PacketPlayOutPosition.EnumPlayerTeleportFlags var3;
            for (Iterator<EnumPlayerTeleportFlags> var2 = var0.iterator(); var2.hasNext(); var1 |= var3.a()) {
                var3 = var2.next();
            }

            return var1;
        }

        private int a() {
            return 1 << this.f;
        }

        private boolean b(int var1) {
            return (var1 & this.a()) == this.a();
        }
    }
}
