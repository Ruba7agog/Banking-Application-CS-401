import org.junit.Test;
import static org.junit.Assert.*;

public class SuccessMessageTest {

    @Test
    public void testMessageOnlyConstructor() {
        SuccessMessage msg = new SuccessMessage("Operation successful");

        assertEquals(Message.TYPE.SUCCESS, msg.getType());
        assertEquals("Operation successful", msg.getMessage());
        assertNull(msg.getSession());
    }

    @Test
    public void testMessageWithSession() {
        SessionInfo session = new SessionInfo();  // use a stub/mock as needed
        SuccessMessage msg = new SuccessMessage("Success!", session);

        assertEquals("Success!", msg.getMessage());
        assertEquals(session, msg.getSession());
    }
}
