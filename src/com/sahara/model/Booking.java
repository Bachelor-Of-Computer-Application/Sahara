package com.sahara.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Booking {

    private int bookingId;
    private int patientId;
    private int caregiverId;
    private int tierId;
    private int hospitalId;
    private String ward;
    private LocalDate admissionDate;
    private LocalDate dischargeDate;
    private int totalDays;
    private double totalCost;
    private String status;
    private String notes;
    private LocalDateTime bookedAt;

    public Booking() {}

    public Booking(int bookingId, int patientId, int caregiverId,
                   int tierId, int hospitalId, String ward,
                   LocalDate admissionDate, LocalDate dischargeDate,
                   int totalDays, double totalCost, String status,
                   String notes, LocalDateTime bookedAt) {
        this.bookingId     = bookingId;
        this.patientId     = patientId;
        this.caregiverId   = caregiverId;
        this.tierId        = tierId;
        this.hospitalId    = hospitalId;
        this.ward          = ward;
        this.admissionDate = admissionDate;
        this.dischargeDate = dischargeDate;
        this.totalDays     = totalDays;
        this.totalCost     = totalCost;
        this.status        = status;
        this.notes         = notes;
        this.bookedAt      = bookedAt;
    }

    public int getBookingId()             { return bookingId;     }
    public int getPatientId()             { return patientId;     }
    public int getCaregiverId()           { return caregiverId;   }
    public int getTierId()                { return tierId;        }
    public int getHospitalId()            { return hospitalId;    }
    public String getWard()               { return ward;          }
    public LocalDate getAdmissionDate()   { return admissionDate; }
    public LocalDate getDischargeDate()   { return dischargeDate; }
    public int getTotalDays()             { return totalDays;     }
    public double getTotalCost()          { return totalCost;     }
    public String getStatus()             { return status;        }
    public String getNotes()              { return notes;         }
    public LocalDateTime getBookedAt()    { return bookedAt;      }

    public void setBookingId(int bookingId)              { this.bookingId     = bookingId;     }
    public void setPatientId(int patientId)              { this.patientId     = patientId;     }
    public void setCaregiverId(int caregiverId)          { this.caregiverId   = caregiverId;   }
    public void setTierId(int tierId)                    { this.tierId        = tierId;        }
    public void setHospitalId(int hospitalId)            { this.hospitalId    = hospitalId;    }
    public void setWard(String ward)                     { this.ward          = ward;          }
    public void setAdmissionDate(LocalDate admissionDate){ this.admissionDate = admissionDate; }
    public void setDischargeDate(LocalDate dischargeDate){ this.dischargeDate = dischargeDate; }
    public void setTotalDays(int totalDays)              { this.totalDays     = totalDays;     }
    public void setTotalCost(double totalCost)           { this.totalCost     = totalCost;     }
    public void setStatus(String status)                 { this.status        = status;        }
    public void setNotes(String notes)                   { this.notes         = notes;         }
    public void setBookedAt(LocalDateTime bookedAt)      { this.bookedAt      = bookedAt;      }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", patientId=" + patientId +
                ", caregiverId=" + caregiverId +
                ", status='" + status + '\'' +
                ", totalCost=" + totalCost +
                '}';
    }
}