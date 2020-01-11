package teamcode.league3;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.AbstractOpMode;
import teamcode.common.Vector2D;

@Autonomous(name = "Red Park")
public class RedParkAuto extends AbstractOpMode {

    private static final double SPEED = 1;

    private GPS gps;
    private DriveSystem drive;

    @Override
    protected void onInitialize() {
        Vector2D startPosition = new Vector2D(135, 96);
        double startRotation = -Math.PI / 2;
        gps = new GPS(hardwareMap, startPosition, startRotation);
        drive = new DriveSystem(hardwareMap, gps, startPosition, startRotation);
    }

    @Override
    protected void onStart() {
        drive.goTo(new Vector2D(135, 72), SPEED);
    }

    @Override
    protected void onStop() {
        gps.shutdown();
    }

}
