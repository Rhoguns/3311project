package payment;

import admin.NotificationPolicy;
import admin.RefundPolicy;
import booking.Booking;
import booking.state.PaidState;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import payment.method.PaymentMethod;
import payment.state.PaymentPendingState;
import payment.state.PaymentState;

/**
 * Payment record with State Pattern lifecycle and static processing methods.
 */
public class Payment {

    private static final List<Payment> allPayments = new ArrayList<>();

    private String transactionId;
    private String bookingId;
    private String clientId;
    private double amount;
    private PaymentState state;
    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String description;

    public Payment(String transactionId, String bookingId, String clientId,
            double amount, PaymentMethod paymentMethod) {
        this.transactionId = transactionId;
        this.bookingId = bookingId;
        this.clientId = clientId;
        this.amount = amount;
        this.state = new PaymentPendingState();
        this.paymentMethod = paymentMethod;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.description = "";
    }

    public void setState(PaymentState state) {
        this.state = state;
        this.updatedAt = LocalDateTime.now();
    }

    public PaymentState getState() {
        return state;
    }

    public String getStateName() {
        return state.getStateName();
    }

    public void markSuccess() {
        state.paymentSuccess(this);
    }

    public void markFailed() {
        state.paymentFail(this);
    }

    public void refund() {
        state.refund(this);
    }

    public static Payment processPayment(Booking booking, PaymentMethod paymentMethod) {
        if (booking == null) {
            throw new IllegalStateException("Booking does not exist (null value), cannot process payment");
        }

        String stateName = booking.getState().toString();
        if (!"PENDING_PAYMENT".equals(stateName)) {
            throw new IllegalStateException("Booking " + booking.getBookingId()
                    + " is in state " + stateName + ". Must be PENDING_PAYMENT.");
        }

        System.out.println("\n[Payment] Validating payment method...");
        paymentMethod.validate();
        System.out.println("[Payment] Validated: " + paymentMethod.getMaskedDetails());

        Payment payment = new Payment("PENDING-" + booking.getBookingId(), booking.getBookingId(),
                paymentMethod.getClientId(), booking.getTotalAmount(), paymentMethod);
        payment.setDescription("Payment for booking " + booking.getBookingId());
        allPayments.add(payment);

        System.out.println("[Payment] Processing $" + String.format("%.2f", booking.getTotalAmount()) + "...");
        String transactionId = paymentMethod.processPayment(booking.getTotalAmount());

        payment.transactionId = transactionId;
        payment.markSuccess();

        booking.setState(new PaidState());
        System.out.println("[Payment] Booking " + booking.getBookingId() + " -> PAID.");

        sendPaymentConfirmation(payment);
        return payment;
    }

    public static Payment processRefund(String transactionId, PaymentMethod paymentMethod) {
        Payment payment = getPaymentByTransactionId(transactionId);
        if (!payment.getStateName().equals(RefundPolicy.getInstance().getRefundPolicy())) {
            throw new IllegalStateException("Can only refund SUCCESSFUL payments. Current: " + payment.getStateName());
        }

        boolean refunded = paymentMethod.processRefund(transactionId, payment.getAmount());
        if (refunded) {
            payment.refund();
            payment.setDescription(payment.getDescription() + " [REFUNDED]");
            System.out.println("[Payment] Refund processed for " + transactionId);
        }
        return payment;
    }

    public static List<Payment> getPaymentHistory(String clientId) {
        List<Payment> result = new ArrayList<>();
        for (Payment payment : allPayments) {
            if (payment.getClientId().equals(clientId)) {
                result.add(payment);
            }
        }
        return result;
    }

    public static List<Payment> getPaymentsByState(String clientId, String stateName) {
        List<Payment> result = new ArrayList<>();
        for (Payment payment : allPayments) {
            if (payment.getClientId().equals(clientId)
                    && payment.getStateName().equals(stateName)) {
                result.add(payment);
            }
        }
        return result;
    }

    public static Payment getPaymentByTransactionId(String transactionId) {
        for (Payment payment : allPayments) {
            if (payment.getTransactionId().equals(transactionId)) {
                return payment;
            }
        }
        throw new IllegalArgumentException("Payment not found: " + transactionId);
    }

    public static void clearHistory() {
        allPayments.clear();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getClientId() {
        return clientId;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Payment{txnId='" + transactionId + "'"
                + ", bookingId='" + bookingId + "'"
                + ", amount=$" + String.format("%.2f", amount)
                + ", state=" + state.getStateName()
                + ", method=" + paymentMethod.getType()
                + ", created=" + createdAt + "}";
    }

    private static void sendPaymentConfirmation(Payment payment) {
        if (NotificationPolicy.getInstance().getNotificationPolicy()) {
            System.out.println("\n===== PAYMENT CONFIRMATION =====");
            System.out.println("Transaction ID: " + payment.getTransactionId());
            System.out.println("Booking ID:     " + payment.getBookingId());
            System.out.println("Amount:         $" + String.format("%.2f", payment.getAmount()));
            System.out.println("State:          " + payment.getStateName());
            System.out.println("Method:         " + payment.getPaymentMethod().getType());
            System.out.println("Date:           " + payment.getCreatedAt());
            System.out.println("================================\n");
        }
    }
}
