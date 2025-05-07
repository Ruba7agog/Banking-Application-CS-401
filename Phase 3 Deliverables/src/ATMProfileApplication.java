import java.math.BigDecimal;
import java.util.List;

/**
 * ATMApplication handles client interactions for ATM operations, using DTO messages
 * instead of direct domain objects. All communication is via ConnectionHandler messaging.
 */
public class ATMProfileApplication {
    private static final BigDecimal ATM_TRANSACTION_LIMIT = new BigDecimal("9999.99");

    private final ConnectionHandler handler;
    private final SessionInfo session;
    private ProfileMessage profile;
    private AccountMessage account;
    private List<AccountSummary> accountSummaries;
    private List<Transaction> transactionHistory;

    public ATMProfileApplication(SessionInfo session, ConnectionHandler handler) {
        this.handler = handler;
        this.session = session;
        loadProfile();
        this.accountSummaries = profile.getSummaries();
    }
    
    public ProfileMessage getProfile() {
    	return profile;
    }

    public AccountMessage getAccount() {
        return account;
    }
    
    public List<Transaction> gettransactionHistory() {
    	return transactionHistory;
    }
    
    public List<AccountSummary> getaccountSummaries() {
    	return accountSummaries;
    }

    public void withdraw(String amountStr) {
        if (!validateAmount(amountStr)) return;
        performTransaction(amountStr, TransactionMessage.OPERATION.WITHDRAW);
    }

    public void deposit(String amountStr) {
        if (!validateAmount(amountStr)) return;
        performTransaction(amountStr, TransactionMessage.OPERATION.DEPOSIT);
    }

    public void loadAccount(String accID) {
        if (accID == null || accID.isBlank()) {
            System.err.println("Account ID is missing.");
            return;
        }

        AccountMessage request = new AccountMessage(
            Message.TYPE.LOAD_ACCOUNT,
            session,
            profile.getUsername(),
            accID
        );
        handler.sendMessage(request);

        try {
            Message response = handler.getMessage();
            if (response instanceof AccountMessage msg) {
                this.account = msg;
            } else if (response instanceof FailureMessage fm) {
                System.err.println("Load account failed: " + fm.getMessage());
            } else {
                System.err.println("Unexpected response type: " +
                    (response == null ? "null" : response.getClass().getSimpleName()));
            }
        } catch (Exception e) {
            System.err.println("Error loading account: " + e.getMessage());
        }
    }
    
    public void refreshAccount() {
        if (account != null) {
            loadAccount(account.getID());
        }
    }

    public void loadTransactionHistory() {
        if (account == null) {
            System.err.println("No account loaded.");
            return;
        }
        else {
        	this.transactionHistory = account.getTransactionHistory();
        }
    }

    public void exitAccount() {
        if (account == null) {
            System.err.println("No account to exit.");
            return;
        }

        AccountMessage request = new AccountMessage(
            Message.TYPE.EXIT_ACCOUNT,
            session,
            profile.getUsername(),
            account.getID()
        );
        handler.sendMessage(request);

        try {
            Message response = handler.getMessage();
            if (response instanceof SuccessMessage) {
                this.account = null;
                System.out.println("Exited account session.");
            } else if (response instanceof FailureMessage fm) {
                System.err.println("Failed to exit account: " + fm.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error exiting account: " + e.getMessage());
        }
    }
    
    public void selectAccount(String id) {
        if (id == null) return;

        for (AccountSummary summary : accountSummaries) {
            if (summary != null && id.equals(summary.getID())) {
                loadAccount(id);
                return;
            }
        }

        System.err.println("Account not found: " + id);
    }
    
    //
    // retrieves profile info. call after any updates are made
    public void loadProfile() {
        // create ProfileMessage object to send
        Message profileMessage = new ProfileMessage(Message.TYPE.LOAD_PROFILE, this.session, this.session.getUsername());
        handler.sendMessage(profileMessage);

        // BLOCK and wait for server response
        try {
            Message serverResponse = handler.getMessage();
            if (serverResponse.getType() == Message.TYPE.LOAD_PROFILE && serverResponse instanceof ProfileMessage) {
                // cast serverResponse to ProfileMessage
                ProfileMessage msg = (ProfileMessage) serverResponse;
                // create ClientProfile object from server response
                this.profile = msg;
                this.accountSummaries = msg.getSummaries();
            } else if (serverResponse.getType() == Message.TYPE.FAILURE && serverResponse instanceof FailureMessage) {
                // cast to FailureMessage
                FailureMessage msg = (FailureMessage) serverResponse;
                System.out.println("Error: " + msg.getMessage());
            } else {
                System.out.println("Error: unexpected message type received");
            }
        } catch (Exception e) {
            System.out.println("Request interrupted");
        }
    }
    
    public void logoutClient() {
        Message logoutMsg = new LogoutMessage(
                Message.TYPE.LOGOUT_CLIENT,
                this.session);
        handler.sendMessage(logoutMsg);

        Message msg = handler.getMessage();
        if (msg instanceof SuccessMessage) {
            System.out.println("Logged out successfully: " + ((SuccessMessage) msg).getMessage());

        }

        handler.setLoggedOut(true);
    }
    
    private boolean validateAmount(String amountStr) {
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            System.err.println("Please enter a valid number.");
            return false;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.err.println("Amount must be greater than zero.");
            return false;
        }

        if (amount.compareTo(ATM_TRANSACTION_LIMIT) > 0) {
            System.err.println("Amount exceeds ATM limit; please see a teller.");
            return false;
        }

        return true;
    }

    private void performTransaction(String amountStr, TransactionMessage.OPERATION op) {
        if (account == null) {
            System.err.println("No account loaded.");
            return;
        }

        TransactionMessage tx = new TransactionMessage(
            session,
            amountStr,
            op,
            account.getID()
        );
        handler.sendMessage(tx);

        try {
            Message resp = handler.getMessage();
            if (resp instanceof SuccessMessage) {
                System.out.println("Transaction successful.");
                loadAccount(account.getID());  // Refresh account state
            } else if (resp instanceof FailureMessage fm) {
                System.err.println("Transaction failed: " + fm.getMessage());
            } else {
                System.err.println("Unexpected transaction response.");
            }
        } catch (Exception e) {
            System.err.println("Transaction error: " + e.getMessage());
        }
    }

	public String getLegalName() {
		return profile.getLegalName();
	}

	public String getPhoneNumber() {
		return profile.getPhone();
	}

	public String getUsername() {
		return session.getUsername();
	}
}

