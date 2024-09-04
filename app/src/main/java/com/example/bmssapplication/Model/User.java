package com.example.bmssapplication.Model;

import java.io.Serializable;

public class User implements Serializable {

    private String uID, UserName, uEmail, uPassword;
    private int uAge, numOfPlans;

    float saving = 0.0f;

    public User() {
    }


    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuPassword() {
        return uPassword;
    }

    public void setuPassword(String uPassword) {
        this.uPassword = uPassword;
    }

    public int getuAge() {
        return uAge;
    }

    public void setuAge(int uAge) {
        this.uAge = uAge;
    }

    public int getNumOfPlans() {
        return numOfPlans;
    }

    public void setNumOfPlans(int numOfPlans) {
        this.numOfPlans = numOfPlans;
    }

    public float getSaving() {
        return saving;
    }

    public void setSaving(float saving) {
        this.saving = saving;
    }
}


