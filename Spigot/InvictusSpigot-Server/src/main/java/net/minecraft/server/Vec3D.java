package net.minecraft.server;

public class Vec3D {
    public final double a;

    public final double b;

    public final double c;

    public Vec3D(double paramDouble1, double paramDouble2, double paramDouble3) {
        if (paramDouble1 == -0.0D)
            paramDouble1 = 0.0D;
        if (paramDouble2 == -0.0D)
            paramDouble2 = 0.0D;
        if (paramDouble3 == -0.0D)
            paramDouble3 = 0.0D;
        this.a = paramDouble1;
        this.b = paramDouble2;
        this.c = paramDouble3;
    }

    public Vec3D(BaseBlockPosition paramBaseBlockPosition) {
        this(paramBaseBlockPosition.getX(), paramBaseBlockPosition.getY(), paramBaseBlockPosition.getZ());
    }

    public Vec3D a() {
        double d = MathHelper.sqrt(this.a * this.a + this.b * this.b + this.c * this.c);
        if (d < 1.0E-4D)
            return new Vec3D(0.0D, 0.0D, 0.0D);
        return new Vec3D(this.a / d, this.b / d, this.c / d);
    }

    public double b(Vec3D paramVec3D) {
        return this.a * paramVec3D.a + this.b * paramVec3D.b + this.c * paramVec3D.c;
    }

    public Vec3D d(Vec3D paramVec3D) {
        return a(paramVec3D.a, paramVec3D.b, paramVec3D.c);
    }

    public Vec3D a(double paramDouble1, double paramDouble2, double paramDouble3) {
        return add(-paramDouble1, -paramDouble2, -paramDouble3);
    }

    public Vec3D e(Vec3D paramVec3D) {
        return add(paramVec3D.a, paramVec3D.b, paramVec3D.c);
    }

    public Vec3D add(double paramDouble1, double paramDouble2, double paramDouble3) {
        return new Vec3D(this.a + paramDouble1, this.b + paramDouble2, this.c + paramDouble3);
    }

    public double distanceSquared(Vec3D paramVec3D) {
        double d1 = paramVec3D.a - this.a;
        double d2 = paramVec3D.b - this.b;
        double d3 = paramVec3D.c - this.c;
        return d1 * d1 + d2 * d2 + d3 * d3;
    }

    public double b() {
        return MathHelper.sqrt(this.a * this.a + this.b * this.b + this.c * this.c);
    }

    public Vec3D a(Vec3D paramVec3D, double paramDouble) {
        double d1 = paramVec3D.a - this.a;
        double d2 = paramVec3D.b - this.b;
        double d3 = paramVec3D.c - this.c;
        if (d1 * d1 < 1.0000000116860974E-7D)
            return null;
        double d4 = (paramDouble - this.a) / d1;
        if (d4 < 0.0D || d4 > 1.0D)
            return null;
        return new Vec3D(this.a + d1 * d4, this.b + d2 * d4, this.c + d3 * d4);
    }

    public Vec3D b(Vec3D paramVec3D, double paramDouble) {
        double d1 = paramVec3D.a - this.a;
        double d2 = paramVec3D.b - this.b;
        double d3 = paramVec3D.c - this.c;
        if (d2 * d2 < 1.0000000116860974E-7D)
            return null;
        double d4 = (paramDouble - this.b) / d2;
        if (d4 < 0.0D || d4 > 1.0D)
            return null;
        return new Vec3D(this.a + d1 * d4, this.b + d2 * d4, this.c + d3 * d4);
    }

    public Vec3D c(Vec3D paramVec3D, double paramDouble) {
        double d1 = paramVec3D.a - this.a;
        double d2 = paramVec3D.b - this.b;
        double d3 = paramVec3D.c - this.c;
        if (d3 * d3 < 1.0000000116860974E-7D)
            return null;
        double d4 = (paramDouble - this.c) / d3;
        if (d4 < 0.0D || d4 > 1.0D)
            return null;
        return new Vec3D(this.a + d1 * d4, this.b + d2 * d4, this.c + d3 * d4);
    }

    public String toString() {
        return "(" + this.a + ", " + this.b + ", " + this.c + ")";
    }

    public Vec3D a(float paramFloat) {
        float f1 = MathHelper.cos(paramFloat);
        float f2 = MathHelper.sin(paramFloat);
        double d2 = this.b * f1 + this.c * f2;
        double d3 = this.c * f1 - this.b * f2;
        return new Vec3D(a, d2, d3);
    }

    public Vec3D b(float paramFloat) {
        float f1 = MathHelper.cos(paramFloat);
        float f2 = MathHelper.sin(paramFloat);
        return new Vec3D(a * f1 + c * f2, b, c * f1 - a * f2);
    }
}
