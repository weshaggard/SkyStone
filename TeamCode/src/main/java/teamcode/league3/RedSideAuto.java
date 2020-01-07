package teamcode.league3;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.AbstractOpMode;
import teamcode.common.Vector2D;
import teamcode.league3.VisionOnInit.SkystonePos;


@Autonomous(name = "Red side Auto")
public class RedSideAuto extends AbstractOpMode {

    private static final double UNIVERSAL_GOTO_SPEED = 0.6;
    private static final double ROTATION_SPEED = 0.5;
    private MoonshotArmSystem arm;
    private DriveSystem drive;
    private VisionOnInit vision;
    private GPS gps;

    private SkystonePos pos;

    @Override
    protected void onInitialize() {
        gps = new GPS(hardwareMap, new Vector2D(15.5, 36), 0);
        drive = new DriveSystem(hardwareMap, gps, new Vector2D(15.5, 36), 0);
        vision = new VisionOnInit(hardwareMap);
        arm = new MoonshotArmSystem(hardwareMap);
        while (!opModeIsActive()) {
            pos = vision.vuforiascan(false, true);
        }
    }

    /* general note for people,
    | 6 5 4 3 2 1
    |
    |_ _ _ _ _ _ _ _ _ ____

     */
    @Override
    protected void onStart() {
        new Thread() {
            public void run() {
                while (opModeIsActive()) {
                    telemetry.addData("GPS Pos: ", gps.getPosition());
                    telemetry.addData("GPS rot: ", Math.toDegrees(gps.getRotation()));
                    telemetry.update();

                }
            }
        }.start();
        if (pos == SkystonePos.LEFT) {
            moveToStoneFromStart(1);
        } else if (pos == SkystonePos.CENTER) {
            moveToStoneFromStart(2);
            moveToFoundation();
        } else if (pos == SkystonePos.RIGHT) {
            moveToStoneFromStart(3);
        }
    }

    /**
     * this should "zero out" by the end at the tape line, which is (36, 72)
     */
    private void moveToFoundation() {
        drive.goTo(new Vector2D(36, 72), UNIVERSAL_GOTO_SPEED);
        drive.setRotation(Math.toRadians(-180), ROTATION_SPEED);
        drive.goTo(new Vector2D(50, 120), UNIVERSAL_GOTO_SPEED);
        arm.clampFoundation();
        //arm.score(0.5);
        pseudoArc();
    }

    /**
     * assumes the robot is in the "zero position on the tape
     *
     * @param stoneNum
     */
    private void moveToStoneFromZero(int stoneNum) {

    }

    private void moveToStoneFromStart(int stoneNum) {
        drive.goTo(new Vector2D(45.5, 36 + (8 * stoneNum - 16)), UNIVERSAL_GOTO_SPEED);
        drive.setRotation(Math.toRadians(-90), ROTATION_SPEED);

    }


    //I know you guys want to use strictly goTo() but this is soo much easier, plus it zeros the position by
    private void pseudoArc() {
        for (int i = 1; i <= 3; i++) {
            drive.vertical(-7, UNIVERSAL_GOTO_SPEED);
            drive.setRotation(Math.toRadians(gps.getRotation() - (30 * i)), ROTATION_SPEED);
        }
        drive.goTo(new Vector2D(36, 72), UNIVERSAL_GOTO_SPEED);
    }

    @Override
    protected void onStop() {

    }
}
