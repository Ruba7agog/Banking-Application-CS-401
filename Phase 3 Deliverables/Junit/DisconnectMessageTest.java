import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


public class DisconnectMessageTest {

    private DisconnectMessage message;

    @Before
    public void setUp() {
        // Replace with actual way to create SessionInfo if needed
        SessionInfo session = new SessionInfo();  
        message = new DisconnectMessage(session);
    }

    @Test
    public void testMessageTypeIsDisconnect() {
        assertEquals(Message.TYPE.DISCONNECT, message.getType());
    }
}
