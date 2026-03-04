package admin;

public class CancellationPolicy {
	private int cancellationWindowTime;
	
	private static final CancellationPolicy instance = new CancellationPolicy();
	
	private CancellationPolicy() {
		this.cancellationWindowTime = 0;
	}
	
	public static CancellationPolicy getInstance() {
		return instance;
	}
	
	/**
	 * Return the cancellation window time of the policy.
	 * @return the integer representing the policy's cancellation window time
	 */
	public int getCancellationPolicy() {
		return this.cancellationWindowTime;
	}
	/**
	 * Change the value of this policy with the passed parameter
	 * @param time 	The integer value that the policy's variable will be set to
	 */
	private void change(int time) {
		this.cancellationWindowTime = time;
	}
	
	/**
	 * Invoke the method to change this policy's variable with the given parameter
	 * @param time 	The integer value that the policy's variable will be set to
	 */
	public void applyChange(int time) {
		change(time);
	}
}
