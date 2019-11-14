package teamcode.common;

public class Vector3 implements Cloneable {

    private double x;
    private double y;
    private double z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    @Override
    public Vector3 clone() {
        Vector3 clone;
        try {
            clone = (Vector3) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            clone = null;
        }
        return clone;
    }

}
