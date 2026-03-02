package booking.model;

/**
 * Represents a consultant who provides a service.
 */
public class Consultant {

    private String consultantId;
    private String name;
    private String specialization;

    public Consultant(String consultantId, String name, String specialization) {
        this.consultantId = consultantId;
        this.name = name;
        this.specialization = specialization;
    }

    public String getConsultantId() {
        return consultantId;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    @Override
    public String toString() {
        return "Consultant{id='" + consultantId + "', name='" + name + "'}";
    }
}
