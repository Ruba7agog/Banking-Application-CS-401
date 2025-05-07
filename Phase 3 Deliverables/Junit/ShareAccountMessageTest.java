import org.junit.Test;
import static org.junit.Assert.*;

public class ShareAccountMessageTest {

    @Test
    public void testFields() {
        SessionInfo session = new SessionInfo();
        ShareAccountMessage msg = new ShareAccountMessage(
            Message.TYPE.SHARE_ACCOUNT,
            session,
            "ownerUser",
            "targetUser",
            "SHARED123"
        );

        assertEquals("ownerUser", msg.getOwnerProfile());
        assertEquals("targetUser", msg.getTargetProfile());
        assertEquals("SHARED123", msg.getSharedAccountID());
        assertEquals(session, msg.getSession());
        assertEquals(Message.TYPE.SHARE_ACCOUNT, msg.getType());
    }
}
