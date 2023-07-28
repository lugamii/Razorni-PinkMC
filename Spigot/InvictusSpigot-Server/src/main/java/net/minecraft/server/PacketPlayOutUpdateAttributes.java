package net.minecraft.server;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PacketPlayOutUpdateAttributes implements Packet<PacketListenerPlayOut> {

    private int a;
    private final List<AttributeSnapshot> b = Lists.newArrayList();

    public PacketPlayOutUpdateAttributes() {
    }

    public PacketPlayOutUpdateAttributes(int i, Collection<AttributeInstance> collection) {
        this.a = i;
        for (AttributeInstance attributeinstance : collection) {
            this.b.add(new AttributeSnapshot(attributeinstance.getAttribute().getName(), attributeinstance.b(), attributeinstance.c()));
        }
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e();
        int i = packetdataserializer.readInt();

        for (int j = 0; j < i; ++j) {
            String s = packetdataserializer.c(64);
            double d0 = packetdataserializer.readDouble();
            ArrayList<AttributeModifier> arraylist = Lists.newArrayList();
            int k = packetdataserializer.e();

            for (int l = 0; l < k; ++l) {
                arraylist.add(new AttributeModifier(packetdataserializer.g(), "Unknown synced attribute modifier", packetdataserializer.readDouble(), packetdataserializer.readByte()));
            }

            this.b.add(new AttributeSnapshot(s, d0, arraylist));
        }

    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.b(this.a);
        packetdataserializer.writeInt(this.b.size());

        for (AttributeSnapshot packetplayoutupdateattributes_attributesnapshot : this.b) {
            packetdataserializer.a(packetplayoutupdateattributes_attributesnapshot.a());
            packetdataserializer.writeDouble(packetplayoutupdateattributes_attributesnapshot.b());
            packetdataserializer.b(packetplayoutupdateattributes_attributesnapshot.c().size());

            for (AttributeModifier attributemodifier : packetplayoutupdateattributes_attributesnapshot.c()) {
                packetdataserializer.a(attributemodifier.a());
                packetdataserializer.writeDouble(attributemodifier.d());
                packetdataserializer.writeByte(attributemodifier.c());
            }
        }

    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }


    public class AttributeSnapshot {

        private final String b;
        private final double c;
        private final Collection<AttributeModifier> d;

        public AttributeSnapshot(String s, double d0, Collection<AttributeModifier> collection) {
            this.b = s;
            this.c = d0;
            this.d = collection;
        }

        public String a() {
            return this.b;
        }

        public double b() {
            return this.c;
        }

        public Collection<AttributeModifier> c() {
            return this.d;
        }
    }
}
