package com.example.garbagecollectionproject;

import androidx.annotation.RequiresPermission;

public class ReadWriteUserDetails {
    public String fullName,gender,mobile,role;
    public Double lat,lng;
    public int bin;
    public ReadWriteUserDetails(){};
    public ReadWriteUserDetails(int bin){
        this.bin=bin;
    }
    public ReadWriteUserDetails(String fullName,String gender, String mobile, Double lat, Double lng,int bin){
        this.fullName=fullName;
        this.gender=gender;
        this.mobile=mobile;
        this.lat=lat;
        this.lng=lng;
        this.bin=bin;
    }
}
