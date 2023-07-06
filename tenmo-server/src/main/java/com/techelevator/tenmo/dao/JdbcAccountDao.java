package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JdbcAccountDao implements AccountDao{
    private JdbcTemplate jdbcTemplate;



    public JdbcAccountDao (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account findByAccountAccountById(int id){
        Account account = null;
        try {
            String sql = "SELECT * FROM account WHERE account_id = ?;";
//            account = jdbcTemplate.queryForObject(sql, Account.class, id);

            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);

            if (results.next()) {
                account = mapRowToAccount(results);
            }
        } catch (CannotGetJdbcConnectionException e){
           throw new DataIntegrityViolationException("");
        }
        return account;
    }
    @Override
    public boolean isTransferable(int id, double moneySend){
        double accountBalance = 0.0;
        String sql = "SELECT balance FROM account WHERE account_id = ?;";
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
            if(rowSet.next()){
                accountBalance = rowSet.getDouble("balance");
            }
        } catch (CannotGetJdbcConnectionException e){
            throw new DataIntegrityViolationException("");
        }
        if(accountBalance >= moneySend){
            return true;
        }
        return false;
    }
    @Override
    public double getBalanceByAccountId(int id){
        double accountBalance = 0.0;
        String sql = "SELECT balance FROM account WHERE account_id = ?;";
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
            if(rowSet.next()){
                accountBalance = rowSet.getDouble("balance");
            }
        } catch (CannotGetJdbcConnectionException e){
            throw new DataIntegrityViolationException("");
        }
        return accountBalance;
    }
    @Override
    public Account updatedBalance(double money, int id){
        Account account = null;
        String sql = "UPDATE account SET balance = ? WHERE account_id = ?;";
        try{
            int numberOfRows = jdbcTemplate.update(sql, money, id);

            if (numberOfRows == 0){
                throw new DataIntegrityViolationException("Data Integrity Violation!");
            } else {
                account = findByAccountAccountById(id);
            }
        } catch (DataAccessException e){
            return null;

        }
        return account;
    }
@Override
    public void deductBalance(double moneySent, int id) {
        String sql = "SELECT balance FROM account WHERE account_id = ?;";
        double capturedBalance = 0.0;
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);

            if (results.next()) {
                capturedBalance = results.getDouble("balance");
            }

        } catch (DataIntegrityViolationException e) {
             throw new DataIntegrityViolationException("Data integrity violation");
        }

        capturedBalance -= moneySent;

        updatedBalance(capturedBalance, id);
    }
    @Override
    public void increaseBalance(double moneySent, int id) {
        String sql = "SELECT balance FROM account WHERE account_id = ?;";
        double capturedBalance = 0.0;
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);

            if (results.next()) {
                capturedBalance = results.getDouble("balance");
            }

        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Data integrity violation");
        }

        capturedBalance += moneySent;

        updatedBalance(capturedBalance, id);
    }

    public void sendTransactionInfo(int id, int id2, double money){
        String sql = "INSERT INTO transaction(sender_id, receiver_id, money_amount) VALUES (?, ?, ?);";
        try {
            jdbcTemplate.update(sql, id, id2, money);
        } catch (DataIntegrityViolationException e){
            throw new DataIntegrityViolationException("Data Integrity Violation", e);
        }

    }



    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setId(rs.getInt("account_id"));
        account.setUser_id(rs.getInt("user_id"));
        account.setBalance(rs.getDouble("balance"));

        return account;
    }
}
