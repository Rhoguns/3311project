# 3311project

GitHub repository: https://github.com/Rhoguns/3311project

## Architecture overview
This project is a Java-based backend for a consulting and booking system. The code is organized by feature/module:

- `admin/` — system-wide policy configuration
- `booking/` — booking logic and booking lifecycle
- `consultant/` — consultant profiles and availability slots
- `payment/` — payment processing and payment states

The architecture is modular and object-oriented, with each package responsible for a specific part of the system.

## Design patterns used
- **Singleton Pattern**
  - Used in `Admin.java`
  - Used for system-wide policies such as:
    - `CancellationPolicy.java`
    - `PricingPolicy.java`
    - `NotificationPolicy.java`
    - `RefundPolicy.java`

- **Command Pattern**
  - Used for updating/administering system policies
  - Implemented through:
    - `PolicyCommand.java`
    - `CancellationPolicyCommand.java`
    - `PricingPolicyCommand.java`
    - `NotificationPolicyCommand.java`
    - `RefundPolicyCommand.java`

- **State Pattern**
  - Used for booking state transitions in `booking/state/`
    - `RequestedState`
    - `ConfirmedState`
    - `PendingPaymentState`
    - `PaidState`
    - `CompletedState`
    - `RejectedState`
    - `CancelledState`
  - Used for payment state transitions in `payment/state/`
    - `PaymentPendingState`
    - `PaymentSuccessfulState`
    - `PaymentFailedState`
    - `PaymentRefundedState`

## How to run the application
From the project root, compile and run the test/demo file in `backend/src`:
