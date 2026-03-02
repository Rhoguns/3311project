package booking.state;

import booking.Booking;


public class RejectedState implements BookingState {

    @Override
    public void handle(Booking booking) {
        System.out.println("Booking " + booking.getBookingId()
                + ": Booking was rejected. No further action.");
    }

    @Override
    public String toString() {
        return "REJECTED";
    }
}
