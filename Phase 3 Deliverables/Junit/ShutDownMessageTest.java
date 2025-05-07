import org.junit.Test;
import static org.junit.Assert.*;

public class ShutDownMessageTest {

    @Test
    public void testTypeAndSession() {
        ShutDownMessage msg = new ShutDownMessage();

        assertEquals(Message.TYPE.SHUTDOWN, msg.getType());
        assertNull(msg.getSession());
    }
}
