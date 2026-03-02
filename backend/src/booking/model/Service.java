package booking.model;

/**
 * Represents a service that can be booked.
 */
public class Service {

    private String serviceId;
    private String name;
    private double price;

    public Service(String serviceId, String name, double price) {
        this.serviceId = serviceId;
        this.name = name;
        this.price = price;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Service{id='" + serviceId + "', name='" + name + "', price=" + price + '}';
    }
}
