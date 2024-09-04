package com.example.bmssapplication.Model;

import java.io.Serializable;

public class Payment implements Serializable {

    String uID;

    private String PaymentID, CardholderName, CardNumber, Cvv, ExpirationMonth, ExpirationYear;

    private int numOfPlan;
    private float totalPrice;


    public Payment() {
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getPaymentID() {
        return PaymentID;
    }

    public void setPaymentID(String paymentID) {
        PaymentID = paymentID;
    }

    public String getCardholderName() {
        return CardholderName;
    }

    public void setCardholderName(String cardholderName) {
        CardholderName = cardholderName;
    }

    public String getCardNumber() {
        return CardNumber;
    }

    public void setCardNumber(String cardNumber) {
        CardNumber = cardNumber;
    }

    public String getCvv() {
        return Cvv;
    }

    public void setCvv(String cvv) {
        Cvv = cvv;
    }

    public String getExpirationMonth() {
        return ExpirationMonth;
    }

    public void setExpirationMonth(String expirationMonth) {
        ExpirationMonth = expirationMonth;
    }

    public String getExpirationYear() {
        return ExpirationYear;
    }

    public void setExpirationYear(String expirationYear) {
        ExpirationYear = expirationYear;
    }

    public int getNumOfPlan() {
        return numOfPlan;
    }

    public void setNumOfPlan(int numOfPlan) {
        this.numOfPlan = numOfPlan;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }
}
