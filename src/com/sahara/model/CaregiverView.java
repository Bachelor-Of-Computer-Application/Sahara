package com.sahara.model;

public class CaregiverView {

    private final Caregiver caregiver;
    private final String fullName;

    public CaregiverView(Caregiver caregiver, String fullName) {
        this.caregiver = caregiver;
        this.fullName = fullName;
    }

    public Caregiver getCaregiver() { return caregiver; }
    public String getFullName()     { return fullName;  }

    // Convenience getters (used by TableView to display columns)
    public int getCaregiverId()      { return caregiver.getCaregiverId();      }
    public String getGender()        { return caregiver.getGender();           }
    public int getAge()              { return caregiver.getAge();              }
    public int getExperienceYears()  { return caregiver.getExperienceYears();  }
    public double getAvgRating()     { return caregiver.getAvgRating();        }
    public boolean isVerified()      { return caregiver.isVerified();          }
    public String getBio()           { return caregiver.getBio();              }
}