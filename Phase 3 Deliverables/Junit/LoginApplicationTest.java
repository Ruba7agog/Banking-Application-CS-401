import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoginApplicationTest {

    private LoginApplication app;
    private String[] errorMsg;
    private boolean[] loginCalled;

    @Before
    public void setUp() {
        app = new LoginApplication();

        // Use array to allow modification in anonymous class
        errorMsg = new String[1];
        loginCalled = new boolean[1];

        // Inject a lightweight anonymous LoginGUI class override
        app.setGUI(new LoginGUI(null) {
            @Override
            public void showError(String msg) {
                errorMsg[0] = msg;
            }

            @Override
            public void Login() {
                loginCalled[0] = true;
            }
        });
    }

    @Test
    public void testHandleConnectionError() {
        app.handleConnectionError("Server down");
        assertEquals("Server down", errorMsg[0]);
    }

    @Test
    public void testHandleSessionTimeout() {
        app.handleSessionTimeout();
        assertEquals("Session timed out due to inactivity.", errorMsg[0]);
        assertTrue(loginCalled[0]);
    }
}
