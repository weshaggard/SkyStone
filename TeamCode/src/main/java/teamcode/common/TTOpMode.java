package teamcode.common;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public abstract class TTOpMode extends LinearOpMode {

    private static TTOpMode opMode;

    private List<Timer> timers;

    public static TTOpMode currentOpMode() {
        return opMode;
    }

    @Override
    public final void runOpMode() {
        opMode = this;
        timers = new ArrayList<>();
        onInitialize();
        waitForStart();
        onStart();
        onStop();
        for(Timer timer:timers){
            timer.cancel();
        }
    }

    protected abstract void onInitialize();

    protected abstract void onStart();

    protected abstract void onStop();

    /**
     * Use getNewTimer() instead.
     */
    @Deprecated
    public Timer getTimer(){
        return getNewTimer();
    }

    public Timer getNewTimer() {
        Timer timer = new Timer();
        timers.add(timer);
        return timer;
    }

}
