package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransactionDao implements TransactionDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcTransactionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Transaction> listTransactionsById(int id) {
        List<Transaction> transactionList = new ArrayList<>();
        try {
            String sql = "SELECT * FROM user_transactions " +
                    "WHERE sender_id = ? OR receiver_id = ?;";
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id, id);
            while (results.next()) {
                Transaction transaction = mapRowToTransaction(results);
                transactionList.add(transaction);
            }
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Data Integrity Violation");
        }

        return transactionList;
    }

    public Transaction getTransactionById(int id) {
        Transaction transaction = null;

        String sql = "SELECT * FROM user_transactions " +
                "WHERE transaction_id = ?;";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if (results.next()) {
                transaction = mapRowToTransaction(results);
            }
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Data Integrity Violation", e);
        }

        return transaction;
    }

    @Override
    public boolean create(int id, int id2, double money){
        String sql = "INSERT INTO user_transactions (sender_id, receiver_id, money_amount) VALUES (?, ?, ?);";
        try {
            jdbcTemplate.update(sql, id, id2, money);
        } catch (DataIntegrityViolationException e){
            throw new DataIntegrityViolationException("Data Integrity Violation", e);
        }

        return true;
    }

    private Transaction mapRowToTransaction(SqlRowSet rs) {
//        User user = new User();
//        user.setId(rs.getLong("user_id"));
//        user.setUsername(rs.getString("username"));
//        user.setPassword(rs.getString("password_hash"));
//        user.setActivated(true);
//        user.setAuthorities("USER");
//        return user;
        Transaction transaction = new Transaction();
        transaction.setTransaction_id(rs.getInt("transaction_id"));
        transaction.setSender_id(rs.getInt("sender_id"));
        transaction.setReceiver_id(rs.getInt("receiver_id"));
        transaction.setBalance(rs.getDouble("money_amount"));
        return transaction;
    }

}
