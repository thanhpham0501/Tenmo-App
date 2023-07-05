package com.techelevator.tenmo.model;

public class Account {
    private int id;
    private int user_id;
    private double balance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Account(int id, int user_id, double balance) {
        this.id = id;
        this.user_id = user_id;
        this.balance = balance;
    }
}
