package com.revolut.repository.impl;

import com.google.inject.Singleton;
import com.revolut.model.Account;
import com.revolut.repository.AccountStorageDao;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * This class represents implementation of
 * AccountStorageDao interface. Please use
 * below class to work with in-memory storage.
 * It's singleton class.
 * @Author Iurii
 * @Version 1.0
 */
@Singleton
public class AccountStorageDaoImpl implements AccountStorageDao {
    private final ConcurrentHashMap<Long, Account> accountMap = new ConcurrentHashMap<>();

    public Account findAccountById(Long accountId) {
        return accountMap.get(accountId);
    }

    public List<Account> getAllAccounts() {
        return accountMap.values().stream().collect(Collectors.toList());
    }

    public void saveAccount(Account account) {
        if (account.getLock() == null) {
            account.setLock(new Object());
        }
        Optional.ofNullable(account).
                map(Account::getAccountId).
                ifPresent(accountId -> accountMap.put(accountId, account));
    }

    public void saveAccounts(List<Account> accountList) {
        if (!CollectionUtils.isEmpty(accountList)) {
            accountList.forEach(this::saveAccount);
        }
    }

    public void deleteAccountById(Long accountId) {
        accountMap.remove(accountId);
    }

    public void deleteAllAccounts() {
        accountMap.clear();
    }

    public int count() {
        return accountMap.size();
    }
}
