package teamcode.state;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.AbstractOpMode;
import teamcode.common.Utils;
import teamcode.common.Vector2D;

@Autonomous(name = "Red Pull Foundation and Pivot")
public class RedPullFoundationAndPivotAuto extends AbstractOpMode {

    private static final double SPEED = 1;

    private GPS gps;
    private DriveSystem drive;
    private MoonshotArmSystem arm;

    @Override
    protected void onInitialize() {
        Vector2D startPosition = new Vector2D(144 - 9, 105);
        double startRotation = 0;
        gps = new GPS(hardwareMap, startPosition, startRotation);
        drive = new DriveSystem(hardwareMap, gps, startPosition, startRotation);
    }

    @Override
    protected void onStart() {
        // approach
        Vector2D foundationPosition = new Vector2D(144 - 15, 144 - 30);
        drive.goTo(foundationPosition, SPEED);

        // grab
        arm.adjustFoundation();
        Utils.sleep(1000);

        // pull
        Vector2D pivotPosition = new Vector2D(144 - 19, 144 - 30);
        drive.goTo(pivotPosition, SPEED);

        // pivot
        drive.setRotation(-Math.PI / 2, SPEED);
        arm.adjustFoundation();

        // push
        drive.continuous(Vector2D.up().multiply(0.5), 0);
        Utils.sleep(1000);

        // park
        Vector2D parkPosition = new Vector2D(108, 72);
        drive.goTo(parkPosition, SPEED);
    }

    @Override
    protected void onStop() {
        gps.shutdown();
    }

}
