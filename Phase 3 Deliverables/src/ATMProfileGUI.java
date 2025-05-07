import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ATMProfileGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private final ATMProfileApplication atmApp;
    private final LoginGUI loginGUI;
    private final List<AccountSummary> accounts;

    private JList<AccountSummary> accountList;
    private JLabel idLabel, balanceLabel, sharedLabel;
    private BigDecimal currentBalance = BigDecimal.ZERO;
    private String currentAccountID = null;

    private static final Color BRAND_DARK = Color.decode("#00875A");
    private static final Color BRAND_LIGHT = Color.decode("#30C88B");

    private JButton depositBtn, withdrawBtn, historyBtn, logoutBtn;

    public ATMProfileGUI(SessionInfo session, ConnectionHandler handler, LoginGUI loginGUI) {
        this.atmApp = new ATMProfileApplication(session, handler);
        this.loginGUI = loginGUI;
        this.accounts = atmApp.getaccountSummaries();
        initLookAndFeel();
        initComponents();
    }

    private void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}
    }

    private void initComponents() {
        setTitle("ATM & Profile");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        setContentPane(new GradientPanel());
        getContentPane().setLayout(new BorderLayout(10, 10));

        String fullName = atmApp.getLegalName();
        String phone = atmApp.getPhoneNumber();
        String username = atmApp.getUsername();

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        JLabel idLabelTop = new JLabel("Bank ID: " + username, SwingConstants.CENTER);
        JLabel phoneLabel = new JLabel("Phone: " + phone, SwingConstants.CENTER);
        idLabelTop.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        topPanel.add(idLabelTop);
        topPanel.add(phoneLabel);

        JLabel title = new JLabel("Welcome, " + fullName, SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        logoutBtn = stylishButton("Logout");
        logoutBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        logoutBtn.addActionListener(e -> {
            atmApp.logoutClient();
            dispose();
            loginGUI.setVisible(true);
        });

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(true);
        headerPanel.setBackground(BRAND_DARK);
        headerPanel.setPreferredSize(new Dimension(0, 45));
        headerPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
        headerPanel.add(title, BorderLayout.CENTER);
        headerPanel.add(logoutBtn, BorderLayout.EAST);

        JPanel overallTopPanel = new JPanel(new BorderLayout());
        overallTopPanel.setOpaque(false);
        overallTopPanel.add(headerPanel, BorderLayout.NORTH);
        overallTopPanel.add(topPanel, BorderLayout.CENTER);
        getContentPane().add(overallTopPanel, BorderLayout.NORTH);

        DefaultListModel<AccountSummary> model = new DefaultListModel<>();
        if (accounts != null && !accounts.isEmpty()) {
            model.addElement(accounts.get(0)); // Show only first account
        }

        accountList = new JList<>(model);
        accountList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountList.setFixedCellHeight(35);
        accountList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        accountList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object val, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, val, index, isSelected, cellHasFocus);
                if (val instanceof AccountSummary s) {
                    label.setText(s.getID() != null ? s.getID() : "Invalid Account");
                }
                label.setBorder(new EmptyBorder(4, 10, 4, 10));
                return label;
            }
        });
        accountList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                AccountSummary selected = accountList.getSelectedValue();
                if (selected != null) {
                    atmApp.selectAccount(selected.getID());
                    updateAccountInfo(atmApp.getAccount());
                }
            }
        });

        JScrollPane scroll = new JScrollPane(accountList);
        scroll.setPreferredSize(new Dimension(200, 0));
        scroll.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BRAND_DARK));
        getContentPane().add(scroll, BorderLayout.WEST);

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.setOpaque(false);
        idLabel = stylisedLabel("Account ID:");
        balanceLabel = stylisedLabel("Balance:");
        sharedLabel = stylisedLabel("Shared Status:");
        infoPanel.add(idLabel);
        infoPanel.add(balanceLabel);
        infoPanel.add(sharedLabel);

        depositBtn = stylishButton("Deposit");
        withdrawBtn = stylishButton("Withdraw");
        historyBtn = stylishButton("History");

        depositBtn.addActionListener(e -> {
            String amt = JOptionPane.showInputDialog(this, "Enter deposit amount:");
            if (amt != null) {
            	atmApp.deposit(amt);
            	atmApp.refreshAccount();
            	updateAccountInfo(atmApp.getAccount());
            }
        });

        withdrawBtn.addActionListener(e -> {
            String amt = JOptionPane.showInputDialog(this, "Enter withdrawal amount:");
            if (amt != null) {
            	atmApp.withdraw(amt);
            	atmApp.refreshAccount();
            	updateAccountInfo(atmApp.getAccount());
            }
        });

        historyBtn.addActionListener(e -> {
            atmApp.loadTransactionHistory();
            List<Transaction> history = atmApp.gettransactionHistory();
            if (history != null) history.forEach(System.out::println);
        });

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setOpaque(false);
        btnPanel.add(depositBtn);
        btnPanel.add(withdrawBtn);
        btnPanel.add(historyBtn);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(20, 20, 20, 20));
        center.add(infoPanel, BorderLayout.CENTER);
        center.add(btnPanel, BorderLayout.SOUTH);

        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        card.add(center);

        JPanel cardHolder = new JPanel(new GridBagLayout());
        cardHolder.setOpaque(false);
        cardHolder.add(card);
        getContentPane().add(cardHolder, BorderLayout.CENTER);

        if (!model.isEmpty()) accountList.setSelectedIndex(0);
        else updateAccountInfo(null);
    }

    private void updateAccountInfo(AccountMessage msg) {
        if (msg == null) {
            idLabel.setText("Account ID: N/A");
            balanceLabel.setText("Balance: N/A");
            sharedLabel.setText("Shared: N/A");
            currentBalance = BigDecimal.ZERO;
            currentAccountID = null;
        } else {
            idLabel.setText("Account ID: " + msg.getID());
            currentBalance = msg.getBalance();
            balanceLabel.setText("Balance: $" + currentBalance);
            long owners = accounts.stream().filter(s -> s.getID().equals(msg.getID())).count();
            sharedLabel.setText("Shared: " + (owners > 1 ? "Yes" : "No"));
            currentAccountID = msg.getID();
        }
    }

    private JLabel stylisedLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        return lbl;
    }

    private JButton stylishButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(BRAND_DARK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setPreferredSize(new Dimension(110, 35));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(BRAND_LIGHT); }
            public void mouseExited (java.awt.event.MouseEvent evt) { btn.setBackground(BRAND_DARK);  }
        });
        return btn;
    }

    private class GradientPanel extends JPanel {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, BRAND_LIGHT, 0, getHeight(), Color.WHITE));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
