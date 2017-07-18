package com.revolut.repository;

import com.revolut.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountStorageDao {
    Optional<Account> findAccountById(Long accountId);
    List<Account> getAllAccounts();
    void saveAccount(Account account);
    void saveAccounts(List<Account> accountList);
    void deleteAccountById(Long accountId);
    void deleteAllAccounts();
    int count();
}
