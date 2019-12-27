package teamcode.test.RoadRunner.FtcDashboard.src.main.java.com.acmerobotics.dashboard.message.redux;

import teamcode.test.RoadRunner.FtcDashboard.src.main.java.com.acmerobotics.dashboard.message.Message;
import teamcode.test.RoadRunner.FtcDashboard.src.main.java.com.acmerobotics.dashboard.message.MessageType;

public class GetConfig extends Message {
    public GetConfig() {
        super(MessageType.GET_CONFIG);
    }
}
