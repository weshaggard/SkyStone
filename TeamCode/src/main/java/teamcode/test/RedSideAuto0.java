package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.AbstractOpMode;
import teamcode.common.Vector2D;
import teamcode.league3.DriveSystem;
import teamcode.league3.GPS;

@Autonomous(name = "Red Side Auto 0")
public class RedSideAuto0 extends AbstractOpMode {

    private static final double SPEED = 1;

    private DriveSystem driveSystem;

    @Override
    protected void onInitialize() {
        Vector2D startPosition = new Vector2D(5 * 24, 2 * 24);
        double startRotation = Math.PI;
        GPS gps = new GPS(hardwareMap, startPosition, startRotation);
        driveSystem = new DriveSystem(hardwareMap, gps, startPosition, startRotation);
    }

    @Override
    protected void onStart() {
        driveSystem.goTo(new Vector2D(3.5 * 24, 2 * 24), SPEED);
        driveSystem.goTo(new Vector2D(4.5 * 24, 2 * 24), SPEED);
        //driveSystem.setRotation(Math.PI / 2, SPEED);
        driveSystem.goTo(new Vector2D(4.5 * 24, 5 * 24), SPEED);
//        driveSystem.setRotation(Math.PI, SPEED);
        driveSystem.goTo(new Vector2D(3 * 24, 5 * 24), SPEED);
        driveSystem.goTo(new Vector2D(4.5 * 24, 5 * 24), SPEED);
  //      driveSystem.setRotation(Math.PI / 2, SPEED);
        driveSystem.goTo(new Vector2D(4.5 * 24, 2 * 24), SPEED);
    //    driveSystem.setRotation(Math.PI, SPEED);
    }

    @Override
    protected void onStop() {

    }
}
