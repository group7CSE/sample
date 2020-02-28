package com.example.tracker;

import com.google.firebase.database.IgnoreExtraProperties;



@IgnoreExtraProperties
public class User {

    public String block;
    public String pos1;
    public String pos2;
    public String pos3;
    public String mac;
    public String ssid;
    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String block, String pos1, String pos2, String pos3, String mac, String ssid) {
        this.block = block;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.pos3 = pos3;
        this.mac = mac;
        this.ssid = ssid;
    }
}