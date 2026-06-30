package com.sahara.model;

import java.time.LocalDate;

public class Availability {

    private int availabilityId;
    private int caregiverId;
    private LocalDate availableDate;
    private boolean isAvailable;

    public Availability() {}

    public Availability(int availabilityId, int caregiverId,
                        LocalDate availableDate, boolean isAvailable) {
        this.availabilityId = availabilityId;
        this.caregiverId    = caregiverId;
        this.availableDate  = availableDate;
        this.isAvailable    = isAvailable;
    }

    public int getAvailabilityId()       { return availabilityId; }
    public int getCaregiverId()          { return caregiverId;    }
    public LocalDate getAvailableDate()  { return availableDate;  }
    public boolean isAvailable()         { return isAvailable;    }

    public void setAvailabilityId(int availabilityId)      { this.availabilityId = availabilityId; }
    public void setCaregiverId(int caregiverId)            { this.caregiverId    = caregiverId;    }
    public void setAvailableDate(LocalDate availableDate)  { this.availableDate  = availableDate;  }
    public void setAvailable(boolean isAvailable)          { this.isAvailable    = isAvailable;    }

    @Override
    public String toString() {
        return "Availability{" +
                "availabilityId=" + availabilityId +
                ", caregiverId=" + caregiverId +
                ", availableDate=" + availableDate +
                ", isAvailable=" + isAvailable +
                '}';
    }
}