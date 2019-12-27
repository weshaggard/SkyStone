package teamcode.test.RoadRunner.FtcDashboard.src.main.java.com.acmerobotics.dashboard.message.redux;

import teamcode.test.RoadRunner.FtcDashboard.src.main.java.com.acmerobotics.dashboard.config.variable.CustomVariable;
import teamcode.test.RoadRunner.FtcDashboard.src.main.java.com.acmerobotics.dashboard.message.Message;
import teamcode.test.RoadRunner.FtcDashboard.src.main.java.com.acmerobotics.dashboard.message.MessageType;

public class SaveConfig extends Message {
    private CustomVariable configDiff;

    public SaveConfig(CustomVariable configDiff) {
        super(MessageType.SAVE_CONFIG);

        this.configDiff = configDiff;
    }

    public CustomVariable getConfigDiff() {
        return configDiff;
    }
}
