import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SavingAccountTest {

    private SavingAccount account;

    @Before
    public void setUp() {
        account = new SavingAccount(2); // custom limit of 2
        account.setID("SAV001");
    }

    @Test
    public void testInitialWithdrawSettings() {
        assertEquals(2, account.getWithdrawLimit());
        assertEquals(0, account.getWithdrawCount());
    }

    @Test
    public void testDepositDoesNotAffectWithdrawCount() {
        Transaction deposit = new Transaction("200.00", Transaction.OPERATION.DEPOSIT);
        account.addTransaction(deposit);

        assertEquals(new BigDecimal("200.00"), account.getBalance());
        assertEquals(0, account.getWithdrawCount());
    }

    @Test
    public void testWithdrawIncreasesCount() {
        Transaction withdraw = new Transaction("50.00", Transaction.OPERATION.WITHDRAW);
        account.addTransaction(withdraw);

        assertEquals(new BigDecimal("50.00"), account.getBalance());
        assertEquals(1, account.getWithdrawCount());
    }

    @Test(expected = IllegalStateException.class)
    public void testWithdrawLimitThrowsException() {
        Transaction w1 = new Transaction("10.00", Transaction.OPERATION.WITHDRAW);
        Transaction w2 = new Transaction("15.00", Transaction.OPERATION.WITHDRAW);
        Transaction w3 = new Transaction("20.00", Transaction.OPERATION.WITHDRAW); // third one should fail

        account.addTransaction(w1);
        account.addTransaction(w2);
        account.addTransaction(w3); // should throw
    }
}
