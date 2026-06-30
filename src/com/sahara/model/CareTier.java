package com.sahara.model;

public class CareTier {

    private int tierId;
    private String tierName;
    private String description;
    private double pricePerDay;

    public CareTier() {}

    public CareTier(int tierId, String tierName,
                    String description, double pricePerDay) {
        this.tierId      = tierId;
        this.tierName    = tierName;
        this.description = description;
        this.pricePerDay = pricePerDay;
    }

    public int getTierId()         { return tierId;      }
    public String getTierName()    { return tierName;    }
    public String getDescription() { return description; }
    public double getPricePerDay() { return pricePerDay; }

    public void setTierId(int tierId)            { this.tierId      = tierId;      }
    public void setTierName(String tierName)     { this.tierName    = tierName;    }
    public void setDescription(String desc)      { this.description = desc;        }
    public void setPricePerDay(double price)     { this.pricePerDay = price;       }

    @Override
    public String toString() {
        return "CareTier{" +
                "tierId=" + tierId +
                ", tierName='" + tierName + '\'' +
                ", pricePerDay=" + pricePerDay +
                '}';
    }
}