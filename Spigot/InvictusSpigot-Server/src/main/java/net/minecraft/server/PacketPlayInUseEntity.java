package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInUseEntity implements Packet<PacketListenerPlayIn> {
	private int a;

	private EnumEntityUseAction action;

	private Vec3D c;

	public void a(PacketDataSerializer serializer) throws IOException {
		this.a = serializer.e();
		this.action = serializer.<EnumEntityUseAction>a(EnumEntityUseAction.class);
		if (this.action == EnumEntityUseAction.INTERACT_AT)
			this.c = new Vec3D(serializer.readFloat(), serializer.readFloat(), serializer.readFloat());
	}

	public void b(PacketDataSerializer serializer) throws IOException {
		serializer.b(this.a);
		serializer.a(this.action);
		if (this.action == EnumEntityUseAction.INTERACT_AT) {
			serializer.writeFloat((float)this.c.a);
			serializer.writeFloat((float)this.c.b);
			serializer.writeFloat((float)this.c.c);
		}
	}

	public void a(PacketListenerPlayIn listener) {
		listener.a(this);
	}

	public Entity a(World world) {
		return world.a(this.a);
	}

	public int getEntityId() {
		return this.a;
	}

	public EnumEntityUseAction a() {
		return this.action;
	}

	public EnumEntityUseAction getAction() {
		return this.action;
	}

	public Vec3D b() {
		return this.c;
	}

	public enum EnumEntityUseAction {
		INTERACT, ATTACK, INTERACT_AT;
	}
}
