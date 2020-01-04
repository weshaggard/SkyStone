package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

import teamcode.common.AbstractOpMode;
import teamcode.league3.Constants;
import teamcode.league3.DriveSystem;
import teamcode.league3.MoonshotArmSystem;
import teamcode.test.VisionOnInit.SkystonePos;

@Autonomous(name = "Blue Side Auto")
public class BlueSideAuto extends AbstractOpMode {
    //components
    private DriveSystem driveSystem;
    private MoonshotArmSystem arm;
    private VisionOnInit vision;
    //localizer constants
    private static final int INCHES_FROM_THE_WALL_AT_START = 48;
    private static final int INCHES_FROM_THE_FOUNDATION_AT_START = 72;
    private static int inchesLateralFromStart = 0;
    private static int inchesVerticalFromStart = 0;
    //speed constants
    private double VERTICAL_SPEED = 0.6;
    private double LATERAL_SPEED = 0.6;
    private double TURN_SPEED = 0.4;
    private SkystonePos pos;


    @Override
    protected void onInitialize() {
        driveSystem = null;
        arm = new MoonshotArmSystem(this.hardwareMap);
        vision = new VisionOnInit(this.hardwareMap);
        pos = vision.vuforiascan(false, false);
    }

    @Override
    protected void onStart() {
        if (pos == SkystonePos.LEFT) {
            moveToStone(6);
            moveToFoundation(6);
            pseudoArc();
        } else if (pos == SkystonePos.CENTER) {
            moveToStone(5);
            moveToFoundation(5);
            pseudoArc();
        } else {
            moveToStone(4);
            moveToFoundation(4);
            pseudoArc();
            moveToNextStone(1);
        }


    }

    /**
     * supposed to be called when on bridge tape, assumes the stone has not been manipulated
     *
     * @param stoneNum the stone being intaken
     */
    //supposed to be called when on the bridge tape, assumes the stone is in the correct spot
    private void moveToNextStone(int stoneNum) {
        driveSystem.vertical(12 * (48 - (8 * stoneNum)), VERTICAL_SPEED);
        driveSystem.turn(-50, TURN_SPEED);
        driveSystem.vertical(18, VERTICAL_SPEED);


    }

    /**
     * supposed to also be called on the bridge tape, this requires that you know how to get to the stone in question
     * (this is for approximations of how to get stones that were manipulated in an attempt to get the skystone)
     *
     * @param deltaLateral     change in relative x or lateral distance from where the robot is to IMMEDIATELY IN FRONT OF the stone
     * @param deltaVertical    change in relative y or vertical distance from where the robot is to IMMEDIATELY IN FRONT OF the stone
     * @param stoneOrientation the change in angle from a stones default orientation (see comment at the bottom for details), in degrees
     */
    private void MoveToNextStone(double deltaLateral, double deltaVertical, double stoneOrientation) {
        driveSystem.vertical(deltaVertical, VERTICAL_SPEED);
        if (stoneOrientation >= 0 && stoneOrientation < 90) {
            driveSystem.lateral(deltaLateral - Constants.STONE_LENGTH_INCHES * Math.sin(Math.toRadians(stoneOrientation)), LATERAL_SPEED);
            driveSystem.turn(stoneOrientation, TURN_SPEED);
        } else if (stoneOrientation >= 90 && stoneOrientation < 180) {
            driveSystem.vertical(deltaVertical, VERTICAL_SPEED);
            driveSystem.turn(90, TURN_SPEED);
            driveSystem.vertical(deltaLateral, VERTICAL_SPEED);
            driveSystem.lateral(Constants.STONE_LENGTH_INCHES * Math.sin(stoneOrientation), LATERAL_SPEED);
        }
        //intake here
        driveSystem.vertical(Constants.STONE_LENGTH_INCHES, VERTICAL_SPEED);

    }

    @Override
    protected void onStop() {

    }

    //TODO add arm/intake functionality to all of this
    //Only works with First Stone!!, zeroing simply isnt efficient
    private void moveToStone(int stoneNum) {
        int inchesToStoneLateral = INCHES_FROM_THE_WALL_AT_START - (8 * stoneNum);
        driveSystem.lateral(inchesToStoneLateral, LATERAL_SPEED);
        inchesLateralFromStart += inchesToStoneLateral;

        driveSystem.vertical(28, VERTICAL_SPEED);
        driveSystem.turn(45, TURN_SPEED);
        inchesVerticalFromStart += 28;
        sleep(500);
        driveSystem.vertical(-12, VERTICAL_SPEED);
        inchesVerticalFromStart -= 12;
        driveSystem.turn(180, TURN_SPEED);
    }

    private void moveToFoundation(int stoneNum) {
        driveSystem.lateral(INCHES_FROM_THE_FOUNDATION_AT_START + inchesLateralFromStart, 0.6);
        //move arm out
        driveSystem.vertical(-26, VERTICAL_SPEED);
        //grab foundation and score
    }


    private void pseudoArc() {
        for (int i = 0; i < 3; i++) {
            driveSystem.vertical(-7, VERTICAL_SPEED);
            driveSystem.turn(30, TURN_SPEED);
        }
        driveSystem.vertical(32, VERTICAL_SPEED);
    }
}






/*
       stone @ 0 degrees
       |---|
       |   |
       |   |
       |   |
       |---|




       stone @ 90 degrees (270 technically but driveSystem.turn will interpret that as -90)
       |---------|
       |  stone  |
       |---------|





 */