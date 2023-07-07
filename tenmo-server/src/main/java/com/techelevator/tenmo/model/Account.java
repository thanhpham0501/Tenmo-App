package com.techelevator.tenmo.model;

import org.springframework.stereotype.Component;


public class Account {
    private int account_id;
    private int user_id;
    private double balance;

    public int getId() {
        return account_id;
    }

    public void setId(int account_id) {
        this.account_id = account_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Account () {}

    public Account(int account_id, int user_id, double balance) {
        this.account_id = account_id;
        this.user_id = user_id;
        this.balance = balance;
    }
}
