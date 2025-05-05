import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter; 
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class LoginGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private final LoginApplication loginApp;
    private JToggleButton tellerBtn, clientBtn;
    private JTextField empField, clientField;
    private JPasswordField empPass, clientPass;
    private JLabel statusLabel; 
    private JPanel cardsPanel;

    public LoginGUI(LoginApplication app) {
        this.loginApp = app;
        this.loginApp.setGUI(this);
        initLookAndFeel();
        initComponents();
    }

    private void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(
                "javax.swing.plaf.nimbus.NimbusLookAndFeel"
            );
        } catch (Exception ignored) {}
    }

    
    private void initComponents() {
        Color bg = Color.decode("#e0fff6");

        setTitle("Bank Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                int choice = JOptionPane.showConfirmDialog(LoginGUI.this, "Are you sure you want to exit the application?", "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                if (choice == JOptionPane.YES_OPTION) {
                    loginApp.exitApplication(); 
                }
        }});

        setSize(450, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        // ----- Layout -----
        getContentPane().setBackground(bg);
        getContentPane().setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel(
            "Welcome to Smith Banking!",
            SwingConstants.CENTER
        );
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.setOpaque(true);
        header.setBackground(bg);
        header.setBorder(new EmptyBorder(20, 0, 20, 0));
        getContentPane().add(header, BorderLayout.NORTH);

        // Toggle buttons
        tellerBtn = new JToggleButton("Teller");
        clientBtn = new JToggleButton("Client");
        tellerBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        clientBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ButtonGroup grp = new ButtonGroup();
        grp.add(tellerBtn); grp.add(clientBtn);
        tellerBtn.setSelected(true);

        Color toggleBg  = Color.decode("#a1d6c7");
        Color toggleSel = Color.decode("#8ebdb0");
        Color toggleFg  = Color.BLACK;
        tellerBtn.setUI(new BasicToggleButtonUI());
        clientBtn.setUI(new BasicToggleButtonUI());
        for (JToggleButton tb : new JToggleButton[]{tellerBtn, clientBtn}) {
            tb.setOpaque(true);
            tb.setContentAreaFilled(true);
            tb.setBorderPainted(false);
            tb.setBackground(toggleBg);
            tb.setForeground(toggleFg);
            tb.addItemListener(e -> {
                JToggleButton source = (JToggleButton) e.getSource();
                source.setBackground(source.isSelected() ? toggleSel : toggleBg);
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String command = source.getActionCommand();
                    CardLayout cl = (CardLayout) (cardsPanel.getLayout());
                    cl.show(cardsPanel, command); 
                }
           });
        }
        tellerBtn.setActionCommand("Teller");
        clientBtn.setActionCommand("Client");

        JPanel togglePanel = new JPanel(new GridLayout(1,2,10,0));
        togglePanel.setOpaque(false);
        togglePanel.setBorder(new EmptyBorder(15,50,15,50));
        togglePanel.add(tellerBtn);
        togglePanel.add(clientBtn);

        // Forms
        JPanel cards = new JPanel(new CardLayout());
        cards.setOpaque(false);
        cards.add(buildForm(
                      "Employee Username:", empField = new JTextField(15),
                      "Password:",             empPass  = new JPasswordField(15)
                  ), "Teller");
        cards.add(buildForm(
                      "Bank Username:",     clientField = new JTextField(15),
                      "Password:",          clientPass  = new JPasswordField(15)
                  ), "Client");
        tellerBtn.addActionListener(e ->
            ((CardLayout)cards.getLayout()).show(cards, "Teller")
        );
        clientBtn.addActionListener(e ->
            ((CardLayout)cards.getLayout()).show(cards, "Client")
        );

        // Action buttons
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.addActionListener(this::executeLoginWorker);
        JButton exitBtn = new JButton("Exit");
        exitBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        exitBtn.addActionListener(e -> loginApp.exitApplication());
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(loginBtn);
        btnPanel.add(exitBtn);

        getRootPane().setDefaultButton(loginBtn);

        // Assemble center
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(10,30,20,30));
        center.add(togglePanel, BorderLayout.NORTH);
        center.add(cards,       BorderLayout.CENTER);
        center.add(btnPanel,    BorderLayout.SOUTH);
        getContentPane().add(center, BorderLayout.CENTER);

    // Builds a two‐row form
    private JPanel buildForm(String lbl1, JTextField tf1,
                             String lbl2, JPasswordField pf2) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.gridx=0; gbc.gridy=0; gbc.anchor=GridBagConstraints.WEST;
        p.add(new JLabel(lbl1), gbc);
        gbc.gridx=1; p.add(tf1, gbc);
        gbc.gridx=0; gbc.gridy=1; p.add(new JLabel(lbl2), gbc);
        gbc.gridx=1; p.add(pf2, gbc);
        return p;
    }

    // Called when you click “Login”
    private void executeLoginWorker(ActionEvent e) {
        boolean isTeller = tellerBtn.isSelected();
        String user = (isTeller ? empField.getText()
                               : clientField.getText()).trim();
        String pass = new String(isTeller
                                 ? empPass.getPassword()
                                 : clientPass.getPassword()
                                ).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "User ID and Password cannot be empty.",
                "Login Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // send to server
        setLoginInProgress(true);

        // Create and execute the SwingWorker
        LoginWorker worker = new LoginWorker(user, pass, isTeller);
        worker.execute();

        // optionally disable inputs until reply...
    }

        private class LoginWorker extends SwingWorker<Message, Void> {
        private final String username;
        private final String password;
        private final boolean isTellerLogin;

        public LoginWorker(String username, String password, boolean isTellerLogin) {
            this.username = username;
            this.password = password;
            this.isTellerLogin = isTellerLogin;
        }

        @Override
        protected Message doInBackground() throws Exception {
            // This runs on a background thread
            // Call the appropriate method in LoginApplication
            // These methods now block and return the result Message
            System.out.println("[LoginGUI] Worker starting login for: " + username); // Debug
            if (isTellerLogin) {
                return loginApp.TellerLogin(username, password);
            } else {
                return loginApp.ClientLogin(username, password);
            }
            // Exceptions thrown here (e.g., connection error during blocking call)
            // will be wrapped in an ExecutionException caught by get() in done()
        }

        @Override
        protected void done() {
            // This runs on the EDT after doInBackground finishes
            System.out.println("Login worker finished."); // Debug
            Message result = null; // Initialize result
            try {
                result = get(); // Get the result (SuccessMessage or FailureMessage)
                System.out.println("Worker result received: " + (result != null ? result.getType() : "null")); // Debug
                // Process the result - null check added for safety
                 if (result != null) {
                    handleAuthResult(result);
                 } else {
                    showError("Login failed: No response received from the application.");
                    resetToLoginScreen();
                 }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("[LoginGUI Error] Login worker interrupted: " + e.getMessage());
                showError("Login process was interrupted.");
                resetToLoginScreen(); 
            } catch (ExecutionException e) {
                // exception occurred within doInBackground()
                System.err.println("[LoginGUI Error] Exception during login execution: " + e.getCause());
                e.getCause().printStackTrace();
                String errorMsg = "An error occurred during login: ";
                if (e.getCause() instanceof IOException) {
                     errorMsg = "Connection error during login. Please check server.";
                } else {
                     errorMsg += e.getCause().getMessage();
                }
                showError(errorMsg);
                resetToLoginScreen();
            } finally {
                 if (isVisible()) { 
                     setLoginInProgress(false);
                 }
            }
        }
    }

    public void handleAuthResult(Message msg) {
        // No need for SwingUtilities.invokeLater here, done() is already on EDT

        if (msg instanceof SuccessMessage successMsg) {
            // Login successful
            setVisible(false); // Hide login window

            SessionInfo session = successMsg.getSession();
            if (session == null) {
                 showError("Login successful but received invalid session from server.");
                 resetToLoginScreen(); // Go back to login
                 setVisible(true); // Show login again
                 return;
            }

            System.out.println("[LoginGUI] Login Success for " + session.getRole() + ": " + session.getUsername());

            // Launch the next appropriate GUI
            if (session.getRole() == SessionInfo.ROLE.TELLER) {
                // --- Teller GUI Launch ---
                TellerApplication tellerApp = loginApp.getTellerApp();
                if (tellerApp != null) {
                     // Assuming TellerProfileGUI constructor takes TellerApplication
                     TellerProfileGUI tellerGui = new TellerProfileGUI(tellerApp); // Adjust constructor if needed
                     tellerGui.display(); // Use display method to ensure EDT
                     dispose(); // Dispose login window fully after launching next
                } else {
                     showError("Login successful but Teller application failed to initialize.");
                     resetToLoginScreen();
                     setVisible(true); // Show login again
                }

            } else if (session.getRole() == SessionInfo.ROLE.CLIENT) {
                // --- Client GUI Launch ---
                ClientProfileApplication clientApp = loginApp.getClientProfileApp();
                if (clientApp != null) {
                    // --- Load Profile in Background BEFORE showing ATM GUI ---
                    loadClientProfileAndShowAtmGui(clientApp);
                    // Login window is disposed by the profile loader worker upon success
                } else {
                    showError("Login successful but Client application failed to initialize.");
                    resetToLoginScreen();
                    setVisible(true); // Show login again
                }

            } else {
                 showError("Login successful but received unknown user role from server.");
                 resetToLoginScreen();
                 setVisible(true); // Show login again
            }

        } else if (msg instanceof FailureMessage failMsg) {
            // Login failed - Show error message from server
            JOptionPane.showMessageDialog(
                this,
                failMsg.getMessage(), // Get message from FailureMessage
                "Login Failed",
                JOptionPane.ERROR_MESSAGE
            );
            resetToLoginScreen(); // Stay on login screen, clear password potentially
        } else {
            // Unexpected message type (could be null if get() failed unexpectedly)
             showError("Received unexpected or null response from server during login.");
             resetToLoginScreen();
        }
    }

    private void loadClientProfileAndShowAtmGui(ClientProfileApplication clientApp) {
        // Update status on the (soon to be hidden) login window
        statusLabel.setText("Login successful. Loading profile...");
        statusLabel.setForeground(Color.BLUE);
        // Keep UI disabled while profile loads
        setLoginInProgress(true);


        SwingWorker<ProfileMessage, Void> profileLoader = new SwingWorker<>() {
            @Override
            protected ProfileMessage doInBackground() throws Exception {
                System.out.println("[LoginGUI] ProfileLoader worker starting profile request..."); // Debug
                // Call the blocking method to request profile data
                // Modify requestProfile in ClientProfileApplication to return ProfileMessage
                // or throw specific exceptions on failure.
                return clientApp.requestProfile();
            }

            @Override
            protected void done() {
                System.out.println("[LoginGUI] ProfileLoader worker finished."); // Debug
                ProfileMessage profileMsg = null;
                try {
                    profileMsg = get(); // Get the loaded profile message
                    if (profileMsg != null) {
                        // Profile loaded successfully, now launch ATM GUI
                        System.out.println("[LoginGUI] Profile loaded successfully, launching ATM GUI."); // Debug
                        ATMProfileGUI atmGui = new ATMProfileGUI(clientApp, profileMsg);
                        atmGui.display(); // Show the ATM GUI
                        dispose(); // Dispose login window AFTER launching ATM GUI
                    } else {
                        // Failed to load profile (requestProfile returned null)
                        showError("Login successful, but failed to load your profile data from the server.");
                        resetToLoginScreen(); // Show login screen again
                        setVisible(true); // Re-show login window as ATM couldn't load
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("[LoginGUI Error] Profile loading worker interrupted: " + e.getMessage());
                    showError("Profile loading was interrupted.");
                    resetToLoginScreen(); setVisible(true);
                } catch (ExecutionException e) {
                    System.err.println("[LoginGUI Error] Exception during profile loading execution: " + e.getCause());
                    e.getCause().printStackTrace();
                    showError("Error loading profile: " + e.getCause().getMessage());
                    resetToLoginScreen(); setVisible(true);
                } finally {
                     // Re-enable login UI if still visible (e.g., if profile load failed)
                     if (isVisible()) {
                          setLoginInProgress(false);
                     }
                }
            }
        };
        profileLoader.execute();
   }


    /**
     * This gets called by your LoginApplication once the
     * ConnectionHandler sees SUCCESS or FAILURE.
     */
   /*  public void handleAuthResult(Message msg) {
        SwingUtilities.invokeLater(() -> {
            if (msg.getType() == Message.TYPE.SUCCESS) {
                // hide login window
                setVisible(false);

                // cast to SuccessMessage
                SuccessMessage success = (SuccessMessage) msg;
                SessionInfo session = success.getSession();

                // launch the next GUI based on role
                if (session.getRole() == SessionInfo.ROLE.TELLER) {
                	TellerApplication tApp = new TellerApplication(loginApp);
                    new TellerProfileGUI(tApp).Login();
                } else {
                	ClientProfileApplication cApp =
                            new ClientProfileApplication(loginApp);      // dummy stub

                    ATMProfileGUI gui =
                            new ATMProfileGUI(cApp, cApp.getProfile());  // needs ProfileMessage
                    gui.setVisible(true);

                }

            } else {
                // FAILURE
                FailureMessage fail = (FailureMessage) msg;
                JOptionPane.showMessageDialog(
                    this,
                    fail.getMessage(),         // <-- getMessage() NOT getText()
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
                );
                // re-enable inputs here if you disabled them
            }
        });
    }*/

   
    private void setLoginInProgress(boolean inProgress) {
        // Run UI updates on the EDT
        SwingUtilities.invokeLater(() -> {
            boolean enabled = !inProgress; 
            loginBtn.setEnabled(enabled);
            exitBtn.setEnabled(enabled); 
            clientBtn.setEnabled(enabled);
            empField.setEnabled(enabled);
            clientField.setEnabled(enabled);
            empPass.setEnabled(enabled);
            clientPass.setEnabled(enabled);

            if (inProgress) {
                statusLabel.setText("Attempting login, please wait...");
                statusLabel.setForeground(Color.BLUE);
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); // Show wait cursor
            } else {
                // statusLabel text is updated by handleAuthResult or resetToLoginScreen
                // statusLabel.setText("Please enter your credentials.");
                // statusLabel.setForeground(Color.GRAY);
                setCursor(Cursor.getDefaultCursor()); // Restore default cursor
            }
        });
    }

    public void resetToLoginScreen() {
        // Ensure UI updates happen on the EDT
        SwingUtilities.invokeLater(() -> {
             // Clear password fields
             empPass.setText("");
             clientPass.setText("");
             // Optionally clear username fields or set focus
             if (clientBtn.isSelected()) {
                  clientField.requestFocusInWindow();
             } else {
                  empField.requestFocusInWindow();
             }
             statusLabel.setText("Login failed. Please try again.");
             statusLabel.setForeground(Color.RED);
             setLoginInProgress(false); // Ensure components are enabled
        });
   }


    public void showError(String msg) {
        JOptionPane.showMessageDialog(
            this, msg, "Error", JOptionPane.ERROR_MESSAGE
        );
    }

    /** Start off the UI */
    public void Login() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }
}
