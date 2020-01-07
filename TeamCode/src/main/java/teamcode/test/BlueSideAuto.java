package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

import teamcode.common.AbstractOpMode;
import teamcode.common.Vector2D;
import teamcode.league3.Constants;
import teamcode.league3.DriveSystem;
import teamcode.league3.GPS;
import teamcode.league3.MoonshotArmSystem;
import teamcode.test.VisionOnInit.SkystonePos;

@Autonomous(name = "Blue Side Auto")
public class BlueSideAuto extends AbstractOpMode {
    //components
    private DriveSystem driveSystem;
    private MoonshotArmSystem arm;
    private VisionOnInit vision;
    private GPS gps;
    //localizer constants
    private static final int INCHES_FROM_THE_WALL_AT_START = 48;
    private static final int INCHES_FROM_THE_FOUNDATION_AT_START = 72;
    //speed constants
    private double VERTICAL_SPEED = 0.6;
    private double LATERAL_SPEED = 0.6;
    private double TURN_SPEED = 0.4;
    private SkystonePos pos;
    Runnable grabStone = new Runnable() {
        @Override
        public void run() {
            arm.intake(0.7);
        }
    };
    private Thread intake = new Thread(grabStone);
    

    @Override
    protected void onInitialize() {
        //driveSystem = null;
        arm = new MoonshotArmSystem(this.hardwareMap);
        vision = new VisionOnInit(this.hardwareMap);
        Vector2D startPosition = new Vector2D(0, 48);
        double startRotation = Math.PI / 2;
        gps = new GPS(hardwareMap, startPosition, startRotation);
        driveSystem = new DriveSystem(hardwareMap, gps, startPosition, startRotation);
        while(!opModeIsActive()) {
            pos = vision.vuforiascan(false, false);
        }
    }

    @Override
    protected void onStart(){
        if (pos == SkystonePos.LEFT) {
            moveToStone(6);
            moveToFoundation(6);
        } else if (pos == SkystonePos.CENTER) {
            moveToStone(5);
            moveToFoundation(5);
        } else {
            moveToStone(4);
            moveToFoundation(4);

        }
    }
    @Override
    protected void onStop() {

    }


    private void moveToFoundation(int stoneNum) {

    }

    private void moveToStone(int stoneNum) {
        driveSystem.goTo(new Vector2D(24, stoneNum * 8), 0.6);
        driveSystem.setRotation(0,0);


    }

    private void pseudoArc() {
        for (int i = 1; i <= 3; i++) {
            driveSystem.vertical(-7, VERTICAL_SPEED);
            driveSystem.setRotation(Math.toRadians(-30) * i, TURN_SPEED);
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