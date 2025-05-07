import org.junit.Test;
import static org.junit.Assert.*;

import java.math.BigDecimal;

public class AccountSummaryTest {

    @Test
    public void testConstructorAndGetters() {
        String id = "ACC456";
        AccountSummary.ACCOUNT_TYPE type = AccountSummary.ACCOUNT_TYPE.SAVING;
        BigDecimal balance = new BigDecimal("1234.56");

        AccountSummary summary = new AccountSummary(id, type, balance);

        assertEquals("ACC456", summary.getID());
        assertEquals(AccountSummary.ACCOUNT_TYPE.SAVING, summary.getType());
        assertEquals("1234.56", summary.getBalance());  // should match string value
    }

    @Test
    public void testZeroBalance() {
        AccountSummary summary = new AccountSummary("ACC000", AccountSummary.ACCOUNT_TYPE.CHECKING, BigDecimal.ZERO);
        assertEquals("0", summary.getBalance());
    }

    @Test
    public void testNegativeBalance() {
        AccountSummary summary = new AccountSummary("ACCNEG", AccountSummary.ACCOUNT_TYPE.CREDIT_LINE, new BigDecimal("-42.99"));
        assertEquals("-42.99", summary.getBalance());
    }
}
