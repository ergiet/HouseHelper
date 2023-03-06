package com.example.househelper;

import com.google.firebase.Timestamp;

public class BalanceListModel {
    String description,username;
    double amount;
    Timestamp date;
    private double currentBalance;

    public BalanceListModel(String description, String username, double amount, Timestamp date) {
        this.description = description;
        this.username = username;
        this.amount = amount;
        this.date = date;
    }


    public String getDescription() {
        return description;
    }

    public String getUsername() {
        return username;
    }

    public double getAmount() {
        return amount;
    }

    public Timestamp getDate() {
        return date;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }
}