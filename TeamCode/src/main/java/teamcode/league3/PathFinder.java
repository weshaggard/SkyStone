package teamcode.league3;

import java.util.LinkedList;
import java.util.Queue;

import teamcode.common.Vector2D;

public class PathFinder {

    private final DriveSystem driveSystem;
    private final Vector2D currentPosition;
    private final Queue<Vector2D> targets;

    public PathFinder(DriveSystem driveSystem, Vector2D currentPosition) {
        this.driveSystem = driveSystem;
        this.currentPosition = currentPosition;
        this.targets = new LinkedList<>();
    }

    public void addTarget(Vector2D target) {
        targets.add(target);
    }

    public void setPower(double power){

    }

}
