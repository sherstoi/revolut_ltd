package com.revolut.service.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.revolut.exceptions.NotEnoughtMoneyException;
import com.revolut.model.Account;
import com.revolut.repository.AccountStorageDao;
import com.revolut.service.AccountService;

import java.math.BigDecimal;
import java.util.List;

/**
 * This class represent implementation of AccountService
 * class. Please use this class to work with money transactions.
 * It's singleton class.
 * @Author Iurii
 * @Version 1.0
 */
@Singleton
public class AccountServiceImpl implements AccountService {
    private AccountStorageDao accountStorageDao;

    @Inject
    public AccountServiceImpl(AccountStorageDao accountStorageDao) {
        this.accountStorageDao = accountStorageDao;
    }

    public void saveAccount(Account account) {
        accountStorageDao.saveAccount(account);
    }

    public void saveAccounts(List<Account> accountList) {
        accountStorageDao.saveAccounts(accountList);
    }

    public void withdrawn(Account account, BigDecimal balance) {
        synchronized (account.getLock()) {
            BigDecimal newBalance = account.getBalance().subtract(balance);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new NotEnoughtMoneyException("There is not enought money on account to make withdrawn operation!");
            }
            account.setBalance(newBalance);
        }
        saveAccount(account);
    }

    public void deposit(Account account, BigDecimal balance) {
        synchronized (account.getLock()) {
            BigDecimal newBalance = account.getBalance().add(balance);
            account.setBalance(newBalance);
        }
        saveAccount(account);
    }

    public void transferMoney(Account fromAccount, Account toAccount, BigDecimal money) {
        Object lock1 = fromAccount.getAccountId() < toAccount.getAccountId() ?
                fromAccount.getLock() : toAccount.getLock();
        Object lock2 = fromAccount.getAccountId() < toAccount.getAccountId() ?
                toAccount.getLock() : fromAccount.getLock();
        synchronized (lock1) {
            synchronized (lock2) {
                withdrawn(fromAccount, money);
                deposit(toAccount, money);
            }
        }
        saveAccount(fromAccount);
        saveAccount(toAccount);
    }

    public List<Account> findAllAccounts() {
        return accountStorageDao.getAllAccounts();
    }

    public Account findAccountById(Long accountId) {
        return accountStorageDao.findAccountById(accountId).
                map(account -> account).
                orElse(null);
    }

    public void deleteAccountById(Long accountId) {
        accountStorageDao.deleteAccountById(accountId);
    }

    public void deleteAllAccounts() {
        accountStorageDao.deleteAllAccounts();
    }
}
