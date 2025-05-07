import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CheckingAccountTest {

    private CheckingAccount account;

    @Before
    public void setUp() {
        account = new CheckingAccount();
        account.setID("CHK001");
    }

    @Test
    public void testConstructorAndID() {
        assertEquals("CHK001", account.getID());
        assertEquals(BigDecimal.ZERO, account.getBalance());
        assertTrue(account.getTransactionHistory().isEmpty());
    }

    @Test
    public void testAddTransaction() {
        Transaction deposit = new Transaction("150.00", Transaction.OPERATION.DEPOSIT);
        account.addTransaction(deposit);

        assertEquals(new BigDecimal("150.00"), account.getBalance());
        assertEquals(1, account.getTransactionHistory().size());
    }
}
