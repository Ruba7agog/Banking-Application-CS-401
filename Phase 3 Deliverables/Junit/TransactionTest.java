import org.junit.Test;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Date;

public class TransactionTest {

    @Test
    public void testConstructorAndFields() {
        Transaction t = new Transaction("100.00", Transaction.OPERATION.DEPOSIT);

        assertEquals(new BigDecimal("100.00"), t.getAmount());
        assertEquals(Transaction.OPERATION.DEPOSIT, t.getOperation());
        assertNotNull(t.getCreated());
    }

    @Test
    public void testDateFormatting() {
        Transaction t = new Transaction("50.00", Transaction.OPERATION.WITHDRAW);
        String dateStr = t.getDate();

        assertNotNull(dateStr);
        assertTrue(dateStr.matches("\\d{2}/\\d{2}/\\d{4}")); // MM/dd/yyyy
    }
}
