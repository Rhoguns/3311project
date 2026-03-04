package consultant;

import java.util.ArrayList;
import java.util.List;

import booking.Booking;
import booking.states.ConfirmedState;
import booking.states.RejectedState;
import booking.states.CompletedState;

public class Consultant {

    private String consultantId;
    private String name;
    private String email;
    private String status;  

    private List<AvailabilitySlot> availabilitySlots;

    public Consultant(String consultantId, String name, String email) {
        this.consultantId = consultantId;
        this.name = name;
        this.email = email;
        this.status = "PENDING";
        this.availabilitySlots = new ArrayList<>();
    }


    // Register consultant
    public void register() {
        this.status = "PENDING";
    }

    // Manage availability
    public void manageAvailability(List<AvailabilitySlot> slots) {
        this.availabilitySlots = slots;
    }

    public void addAvailabilitySlot(AvailabilitySlot slot) {
        this.availabilitySlots.add(slot);
    }

    // Booking Actions
    public void acceptBooking(Booking booking) {
        booking.setState(new ConfirmedState());
    }

    public void rejectBooking(Booking booking) {
        booking.setState(new RejectedState());
    }

    public void completeBooking(Booking booking) {
        booking.setState(new CompletedState());
    }

    
    // Getters
    public String getConsultantId() {
        return consultantId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public List<AvailabilitySlot> getAvailabilitySlots() {
        return availabilitySlots;
    }
}
