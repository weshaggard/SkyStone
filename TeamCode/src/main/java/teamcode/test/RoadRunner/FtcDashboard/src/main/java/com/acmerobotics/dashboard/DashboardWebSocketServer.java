package teamcode.test.RoadRunner.FtcDashboard.src.main.java.com.acmerobotics.dashboard;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.

/**
 * WebSocket server that handles dashboard client connections.
 */
public class DashboardWebSocketServer extends NanoWSD {
    private static final int PORT = 8000;
    
    private FtcDashboard dashboard;
    
    DashboardWebSocketServer(FtcDashboard dashboard) {
        super(PORT);
        this.dashboard = dashboard;
    }

    @Override
    protected WebSocket openWebSocket(NanoHTTPD.IHTTPSession handshake) {
        return new DashboardWebSocket(handshake, dashboard);
    }

}
