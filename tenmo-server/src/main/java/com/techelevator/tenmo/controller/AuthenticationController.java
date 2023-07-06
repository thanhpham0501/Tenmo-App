package com.techelevator.tenmo.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.TransactionDao;
import com.techelevator.tenmo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.security.jwt.TokenProvider;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller to authenticate users.
 */
@RestController
public class AuthenticationController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private UserDao userDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private TransactionDao transactionDao;


    public AuthenticationController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, UserDao userDao) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDao = userDao;

    }



    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public LoginResponse login(@Valid @RequestBody LoginDTO loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, false);
        
        User user = userDao.findByUsername(loginDto.getUsername());

        return new LoginResponse(jwt, user);
    }


    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void register(@Valid @RequestBody RegisterUserDTO newUser) {
        if (!userDao.create(newUser.getUsername(), newUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User registration failed.");
        }
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public List<User> listUsers() {

        return userDao.findAll();

    }

    @RequestMapping(path = "/account/{id}", method = RequestMethod.GET)
    public Account showAccountDetails(@PathVariable int id) {
        Account account = accountDao.findByAccountAccountById(id);
        return account;
    }
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path = "/account/{id}/{id2}/{money}", method = RequestMethod.POST)
    public void transferMoney (@PathVariable int id, @PathVariable int id2, @PathVariable double money) {
        if (accountDao.isTransferable(id, money) && money > 0.0 && id != id2) {
            accountDao.deductBalance(money, id);
            accountDao.increaseBalance(money, id2);
//            accountDao.sendTransactionInfo(id, id2, money);
            transactionDao.create(id, id2, money);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Insufficient funds.");
        }
    }

    @RequestMapping(path = "/account/{id}/transactions", method = RequestMethod.GET)
    public List<Transaction> listTransactions(@PathVariable int id) {
        return transactionDao.listTransactionsById(id);
    }

    @RequestMapping(path = "/account/{id}/transactions/{id2}", method = RequestMethod.GET)
    public Transaction getTransactionById(@PathVariable int id, @PathVariable int id2) {
        return transactionDao.getTransactionById(id2);
    }



    /**
     * Object to return as body in JWT Authentication.
     */
    static class LoginResponse {

        private String token;
        private User user;

        LoginResponse(String token, User user) {
            this.token = token;
            this.user = user;
        }

        public String getToken() {
            return token;
        }

        void setToken(String token) {
            this.token = token;
        }

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}
    }
}

