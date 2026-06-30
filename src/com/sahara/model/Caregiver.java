package com.sahara.model;

public class Caregiver {

    private int caregiverId;
    private int userId;
    private String gender;
    private int age;
    private String address;
    private int experienceYears;
    private String bio;
    private boolean isVerified;
    private double avgRating;

    public Caregiver() {}

    public Caregiver(int caregiverId, int userId, String gender,
                     int age, String address, int experienceYears,
                     String bio, boolean isVerified, double avgRating) {
        this.caregiverId     = caregiverId;
        this.userId          = userId;
        this.gender          = gender;
        this.age             = age;
        this.address         = address;
        this.experienceYears = experienceYears;
        this.bio             = bio;
        this.isVerified      = isVerified;
        this.avgRating       = avgRating;
    }

    public int getCaregiverId()      { return caregiverId;     }
    public int getUserId()           { return userId;          }
    public String getGender()        { return gender;          }
    public int getAge()              { return age;             }
    public String getAddress()       { return address;         }
    public int getExperienceYears()  { return experienceYears; }
    public String getBio()           { return bio;             }
    public boolean isVerified()      { return isVerified;      }
    public double getAvgRating()     { return avgRating;       }

    public void setCaregiverId(int caregiverId)        { this.caregiverId     = caregiverId;     }
    public void setUserId(int userId)                  { this.userId          = userId;          }
    public void setGender(String gender)               { this.gender          = gender;          }
    public void setAge(int age)                        { this.age             = age;             }
    public void setAddress(String address)             { this.address         = address;         }
    public void setExperienceYears(int experienceYears){ this.experienceYears = experienceYears; }
    public void setBio(String bio)                     { this.bio             = bio;             }
    public void setVerified(boolean isVerified)        { this.isVerified      = isVerified;      }
    public void setAvgRating(double avgRating)         { this.avgRating       = avgRating;       }

    @Override
    public String toString() {
        return "Caregiver{" +
                "caregiverId=" + caregiverId +
                ", userId=" + userId +
                ", gender='" + gender + '\'' +
                ", isVerified=" + isVerified +
                ", avgRating=" + avgRating +
                '}';
    }
}