import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import javax.swing.*;

public class TellerProfileGUITest {

    private boolean logoutCalled = false;
    private boolean loginShown = false;

    // Fake LoginGUI that only tracks visibility
    private class StubLoginGUI extends LoginGUI {
        public StubLoginGUI() {
            super(null);
        }

        @Override
        public void setVisible(boolean visible) {
            loginShown = visible;
        }
    }

    // Subclass the GUI and override the logic instead of replacing the app field
    private class TestableTellerProfileGUI extends TellerProfileGUI {
        public TestableTellerProfileGUI() {
            super(null, null, new StubLoginGUI());
        }

        @Override
        public void dispose() {
            // Prevent actual window disposal during test
        }

        @Override
        public void Login() {
            // Prevent UI launch
        }

        // Override TellerApplication-dependent methods
        @Override
        public void initComponents() {
            super.initComponents();

            // Replace teller info labels with dummy data
            try {
                java.lang.reflect.Field appField = TellerProfileGUI.class.getDeclaredField("app");
                appField.setAccessible(true);
                Object fakeApp = new Object() {
                    public String getTellerName() { return "Test Teller"; }
                    public String getBranch() { return "Test Branch"; }
                    public void logoutTeller() { logoutCalled = true; }
                };
                appField.set(this, fakeApp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private TellerProfileGUI gui;

    @Before
    public void setUp() {
        gui = new TestableTellerProfileGUI();
    }

    @Test
    public void testLogoutButtonCallsLogout() {
        // Simulate clicking the logout button
        for (java.awt.Component c : gui.getContentPane().getComponents()) {
            if (c instanceof JPanel) {
                for (java.awt.Component inner : ((JPanel) c).getComponents()) {
                    if (inner instanceof JButton && ((JButton) inner).getText().contains("Log Out")) {
                        ((JButton) inner).doClick();
                    }
                }
            }
        }

        assertTrue("logoutTeller() should be called", logoutCalled);
        assertTrue("Login GUI should be shown again", loginShown);
    }
}
