import org.junit.Test;
import static org.junit.Assert.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class AccountMessageTest {

    // Dummy versions to prevent compile errors — make sure these exist in your project too
    public static class SessionInfo {}
    public static class Transaction {}

    @Test
    public void testCreateRequestConstructor() {
        SessionInfo session = new SessionInfo();
        AccountMessage msg = new AccountMessage(TYPE.LOAD_ACCOUNT, session, "johndoe", "acc123");

        assertEquals("johndoe", msg.getUsername());
        assertEquals("acc123", msg.getID());
        assertEquals(BigDecimal.ZERO, msg.getBalance());
        assertEquals(0, msg.getWithdrawCount());
        assertEquals(0, msg.getWithdrawLimit());
    }

    @Test
    public void testCreateNewCreditAccount() {
        SessionInfo session = new SessionInfo();
        AccountMessage msg = new AccountMessage(session, "janedoe", AccountMessage.ACCOUNT_TYPE.CREDIT_LINE, "5000", 0);

        assertEquals("janedoe", msg.getUsername());
        assertEquals(BigDecimal.ZERO, msg.getBalance());
        assertEquals(new BigDecimal("5000"), msg.getCreditLimit());
        assertEquals(0, msg.getWithdrawLimit());
    }

    @Test
    public void testCheckingAccountConstructor() {
        SessionInfo session = new SessionInfo();
        List<Transaction> txns = Arrays.asList(); // empty list
        AccountMessage msg = new AccountMessage(TYPE.LOAD_ACCOUNT, session, "bob", "acc456", new BigDecimal("123.45"), txns);

        assertEquals(AccountMessage.ACCOUNT_TYPE.CHECKING, msg.getAccountType());
        assertEquals("acc456", msg.getID());
        assertEquals(new BigDecimal("123.45"), msg.getBalance());
        assertEquals(txns, msg.getTransactionHistory());
    }

    @Test
    public void testSavingsAccountConstructor() {
        SessionInfo session = new SessionInfo();
        List<Transaction> txns = Arrays.asList(); // empty list
        LocalDate today = LocalDate.now();
        AccountMessage msg = new AccountMessage(TYPE.LOAD_ACCOUNT, session, "alice", "acc789", new BigDecimal("789.00"), txns, 2, 5, today);

        assertEquals(AccountMessage.ACCOUNT_TYPE.SAVING, msg.getAccountType());
        assertEquals("acc789", msg.getID());
        assertEquals(new BigDecimal("789.00"), msg.getBalance());
        assertEquals(2, msg.getWithdrawCount());
        assertEquals(5, msg.getWithdrawLimit());
        assertEquals(txns, msg.getTransactionHistory());
    }

    @Test
    public void testCreditLineConstructor() {
        SessionInfo session = new SessionInfo();
        List<Transaction> txns = Arrays.asList(); // empty list
        AccountMessage msg = new AccountMessage(TYPE.LOAD_ACCOUNT, session, "eve", "acc111", new BigDecimal("200.00"), txns, new BigDecimal("1000.00"));

        assertEquals(AccountMessage.ACCOUNT_TYPE.CREDIT_LINE, msg.getAccountType());
        assertEquals("acc111", msg.getID());
        assertEquals(new BigDecimal("200.00"), msg.getBalance());
        assertEquals(new BigDecimal("1000.00"), msg.getCreditLimit());
        assertEquals(txns, msg.getTransactionHistory());
    }
}
