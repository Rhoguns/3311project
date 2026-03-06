package consultant;

import java.util.ArrayList;
import java.util.List;

import admin.Admin;
import booking.Booking;
import booking.state.ConfirmedState;
import booking.state.PendingPaymentState;
import booking.state.RejectedState;
import booking.state.CompletedState;

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
        register();
    }


    // Register consultant
    public void register() {
        boolean approved = Admin.getInstance().reviewConsultant(this);
    	if(approved) {
    		System.out.println("APPROVED");
    		this.status = "APPROVED";
    	}
    	else {
    		System.out.println("REJECTED");
    		this.status = "REJECTED";
    	}
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
        if(booking == null) {
    		return;
    	}
        booking.setState(new ConfirmedState());
        booking.setState(new PendingPaymentState());
    }

    public void rejectBooking(Booking booking) {
        if(booking == null) {
    		return;
    	}
        booking.setState(new RejectedState());
    }

    public void completeBooking(Booking booking) {
        if(booking == null) {
    		return;
    	}
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
