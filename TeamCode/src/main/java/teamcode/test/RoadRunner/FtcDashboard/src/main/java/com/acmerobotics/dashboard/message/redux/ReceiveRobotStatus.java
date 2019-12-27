package teamcode.test.RoadRunner.FtcDashboard.src.main.java.com.acmerobotics.dashboard.message.redux;

import teamcode.test.RoadRunner.FtcDashboard.src.main.java.com.acmerobotics.dashboard.RobotStatus;
import teamcode.test.RoadRunner.FtcDashboard.src.main.java.com.acmerobotics.dashboard.message.Message;
import teamcode.test.RoadRunner.FtcDashboard.src.main.java.com.acmerobotics.dashboard.message.MessageType;

public class ReceiveRobotStatus extends Message {
    private RobotStatus status;

    public ReceiveRobotStatus(RobotStatus robotStatus) {
        super(MessageType.RECEIVE_ROBOT_STATUS);

        status = robotStatus;
    }
}
