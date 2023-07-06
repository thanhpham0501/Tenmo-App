package com.techelevator.tenmo.model;

public class Transaction {

    private int transaction_id;

    private int sender_id;

    private int receiver_id;

    private double balance;

    public Transaction() {}

    public Transaction(int transaction_id, int sender_id, int receiver_id, double balance) {
        this.transaction_id = transaction_id;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.balance = balance;
    }

    public int getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public int getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
