import org.junit.Test;
import static org.junit.Assert.*;

public class DatabaseTest {

    @Test
    public void testSingletonInstance() {
        Database db1 = Database.getInstance();
        Database db2 = Database.getInstance();

        assertNotNull(db1);
        assertSame(db1, db2); // both should point to the same instance
    }

    @Test
    public void testDefaultNextAccountId() {
        Database db = Database.getInstance();
        int current = db.getNextAccountId();
        db.setNextAccountID(current + 5);
        assertEquals(current + 5, db.getNextAccountId());
    }

    @Test
    public void testClientAndAccountMapsExist() {
        Database db = Database.getInstance();
        assertNotNull(db.getClientDatabase());
        assertNotNull(db.getAccountDatabase());
        assertNotNull(db.getTellerDatabase());
    }
}
