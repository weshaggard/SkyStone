package teamcode.state;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.Timer;
import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.Vector2D;

@Autonomous(name = "Red Park")
public class RedParkAuto extends AbstractOpMode {

    private DriveSystem drive;
    private Timer timer;

    @Override
    protected void onInitialize() {
        drive = new DriveSystem(hardwareMap);
        timer = new Timer();
    }

    @Override
    protected void onStart() {
        TimerTask brakeTask = new TimerTask() {
            @Override
            public void run() {
                drive.brake();
            }
        };
        timer.schedule(brakeTask, 2000);
        drive.continuous(new Vector2D(0, 0.3), 0);
        while (opModeIsActive()) ;
    }

    @Override
    protected void onStop() {
    }


}
