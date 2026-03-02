package booking;

import booking.model.Client;
import booking.model.Consultant;
import booking.model.Service;
import booking.state.BookingState;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// BookingService class that manages the lifecycle of Booking objects
 
public class BookingService {

    private final Map<String, Booking> bookings = new HashMap<>();

    /**
     * Create a new booking for the given client, consultant, and service.
     * The booking starts in the {@link booking.state.RequestedState}.
     *
     * @param client     the client requesting the booking
     * @param consultant the consultant assigned to the booking
     * @param service    the service being booked
     * @return the newly created Booking
     */
    public Booking createBooking(Client client, Consultant consultant, Service service) {
        String bookingId = UUID.randomUUID().toString().substring(0, 8);
        double totalAmount = service.getPrice();

        Booking booking = new Booking(bookingId, totalAmount);
        bookings.put(bookingId, booking);

        System.out.println("Created " + booking
                + " for " + client.getName()
                + " with " + consultant.getName()
                + " [" + service.getName() + "]");
        return booking;
    }

    /**
     * Cancel an existing booking by its ID.
     *
     * @param bookingId the unique identifier of the booking to cancel
     * @throws IllegalArgumentException if the booking ID is not found
     */
    public void cancelBooking(String bookingId) {
        Booking booking = findBooking(bookingId);
        booking.cancel();
    }

    /**
     * Force-update the state of an existing booking.
     *
     * @param bookingId the unique identifier of the booking
     * @param newState  the new BookingState to set
     * @throws IllegalArgumentException if the booking ID is not found
     */
    public void updateBookingState(String bookingId, BookingState newState) {
        Booking booking = findBooking(bookingId);
        System.out.println("Booking " + bookingId + ": State updated to " + newState);
        booking.setState(newState);
    }

    /**
     * Retrieve a booking by its ID.
     *
     * @param bookingId the booking identifier
     * @return the Booking
     * @throws IllegalArgumentException if not found
     */
    public Booking getBooking(String bookingId) {
        return findBooking(bookingId);
    }


    private Booking findBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found: " + bookingId);
        }
        return booking;
    }
}
