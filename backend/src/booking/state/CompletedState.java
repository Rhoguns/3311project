package booking.state;

import booking.Booking;


public class CompletedState implements BookingState {

    @Override
    public void handle(Booking booking) {
        System.out.println("Booking " + booking.getBookingId()
                + ": Already completed. No further action.");
    }

    @Override
    public String toString() {
        return "COMPLETED";
    }
}
