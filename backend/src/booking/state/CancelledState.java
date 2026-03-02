package booking.state;

import booking.Booking;


public class CancelledState implements BookingState {

    @Override
    public void handle(Booking booking) {
        System.out.println("Booking " + booking.getBookingId()
                + ": Booking was cancelled. No further action.");
    }

    @Override
    public String toString() {
        return "CANCELLED";
    }
}
