package admin;

public class CancellationPolicy {
	private ArrayList<BookingState> finalStates;
	
	private static final CancellationPolicy instance = new CancellationPolicy();
	
	private CancellationPolicy() {
		finalStates = new ArrayList<>();
		finalStates.add(new CompletedState());
		finalStates.add(new CancelledState());
		finalStates.add(new RejectedState());
	}
	
	public static CancellationPolicy getInstance() {
		return instance;
	}
	
	/**
	 * Return the policy's states that don't allow cancellation.
	 * @return the ArrayList<BookingState> representing the states that the policy prohibits cancellations in.
	 */
	public ArrayList<BookingState> getCancellationPolicy() {
		return this.finalStates;
	}
	/**
	 * Change the value of this policy with the passed parameter
	 * @param newStates 	The ArrayList<BookingState> value that the policy's variable will be set to
	 */
	private void change(ArrayList<BookingState> newStates) {
		this.finalStates.clear();
		for(int i = 0; i < newStates.size(); i++) {
			this.finalStates.add(newStates.get(i));
		}
		System.out.println("Cancellation Policy changed.");
	}
	
	/**
	 * Invoke the method to change this policy's variable with the given parameter
	 * @param newStates 	The ArrayList<BookingState> value that the policy's variable will be set to
	 */
	public void applyChange(ArrayList<BookingState> newStates) {
		change(newStates);
	}
}

