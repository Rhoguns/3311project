package admin;

public class RefundPolicyCommand implements PolicyCommand{
	private RefundPolicy policy;
	private int newRefundWindowTime;
	
	public RefundPolicyCommand(RefundPolicy policy, int refundWindowTime) {
		this.policy = policy;
		this.newRefundWindowTime = refundWindowTime;
	}
	
	@Override
	public boolean update() {
		policy.applyChange(newRefundWindowTime);
		return true;
	}
}
