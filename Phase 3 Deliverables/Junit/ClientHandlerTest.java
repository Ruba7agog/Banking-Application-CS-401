import org.junit.Test;
import static org.junit.Assert.*;

import java.net.Socket;

public class ClientHandlerTest {

    @Test
    public void testClientHandlerInitialization() {
        Socket dummySocket = new Socket(); // not connected, just for object instantiation
        CentralServer dummyServer = new CentralServer(); // minimal startup
        ClientHandler handler = new ClientHandler(dummySocket, dummyServer);

        assertNotNull(handler);
        assertFalse(handler.isAuthenticated());
    }

    @Test
    public void testSetAuthentication() {
        Socket dummySocket = new Socket();
        CentralServer dummyServer = new CentralServer();
        ClientHandler handler = new ClientHandler(dummySocket, dummyServer);

        handler.setAuthenticated(true);
        assertTrue(handler.isAuthenticated());
    }
}
