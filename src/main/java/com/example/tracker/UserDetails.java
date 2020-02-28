package com.example.tracker;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserDetails {

    public String Name;
    public String Email;
    public String Rollno;
    public String Password;

    public UserDetails(String name,String rollno, String email,String password) {
        this.Name = name;
        this.Rollno = rollno;
        this.Email = email;
        this.Password = password;
    }

}