package teamcode.state;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.AbstractOpMode;
import teamcode.common.Utils;
import teamcode.common.Vector2D;

@Autonomous(name = "Blue Pull Foundation Simple")
public class BluePullFoundationSimpleAuto extends AbstractOpMode {

    private static final double SPEED = 1;

    private GPS gps;
    private DriveSystem drive;
    private MoonshotArmSystem arm;

    @Override
    protected void onInitialize() {
        Vector2D startPosition = new Vector2D(9, 4 * 26);
        double startRotation = Math.PI;
        gps = new GPS(hardwareMap, startPosition, startRotation);
        drive = new DriveSystem(hardwareMap, gps, startPosition, startRotation);
    }

    @Override
    protected void onStart() {
        // approach
        Vector2D foundationPosition = new Vector2D(15, 144 - 30);
        drive.goTo(foundationPosition, SPEED);

        // grab
        arm.adjustFoundation();
        Utils.sleep(1000);

        // pull
        Vector2D pullPosition = new Vector2D(19, 144 - 30);
        drive.goTo(pullPosition, SPEED);

        // let go
        arm.adjustFoundation();
        Utils.sleep(1000);

        // park
        Vector2D parkPosition = new Vector2D(9, 72);
        drive.goTo(parkPosition, SPEED);
    }

    @Override
    protected void onStop() {
        gps.shutdown();
    }

}
