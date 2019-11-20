package teamcode.common;

public class Transform {

    private Vector3D position;
    private Vector3D rotation;

    public Transform(Vector3D position, Vector3D rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public Vector3D getPosition() {
        return position;
    }

    public void setPosition(Vector3D position) {
        this.position = position;
    }

    public Vector3D getRotation() {
        return rotation;
    }

    public void setRotation(Vector3D rotation) {
        this.rotation = rotation;
    }

}
