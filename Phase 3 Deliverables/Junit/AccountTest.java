import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountTest {

    private Account testAccount;

    // Simple subclass to allow instantiation
    private class TestAccount extends Account {
        public TestAccount(String id, BigDecimal balance, List<Transaction> history) {
            super(id, balance, history);
        }

        public TestAccount() {
            super();
        }
    }

    @Before
    public void setUp() {
        testAccount = new TestAccount();
        testAccount.setID("ACC001");
    }

    @Test
    public void testInitialBalanceAndHistory() {
        assertEquals(BigDecimal.ZERO, testAccount.getBalance());
        assertTrue(testAccount.getTransactionHistory().isEmpty());
    }

    @Test
    public void testSetIDOnlyOnce() {
        assertEquals("ACC001", testAccount.getID());
        testAccount.setID("NEWID");  // Should not change the ID
        assertEquals("ACC001", testAccount.getID());
    }

    @Test
    public void testAddTransactionUpdatesBalanceAndHistory() {
        Transaction mockTrans = new Transaction("100.00", Transaction.OPERATION.DEPOSIT);
        testAccount.addTransaction(mockTrans);

        assertEquals(new BigDecimal("100.00"), testAccount.getBalance());
        assertEquals(1, testAccount.getTransactionHistory().size());
        assertEquals(mockTrans, testAccount.getTransactionHistory().get(0));
    }
}
