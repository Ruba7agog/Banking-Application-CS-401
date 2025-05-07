import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public abstract class Account implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int count = 1;
	private String id;
	private BigDecimal balance;
	private List<Transaction> transactionHistory;
	
	protected Account() {
		this.balance = new BigDecimal(0);
        this.transactionHistory = new ArrayList<>();
        setID();
    }
	public BigDecimal getBalance() {
		return this.balance;
	}
	public String getID() {
		return this.id;
	}
	public List<Transaction> getTransHistory() {
		return this.transactionHistory;
	}
	public void addTransaction(Transaction trans) {
		this.transactionHistory.add(trans);
		this.balance.add(trans.getAmount());
	}
	private void setID() {
        this.id = "ACC" + count++;
    }
}
