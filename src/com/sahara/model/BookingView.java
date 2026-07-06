package com.sahara.model;

public class BookingView {

    private final Booking booking;
    private final String patientName;
    private final String caregiverName;
    private final String hospitalName;

    public BookingView(Booking booking, String patientName,
                       String caregiverName, String hospitalName) {
        this.booking = booking;
        this.patientName = patientName;
        this.caregiverName = caregiverName;
        this.hospitalName = hospitalName;
    }

    public Booking getBooking()       { return booking;       }
    public String getPatientName()    { return patientName;   }
    public String getCaregiverName()  { return caregiverName; }
    public String getHospitalName()   { return hospitalName;  }

    // Convenience getters (used by TableView to display columns)
    public int getBookingId()     { return booking.getBookingId();  }
    public String getWard()       { return booking.getWard();       }
    public String getStatus()     { return booking.getStatus();     }
    public double getTotalCost()  { return booking.getTotalCost();  }
    public int getTotalDays()     { return booking.getTotalDays();  }
}