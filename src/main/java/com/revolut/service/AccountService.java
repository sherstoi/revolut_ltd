package com.revolut.service;

import com.revolut.model.Account;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by iurii on 7/16/17.
 */
public interface AccountService {
    void deposit(Account account, BigDecimal balance);
    void withdrawn(Account account, BigDecimal balance);
    void saveAccount(Account account);
    void saveAccounts(List<Account> accountList);
    List<Account> findAllAccounts();
    Account findAccountById(Long accountId);
    void deleteAccountById(Long accountId);
    void deleteAllAccounts();
    void transferMoney(Account fromAccount, Account toAccount, BigDecimal money);
}
