import org.junit.Test;
import static org.junit.Assert.*;

public class CentralServerTest {

    @Test
    public void testCentralServerConstruction() {
        CentralServer server = new CentralServer();
        assertNotNull(server);
    }

    @Test
    public void testAccountIdGenerationIncrements() {
        CentralServer server = new CentralServer();
        String id1 = serverTestHelperGenerateId(server);
        String id2 = serverTestHelperGenerateId(server);

        assertNotEquals(id1, id2);
    }

    // Call a private method through a helper (only for testing demo)
    private String serverTestHelperGenerateId(CentralServer server) {
        try {
            var method = CentralServer.class.getDeclaredMethod("generateNewAccountId");
            method.setAccessible(true);
            return (String) method.invoke(server);
        } catch (Exception e) {
            fail("Failed to access generateNewAccountId");
            return null;
        }
    }
}
