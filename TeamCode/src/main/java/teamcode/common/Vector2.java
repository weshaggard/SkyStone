package teamcode.common;

/**
 * Represents a 2-dimensional vector.
 */
public final class Vector2 implements Cloneable {

    private double x;
    private double y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2 forward() {
        return new Vector2(0, 1);
    }

    public static Vector2 right() {
        return new Vector2(1, 0);
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

    public void multiply(double scalar) {
        x *= scalar;
        y *= scalar;
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public void normalize() {
        double magnitude = magnitude();
        x /= magnitude;
        y /= magnitude;
    }

    /**
     * @param other, a 2 dimensional vector added to the vector which is the
     * @return a new vector that is the sum of the 2 passed in vectors
     */
    public void add(Vector2 other) {
        x += other.x;
        y += other.y;
    }

    public double dotProduct(Vector2 other) {
        return this.x * other.x + this.y * other.y;
    }

    /**
     * @return the angle in radians
     */
    public double angleBetween(Vector2 other) {
        return Math.acos(this.dotProduct(other) / (this.magnitude() * other.magnitude()));
    }

    public boolean isZero() {
        return x == 0.0 && y == 0.0;
    }

    /**
     * @return the angle in radians from -pi to pi.
     */
    public double getDirection() {
        return Math.atan2(y, x);
    }

    @Override
    public String toString() {
        return String.format("[x=%.1f,y=%.1f]", x, y);
    }

    @Override
    public Vector2 clone() {
        Vector2 clone;
        try {
            clone = (Vector2) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            clone = null;
        }
        return clone;
    }


}