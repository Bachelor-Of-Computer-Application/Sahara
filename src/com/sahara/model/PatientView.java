package com.sahara.model;

public class PatientView {

    private final Patient patient;
    private final String fullName;
    private final String email;
    private final String phone;

    public PatientView(Patient patient, String fullName,
                       String email, String phone) {
        this.patient  = patient;
        this.fullName = fullName;
        this.email    = email;
        this.phone    = phone;
    }

    public Patient getPatient()          { return patient;  }
    public String getFullName()          { return fullName; }
    public String getEmail()             { return email;    }
    public String getPhone()             { return phone;    }

    // Convenience getters for TableView
    public int getPatientId()            { return patient.getPatientId();        }
    public String getGender()            { return patient.getGender();           }
    public int getAge()                  { return patient.getAge();              }
    public String getAddress()           { return patient.getAddress();          }
    public String getEmergencyContact()  { return patient.getEmergencyContact(); }
}