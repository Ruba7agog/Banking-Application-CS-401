import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Transaction implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public enum OPERATION {
		WITHDRAW,
		DEPOSIT
	}
	private Date created;
	private BigDecimal amount;
	private OPERATION op;
	
	public Transaction(String amount, OPERATION op) {
		this.created = new Date();
		this.amount = new BigDecimal(amount);
		this.op = op;
	}
	public String getDate() {
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		String formatDate = format.format(created);
		return formatDate;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public OPERATION getOperation() {
		return op;
	}
}
