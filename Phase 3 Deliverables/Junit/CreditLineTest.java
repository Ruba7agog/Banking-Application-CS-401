import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.math.BigDecimal;

public class CreditLineTest {

    private CreditLine creditLine;

    @Before
    public void setUp() {
        creditLine = new CreditLine("500.00"); // $500 credit limit
    }

    @Test
    public void testGetCreditLimit() {
        assertEquals(new BigDecimal("500.00"), creditLine.getCreditLimit());
    }

    @Test
    public void testSetCreditLimit() {
        creditLine.setCreditLimit("1000.00");
        assertEquals(new BigDecimal("1000.00"), creditLine.getCreditLimit());
    }

    @Test
    public void testAddTransactionWithinLimit() {
        Transaction deposit = new Transaction("200.00", Transaction.OPERATION.DEPOSIT);
        creditLine.addTransaction(deposit); // Should not throw
        assertEquals(new BigDecimal("200.00"), creditLine.getBalance());
    }

    @Test(expected = IllegalStateException.class)
    public void testAddTransactionExceedsLimit() {
        Transaction withdrawal = new Transaction("-600.00", Transaction.OPERATION.WITHDRAW);
        creditLine.addTransaction(withdrawal); // Should throw exception
    }
}
