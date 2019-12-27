package teamcode.test.RoadRunner.FtcDashboard.src.main.java.com.acmerobotics.dashboard.message.redux;

import teamcode.test.RoadRunner.FtcDashboard.src.main.java.com.acmerobotics.dashboard.message.Message;
import teamcode.test.RoadRunner.FtcDashboard.src.main.java.com.acmerobotics.dashboard.message.MessageType;
import com.qualcomm.robotcore.hardware.Gamepad;

public class ReceiveGamepadState extends Message {
    private Gamepad gamepad1;
    private Gamepad gamepad2;

    public ReceiveGamepadState() {
        super(MessageType.RECEIVE_GAMEPAD_STATE);
    }

    public Gamepad getGamepad1() {
        return gamepad1;
    }

    public Gamepad getGamepad2() {
        return gamepad2;
    }
}
