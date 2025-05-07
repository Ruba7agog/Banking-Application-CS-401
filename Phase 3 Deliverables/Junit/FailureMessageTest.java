import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class FailureMessageTest {

    private FailureMessage failureMessage;

    @Before
    public void setUp() {
        failureMessage = new FailureMessage("Error occurred");
    }

    @Test
    public void testGetMessage() {
        assertEquals("Error occurred", failureMessage.getMessage());
    }

    @Test
    public void testMessageTypeIsFailure() {
        assertEquals(Message.TYPE.FAILURE, failureMessage.getType());
    }
}
