package com.example.tracker;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class tracking_detail {

    public String RollNo;
    public String ID;
    public String MAC;
    public String Date;
    public String Time;

    public tracking_detail(String roll_No, String Id, String mac, String date, String time) {
        this.RollNo = roll_No;
        this.ID = Id;
        this.MAC = mac;
        this.Date = date;
        this.Time = time;
    }
}