package com.example.garbagecollectionproject;

public class Collector {
    String fullName,mobile;
    double ratingAvg;
    

    public Collector(String fullName, String mobile, long ratingAvg) {
        this.fullName = fullName;
        this.mobile = mobile;
        this.ratingAvg = ratingAvg;
    }

    public Collector(){}

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public double getRatingAvg() {
        return ratingAvg;
    }

    public void setRatingAvg(long ratingAvg) {
        this.ratingAvg = ratingAvg;
    }
}
