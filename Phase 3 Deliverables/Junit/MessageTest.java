import org.junit.Test;
import static org.junit.Assert.*;

public class MessageTest {

    // Concrete subclass of abstract Message for testing
    private static class TestMessage extends Message {
        private static final long serialVersionUID = 1L;

        public TestMessage(TYPE type, SessionInfo session) {
            super(type, session);
        }
    }

    @Test
    public void testGetType() {
        Message msg = new TestMessage(Message.TYPE.SUCCESS, null);
        assertEquals(Message.TYPE.SUCCESS, msg.getType());
    }

    @Test
    public void testGetSession() {
        SessionInfo session = new SessionInfo();  // Only works if default constructor exists
        Message msg = new TestMessage(Message.TYPE.LOGIN_CLIENT, session);
        assertEquals(session, msg.getSession());
    }
}
