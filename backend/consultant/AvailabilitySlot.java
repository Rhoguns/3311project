package consultant;

import java.util.Date;

public class AvailabilitySlot {

    private String slotId;
    private Date date;
    private String startTime;
    private String endTime;
    private boolean isAvailable;

    public AvailabilitySlot(String slotId, Date date, String startTime, String endTime) {
        this.slotId = slotId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAvailable = true;  
    }

    // Business Logic
    public void markAvailable() {
        this.isAvailable = true;
    }

    public void markUnavailable() {
        this.isAvailable = false;
    }

    // Getters
    public String getSlotId() {
        return slotId;
    }

    public Date getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}
