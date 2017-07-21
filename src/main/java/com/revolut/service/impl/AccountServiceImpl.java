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

    private boolean withdrawn(Account account, BigDecimal balance) {
        boolean wasUpdated = false;
        if (account != null) {
            BigDecimal newBalance = account.getBalance().subtract(balance);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new NotEnoughtMoneyException("There is not enought money on account to make withdrawn operation!");
            }
            account.setBalance(newBalance);
            wasUpdated = true;
        }

        return wasUpdated;
    }

    private boolean deposit(Account account, BigDecimal balance) {
        boolean wasUpdated = false;
        if (account != null) {
            BigDecimal newBalance = account.getBalance().add(balance);
            account.setBalance(newBalance);
            wasUpdated = true;
        }

        return wasUpdated;
    }

    public Boolean moneyTransfer(Long fromAccountId, Long toAccountId, BigDecimal money) {
        Boolean moneyWasTransfered = false;
        Account fromAccount = accountStorageDao.findAccountById(fromAccountId);
        Account toAccount = accountStorageDao.findAccountById(toAccountId);

        if (fromAccount != null && toAccount != null) {
            Object lock1 = fromAccount.getAccountId() < toAccount.getAccountId() ?
                    fromAccount.getLock() : toAccount.getLock();
            Object lock2 = fromAccount.getAccountId() < toAccount.getAccountId() ?
                    toAccount.getLock() : fromAccount.getLock();

            synchronized (lock1) {
                synchronized (lock2) {
                    BigDecimal accFromBalance = fromAccount.getBalance();
                    BigDecimal accToBalance = toAccount.getBalance();
                    boolean wasDepos = false, wasWithdraw = false;
                    try {
                        wasWithdraw = withdrawn(fromAccount, money);
                        wasDepos = deposit(toAccount, money);
                        moneyWasTransfered = true;
                    } finally {
                        if (!wasDepos || !wasWithdraw) {
                            fromAccount.setBalance(accFromBalance);
                            toAccount.setBalance(accToBalance);
                        }
                    }
                }
            }
        }

        return moneyWasTransfered;
    }

    public List<Account> findAllAccounts() {
        return accountStorageDao.getAllAccounts();
    }

    public Account findAccountById(Long accountId) {
        return accountStorageDao.findAccountById(accountId);
    }

    public void deleteAccountById(Long accountId) {
        accountStorageDao.deleteAccountById(accountId);
    }

    public void deleteAllAccounts() {
        accountStorageDao.deleteAllAccounts();
    }
}
