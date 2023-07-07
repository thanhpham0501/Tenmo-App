package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;

import java.util.List;

public interface TransactionDao {

    List<Transaction> listTransactionsById(int id);


    Transaction getTransactionById(int userId, int transactionId);

    boolean create(int sender_id, int receiver_id, double money_sent);


}
