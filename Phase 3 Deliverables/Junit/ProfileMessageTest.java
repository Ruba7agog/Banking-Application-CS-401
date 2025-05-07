import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Collections;

public class ProfileMessageTest {

    @Test
    public void testLoadProfileConstructor() {
        SessionInfo session = new SessionInfo();
        ProfileMessage msg = new ProfileMessage(Message.TYPE.LOAD_PROFILE, session, "user1");

        assertEquals("user1", msg.getUsername());
        assertEquals("", msg.getPassword());
        assertEquals("", msg.getPhone());
        assertEquals("", msg.getAddress());
        assertEquals("", msg.getLegalName());
        assertNull(msg.getSummaries());
    }

    @Test
    public void testFullConstructor() {
        SessionInfo session = new SessionInfo();
        ProfileMessage msg = new ProfileMessage(
            Message.TYPE.SAVE_PROFILE,
            session,
            "user1",
            "pass123",
            "123-4567",
            "123 Main St",
            "John Doe",
            Collections.emptyList()
        );

        assertEquals("user1", msg.getUsername());
        assertEquals("pass123", msg.getPassword());
        assertEquals("123-4567", msg.getPhone());
        assertEquals("123 Main St", msg.getAddress());
        assertEquals("John Doe", msg.getLegalName());
        assertNotNull(msg.getSummaries());
    }
}
