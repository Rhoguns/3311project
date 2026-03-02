package booking.state;

import booking.Booking;


public class PaidState implements BookingState {

    @Override
    public void handle(Booking booking) {
        System.out.println("Booking " + booking.getBookingId()
                + ": Service delivered. Booking completed.");
        booking.setState(new CompletedState());
    }

    @Override
    public String toString() {
        return "PAID";
    }
}
