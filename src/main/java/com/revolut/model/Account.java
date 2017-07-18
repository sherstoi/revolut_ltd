package com.revolut.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;


/**
 * This class represent account domain model.
 * @Author Iurii
 * @Version 1.0
 */
public class Account {
    private final Object lock = new Object();

    @JsonProperty
    private Long accountId;
    @JsonProperty
    private BigDecimal balance;

    public static Account of(Long accountId, BigDecimal balance) {
        Account account = new Account();
        account.setAccountId(accountId);
        account.setBalance(balance);

        return account;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Object getLock() {
        return lock;
    }
}
