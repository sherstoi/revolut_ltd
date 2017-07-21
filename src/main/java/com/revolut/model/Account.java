package com.revolut.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.revolut.config.JSONValueDeserializer;

import java.math.BigDecimal;


/**
 * This class represent account domain model.
 * @Author Iurii
 * @Version 1.0
 */
public class Account {
    @JsonSerialize(using = ToStringSerializer.class)
    private Object lock;
    @JsonProperty
    private Long accountId;
    @JsonProperty
    private BigDecimal balance;

    public static Account of(Long accountId, BigDecimal balance) {
        Account account = new Account();
        account.setAccountId(accountId);
        account.setBalance(balance);
        account.setLock(new Object());

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

    public void setLock(Object lock) {this.lock = lock;}

}
