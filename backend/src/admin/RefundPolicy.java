package admin;

public class RefundPolicy {
	private int refundWindowTime;
	
	private static final RefundPolicy instance = new RefundPolicy();
	
	private RefundPolicy() {
		this.refundWindowTime = 0;
	}
	
	public static RefundPolicy getInstance() {
		return instance;
	}
	
	/**
	 * Get the refund window time in days the policy enforces.
	 * @return the integer value representing the number of days the refund window lasts
	 */
	public int getRefundPolicy() {
		return this.refundWindowTime;
	}
	
	/**
	 * Change the value of this policy with the passed parameter
	 * @param time 	The integer value that the policy's variable will be set to
	 */
	private void change(int time) {
		this.refundWindowTime = time;
	}
	
	/**
	 * Invoke the method to change this policy's variable with the given parameter
	 * @param time 	The integer value that the policy's variable will be set to
	 */
	public void applyChange(int time) {
		change(time);
	}
}
