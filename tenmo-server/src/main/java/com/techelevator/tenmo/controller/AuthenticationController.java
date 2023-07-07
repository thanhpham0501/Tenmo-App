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
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.security.jwt.TokenProvider;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller to authenticate users.
 */
@RestController
@PreAuthorize("isAuthenticated()")
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


    @PreAuthorize("permitAll")
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

    @PreAuthorize("permitAll")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void register(@Valid @RequestBody RegisterUserDTO newUser) {
        if (!userDao.create(newUser.getUsername(), newUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User registration failed.");
        }
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<User> listUsers() {

        return userDao.findAll();

    }
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @RequestMapping(path = "/account", method = RequestMethod.GET)
    public Account showAccountDetails( Principal principal) {
        String userName = principal.getName();
        System.out.println(userName);
        int userId = userDao.findIdByUsername(userName);
        Account account = accountDao.findByAccountAccountById(userId);
        return account;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path = "/account/{id}/{id2}/{money}", method = RequestMethod.POST)
    public void transferMoney (@PathVariable int id, @PathVariable int id2, @PathVariable double money) {
        if (accountDao.isTransferable(id, money) && money > 0.0 && id != id2) {
            accountDao.deductBalance(money, id);
            accountDao.increaseBalance(money, id2);
            transactionDao.create(id, id2, money);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Insufficient funds.");
        }
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @RequestMapping(path = "/account/transactions", method = RequestMethod.GET)
    public List<Transaction> listTransactions(Principal principal) {
        String userName = principal.getName();
        int userId = userDao.findIdByUsername(userName);
        return transactionDao.listTransactionsById(userId);
    }


    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @RequestMapping(path = "/account/transactions/{id}", method = RequestMethod.GET)
    public Transaction getTransactionById(@PathVariable int id, Principal principal) {
        String userName = principal.getName();
        int userId = userDao.findIdByUsername(userName);
        return transactionDao.getTransactionById(userId, id);
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

