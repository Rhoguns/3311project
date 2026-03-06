laloblade
laloblade
Invisible

Door — 14:03
thanks
Door — 15:00
@everyone
Door — 15:17
https://claude.ai/share/611b7903-565c-40a6-a71d-bded6cba136c
Code demo explanation
Shared via Claude, an AI assistant from Anthropic
admin
package admin;

import consultant.Consultant;

public class Admin {
    private static final Admin instance = new Admin();

Admin.java
3 KB
package admin;

public class RefundPolicy {
	private String acceptedState;
	
	private static final RefundPolicy instance = new RefundPolicy();

RefundPolicy.java
2 KB
package admin;

import java.util.ArrayList;

import booking.Booking;
import booking.state.BookingState;

CancellationPolicy.java
3 KB
package admin;

public class CancellationPolicyCommand implements PolicyCommand {
	private CancellationPolicy policy;
	private int newWindowTime;
	

CancellationPolicyCommand.java
1 KB
Booking
package booking;

import java.time.LocalDateTime;
import java.util.ArrayList;

import admin.CancellationPolicy;

Booking.java
4 KB
package booking;

import booking.model.Client;
import booking.model.Service;
import booking.state.BookingState;
import consultant.AvailabilitySlot;

BookingService.java
4 KB
package booking.model;

import admin.PricingPolicy;

/**
 * Represents a consulting service that can be booked.

Service.java
2 KB
Consultant
package consultant;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

AvailabilitySlot.java
3 KB
package consultant;

import java.util.ArrayList;
import java.util.List;

import admin.NotificationPolicy;

Consultant.java
4 KB
Door — 15:30
@laloblade
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

Project3311Test.java
29 KB
Door — 15:37
this is the compiled local file
Attachment file type: archive
3311project-local-working.zip
1.11 MB
laloblade — 15:52
ill join in 10 mins
Door — 16:08
package consultant;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

AvailabilitySlot.java
3 KB
package consultant;

import java.util.ArrayList;
import java.util.List;

import admin.NotificationPolicy;

Consultant.java
4 KB
﻿
package consultant;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class AvailabilitySlot {

    private String slotId;
    private Date date;
    private String startTime;
    private String endTime;
    private boolean isAvailable;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public AvailabilitySlot(String slotId, Date date, String startTime, String endTime) {
        this.slotId = slotId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAvailable = true;
        this.startDateTime = date == null ? null : LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        this.endDateTime = this.startDateTime;
    }

    public AvailabilitySlot(String slotId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.slotId = slotId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.date = startDateTime == null ? null : Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
        this.startTime = startDateTime == null ? null : startDateTime.toLocalTime().toString();
        this.endTime = endDateTime == null ? null : endDateTime.toLocalTime().toString();
        this.isAvailable = true;
    }

    public void markAvailable() {
        this.isAvailable = true;
    }

    public void markUnavailable() {
        this.isAvailable = false;
    }

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

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    @Override
    public String toString() {
        if (startDateTime != null) {
            return "AvailabilitySlot{id='" + slotId + "', start=" + startDateTime + ", end=" + endDateTime + ", available=" + isAvailable + '}';
        }
        return "AvailabilitySlot{id='" + slotId + "', date=" + date + ", start='" + startTime + "', end='" + endTime + "', available=" + isAvailable + '}';
    }
}
AvailabilitySlot.java
3 KB
