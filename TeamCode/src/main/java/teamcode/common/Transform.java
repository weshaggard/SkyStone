package teamcode.common;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;

public class Transform {

    private Vector3 position;
    private Vector3 rotation;

    public Transform(Vector3 position,Vector3 rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public static Transform fromMatrix(OpenGLMatrix matrix){
        throw new RuntimeException("Not yet supported");
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public Vector3 getRotation() {
        return rotation;
    }

    public void setRotation(Vector3 rotation) {
        this.rotation = rotation;
    }

}
