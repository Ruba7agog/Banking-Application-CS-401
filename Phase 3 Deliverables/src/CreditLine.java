
public class CreditLine {
	private float creditLimit; // maximum amount of money they can go over
	
	public CreditLine(float limit) {
		super();
		this.creditLimit = limit;
	}
	public void setCreditLimit(float newLimit) {
		this.creditLimit = newLimit;
	}
	public float getCreditLimit() {
		return creditLimit;
	}
}
