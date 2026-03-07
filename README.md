# 3311project

GitHub repository: https://github.com/Rhoguns/3311project

## Architecture overview
This project is a Java backend for a consulting and booking system. The code is organized by feature and actor

- `admin` folder — system-wide policy configuration
- `booking` folder — booking logic and booking lifecycle
- `consultant` folder — consultant profiles and availability slots
- `payment` folder — payment processing and payment states

The architecture is modular and object-oriented with each package responsible for a specific subsystem

## Design patterns used
- **Singleton Pattern**
  - Used in `Admin.java`
  - Used for system-wide policies such as:
    - `CancellationPolicy.java`
    - `PricingPolicy.java`
    - `NotificationPolicy.java`
    - `RefundPolicy.java`

- **Command Pattern**
  - Used for updating system policies
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
From the project root, compile and run the test file in `backend/src`:

## Team member contributions
- **Hasan Kerret** — GitHub: `lalogongum`  
  Contributed to the **consultant subsystem** and the **consultant class diagram**.

- **Justin Fera** — GitHub: `Justin1374`  
  Contributed to the **admin subsystem** and the **admin class diagram**.

- **Anh Tu Le** — GitHub: `TuLe12` (York account) / `tim96121204` (personal local account)  
  Contributed to the **payment subsystem**, **payment class diagram**, and **test file**.

- **Philips Rhoguns** — GitHub: `Rhoguns`  
  Contributed to the **booking subsystem**, **booking class diagram**, and **compiling the individual class diagrams into one**.
