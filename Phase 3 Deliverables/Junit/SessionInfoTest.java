import org.junit.Test;
import static org.junit.Assert.*;

public class SessionInfoTest {

    @Test
    public void testConstructorAndFields() {
        SessionInfo session = new SessionInfo("testUser", SessionInfo.ROLE.CLIENT);

        assertEquals("testUser", session.getUsername());
        assertEquals(SessionInfo.ROLE.CLIENT, session.getRole());
        assertNotNull(session.getSessionID());
    }

    @Test
    public void testLastActiveSetter() {
        SessionInfo session = new SessionInfo("testUser", SessionInfo.ROLE.TELLER);
        long now = System.currentTimeMillis();

        session.setLastActive(now);
        assertEquals(now, session.getLastActive());
    }
}
