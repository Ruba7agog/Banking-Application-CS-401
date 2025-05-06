import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginGUI extends JFrame implements SessionListener {
    private static final long serialVersionUID = 1L;

    private final LoginApplication loginApp;

    private JToggleButton tellerBtn, clientBtn;
    private JTextField empField, clientField;
    private JPasswordField empPass, clientPass;
    private JButton loginBtn, exitBtn;
    private JPanel cards;

    public LoginGUI() {
        super("Smith Banking Login");
        this.loginApp = new LoginApplication();
        loginApp.setSessionListener(this);
        
        initLookAndFeel();
        initComponents();
    }

    private void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}
    }

    private void initComponents() {
        Color bg = Color.decode("#e0fff6");
        Color toggleBg  = Color.decode("#a1d6c7");
        Color toggleSel = Color.decode("#8ebdb0");
        Color toggleFg  = Color.BLACK;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(bg);
        getContentPane().setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel("Welcome to Smith Banking!", SwingConstants.CENTER);
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
        
        tellerBtn.setUI(new BasicToggleButtonUI());
        clientBtn.setUI(new BasicToggleButtonUI());
        for (JToggleButton tb : new JToggleButton[]{tellerBtn, clientBtn}) {
            tb.setOpaque(true);
            tb.setContentAreaFilled(true);
            tb.setBorderPainted(false);
            tb.setBackground(toggleBg);
            tb.setForeground(toggleFg);
            tb.addItemListener(_ -> tb.setBackground(tb.isSelected() ? toggleSel : toggleBg));
        }
        tellerBtn.setBackground(toggleSel);

        JPanel togglePanel = new JPanel(new GridLayout(1,2,10,0));
        togglePanel.setOpaque(false);
        togglePanel.setBorder(new EmptyBorder(15,50,15,50));
        togglePanel.add(tellerBtn);
        togglePanel.add(clientBtn);

        // Forms
        cards = new JPanel(new CardLayout());
        cards.setOpaque(false);
        cards.add(buildForm(
                "Employee Username:", empField = new JTextField(15),
                "Password:", empPass = new JPasswordField(15)
            ), "Teller");
        cards.add(buildForm(
                "Bank Username:", clientField = new JTextField(15),
                "Password:", clientPass = new JPasswordField(15)
            ), "Client");
        tellerBtn.addActionListener(_ -> ((CardLayout)cards.getLayout()).show(cards, "Teller"));
        clientBtn.addActionListener(_ -> ((CardLayout)cards.getLayout()).show(cards, "Client"));

        // Action buttons
        loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.addActionListener(this::doLogin);
        exitBtn = new JButton("Exit");
        exitBtn.addActionListener(_ -> System.exit(0));
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

        setVisible(true);
    }

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

    private void doLogin(ActionEvent e) {
        boolean isTeller = tellerBtn.isSelected();
        String user = (isTeller ? empField.getText() : clientField.getText()).trim();
        String pass = new String(isTeller ? empPass.getPassword() : clientPass.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "User ID and Password cannot be empty.",
                "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        loginBtn.setEnabled(false);
        // Run login off EDT
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() {
                loginApp.login(user, pass, isTeller);
                return null;
            }
            @Override protected void done() {
                loginBtn.setEnabled(true);
            }
        }.execute();
    }

    // SessionListener callbacks
    @Override
    public void onLoginSuccess(SessionInfo session, ConnectionHandler handler) {
        SwingUtilities.invokeLater(() -> {
            setVisible(false);
            if (session.getRole() == SessionInfo.ROLE.TELLER) {
                System.out.println("Successfully logged in as teller");
            } else {
                //new ATMProfileGUI(handler, session).display();
            	System.out.println("Successfully logged in as client");
            }
            dispose();
        });
    }

    @Override
    public void onLoginFailure(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                message, "Login Failed", JOptionPane.ERROR_MESSAGE);
        });
    }

    @Override
    public void onConnectionLost(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                message, "Connection Lost", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        });
    }

    @Override
    public void onServerShutdown() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                "The server is shutting down.", "Shutdown",
                JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        });
    }
}


