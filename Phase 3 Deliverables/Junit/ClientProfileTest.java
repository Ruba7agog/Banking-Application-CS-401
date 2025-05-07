import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;


public class ClientProfileTest {

    @Test
    public void testConstructorAndGetters() {
        ClientProfile client = new ClientProfile("user1", "pass", "123456", "Street 1", "John Doe");
        assertEquals("user1", client.getUsername());
        assertEquals("pass", client.getPassword());
        assertEquals("123456", client.getPhone());
        assertEquals("Street 1", client.getAddress());
        assertEquals("John Doe", client.getLegalName());
    }

    @Test
    public void testAddAccountID() {
        ClientProfile client = new ClientProfile("user1", "pass", "123456", "Street 1", "John Doe");
        client.addAccountID("ACC123");
        List<String> ids = client.getAccountIDs();
        assertTrue(ids.contains("ACC123"));
    }

    @Test
    public void testRemoveAccountID() {
        ClientProfile client = new ClientProfile("user1", "pass", "123456", "Street 1", "John Doe");
        client.addAccountID("ACC123");
        client.removeAccountID("ACC123");
        List<String> ids = client.getAccountIDs();
        assertFalse(ids.contains("ACC123"));
    }
}
