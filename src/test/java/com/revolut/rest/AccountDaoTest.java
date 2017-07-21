package com.revolut.rest;

import com.revolut.model.Account;
import com.revolut.repository.AccountStorageDao;
import com.revolut.repository.impl.AccountStorageDaoImpl;
import org.junit.Before;
import org.junit.Test;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AccountDaoTest {
    private static final AccountStorageDao accountStorageDao = new AccountStorageDaoImpl();

    private Account accountDebit = Account.of(11111L, new BigDecimal(50));
    private Account accountCredit = Account.of(22222L, new BigDecimal(100));
    private List<Account> accountList = Arrays.asList(accountCredit, accountDebit);

    @Before
    public void clearStorage() {
        accountStorageDao.deleteAllAccounts();
    }

    @Test
    public void testSaveAndFindNewAccounts() {
        accountStorageDao.saveAccount(accountDebit);
        assertThat(accountStorageDao.count(), is(1));
        assertThat(accountStorageDao.getAllAccounts().size(), is(1));
        Account account = accountStorageDao.findAccountById(accountDebit.getAccountId());
        assertThat(account, notNullValue());
        accountStorageDao.deleteAllAccounts();
        assertThat(accountStorageDao.count(), is(0));
        accountStorageDao.saveAccounts(accountList);
        assertThat(accountStorageDao.count(), is(2));
    }

    @Test
    public void testDeleteAccount() {
        accountStorageDao.saveAccount(accountDebit);
        assertThat(accountStorageDao.count(), is(1));
        accountStorageDao.deleteAccountById(accountDebit.getAccountId());
        assertThat(accountStorageDao.count(), is(0));
        accountStorageDao.saveAccount(accountCredit);
        accountStorageDao.deleteAllAccounts();
        assertThat(accountStorageDao.count(), is(0));
    }
}
