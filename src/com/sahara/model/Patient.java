package com.sahara.model;

public class Patient {

    private int patientId;
    private int userId;
    private String gender;
    private int age;
    private String address;
    private String emergencyContact;

    // ── Empty constructor ─────────────────────────
    public Patient() {}

    // ── Full constructor ──────────────────────────
    public Patient(int patientId, int userId, String gender,
                   int age, String address, String emergencyContact) {
        this.patientId        = patientId;
        this.userId           = userId;
        this.gender           = gender;
        this.age              = age;
        this.address          = address;
        this.emergencyContact = emergencyContact;
    }

    // ── Getters ───────────────────────────────────
    public int getPatientId()           { return patientId;        }
    public int getUserId()              { return userId;           }
    public String getGender()           { return gender;           }
    public int getAge()                 { return age;              }
    public String getAddress()          { return address;          }
    public String getEmergencyContact() { return emergencyContact; }

    // ── Setters ───────────────────────────────────
    public void setPatientId(int patientId)               { this.patientId        = patientId;        }
    public void setUserId(int userId)                     { this.userId           = userId;           }
    public void setGender(String gender)                  { this.gender           = gender;           }
    public void setAge(int age)                           { this.age              = age;              }
    public void setAddress(String address)                { this.address          = address;          }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    // ── toString ──────────────────────────────────
    @Override
    public String toString() {
        return "Patient{" +
                "patientId=" + patientId +
                ", userId=" + userId +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                '}';
    }
}