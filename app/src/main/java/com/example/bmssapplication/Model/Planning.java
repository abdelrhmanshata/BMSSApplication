package com.example.bmssapplication.Model;

import android.annotation.SuppressLint;
import android.widget.Toast;

import java.time.LocalDate;

public class Planning {
    String ID, userID;
    boolean isCheckHome = false, isCheckFood = false, isCheckTransportation = false, isCheckEducation = false, isCheckEntertainment = false, isCheckHealth = false, isCheckInvoices = false, isCheckOthers = false;
    private float uSalary, uSalaryIncome;
    private boolean isAdditionalIncome;

    float home = 0.0f, food = 0.0f, transportation = 0.0f, education = 0.0f, entertainment = 0.0f, health = 0.0f, invoices = 0.0f, others = 0.0f;
    float conHome = 0.0f, conFood = 0.0f, conTransportation = 0.0f, conEducation = 0.0f, conEntertainment = 0.0f, conHealth = 0.0f, conInvoices = 0.0f, conOthers = 0.0f;
    float saving = 0.0f;

    // Get the current date
    @SuppressLint("NewApi")
    String createdDate = String.valueOf(LocalDate.now());

    public Planning() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public float getuSalary() {
        return uSalary;
    }

    public void setuSalary(float uSalary) {
        this.uSalary = uSalary;
    }

    public float getuSalaryIncome() {
        return uSalaryIncome;
    }

    public void setuSalaryIncome(float uSalaryIncome) {
        this.uSalaryIncome = uSalaryIncome;
    }

    public boolean isAdditionalIncome() {
        return isAdditionalIncome;
    }

    public void setAdditionalIncome(boolean additionalIncome) {
        isAdditionalIncome = additionalIncome;
    }

    public boolean isCheckHome() {
        return isCheckHome;
    }

    public void setCheckHome(boolean checkHome) {
        isCheckHome = checkHome;
    }

    public boolean isCheckFood() {
        return isCheckFood;
    }

    public void setCheckFood(boolean checkFood) {
        isCheckFood = checkFood;
    }

    public boolean isCheckTransportation() {
        return isCheckTransportation;
    }

    public void setCheckTransportation(boolean checkTransportation) {
        isCheckTransportation = checkTransportation;
    }

    public boolean isCheckEducation() {
        return isCheckEducation;
    }

    public void setCheckEducation(boolean checkEducation) {
        isCheckEducation = checkEducation;
    }

    public boolean isCheckEntertainment() {
        return isCheckEntertainment;
    }

    public void setCheckEntertainment(boolean checkEntertainment) {
        isCheckEntertainment = checkEntertainment;
    }

    public boolean isCheckHealth() {
        return isCheckHealth;
    }

    public void setCheckHealth(boolean checkHealth) {
        isCheckHealth = checkHealth;
    }

    public boolean isCheckInvoices() {
        return isCheckInvoices;
    }

    public void setCheckInvoices(boolean checkInvoices) {
        isCheckInvoices = checkInvoices;
    }

    public boolean isCheckOthers() {
        return isCheckOthers;
    }

    public void setCheckOthers(boolean checkOthers) {
        isCheckOthers = checkOthers;
    }

    public float getSaving() {
        return saving;
    }

    public void setSaving(float saving) {
        this.saving = saving;
    }

    public float getHome() {
        return home;
    }

    public void setHome(float home) {
        this.home = home;
    }

    public float getFood() {
        return food;
    }

    public void setFood(float food) {
        this.food = food;
    }

    public float getTransportation() {
        return transportation;
    }

    public void setTransportation(float transportation) {
        this.transportation = transportation;
    }

    public float getEducation() {
        return education;
    }

    public void setEducation(float education) {
        this.education = education;
    }

    public float getEntertainment() {
        return entertainment;
    }

    public void setEntertainment(float entertainment) {
        this.entertainment = entertainment;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public float getInvoices() {
        return invoices;
    }

    public void setInvoices(float invoices) {
        this.invoices = invoices;
    }

    public float getOthers() {
        return others;
    }

    public void setOthers(float others) {
        this.others = others;
    }

    public float getConHome() {
        return conHome;
    }

    public void setConHome(float conHome) {
        this.conHome = conHome;
    }

    public float getConFood() {
        return conFood;
    }

    public void setConFood(float conFood) {
        this.conFood = conFood;
    }

    public float getConTransportation() {
        return conTransportation;
    }

    public void setConTransportation(float conTransportation) {
        this.conTransportation = conTransportation;
    }

    public float getConEducation() {
        return conEducation;
    }

    public void setConEducation(float conEducation) {
        this.conEducation = conEducation;
    }

    public float getConEntertainment() {
        return conEntertainment;
    }

    public void setConEntertainment(float conEntertainment) {
        this.conEntertainment = conEntertainment;
    }

    public float getConHealth() {
        return conHealth;
    }

    public void setConHealth(float conHealth) {
        this.conHealth = conHealth;
    }

    public float getConInvoices() {
        return conInvoices;
    }

    public void setConInvoices(float conInvoices) {
        this.conInvoices = conInvoices;
    }

    public float getConOthers() {
        return conOthers;
    }

    public void setConOthers(float conOthers) {
        this.conOthers = conOthers;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public float getSum() {
        return home + food + transportation + education + entertainment + health + invoices + others;
    }
}
