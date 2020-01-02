package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.AbstractOpMode;
import teamcode.league3.DriveSystem;
import teamcode.league3.MoonshotArmSystem;

@Autonomous(name = "Blue Side Auto")
public class BlueSideAuto extends AbstractOpMode {
    DriveSystem driveSystem;
    MoonshotArmSystem armSystem;
    public static final int INCHES_FROM_THE_WALL_AT_START = 48;
    public static final int INCHES_FROM_THE_FOUNDATION_AT_START = 88;
    public static int inchesLateralFromStart = 0;
    public static int inchesVerticalFromStart = 0;
    private  double VERTICAL_SPEED = 0.6;
    private double LATERAL_SPEED = 0.6;

    @Override
    protected void onInitialize(){
        driveSystem = new DriveSystem(hardwareMap);
    }

    @Override
    protected void onStart(){
        moveToStone(6);
        moveToFoundation();

    }

    @Override
    protected void onStop(){

    }

    private void moveToStone(int stoneNum){
        int inchesToStoneLateral = INCHES_FROM_THE_WALL_AT_START - (8 * stoneNum);
        driveSystem.lateral(inchesToStoneLateral, 0.6);
        inchesLateralFromStart += inchesToStoneLateral;
        driveSystem.vertical(28, 0.6);
        inchesVerticalFromStart += 28;
        sleep(500);
        driveSystem.vertical(-12, 0.6);
        inchesVerticalFromStart -= 12;
        driveSystem.turn(180, 0.4);
    }

    private void moveToFoundation(){
        driveSystem.lateral(INCHES_FROM_THE_FOUNDATION_AT_START + inchesLateralFromStart, 0.6);
    }
}
