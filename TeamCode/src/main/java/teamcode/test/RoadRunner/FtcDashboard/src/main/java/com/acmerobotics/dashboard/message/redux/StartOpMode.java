package teamcode.test.RoadRunner.FtcDashboard.src.main.java.com.acmerobotics.dashboard.message.redux;

import teamcode.test.RoadRunner.FtcDashboard.src.main.java.com.acmerobotics.dashboard.message.Message;
import teamcode.test.RoadRunner.FtcDashboard.src.main.java.com.acmerobotics.dashboard.message.MessageType;

public class StartOpMode extends Message {
    public StartOpMode() {
        super(MessageType.START_OP_MODE);
    }
}
