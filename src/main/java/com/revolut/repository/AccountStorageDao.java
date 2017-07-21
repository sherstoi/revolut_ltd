package com.revolut.repository;

import com.revolut.model.Account;

import java.util.List;

public interface AccountStorageDao {
    Account findAccountById(Long accountId);
    List<Account> getAllAccounts();
    void saveAccount(Account account);
    void saveAccounts(List<Account> accountList);
    void deleteAccountById(Long accountId);
    void deleteAllAccounts();
    int count();
}
