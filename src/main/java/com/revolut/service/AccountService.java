package com.revolut.service;

import com.revolut.model.Account;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by iurii on 7/16/17.
 */
public interface AccountService {
    void saveAccount(Account account);
    void saveAccounts(List<Account> accountList);
    List<Account> findAllAccounts();
    Account findAccountById(Long accountId);
    void deleteAccountById(Long accountId);
    void deleteAllAccounts();
    Boolean moneyTransfer(Long fromAccountId, Long toAccountId, BigDecimal money);
}
