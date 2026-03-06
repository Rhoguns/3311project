package booking;

import admin.NotificationPolicy;
import booking.model.Client;
import booking.model.Service;
import booking.state.BookingState;
import consultant.AvailabilitySlot;
import consultant.Consultant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * BookingService class that manages Booking objects and booking queries.
 */
public class BookingService {

    private final Map<String, Booking> bookings = new HashMap<>();

    /**
     * Create a new booking request using an approved consultant and an available slot.
     */
    public Booking createBooking(Client client, Consultant consultant, Service service, AvailabilitySlot slot) {
        if (client == null || consultant == null || service == null || slot == null) {
            throw new IllegalArgumentException("Client, consultant, service and slot are required.");
        }
        if (!"APPROVED".equals(consultant.getStatus())) {
            throw new IllegalStateException("Consultant must be approved before bookings can be requested.");
        }
        if (!consultant.getAvailabilitySlots().contains(slot) || !slot.isAvailable()) {
            throw new IllegalStateException("Selected slot is not available for this consultant.");
        }

        String bookingId = UUID.randomUUID().toString().substring(0, 8);
        double totalAmount = service.getPrice();

        Booking booking = new Booking(bookingId, totalAmount, client, consultant, service, slot);
        bookings.put(bookingId, booking);
        slot.markUnavailable();

        System.out.println("Created " + booking);
        if (NotificationPolicy.getInstance().getNotificationPolicy()) {
            System.out.println("[Notification] Booking request submitted for client " + client.getName() + ".");
        }
        return booking;
    }

    public void cancelBooking(String bookingId) {
        Booking booking = findBooking(bookingId);
        booking.cancel();
    }

    public void updateBookingState(String bookingId, BookingState newState) {
        Booking booking = findBooking(bookingId);
        System.out.println("Booking " + bookingId + ": State updated to " + newState);
        booking.setState(newState);
    }

    public Booking getBooking(String bookingId) {
        return findBooking(bookingId);
    }

    public List<Booking> getBookingsForClient(String clientId) {
        List<Booking> result = new ArrayList<>();
        for (Booking booking : bookings.values()) {
            if (booking.getClient().getClientId().equals(clientId)) {
                result.add(booking);
            }
        }
        return result;
    }

    public List<Booking> getBookingsForConsultant(String consultantId) {
        List<Booking> result = new ArrayList<>();
        for (Booking booking : bookings.values()) {
            if (booking.getConsultant().getConsultantId().equals(consultantId)) {
                result.add(booking);
            }
        }
        return result;
    }

    private Booking findBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found: " + bookingId);
        }
        return booking;
    }

    public Iterable<Booking> getBookingHistory() {
        return bookings.values();
    }
}