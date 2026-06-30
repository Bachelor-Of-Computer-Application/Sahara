package com.sahara.model;

public class Hospital {

    private int hospitalId;
    private String name;
    private String address;
    private String city;

    public Hospital() {}

    public Hospital(int hospitalId, String name,
                    String address, String city) {
        this.hospitalId = hospitalId;
        this.name       = name;
        this.address    = address;
        this.city       = city;
    }

    public int getHospitalId()  { return hospitalId; }
    public String getName()     { return name;       }
    public String getAddress()  { return address;    }
    public String getCity()     { return city;       }

    public void setHospitalId(int hospitalId)   { this.hospitalId = hospitalId; }
    public void setName(String name)            { this.name       = name;       }
    public void setAddress(String address)      { this.address    = address;    }
    public void setCity(String city)            { this.city       = city;       }

    @Override
    public String toString() {
        return "Hospital{" +
                "hospitalId=" + hospitalId +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}