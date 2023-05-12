package com.example.garbagecollectionproject;


import com.google.firebase.Timestamp;

public class Complaint {
    String userId;
    String userName;
    String mobile;
    String collectorId;
    Timestamp dateTime;
    String message;
    Boolean queryResolved;
    String queryResolvedMessage;
    Timestamp queryResolvedTime;
    String complaintId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(String collectorId) {
        this.collectorId = collectorId;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getQueryResolved() {
        return queryResolved;
    }

    public void setQueryResolved(Boolean queryResolved) {
        this.queryResolved = queryResolved;
    }

    public String getQueryResolvedMessage() {
        return queryResolvedMessage;
    }

    public void setQueryResolvedMessage(String queryResolvedMessage) {
        this.queryResolvedMessage = queryResolvedMessage;
    }

    public Timestamp getQueryResolvedTime() {
        return queryResolvedTime;
    }

    public void setQueryResolvedTime(Timestamp queryResolvedTime) {
        this.queryResolvedTime = queryResolvedTime;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public Complaint(String userId, String userName, String mobile, String collectorId, Timestamp dateTime, String message, Boolean queryResolved, String queryResolvedMessage, Timestamp queryResolvedTime, String complaintId) {
        this.userId = userId;
        this.userName = userName;
        this.mobile = mobile;
        this.collectorId = collectorId;
        this.dateTime = dateTime;
        this.message = message;
        this.queryResolved = queryResolved;
        this.queryResolvedMessage = queryResolvedMessage;
        this.queryResolvedTime = queryResolvedTime;
        this.complaintId=complaintId;
    }

    public Complaint(){};



}
