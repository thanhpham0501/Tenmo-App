package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

public interface AccountDao {
    Account findByAccountAccountById(int id);

    boolean isTransferable(int id, double moneySend);

    double getBalanceByAccountId(int id);

    Account updatedBalance(double money, int id);
}
