import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

import admin.Admin;
import admin.NotificationPolicyCommand;
import admin.CancellationPolicyCommand;
import admin.CancellationPolicy;
import admin.NotificationPolicy;
import admin.PricingPolicy;
import admin.PricingPolicyCommand;
import admin.RefundPolicy;
import admin.RefundPolicyCommand;
import booking.Booking;
import booking.BookingService;
import booking.model.Client;
import booking.model.Service;
import consultant.AvailabilitySlot;
import consultant.Consultant;
import payment.Payment;
import payment.method.BankAccountPayment;
import payment.method.CreditCardPayment;
import payment.method.DebitCardPayment;
import payment.method.PayPalPayment;
import payment.method.PaymentMethod;

public class Project3311Test {

    // Shared demo data.
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final BookingService BOOKING_SERVICE = new BookingService();
    private static final Admin ADMIN = Admin.getInstance();
    private static final DateTimeFormatter SLOT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final Client CLIENT = new Client("C-100", "Alice Client", "alice@example.com");
    private static final Consultant CONSULTANT_1 = new Consultant("CONS-1", "Bob Consultant", "bob@consulting.com");
    private static final Consultant CONSULTANT_2 = new Consultant("CONS-2", "Cara Consultant", "cara@consulting.com");
    private static final Consultant INVALID_CONSULTANT = new Consultant("CONS-3", "Pending Consultant", "pending@consulting.com");

    private static final List<Consultant> CONSULTANTS = new ArrayList<>();
    private static final List<Service> SERVICES = new ArrayList<>();

    public static void main(String[] args) {
        seedDemoData();

        boolean running = true;
        while (running) {
            System.out.println("\n----- CONSULTING SYSTEM DEMO -----");
            System.out.println("1. Login as Client");
            System.out.println("2. Login as Consultant");
            System.out.println("3. Login as Admin");
            System.out.println("0. Exit");
            System.out.print("Choose a role: ");

            switch (readInt()) {
            case 1:
                clientMenu();
                break;
            case 2:
                consultantMenu();
                break;
            case 3:
                adminMenu();
                break;
            case 0:
                running = false;
                break;
            default:
                System.out.println("Invalid choice.");
            }
        }

        System.out.println("Demo ended.");
    }

    // Load sample users, services, slots, and policies.
    private static void seedDemoData() {
        CONSULTANTS.clear();
        SERVICES.clear();
        Payment.clearHistory();
        PaymentMethod.clearAllPaymentMethods();

        CONSULTANTS.add(CONSULTANT_1);
        CONSULTANTS.add(CONSULTANT_2);
        CONSULTANTS.add(INVALID_CONSULTANT);

        SERVICES.add(new Service("S-1", "Career Strategy Session", 60, 120.0));
        SERVICES.add(new Service("S-2", "Technical Architecture Review", 90, 220.0));
        SERVICES.add(new Service("S-3", "Resume and Interview Coaching", 45, 95.0));

        CONSULTANT_1.addAvailabilitySlot(new AvailabilitySlot("B1",
                LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(3).plusHours(1)));
        CONSULTANT_1.addAvailabilitySlot(new AvailabilitySlot("B2",
                LocalDateTime.now().plusDays(4), LocalDateTime.now().plusDays(4).plusHours(1)));
        CONSULTANT_2.addAvailabilitySlot(new AvailabilitySlot("C1",
                LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(2)));
        CONSULTANT_2.addAvailabilitySlot(new AvailabilitySlot("C2",
                LocalDateTime.now().plusHours(8), LocalDateTime.now().plusHours(9)));

        ADMIN.approveConsultantRegistration(CONSULTANT_1);
        ADMIN.approveConsultantRegistration(CONSULTANT_2);

        PaymentMethod.addPaymentMethod(new PayPalPayment(
                PaymentMethod.generatePaymentMethodId(),
                CLIENT.getClientId(),
                "Default PayPal",
                "alice.paypal@example.com"));
    }

    // Client menu.
    private static void clientMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n----- CLIENT MENU -----");
            System.out.println("1. Browse consulting services");
            System.out.println("2. Request a booking");
            System.out.println("3. Cancel a booking");
            System.out.println("4. View booking history");
            System.out.println("5. Process payment");
            System.out.println("6. Manage payment methods");
            System.out.println("7. View payment history");
            System.out.println("8. Request refund");
            System.out.println("0. Logout");
            System.out.print("Select an action: ");

            switch (readInt()) {
            case 1:
                browseServices();
                break;
            case 2:
                requestBooking();
                break;
            case 3:
                cancelBooking();
                break;
            case 4:
                viewClientBookings();
                break;
            case 5:
                processBookingPayment();
                break;
            case 6:
                paymentMethodMenu();
                break;
            case 7:
                viewPaymentHistory();
                break;
            case 8:
                requestRefund();
                break;
            case 0:
                back = true;
                break;
            default:
                System.out.println("Invalid choice.");
            }
        }
    }

    // Consultant menu.
    private static void consultantMenu() {
        Consultant consultant = chooseConsultant(true);
        if (consultant == null) {
            return;
        }

        boolean back = false;
        while (!back) {
            System.out.println("\n----- CONSULTANT MENU: " + consultant.getName() + " -----");
            System.out.println("1. Manage availability");
            System.out.println("2. Accept a booking request");
            System.out.println("3. Reject a booking request");
            System.out.println("4. Complete a booking");
            System.out.println("5. View my bookings");
            System.out.println("6. Update service pricing");
            System.out.println("0. Logout");
            System.out.print("Select an action: ");

            switch (readInt()) {
            case 1:
                manageAvailability(consultant);
                break;
            case 2:
                acceptBookingRequest(consultant);
                break;
            case 3:
                rejectBookingRequest(consultant);
                break;
            case 4:
                completeBooking(consultant);
                break;
            case 5:
                viewConsultantBookings(consultant);
                break;
            case 6:
                updateServicePricing(consultant);
                break;
            case 0:
                back = true;
                break;
            default:
                System.out.println("Invalid choice.");
            }
        }
    }

    // Admin menu.
    private static void adminMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n----- ADMIN MENU -----");
            System.out.println("1. Approve consultant registration");
            System.out.println("2. Reject consultant registration");
            System.out.println("3. Configure cancellation window");
            System.out.println("4. Configure pricing policy");
            System.out.println("5. Toggle notifications");
            System.out.println("6. Configure refund policy");
            System.out.println("7. View current policies");
            System.out.println("0. Logout");
            System.out.print("Select an action: ");

            switch (readInt()) {
            case 1:
                approveConsultant();
                break;
            case 2:
                rejectConsultant();
                break;
            case 3:
                configureCancellationPolicy();
                break;
            case 4:
                configurePricingPolicy();
                break;
            case 5:
                toggleNotifications();
                break;
            case 6:
                configureRefundPolicy();
                break;
            case 7:
                viewPolicies();
                break;
            case 0:
                back = true;
                break;
            default:
                System.out.println("Invalid choice.");
            }
        }
    }

    // UC1: Browse services and open slots.
    private static void browseServices() {
        System.out.println("\nAvailable consulting services:");
        for (Service service : SERVICES) {
            System.out.println("- " + formatService(service));
        }

        System.out.println("\nApproved consultants and available slots:");
        for (Consultant consultant : CONSULTANTS) {
            if (!"APPROVED".equals(consultant.getStatus())) {
                continue;
            }
            System.out.println("- " + consultant.getName());
            for (AvailabilitySlot slot : consultant.getAvailabilitySlots()) {
                if (slot.isAvailable()) {
                    System.out.println("    " + formatSlot(slot));
                }
            }
        }
    }

    // UC2: Create a requested booking.
    private static void requestBooking() {
        browseServices();
        Consultant consultant = chooseConsultant(true);
        Service service = chooseService();
        AvailabilitySlot slot = consultant == null ? null : chooseAvailableSlot(consultant);
        if (consultant == null || service == null || slot == null) {
            return;
        }

        try {
            Booking booking = BOOKING_SERVICE.createBooking(CLIENT, consultant, service, slot);
            System.out.println("Booking request created in state: " + booking.getState());
        } catch (Exception e) {
            System.out.println("Could not create booking: " + e.getMessage());
        }
    }

    // UC3: Cancel a booking.
    private static void cancelBooking() {
        Booking booking = chooseClientBooking();
        if (booking == null) {
            return;
        }

        try {
            BOOKING_SERVICE.cancelBooking(booking.getBookingId());
            System.out.println("Booking " + booking.getBookingId() + " cancelled.");
            System.out.println("Current state: " + booking.getState());
        } catch (Exception e) {
            System.out.println("Could not cancel booking: " + e.getMessage());
            System.out.println("Current state: " + booking.getState());
        }
    }

    // UC4: View client booking history.
    private static void viewClientBookings() {
        List<Booking> bookings = BOOKING_SERVICE.getBookingsForClient(CLIENT.getClientId());
        if (bookings.isEmpty()) {
            System.out.println("No bookings found for " + CLIENT.getName() + ".");
            return;
        }

        System.out.println("\nBooking history for " + CLIENT.getName() + ":");
        printItems(bookings);
    }

    // UC5: Pay for a pending booking.
    private static void processBookingPayment() {
        List<Booking> payable = getClientBookingsByState("PENDING_PAYMENT");
        if (payable.isEmpty()) {
            System.out.println("No confirmed bookings are awaiting payment.");
            return;
        }

        System.out.println("\nBookings awaiting payment:");
        Booking booking = chooseFromList(payable, "Choose booking number: ", Project3311Test::formatBooking);
        PaymentMethod method = choosePaymentMethod();
        if (booking == null || method == null) {
            return;
        }

        try {
            Payment.processPayment(booking, method);
        } catch (Exception e) {
            System.out.println("Payment failed: " + e.getMessage());
        }
    }

    // UC6: Manage saved payment methods.
    private static void paymentMethodMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n----- PAYMENT METHOD MENU -----");
            System.out.println("1. View saved methods");
            System.out.println("2. Add a method");
            System.out.println("3. Update a method label");
            System.out.println("4. Remove a method");
            System.out.println("0. Back");
            System.out.print("Select an action: ");

            switch (readInt()) {
            case 1:
                listPaymentMethods();
                break;
            case 2:
                addPaymentMethod();
                break;
            case 3:
                updatePaymentMethod();
                break;
            case 4:
                removePaymentMethod();
                break;
            case 0:
                back = true;
                break;
            default:
                System.out.println("Invalid choice.");
            }
        }
    }

    // UC7: View payment history by state.
    private static void viewPaymentHistory() {
        List<Payment> history = Payment.getPaymentHistory(CLIENT.getClientId());
        if (history.isEmpty()) {
            System.out.println("No payment history available.");
            return;
        }

        printPaymentGroup("Successful payments", Payment.getPaymentsByState(CLIENT.getClientId(), "SUCCESSFUL"));
        printPaymentGroup("Pending payments", Payment.getPaymentsByState(CLIENT.getClientId(), "PENDING"));
        printPaymentGroup("Refunded payments", Payment.getPaymentsByState(CLIENT.getClientId(), "REFUNDED"));
        printPaymentGroup("All recorded payments", history);
    }

    // Request a refund and let the backend enforce the current refund policy.
    private static void requestRefund() {
        List<Payment> history = Payment.getPaymentHistory(CLIENT.getClientId());
        if (history.isEmpty()) {
            System.out.println("No payments available for refund.");
            return;
        }

        Payment payment = chooseFromList(history, "Choose payment number: ", Payment::toString);
        if (payment == null) {
            return;
        }

        try {
            Payment.processRefund(payment.getTransactionId(), payment.getPaymentMethod());
            System.out.println("Refund result: " + payment.getStateName());
        } catch (Exception e) {
            System.out.println("Refund failed: " + e.getMessage());
            System.out.println("Current payment state: " + payment.getStateName());
            System.out.println("Refund policy currently allows: " + RefundPolicy.getInstance().getRefundPolicy());
        }
    }


    // UC8: Manage consultant slots.
    private static void manageAvailability(Consultant consultant) {
        System.out.println("Current slots for " + consultant.getName() + ":");
        printItems(consultant.getAvailabilitySlots());

        System.out.println("1. Add a new slot");
        System.out.println("2. Mark a slot available");
        System.out.println("3. Mark a slot unavailable");
        System.out.print("Choose: ");

        switch (readInt()) {
        case 1:
            System.out.print("Enter slot id: ");
            String slotId = SCANNER.nextLine().trim();
            consultant.addAvailabilitySlot(new AvailabilitySlot(
                    slotId,
                    LocalDateTime.now().plusDays(5),
                    LocalDateTime.now().plusDays(5).plusHours(1)));
            System.out.println("Added a future slot for demo.");
            break;
        case 2:
            AvailabilitySlot openSlot = chooseAnySlot(consultant);
            if (openSlot != null) {
                openSlot.markAvailable();
                System.out.println("Slot marked available.");
            }
            break;
        case 3:
            AvailabilitySlot closeSlot = chooseAnySlot(consultant);
            if (closeSlot != null) {
                closeSlot.markUnavailable();
                System.out.println("Slot marked unavailable.");
            }
            break;
        default:
            System.out.println("No change made.");
        }
    }

    // UC9: Accept a request.
    private static void acceptBookingRequest(Consultant consultant) {
        updateBookingState(consultant, "REQUESTED", true);
    }

    // UC9: Reject a request.
    private static void rejectBookingRequest(consultant.Consultant consultant) {
        updateBookingState(consultant, "REQUESTED", false);
    }

    // UC10: Complete a paid booking.
    private static void completeBooking(Consultant consultant) {
        Booking booking = chooseConsultantBookingByState(consultant, "PAID");
        if (booking == null) {
            return;
        }

        try {
            consultant.completeBooking(booking);
            System.out.println("Booking is now in state: " + booking.getState());
        } catch (Exception e) {
            System.out.println("Could not complete booking: " + e.getMessage());
        }
    }

    // View consultant bookings.
    private static void viewConsultantBookings(Consultant consultant) {
        List<Booking> bookings = BOOKING_SERVICE.getBookingsForConsultant(consultant.getConsultantId());
        if (bookings.isEmpty()) {
            System.out.println("No bookings found for this consultant.");
            return;
        }
        printItems(bookings);
    }

    // Consultants can update a service price within the current pricing policy.
    private static void updateServicePricing(Consultant consultant) {
        Service service = chooseService();
        if (service == null) {
            return;
        }

        double oldPrice = service.getPrice();
        System.out.println("Current service price: $" + oldPrice);
        System.out.println("Allowed range: $" + PricingPolicy.getInstance().getMinPrice()
                + " - $" + PricingPolicy.getInstance().getMaxPrice());
        System.out.print("Enter the new price: ");
        double requestedPrice = readDouble();

        service.setPrice(requestedPrice);
        System.out.println(consultant.getName() + " updated " + service.getName() + ".");
        if (service.getPrice() != requestedPrice) {
            System.out.println("Requested price was outside policy range, so it was adjusted to $"
                    + service.getPrice());
        } else {
            System.out.println("New service price: $" + service.getPrice());
        }
    }


    // UC11: Approve consultant registration.
    private static void approveConsultant() {
        Consultant consultant = chooseConsultant(false);
        if (consultant != null) {
            ADMIN.approveConsultantRegistration(consultant);
        }
    }

    // UC11: Reject consultant registration.
    private static void rejectConsultant() {
        Consultant consultant = chooseConsultant(false);
        if (consultant != null) {
            ADMIN.rejectConsultantRegistration(consultant);
        }
    }

    // UC12: Update cancellation rules.
    private static void configureCancellationPolicy() {
        System.out.print("Enter minimum hours before start required for cancellation: ");
        int hours = readInt();
        ADMIN.configurePolicy(new CancellationPolicyCommand(CancellationPolicy.getInstance(), hours));
        System.out.println("Cancellation window is now " + CancellationPolicy.getInstance().getMinimumHoursBeforeStart() + " hours before start.");
    }

    // UC12: Update pricing rules.
    private static void configurePricingPolicy() {
        System.out.print("Enter new minimum price: ");
        double min = readDouble();
        System.out.print("Enter new maximum price: ");
        double max = readDouble();
        if (min > max) {
            System.out.println("Minimum price cannot be greater than maximum price.");
            return;
        }
        ADMIN.configurePolicy(new PricingPolicyCommand(PricingPolicy.getInstance(), min, max));
        System.out.println("Pricing range updated to $" + PricingPolicy.getInstance().getMinPrice()
                + " - $" + PricingPolicy.getInstance().getMaxPrice());
    }

    // UC12: Toggle notifications.
    private static void toggleNotifications() {
        boolean enabled = NotificationPolicy.getInstance().getNotificationPolicy();
        ADMIN.configurePolicy(new NotificationPolicyCommand(NotificationPolicy.getInstance(), !enabled));
        System.out.println("Notifications are now "
                + (NotificationPolicy.getInstance().getNotificationPolicy() ? "enabled" : "disabled"));
    }

    // UC12: Update refund rules.
    private static void configureRefundPolicy() {
        System.out.print("Enter payment state eligible for refund (usually SUCCESSFUL): ");
        String state = SCANNER.nextLine().trim().toUpperCase();
        if (state.isEmpty()) {
            System.out.println("Refund state cannot be empty.");
            return;
        }
        ADMIN.configurePolicy(new RefundPolicyCommand(RefundPolicy.getInstance(), state));
        System.out.println("Refundable payment state is now: " + RefundPolicy.getInstance().getRefundPolicy());
    }

    // View current policy settings.
    private static void viewPolicies() {
        System.out.println("Cancellation window: " + CancellationPolicy.getInstance().getMinimumHoursBeforeStart() + " hours before start");
        System.out.println("Pricing range: $" + PricingPolicy.getInstance().getMinPrice() + " - $" + PricingPolicy.getInstance().getMaxPrice());
        System.out.println("Notifications enabled: " + NotificationPolicy.getInstance().getNotificationPolicy());
        System.out.println("Refundable payment state: " + RefundPolicy.getInstance().getRefundPolicy());
    }

    // List saved payment methods.
    private static void listPaymentMethods() {
        List<PaymentMethod> methods = PaymentMethod.getPaymentMethods(CLIENT.getClientId());
        if (methods.isEmpty()) {
            System.out.println("No saved payment methods.");
            return;
        }
        printItems(methods);
    }

    // Add a payment method using user input.
    private static void addPaymentMethod() {
        System.out.println("1. Credit card");
        System.out.println("2. Debit card");
        System.out.println("3. PayPal");
        System.out.println("4. Bank transfer");
        System.out.print("Choose a method type: ");

        PaymentMethod method;
        String paymentMethodId = PaymentMethod.generatePaymentMethodId();
        try {
            switch (readInt()) {
            case 1:
                System.out.print("Enter label: ");
                String creditLabel = SCANNER.nextLine().trim();
                System.out.print("Enter card number: ");
                String creditCardNumber = SCANNER.nextLine().trim();
                System.out.print("Enter expiry date (MM/YY): ");
                String creditExpiry = SCANNER.nextLine().trim();
                System.out.print("Enter CVV: ");
                String creditCvv = SCANNER.nextLine().trim();
                System.out.print("Enter card holder name: ");
                String creditHolder = SCANNER.nextLine().trim();
                method = new CreditCardPayment(paymentMethodId, CLIENT.getClientId(), creditLabel,
                        creditCardNumber, creditExpiry, creditCvv, creditHolder);
                break;
            case 2:
                System.out.print("Enter label: ");
                String debitLabel = SCANNER.nextLine().trim();
                System.out.print("Enter card number: ");
                String debitCardNumber = SCANNER.nextLine().trim();
                System.out.print("Enter expiry date (MM/YY): ");
                String debitExpiry = SCANNER.nextLine().trim();
                System.out.print("Enter CVV: ");
                String debitCvv = SCANNER.nextLine().trim();
                System.out.print("Enter card holder name: ");
                String debitHolder = SCANNER.nextLine().trim();
                method = new DebitCardPayment(paymentMethodId, CLIENT.getClientId(), debitLabel,
                        debitCardNumber, debitExpiry, debitCvv, debitHolder);
                break;
            case 3:
                System.out.print("Enter label: ");
                String paypalLabel = SCANNER.nextLine().trim();
                System.out.print("Enter PayPal email: ");
                String paypalEmail = SCANNER.nextLine().trim();
                method = new PayPalPayment(paymentMethodId, CLIENT.getClientId(), paypalLabel,
                        paypalEmail);
                break;
            case 4:
                System.out.print("Enter label: ");
                String bankLabel = SCANNER.nextLine().trim();
                System.out.print("Enter account number: ");
                String accountNumber = SCANNER.nextLine().trim();
                System.out.print("Enter routing number: ");
                String routingNumber = SCANNER.nextLine().trim();
                System.out.print("Enter bank name: ");
                String bankName = SCANNER.nextLine().trim();
                method = new BankAccountPayment(paymentMethodId, CLIENT.getClientId(), bankLabel,
                        accountNumber, routingNumber, bankName);
                break;
            default:
                System.out.println("Invalid method type.");
                return;
            }
            PaymentMethod.addPaymentMethod(method);
            System.out.println("Payment method added.");
        } catch (Exception e) {
            System.out.println("Could not add payment method: " + e.getMessage());
        }
    }

    // Update a payment method label.
    private static void updatePaymentMethod() {
        PaymentMethod existing = choosePaymentMethod();
        if (existing == null) {
            return;
        }
        System.out.print("Enter a new label: ");
        existing.setLabel(SCANNER.nextLine().trim());
        PaymentMethod.updatePaymentMethod(existing);
    }

    // Remove a payment method.
    private static void removePaymentMethod() {
        PaymentMethod method = choosePaymentMethod();
        if (method != null) {
            PaymentMethod.removePaymentMethod(CLIENT.getClientId(), method.getPaymentMethodId());
        }
    }

    // Choose a consultant.
    private static Consultant chooseConsultant(boolean approvedOnly) {
        List<Consultant> choices = new ArrayList<>();
        for (Consultant consultant : CONSULTANTS) {
            if (!approvedOnly || "APPROVED".equals(consultant.getStatus())) {
                choices.add(consultant);
            }
        }
        if (choices.isEmpty()) {
            System.out.println("No consultants available.");
            return null;
        }
        return chooseFromList(choices, "Choose consultant number: ", Project3311Test::formatConsultant);
    }

    // Choose a service.
    private static Service chooseService() {
        return chooseFromList(SERVICES, "Choose service number: ", Project3311Test::formatService);
    }

    // Choose an open slot.
    private static AvailabilitySlot chooseAvailableSlot(Consultant consultant) {
        List<AvailabilitySlot> available = new ArrayList<>();
        for (AvailabilitySlot slot : consultant.getAvailabilitySlots()) {
            if (slot.isAvailable()) {
                available.add(slot);
            }
        }
        if (available.isEmpty()) {
            System.out.println("No available slots for this consultant.");
            return null;
        }
        return chooseFromList(available, "Choose slot number: ", Project3311Test::formatSlot);
    }

    // Choose any slot.
    private static AvailabilitySlot chooseAnySlot(Consultant consultant) {
        List<AvailabilitySlot> slots = consultant.getAvailabilitySlots();
        if (slots.isEmpty()) {
            System.out.println("No slots found.");
            return null;
        }
        return chooseFromList(slots, "Choose slot number: ", Project3311Test::formatSlot);
    }

    // Choose one client booking.
    private static Booking chooseClientBooking() {
        List<Booking> bookings = BOOKING_SERVICE.getBookingsForClient(CLIENT.getClientId());
        if (bookings.isEmpty()) {
            System.out.println("Client has no bookings.");
            return null;
        }
        return chooseFromList(bookings, "Choose booking number: ", Project3311Test::formatBooking);
    }

    // Choose a consultant booking by state.
    private static Booking chooseConsultantBookingByState(Consultant consultant, String state) {
        List<Booking> matches = new ArrayList<>();
        for (Booking booking : BOOKING_SERVICE.getBookingsForConsultant(consultant.getConsultantId())) {
            if (state.equals(booking.getState().toString())) {
                matches.add(booking);
            }
        }
        if (matches.isEmpty()) {
            System.out.println("No bookings found in state " + state + ".");
            return null;
        }
        return chooseFromList(matches, "Choose booking number: ", Project3311Test::formatBooking);
    }

    // Choose a saved payment method.
    private static PaymentMethod choosePaymentMethod() {
        List<PaymentMethod> methods = PaymentMethod.getPaymentMethods(CLIENT.getClientId());
        if (methods.isEmpty()) {
            System.out.println("No payment methods saved. Add one first.");
            return null;
        }
        return chooseFromList(methods, "Choose payment method number: ", Project3311Test::formatPaymentMethod);
    }

    // Handle accept or reject.
    private static void updateBookingState(Consultant consultant, String state, boolean accept) {
        Booking booking = chooseConsultantBookingByState(consultant, state);
        if (booking == null) {
            return;
        }

        try {
            if (accept) {
                consultant.acceptBooking(booking);
            } else {
                consultant.rejectBooking(booking);
            }
            System.out.println("Booking is now in state: " + booking.getState());
        } catch (Exception e) {
            System.out.println("Could not update booking: " + e.getMessage());
        }
    }

    // Filter client bookings by state.
    private static List<Booking> getClientBookingsByState(String state) {
        List<Booking> matches = new ArrayList<>();
        for (Booking booking : BOOKING_SERVICE.getBookingsForClient(CLIENT.getClientId())) {
            if (state.equals(booking.getState().toString())) {
                matches.add(booking);
            }
        }
        return matches;
    }

    // Print items one per line.
    private static void printItems(List<?> items) {
        for (Object item : items) {
            System.out.println(item);
        }
    }

    // Print one payment-history section.
    private static void printPaymentGroup(String title, List<Payment> payments) {
        System.out.println("\n" + title + ":");
        if (payments.isEmpty()) {
            System.out.println("None");
            return;
        }
        printItems(payments);
    }

    // Choose one item from a list.
    private static <T> T chooseFromList(List<T> items, String prompt, Function<T, String> formatter) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + formatter.apply(items.get(i)));
        }
        System.out.print(prompt);
        int choice = readInt();
        if (choice < 1 || choice > items.size()) {
            System.out.println("Invalid choice.");
            return null;
        }
        return items.get(choice - 1);
    }

    // Format a consultant line.
    private static String formatConsultant(Consultant consultant) {
        return consultant.getName() + " [" + consultant.getStatus() + "]";
    }

    // Format a service line.
    private static String formatService(Service service) {
        return service.getName() + " | Duration: " + service.getDurationMinutes()
                + " mins | Base price: $" + service.getPrice();
    }

    // Format a slot line.
    private static String formatSlot(AvailabilitySlot slot) {
        return slot.getSlotId() + " -> " + SLOT_FORMAT.format(slot.getStartDateTime());
    }

    // Format a booking line.
    private static String formatBooking(Booking booking) {
        return booking.getBookingId() + " | " + booking.getService().getName()
                + " | " + booking.getConsultant().getName()
                + " | " + booking.getState()
                + " | " + formatSlot(booking.getSlot());
    }

    // Format a payment method line.
    private static String formatPaymentMethod(PaymentMethod method) {
        return method.getType() + " | " + method.getLabel() + " | " + method.getMaskedDetails();
    }

    // Read an integer.
    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(SCANNER.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid integer: ");
            }
        }
    }

    // Read a decimal number.
    private static double readDouble() {
        while (true) {
            try {
                return Double.parseDouble(SCANNER.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }
}
