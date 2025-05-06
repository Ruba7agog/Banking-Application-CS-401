import java.util.List;
import java.lang.InterruptedException;

public class ClientProfileApplication {

    private ClientProfile profile;
    private ConnectionHandler handler;
    private SessionInfo session;
    private ATMApplication atmApp;
    private List<AccountSummary> accounts;
 
    public void setATMApplication(ATMApplication atmApp) {
        this.atmApp = atmApp;
    }

    public void setConnectionHandler(ConnectionHandler c){
        this.handler = c;
    }

    public void setSession(SessionInfo s){
        this.session = s;
    }

    public ClientProfile getProfile() {
        return this.profile;
    }
    // this sends request to server for clientProfile info
    // ~~~~~~BLOCKS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public Message requestProfile(){
        // create ProfileMessage object to send
        Message profileMessage = new ProfileMessage(Message.TYPE.LOAD_PROFILE, session, session.getUsername());
        handler.send(profileMessage);

        // BLOCK and wait for server response
        try{
            Message serverResponse = handler.getMessage();
            if (serverResponse.getType() == Message.TYPE.LOAD_PROFILE && serverResponse instanceof ProfileMessage){
                // cast serverResponse to ProfileMessage
                ProfileMessage msg = (ProfileMessage) serverResponse;
                // create ClientProfile object from server response
                this.profile = new ClientProfile(
                msg.getUsername(),
                msg.getPassword(), 
                msg.getPhone(),
                msg.getAddress(), 
                msg.getLegalName());  
                this.accounts = msg.getSummaries();
            } else if ( serverResponse.getType() == Message.TYPE.FAILURE && serverResponse instanceof FailureMessage){
                // cast to FailureMessage
                FailureMessage msg = (FailureMessage) serverResponse;
                System.out.println("Error: " + msg.getMessage());
            } else {
                System.out.println("Error: unexpected message type received");
            }
            return serverResponse;
        } catch (Exception e) { 
            return new FailureMessage("An error occurred during load_Profile: " + e.getMessage());
        }
    }




    // this sends all profile info to gui
   /*  public void loadProfile(){

    } */

    // this sends all accountsummary.java info to gui for account thumbnails
    /*public void loadAccounts(){
     
    }*/


    //this will open up ATMApplication & corresponding GUI page
    // gui will send in selected ID
    public void selectAccount(String id) {
        AccountSummary selected = null;
        for (AccountSummary summary : accounts) {
            if (summary.getID().equals(id)) {
                selected = summary;
                break;
            }
        }

        if (atmApp != null && selected != null) {
            atmApp.setClient(profile);
            atmApp.loadAccount(selected.getID()); 
        }
    }

    //this will return to LoginApplication and Login screen
    public void exit() {
        // client-side log out
        Message logoutMsg = new LogoutMessage(
            Message.TYPE.LOGOUT_ATM, 
            this.session
        );
        handler.send(logoutMsg);

        Message msg = handler.getMessage();
        if (msg instanceof SuccessMessage) {
            System.out.println("Logged out successfully: " + ((SuccessMessage) msg).getMessage());

        }

        handler.setLoggedOut(true);
        handler.shutDown();
    }   
}

