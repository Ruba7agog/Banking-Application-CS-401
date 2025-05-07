import org.junit.Test;
import static org.junit.Assert.*;

public class TransactionMessageTest {

    @Test
    public void testTransactionFields() {
        SessionInfo mockSession = new SessionInfo();  // replace with a mock if needed
        TransactionMessage msg = new TransactionMessage(mockSession, "100.00", Transaction.OPERATION.DEPOSIT, "ACC123");

        assertEquals(Message.TYPE.TRANSACTION, msg.getType());
        assertEquals("100.00", msg.getAmount());
        assertEquals(Transaction.OPERATION.DEPOSIT, msg.getOperation());
        assertEquals("ACC123", msg.getAccountID());
        assertEquals(mockSession, msg.getSession());
        assertNotNull(msg.getDate());
    }
}
