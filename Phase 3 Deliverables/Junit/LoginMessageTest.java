import org.junit.Test;
import static org.junit.Assert.*;

public class LoginMessageTest {

    @Test
    public void testLoginMessageFields() {
        LoginMessage msg = new LoginMessage(Message.TYPE.LOGIN_CLIENT, "user1", "pass123");

        assertEquals(Message.TYPE.LOGIN_CLIENT, msg.getType());
        assertEquals("user1", msg.getUsername());
        assertEquals("pass123", msg.getPassword());
        assertNull(msg.getSession());  
    }
}
