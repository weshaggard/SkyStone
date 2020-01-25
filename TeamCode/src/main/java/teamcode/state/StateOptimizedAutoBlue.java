package teamcode.state;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.security.Policy;
import java.util.Timer;
import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Utils;
import teamcode.common.Vector2D;
import teamcode.state.VisionOnInit.SkystonePos;

@Autonomous(name="Blue auto")
public class StateOptimizedAutoBlue extends AbstractOpMode {
    DriveSystem drive;
    MoonshotArmSystem arm;
    VisionOnInit vision;
    GPS gps;
    SkystonePos pos;
    Timer timer1;
    Timer timer2;
    private final double SPEED = 0.6;
    @Override
    protected void onInitialize() {
        gps = new GPS(hardwareMap, new Vector2D(9, 38.5), 0);
        drive = new DriveSystem(hardwareMap, gps, new Vector2D(9, 38.5), 0);
        vision = new VisionOnInit(hardwareMap);
        arm = new MoonshotArmSystem(hardwareMap);
        timer1 = new Timer();
        timer2 = new Timer();
        while(!opModeIsActive()){
            pos = vision.vuforiascan(false, false);
            Debug.log(pos);
        }
    }

    @Override
    protected void onStart() {

        if(pos == SkystonePos.LEFT){
            intakeStone(1, true);
            arm.primeToScoreAUTO();
            scoreStone( true);
            intakeStone(4, false);
            scoreStone(false);
            park();
        }else if(pos == SkystonePos.CENTER){
            intakeStone(2, true);
            scoreStone( true);
            intakeStone(5, false);
            scoreStone( false);
            park();
        }else if(pos == SkystonePos.RIGHT){
            intakeStone(3, true);
            scoreStone( true);
            intakeStone(6, false);
            scoreStone(false);
            park();
        }
    }

    private void scoreStone( boolean pullFoundation) {
        if(pullFoundation){

            drive.goTo(new Vector2D(30, 114), SPEED);
            drive.setRotation(Math.toRadians(180), SPEED);

            drive.goTo(new Vector2D(40, 114), SPEED / 2.0);
            arm.adjustFoundation();
            arm.scoreAUTO();
            drive.goTo(new Vector2D(18, 114), SPEED);

            drive.setRotation(Math.toRadians(270), SPEED);
            arm.adjustFoundation();
        }else{
            //drive.goTo(new Vector2D());
        }
        drive.goTo(new Vector2D(36, 96), SPEED);
        drive.goTo(parkingSpot, SPEED);
    }

    Vector2D parkingSpot = new Vector2D(36, 72);

    private void intakeStone(int stoneNum, boolean isFirst) {
        Debug.clear();
        Debug.log("Intake Thread Before");

        new Thread(){
            public void run(){
                arm.suck(-1);
                Utils.sleep(50);
                Debug.log("Intake active");
                arm.intakeSequence();

            }
        }.start();
        Debug.log("intake thread after");
        if(stoneNum == 1){
//            drive.goTo(new Vector2D(24, 62), SPEED);
//            drive.setRotation(Math.toRadians(-30), SPEED);
            drive.goTo(new Vector2D(49, 51), 0.4);

        }else{
            drive.goTo(new Vector2D(36, 37 - ((6 - stoneNum) - 2) * 8), SPEED);
            drive.goTo(new Vector2D(57, 37 - ((6 - stoneNum) - 2) * 8), SPEED);
            drive.goTo(new Vector2D(57, 37 - ((6 - stoneNum) - 1) * 8), SPEED);
        }

        Utils.sleep(250);
        drive.goTo(new Vector2D(36, 48), SPEED);
        if(isFirst) {
            drive.setRotation(Math.toRadians(90), SPEED);
        }

    }

    private void park(){
        drive.goTo(parkingSpot, SPEED);
    }

    @Override
    protected void onStop() {
        timer1.cancel();
        timer2.cancel();

    }



}
