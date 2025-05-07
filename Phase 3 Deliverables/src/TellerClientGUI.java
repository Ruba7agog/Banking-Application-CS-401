package bankGUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TellerClientGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    /* ─── State ─────────────────────────────────────────────── */
    private final TellerApplication tellerApp;
    private final ProfileMessage    profileMsg;
    private final List<AccountSummary> accounts;
    private final AccountMessage           accountMsg;
    

    private JList<AccountSummary> accountList;
    private JLabel idLbl, balLbl, sharedLbl;
    private JButton depositBtn, withdrawBtn, historyBtn, logoutBtn;

    private BigDecimal currentBalance = BigDecimal.ZERO;
    private String      currentAccID  = null;

    private static final Color BRAND_DARK  = Color.decode("#00875A");
    private static final Color BRAND_LIGHT = Color.decode("#30C88B");

    public TellerClientGUI(TellerApplication app,
            ProfileMessage    profile,
            AccountMessage    account) {
this.tellerApp  = app;
this.profileMsg = profile;
this.accountMsg = account;
this.accounts  = app.getAccounts();

initLookAndFeel();
initComponents();
}
    /* ─── L&F ───────────────────────────────────────────────── */
    private void initLookAndFeel() {
        try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); }
        catch (Exception ignored) {}
    }

    /* ─── UI build (layout identical to ATMProfileGUI) ─────── */
    private void initComponents() {
        setTitle("Client Profile | Teller");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(820, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        setContentPane(new GradientPanel());
        getContentPane().setLayout(new BorderLayout(10,10));

        /* ----- header ------------------------------------------------ */
        String full  = profileMsg.getLegalName();
        String phone = profileMsg.getPhone();
        String user  = profileMsg.getUsername();

        JPanel topInfo = new JPanel(new GridLayout(2,1));
        topInfo.setOpaque(false);
        topInfo.setBorder(new EmptyBorder(5,10,5,10));
        topInfo.add(new JLabel("Bank ID: " + user,  SwingConstants.CENTER));
        topInfo.add(new JLabel("Phone: "  + phone, SwingConstants.CENTER));

        JLabel title = new JLabel("Client: " + full, SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        /* logout button copied from ATMProfileGUI -------------------- */
        logoutBtn = stylishButton("Logout");
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,"Are you sure you want to log out?",
                    "Confirm Logout", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            logoutBtn.setEnabled(false);
            /* dummy async, matching ATMProfileGUI style */
            new SwingWorker<Void,Void>() {
                protected Void doInBackground() {
                    tellerApp.logoutTeller();
                    return null;
                }
                protected void done() {
                    dispose();
                    LoginApplication newApp = new LoginApplication();
                    LoginGUI newGui        = new LoginGUI(newApp);
                    newApp.setGUI(newGui);
                    newGui.Login();
                }
            }.execute();
        });

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BRAND_DARK);
        header.setPreferredSize(new Dimension(0,45));
        header.setBorder(new EmptyBorder(0,10,0,10));
        header.add(title,     BorderLayout.CENTER);
        header.add(logoutBtn, BorderLayout.EAST);

        JPanel topWrap = new JPanel(new BorderLayout());
        topWrap.setOpaque(false);
        topWrap.add(header,  BorderLayout.NORTH);
        topWrap.add(topInfo, BorderLayout.CENTER);
        getContentPane().add(topWrap, BorderLayout.NORTH);

        /* ----- left account list ------------------------------------ */
        DefaultListModel<AccountSummary> model = new DefaultListModel<>();
        accounts.forEach(model::addElement);

        accountList = new JList<>(model);
        accountList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountList.setFixedCellHeight(35);
        accountList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        accountList.setCellRenderer(new DefaultListCellRenderer(){
            @Override public Component getListCellRendererComponent(
                    JList<?> l,Object v,int i,boolean sel,boolean foc){
                JLabel lbl=(JLabel)super.getListCellRendererComponent(l,v,i,sel,foc);
                lbl.setText(((AccountSummary)v).getID());
                lbl.setBorder(new EmptyBorder(4,10,4,10));
                return lbl;
            }
        });
        accountList.addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) loadSelectedAccount();
        });

        JScrollPane sp = new JScrollPane(accountList);
        sp.setPreferredSize(new Dimension(200,0));
        sp.setBorder(BorderFactory.createMatteBorder(0,0,0,1,BRAND_DARK));
        getContentPane().add(sp, BorderLayout.WEST);

        /* ----- centre card (info + buttons) ------------------------- */
        JPanel info = new JPanel(new GridLayout(3,1,5,5));
        info.setOpaque(false);
        idLbl   = makeBig("Account ID:");
        balLbl  = makeBig("Balance:");
        sharedLbl = makeBig("Shared:");
        info.add(idLbl); info.add(balLbl); info.add(sharedLbl);

        depositBtn  = stylishButton("Deposit");
        withdrawBtn = stylishButton("Withdraw");
        historyBtn  = stylishButton("History");

        depositBtn.addActionListener(e -> handleDeposit());
        withdrawBtn.addActionListener(e -> handleWithdraw());
        historyBtn.addActionListener(e -> handleHistory());

        JButton shareBtn  = stylishButton("Share Account");
        shareBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(this,
                "Enter username to share with (feature pending).",
                "Share Account", JOptionPane.INFORMATION_MESSAGE));

        JButton closeBtn  = stylishButton("Close Account");
        closeBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(this,
                "Close‑account feature coming soon.",
                "Close Account", JOptionPane.INFORMATION_MESSAGE));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER,18,0));
        btnRow.setOpaque(false);
        btnRow.add(depositBtn); btnRow.add(withdrawBtn); btnRow.add(historyBtn);
        btnRow.add(shareBtn);   btnRow.add(closeBtn);

        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        inner.setBorder(new EmptyBorder(20,20,20,20));
        inner.add(info,   BorderLayout.CENTER);
        inner.add(btnRow, BorderLayout.SOUTH);

        JPanel card = new JPanel(new BorderLayout()){
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,200));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15,15,15,15));
        card.add(inner);

        JPanel holder = new JPanel(new GridBagLayout());
        holder.setOpaque(false); holder.add(card);
        getContentPane().add(holder, BorderLayout.CENTER);

        /* ----- bottom buttons -------------------------------------- */
        JPanel bottom=new JPanel(new FlowLayout(FlowLayout.CENTER,35,10));
        bottom.setOpaque(false);
        bottom.add(stylishButton("New Account"));
        bottom.add(stylishButton("Edit Client Info"));
        getContentPane().add(bottom, BorderLayout.SOUTH);

        /* select first */
        if(!model.isEmpty()) accountList.setSelectedIndex(0);
    }

    /* ─── Account selection --------------------------------------- */
    private void loadSelectedAccount(){
        AccountSummary s = accountList.getSelectedValue();
        if(s==null){ updateInfo(null); return; }
        tellerApp.loadAccount(s.getID());
        AccountMessage am = new AccountMessage(
                Message.TYPE.LOAD_ACCOUNT,
                tellerApp.getSession(),
                s.getID(),
                new BigDecimal(s.getBalance()),
                List.of());
        updateInfo(am);
    }

    private void updateInfo(AccountMessage m){
        if(m==null){
            idLbl.setText("Account ID: N/A");
            balLbl.setText("Balance: N/A");
            sharedLbl.setText("Shared: N/A");
            currentBalance = BigDecimal.ZERO;
            currentAccID   = null;
        }else{
            idLbl.setText("Account ID: "+m.getID());
            balLbl.setText("Balance: $"+m.getBalance().setScale(2));
            sharedLbl.setText("Shared: —");
            currentBalance = m.getBalance();
            currentAccID   = m.getID();
        }
    }

    /* ─── Deposit / Withdraw / History (logic copied) -------------- */
    private String fmt(BigDecimal a){ return "$"+a.setScale(2); }

    private void handleDeposit() {
        String input = JOptionPane.showInputDialog(this,
                "Enter amount to deposit:","Deposit",
                JOptionPane.PLAIN_MESSAGE);
        if(input==null||input.trim().isEmpty()) return;

        BigDecimal amt;
        try {
            amt = new BigDecimal(input);
            if(amt.compareTo(BigDecimal.ZERO)<=0)
                throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid amount.","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this,
                "Deposit "+fmt(amt)+" ?", "Confirm Deposit",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(ok!=JOptionPane.YES_OPTION) return;

        depositBtn.setEnabled(false);
        new SwingWorker<Boolean,Void>() {
            protected Boolean doInBackground(){ return tellerApp.deposit(input); }
            protected void done(){
                depositBtn.setEnabled(true);
                try{
                    if(get()){
                        loadSelectedAccount();
                        JOptionPane.showMessageDialog(
                                TellerClientGUI.this,"Deposited "+fmt(amt),
                                "Success",JOptionPane.INFORMATION_MESSAGE);
                    }else{
                        JOptionPane.showMessageDialog(
                                TellerClientGUI.this,"Deposit failed.",
                                "Error",JOptionPane.ERROR_MESSAGE);
                    }
                }catch(Exception e){/* ignore */}
            }
        }.execute();
    }

    private void handleWithdraw() {
        String input = JOptionPane.showInputDialog(this,
                "Enter amount to withdraw:","Withdraw",
                JOptionPane.PLAIN_MESSAGE);
        if(input==null||input.trim().isEmpty()) return;

        BigDecimal amt;
        try{
            amt = new BigDecimal(input);
            if(amt.compareTo(BigDecimal.ZERO)<=0||
               amt.compareTo(currentBalance)>0)
                throw new NumberFormatException();
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(this,
                    "Invalid amount.","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this,
                "Withdraw "+fmt(amt)+" ?", "Confirm Withdrawal",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(ok!=JOptionPane.YES_OPTION) return;

        withdrawBtn.setEnabled(false);
        new SwingWorker<Boolean,Void>() {
            protected Boolean doInBackground(){ return tellerApp.withdraw(input); }
            protected void done(){
                withdrawBtn.setEnabled(true);
                try{
                    if(get()){
                        loadSelectedAccount();
                        JOptionPane.showMessageDialog(
                                TellerClientGUI.this,"Withdrew "+fmt(amt),
                                "Success",JOptionPane.INFORMATION_MESSAGE);
                    }else{
                        JOptionPane.showMessageDialog(
                                TellerClientGUI.this,"Withdrawal failed.",
                                "Error",JOptionPane.ERROR_MESSAGE);
                    }
                }catch(Exception e){/* ignore */}
            }
        }.execute();
    }

    private void handleHistory() {
        historyBtn.setEnabled(false);
        new SwingWorker<Void,Void>() {
            protected Void doInBackground(){
                tellerApp.loadTransactionHistory(); /* prints to console */
                return null;
            }
            protected void done(){
                historyBtn.setEnabled(true);
                JOptionPane.showMessageDialog(
                        TellerClientGUI.this,
                        "Transaction history printed to console.",
                        "History",JOptionPane.INFORMATION_MESSAGE);
            }
        }.execute();
    }

    /* ─── helpers -------------------------------------------------- */
    private JLabel makeBig(String t){
        JLabel l=new JLabel(t,SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI",Font.BOLD,18));
        return l;
    }
    private JButton stylishButton(String txt){
        JButton b=new JButton(txt);
        b.setForeground(Color.WHITE); b.setBackground(BRAND_DARK);
        b.setFocusPainted(false); b.setFont(new Font("Segoe UI",Font.PLAIN,14));
        b.setPreferredSize(new Dimension(110,35));
        b.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseEntered(java.awt.event.MouseEvent e){ b.setBackground(BRAND_LIGHT);}
            public void mouseExited (java.awt.event.MouseEvent e){ b.setBackground(BRAND_DARK);}
        });
        return b;
    }

    private static class GradientPanel extends JPanel{
        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0,0,BRAND_LIGHT,0,getHeight(),Color.WHITE));
            g2.fillRect(0,0,getWidth(),getHeight());
        }
    }

    public void display(){ SwingUtilities.invokeLater(()->setVisible(true)); }
}
