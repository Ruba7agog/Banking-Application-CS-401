import org.junit.Test;
import static org.junit.Assert.*;

public class LogoutMessageTest {

    @Test
    public void testLogoutType() {
        SessionInfo session = new SessionInfo();
        LogoutMessage msg = new LogoutMessage(Message.TYPE.LOGOUT_TELLER, session);

        assertEquals(Message.TYPE.LOGOUT_TELLER, msg.getType());
        assertEquals(session, msg.getSession());
    }
}
