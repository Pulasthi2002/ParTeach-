package com.example.par_teach;

public class Notification {
    private String userName;
    private String date;
    private String absentReason;

    public Notification() {
        // Empty constructor needed for Firebase
    }

    public Notification(String userName, String date, String absentReason) {
        this.userName = userName;
        this.date = date;
        this.absentReason = absentReason;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAbsentReason() {
        return absentReason;
    }

    public void setAbsentReason(String absentReason) {
        this.absentReason = absentReason;
    }
}
