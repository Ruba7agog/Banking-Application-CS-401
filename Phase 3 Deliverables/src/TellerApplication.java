import java.math.BigDecimal;
import java.util.List;

public class TellerApplication {

    private ProfileMessage profile;
    private AccountMessage account;
    private ConnectionHandler handler;
    private SessionInfo session;
    private List<AccountSummary> accounts;

    public TellerApplication(SessionInfo session, ConnectionHandler handler) {
    	this.session = session;
    	this.handler = handler;
    }

    public Boolean changeProfileInfo(String username, String password, String phone, String address, String legalName) {
        ProfileMessage msg = new ProfileMessage(Message.TYPE.SAVE_PROFILE,
                this.session,
                username,
                password,
                phone,
                address,
                legalName,
                this.accounts);

        handler.sendMessage(msg);

        try {
            Message response = handler.getMessage();

            if (response instanceof SuccessMessage) {
                requestProfile(); // update locally
                return true;
            } else if (response instanceof FailureMessage) {
                System.out.println("Profile Update failed: " +
                        ((FailureMessage) response).getMessage());
                return false;
            }
        } catch (Exception e) {
            System.out.println("Failed to update profile");
        }
        return false;

    }

    public Boolean deleteProfile() {
        // send mssg of type DELETE_PROFILE
        // handleDeleteProfile only needs username & pw
        ProfileMessage delProfile = new ProfileMessage(Message.TYPE.DELETE_PROFILE,
                this.session,
                profile.getUsername(),
                profile.getPassword(),
                profile.getPhone(),
                profile.getAddress(),
                profile.getLegalName(),
                this.accounts);

        handler.sendMessage(delProfile);

        try {
            Message response = handler.getMessage();

            if (response instanceof SuccessMessage) {
                return true;
            } else if (response instanceof FailureMessage) {
                System.out.println("Profile Deletion failed: " +
                        ((FailureMessage) response).getMessage());
                return false;
            }
        } catch (Exception e) {
            System.out.println("Failed to delete profile");
        }
        return false;

        // return to Teller homepage
    }

    public boolean deleteAccount(String id) {
        // send mssg of type DELETE_ACCOUNT
        AccountMessage delAccount = new AccountMessage(Message.TYPE.DELETE_ACCOUNT,
                this.session,
                profile.getUsername(),
                id);

        handler.sendMessage(delAccount);

        try {
            Message response = handler.getMessage();

            if (response instanceof SuccessMessage) {
                requestProfile(); // update locally
                return true;
            } else if (response instanceof FailureMessage) {
                System.out.println("Account Deletion failed: " +
                        ((FailureMessage) response).getMessage());
                return false;
            }
        } catch (Exception e) {
            System.out.println("Failed to delete account");
        }
        return false;

    }

    // GUI MUST pass in either CHECKING, SAVING, or CREDIT_LINE as input
    // in GUI only take in String if creating credit account
    public boolean addAccount(String type, String creditLimit, String withdrawalLimit) {
        AccountMessage.ACCOUNT_TYPE newType;
        String sentCreditLimit = "0";
        int sentWithdrawalLimit = 0;

        // Parse account type
        try {
            newType = AccountMessage.ACCOUNT_TYPE.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid account type: " + type);
            return false;
        }

        // Validate and set credit limit for CREDIT_LINE
        if (newType == AccountMessage.ACCOUNT_TYPE.CREDIT_LINE) {
            if (!isPositiveDecimal(creditLimit)) {
                System.err.println("Credit limit must be a positive decimal.");
                return false;
            }
            sentCreditLimit = creditLimit.trim();
        }

        // Validate and set withdrawal limit for SAVING
        if (newType == AccountMessage.ACCOUNT_TYPE.SAVING) {
            if (!isNonNegativeInteger(withdrawalLimit)) {
                System.err.println("Withdrawal limit must be a non-negative integer.");
                return false;
            }
            sentWithdrawalLimit = Integer.parseInt(withdrawalLimit.trim());
        }

        // Send account creation request
        handler.sendMessage(new AccountMessage(
            this.session,
            this.profile.getUsername(),
            newType,
            sentCreditLimit,
            sentWithdrawalLimit
        ));

        // Wait for server response
        try {
            Message response = handler.getMessage();
            if (response instanceof SuccessMessage) {
                System.out.println("Server confirmation: " + ((SuccessMessage) response).getMessage());
                requestProfile();
                return true;
            } else if (response instanceof FailureMessage) {
                System.out.println("Error: " + ((FailureMessage) response).getMessage());
            } else {
                System.err.println("Unexpected or null server response.");
            }
        } catch (Exception e) {
            System.err.println("Error during account creation: " + e.getMessage());
        }

        return false;
    }

    public Boolean createNewProfile(
    		String username,
            String password,
            String phone,
            String address,
            String legalName) {
        // create ProfileMessage of type CREATE_PROFILE
        ProfileMessage newProfile = new ProfileMessage(
                this.session,
                username,
                password,
                phone,
                address,
                legalName
                );
        // send over to server
        handler.sendMessage(newProfile);
        // BLOCK and wait for response
        try {
            Message response = handler.getMessage();

            if (response instanceof SuccessMessage) {
                return true;
            } else if (response instanceof FailureMessage) {
                System.out.println("Profile creation failed: " +
                        ((FailureMessage) response).getMessage());
                return false;
            }
        } catch (Exception e) {
            System.out.println("Failed to create profile");
        }
        return false;

    }

    public boolean withdraw(String amount) {
    	
        // check that inputted amount is valid
        try {
            BigDecimal check = new BigDecimal(amount);
            if (check.compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        Boolean result = performTransaction(amount, TransactionMessage.OPERATION.WITHDRAW);

        if (result != false && this.account != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean deposit(String amount) {
        // check that inputted amount is valid
        try {
            BigDecimal check = new BigDecimal(amount);
            if (check.compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        Boolean result = performTransaction(amount, TransactionMessage.OPERATION.DEPOSIT);

        if (result != false && this.account != null) {
            return true;
        } else {
            return false;
        }
    }

    // assigns client object to this.profile
    public void loadClientProfile(String name) {
        // create ProfileMessage object to send
        Message profileMessage = new ProfileMessage(Message.TYPE.LOAD_PROFILE, this.session, name);
        // send to server via handler
        handler.sendMessage(profileMessage);

        // BLOCK and wait for server response
        try {
            Message serverResponse = handler.getMessage();
            if (serverResponse.getType() == Message.TYPE.LOAD_PROFILE && serverResponse instanceof ProfileMessage) {
                // cast serverResponse to ProfileMessage
                ProfileMessage msg = (ProfileMessage) serverResponse;
                // create ClientProfile object from server response
                this.profile = msg;
                this.accounts = msg.getSummaries();
            } else if (serverResponse.getType() == Message.TYPE.FAILURE && serverResponse instanceof FailureMessage) {
                // cast to FailureMessage
                FailureMessage msg = (FailureMessage) serverResponse;
                System.out.println("Error: " + msg.getMessage());
            } else {
                System.out.println("Error: unexpected message type received");
            }
        } catch (Exception e) { // ConnectionHandler.getMessage() throws an InterruptedException
            System.out.println("Request interrupted");
        }
    }

    public void loadAccount(String accID) {
        Message loadAccMsg = new AccountMessage(Message.TYPE.LOAD_ACCOUNT, this.session, this.profile.getUsername(),
                accID);
        handler.sendMessage(loadAccMsg);

        try {
            if (refreshAccount()) {
                // RELAY ACCOUNT TO GUI (What?)
            }
        } catch (Exception e) {
            System.out.println("Account loading interrupted.");
        }
    }

    //
    // retrieves profile info. call after any updates are made
    //
    public void requestProfile() {
        // create ProfileMessage object to send
        Message profileMessage = new ProfileMessage(Message.TYPE.LOAD_PROFILE, this.session, this.profile.getUsername());
        handler.sendMessage(profileMessage);

        // BLOCK and wait for server response
        try {
            Message serverResponse = handler.getMessage();
            if (serverResponse.getType() == Message.TYPE.LOAD_PROFILE && serverResponse instanceof ProfileMessage) {
                // cast serverResponse to ProfileMessage
                ProfileMessage msg = (ProfileMessage) serverResponse;
                // create ClientProfile object from server response
                this.profile = msg;
                this.accounts = msg.getSummaries();
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

    public void exitAccount() {
        Message msg = new AccountMessage(
                Message.TYPE.EXIT_ACCOUNT,
                this.session,
                this.profile.getUsername(),
                this.account.getID());

        handler.sendMessage(msg);
        // return to clientprofile screen
    }

    public void exitProfile() {
        Message msg = new ProfileMessage(
                Message.TYPE.EXIT_PROFILE,
                this.session,
                this.profile.getUsername());

        handler.sendMessage(msg);
        // return to teller homescreen
    }

    public void logoutTeller() {
        Message logoutMsg = new LogoutMessage(
                Message.TYPE.LOGOUT_TELLER,
                this.session);
        handler.sendMessage(logoutMsg);

        Message msg = handler.getMessage();
        if (msg instanceof SuccessMessage) {
            System.out.println("Logged out successfully: " + ((SuccessMessage) msg).getMessage());

        }

        handler.setLoggedOut(true);
    }

    // __________________________________________
    // private helper methods ___________________
    // __________________________________________
    private boolean performTransaction(String amount, TransactionMessage.OPERATION operation) {
        if (account == null || session == null) {
            System.out.println("No active account session");
            return false;
        }

        // make transactionMessage
        TransactionMessage transMsg = new TransactionMessage(
                session,
                amount,
                operation,
                account.getID());
        // send it to the server
        handler.sendMessage(transMsg);

        // BLOCK and wait for server response
        try {
            Message response = handler.getMessage();

            if (response instanceof SuccessMessage) {
                // transaction was a success, so refresh local account data
                boolean refresh = refreshAccount();
                return refresh;
            } else if (response instanceof FailureMessage) {
                System.out.println("Transaction failed: " +
                        ((FailureMessage) response).getMessage());
                return false;
            }

        } catch (Exception e) {
            System.out.println("Transaction was interrupted");
        }

        return false;
    }

    private boolean refreshAccount() {

        if (account == null)
            return false;
        // send LOAD_ACCOUNT request
        AccountMessage requestMsg = new AccountMessage(Message.TYPE.LOAD_ACCOUNT, session, this.profile.getUsername(),
                account.getID());
        handler.sendMessage(requestMsg);
        // BLOCK client to wait for response
        try {
            Message serverResponse = handler.getMessage();
            if (serverResponse instanceof AccountMessage) {
                AccountMessage msg = (AccountMessage) serverResponse;
                account = msg;
                return true;
            } else if (serverResponse instanceof FailureMessage) {
                System.out.println("Error: " + ((FailureMessage) serverResponse).getMessage());
            }
        } catch (Exception e) {
            System.out.println("Account refresh interrupted");
        }
        return false;
    }
    
    private boolean isPositiveDecimal(String value) {
        try {
            BigDecimal bd = new BigDecimal(value.trim());
            return bd.compareTo(BigDecimal.ZERO) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isNonNegativeInteger(String value) {
        try {
            return Integer.parseInt(value.trim()) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String getTellerName() {
        return session.getUsername();    
    }

    public String getBranch() {
        return "Main Branch"; // Or retrieve it if it's in SessionInfo or elsewhere
    }
}
