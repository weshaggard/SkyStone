package teamcode.common;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public abstract class AbstractOpMode extends LinearOpMode {

    private static AbstractOpMode opMode;

    public static AbstractOpMode currentOpMode() {
        return opMode;
    }

    @Override
    public final void runOpMode() {
        opMode = this;
        onInitialize();
        waitForStart();
        onStart();
        onStop();
        Debug.clear();
    }

    protected abstract void onInitialize();

    protected abstract void onStart();

    protected abstract void onStop();

}
