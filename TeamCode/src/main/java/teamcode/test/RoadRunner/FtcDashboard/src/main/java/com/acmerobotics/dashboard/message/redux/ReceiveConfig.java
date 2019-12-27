package teamcode.test.RoadRunner.FtcDashboard.src.main.java.com.acmerobotics.dashboard.message.redux;


import teamcode.test.RoadRunner.FtcDashboard.src.main.java.com.acmerobotics.dashboard.config.variable.CustomVariable;
import teamcode.test.RoadRunner.FtcDashboard.src.main.java.com.acmerobotics.dashboard.message.Message;
import teamcode.test.RoadRunner.FtcDashboard.src.main.java.com.acmerobotics.dashboard.message.MessageType;

public class ReceiveConfig extends Message {
    private CustomVariable configRoot;

    public ReceiveConfig(CustomVariable configRoot) {
        super(MessageType.RECEIVE_CONFIG);

        this.configRoot = configRoot;
    }
}
