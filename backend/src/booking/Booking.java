package booking;

import booking.state.BookingState;
import booking.state.CancelledState;
import booking.state.RequestedState;

/**
 * Booking class that represents a booking in the system.
 * It uses the State design pattern to manage its lifecycle states
 * (Requested, Confirmed, Completed, Rejected, Cancelled).
 */
public class Booking {

    private String bookingId;
    private double totalAmount;
    private BookingState currentState;

    public Booking(String bookingId, double totalAmount) {
        this.bookingId = bookingId;
        this.totalAmount = totalAmount;
        this.currentState = new RequestedState(); 
    }


    /**
     * Set the current state of this booking.
     *
     * @param state the new BookingState
     */
    public void setState(BookingState state) {
        this.currentState = state;
    }

    /**
     * @return the current BookingState
     */
    public BookingState getState() {
        return currentState;
    }

        /**
        * Proceed to the next state based on the current state logic.
        * This method is typically called by the BookingService to advance
        * the booking through its lifecycle.
        */
        
    public void proceed() {
        currentState.handle(this);
    }

    /**
     * Cancel the booking (transitions to CancelledState).
     * Only allowed if the booking is not already in a terminal state
     * (Completed, Rejected, or Cancelled).
     */
    public void cancel() {
        String stateName = currentState.toString();
        if (stateName.equals("COMPLETED") || stateName.equals("REJECTED")
                || stateName.equals("CANCELLED")) {
            System.out.println("Booking " + bookingId
                    + ": Cannot cancel — already in terminal state " + stateName + ".");
            return;
        }
        System.out.println("Booking " + bookingId + ": Cancelling from " + stateName + ".");
        setState(new CancelledState());
    }

    

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "Booking{id='" + bookingId + "', amount=" + totalAmount
                + ", state=" + currentState + '}';
    }
}
