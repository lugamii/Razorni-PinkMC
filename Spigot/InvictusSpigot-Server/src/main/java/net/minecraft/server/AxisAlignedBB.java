package net.minecraft.server;

public class AxisAlignedBB {
    public double a, b, c, d, e, f;

    public AxisAlignedBB(double d0, double d1, double d2, double d3, double d4, double d5) {
        this.a = Math.min(d0, d3);
        this.b = Math.min(d1, d4);
        this.c = Math.min(d2, d5);
        this.d = Math.max(d0, d3);
        this.e = Math.max(d1, d4);
        this.f = Math.max(d2, d5);
    }

    public AxisAlignedBB(BlockPosition blockposition, BlockPosition blockposition1) {
        this.a = blockposition.getX();
        this.b = blockposition.getY();
        this.c = blockposition.getZ();
        this.d = blockposition1.getX();
        this.e = blockposition1.getY();
        this.f = blockposition1.getZ();
    }

    public AxisAlignedBB a(double d0, double d1, double d2) {
        double d3 = this.a;
        double d4 = this.b;
        double d5 = this.c;
        double d6 = this.d;
        double d7 = this.e;
        double d8 = this.f;
        if (d0 < 0.0) {
            d3 += d0;
        } else if (d0 > 0.0) {
            d6 += d0;
        }
        if (d1 < 0.0) {
            d4 += d1;
        } else if (d1 > 0.0) {
            d7 += d1;
        }
        if (d2 < 0.0) {
            d5 += d2;
        } else if (d2 > 0.0) {
            d8 += d2;
        }
        return new AxisAlignedBB(d3, d4, d5, d6, d7, d8);
    }

    public AxisAlignedBB grow(double d0, double d1, double d2) {
        return new AxisAlignedBB(a - d0, b - d1, c - d2, d + d0, e + d1, f + d2);
    }

    public AxisAlignedBB a(AxisAlignedBB axisalignedbb) {
        return new AxisAlignedBB(Math.min(this.a, axisalignedbb.a), Math.min(this.b, axisalignedbb.b), Math.min(this.c, axisalignedbb.c), Math.max(this.d, axisalignedbb.d), Math.max(this.e, axisalignedbb.e), Math.max(this.f, axisalignedbb.f));
    }

    public static AxisAlignedBB a(double d0, double d1, double d2, double d3, double d4, double d5) {
        return new AxisAlignedBB(Math.min(d0, d3), Math.min(d1, d4), Math.min(d2, d5), Math.max(d1, d4), Math.max(d1, d4), Math.max(d2, d5));
    }

    public AxisAlignedBB c(double d0, double d1, double d2) {
        return new AxisAlignedBB(this.a + d0, this.b + d1, this.c + d2, this.d + d0, this.e + d1, this.f + d2);
    }

    public double a(AxisAlignedBB axisalignedbb, double d0) {
        if (axisalignedbb.e > this.b && axisalignedbb.b < this.e && axisalignedbb.f > this.c && axisalignedbb.c < this.f) {
            double d1;
            if (d0 > 0.0 && axisalignedbb.d <= this.a) {
                double d12 = this.a - axisalignedbb.d;
                if (d12 < d0) {
                    d0 = d12;
                }
            } else if (d0 < 0.0 && axisalignedbb.a >= this.d && (d1 = this.d - axisalignedbb.a) > d0) {
                d0 = d1;
            }
            return d0;
        }
        return d0;
    }

    public double b(AxisAlignedBB axisalignedbb, double d0) {
        if (axisalignedbb.d > this.a && axisalignedbb.a < this.d && axisalignedbb.f > this.c && axisalignedbb.c < this.f) {
            double d1;
            if (d0 > 0.0 && axisalignedbb.e <= this.b) {
                double d12 = this.b - axisalignedbb.e;
                if (d12 < d0) {
                    d0 = d12;
                }
            } else if (d0 < 0.0 && axisalignedbb.b >= this.e && (d1 = this.e - axisalignedbb.b) > d0) {
                d0 = d1;
            }
            return d0;
        }
        return d0;
    }

    public double c(AxisAlignedBB axisalignedbb, double d0) {
        if (axisalignedbb.d > this.a && axisalignedbb.a < this.d && axisalignedbb.e > this.b && axisalignedbb.b < this.e) {
            double d1;
            if (d0 > 0.0 && axisalignedbb.f <= this.c) {
                double d12 = this.c - axisalignedbb.f;
                if (d12 < d0) {
                    d0 = d12;
                }
            } else if (d0 < 0.0 && axisalignedbb.c >= this.f && (d1 = this.f - axisalignedbb.c) > d0) {
                d0 = d1;
            }
            return d0;
        }
        return d0;
    }

    public boolean b(AxisAlignedBB axisalignedbb) {
        return axisalignedbb.d > this.a && axisalignedbb.a < this.d && (axisalignedbb.e > this.b && axisalignedbb.b < this.e && axisalignedbb.f > this.c && axisalignedbb.c < this.f);
    }

    public boolean a(Vec3D vec3d) {
        return vec3d.a > this.a && vec3d.a < this.d && (vec3d.b > this.b && vec3d.b < this.e && vec3d.c > this.c && vec3d.c < this.f);
    }

    public double a() {
        return (d - a + e - b + f - c) / 3.0;
    }

    public AxisAlignedBB shrink(double d0, double d1, double d2) {
        return new AxisAlignedBB(a + d0, b + d1, c + d2, d - d0, e - d1, f - d2);
    }

    public MovingObjectPosition a(Vec3D vec3d, Vec3D vec3d1) {
        Vec3D vec3d2 = vec3d.a(vec3d1, this.a);
        Vec3D vec3d3 = vec3d.a(vec3d1, this.d);
        Vec3D vec3d4 = vec3d.b(vec3d1, this.b);
        Vec3D vec3d5 = vec3d.b(vec3d1, this.e);
        Vec3D vec3d6 = vec3d.c(vec3d1, this.c);
        Vec3D vec3d7 = vec3d.c(vec3d1, this.f);
        if (!this.b(vec3d2)) {
            vec3d2 = null;
        }
        if (!this.b(vec3d3)) {
            vec3d3 = null;
        }
        if (!this.c(vec3d4)) {
            vec3d4 = null;
        }
        if (!this.c(vec3d5)) {
            vec3d5 = null;
        }
        if (!this.d(vec3d6)) {
            vec3d6 = null;
        }
        if (!this.d(vec3d7)) {
            vec3d7 = null;
        }
        Vec3D vec3d8 = null;
        if (vec3d2 != null) {
            vec3d8 = vec3d2;
        }
        if (vec3d3 != null && (vec3d8 == null || vec3d.distanceSquared(vec3d3) < vec3d.distanceSquared(vec3d8))) {
            vec3d8 = vec3d3;
        }
        if (vec3d4 != null && (vec3d8 == null || vec3d.distanceSquared(vec3d4) < vec3d.distanceSquared(vec3d8))) {
            vec3d8 = vec3d4;
        }
        if (vec3d5 != null && (vec3d8 == null || vec3d.distanceSquared(vec3d5) < vec3d.distanceSquared(vec3d8))) {
            vec3d8 = vec3d5;
        }
        if (vec3d6 != null && (vec3d8 == null || vec3d.distanceSquared(vec3d6) < vec3d.distanceSquared(vec3d8))) {
            vec3d8 = vec3d6;
        }
        if (vec3d7 != null && (vec3d8 == null || vec3d.distanceSquared(vec3d7) < vec3d.distanceSquared(vec3d8))) {
            vec3d8 = vec3d7;
        }
        if (vec3d8 == null) {
            return null;
        }
        return new MovingObjectPosition(vec3d8, vec3d8 == vec3d2 ? EnumDirection.WEST : (vec3d8 == vec3d3 ? EnumDirection.EAST : (vec3d8 == vec3d4 ? EnumDirection.DOWN : (vec3d8 == vec3d5 ? EnumDirection.UP : (vec3d8 == vec3d6 ? EnumDirection.NORTH : EnumDirection.SOUTH)))));
    }

    private boolean b(Vec3D vec3d) {
        return vec3d != null && vec3d.b >= this.b && vec3d.b <= this.e && vec3d.c >= this.c && vec3d.c <= this.f;
    }

    private boolean c(Vec3D vec3d) {
        return vec3d != null && vec3d.a >= this.a && vec3d.a <= this.d && vec3d.c >= this.c && vec3d.c <= this.f;
    }

    private boolean d(Vec3D vec3d) {
        return vec3d != null && vec3d.a >= this.a && vec3d.a <= this.d && vec3d.b >= this.b && vec3d.b <= this.e;
    }

    public void d(AxisAlignedBB paramAxisAlignedBB) {
        this.a = paramAxisAlignedBB.a;
        this.b = paramAxisAlignedBB.b;
        this.c = paramAxisAlignedBB.c;
        this.d = paramAxisAlignedBB.d;
        this.e = paramAxisAlignedBB.e;
        this.f = paramAxisAlignedBB.f;
    }

    public String toString() {
        return "box[" + this.a + ", " + this.b + ", " + this.c + " -> " + this.d + ", " + this.e + ", " + this.f + "]";
    }
}