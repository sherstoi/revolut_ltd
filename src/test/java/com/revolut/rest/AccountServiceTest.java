package com.revolut.rest;

import com.revolut.exceptions.NotEnoughtMoneyException;
import com.revolut.model.Account;
import com.revolut.repository.AccountStorageDao;
import com.revolut.repository.impl.AccountStorageDaoImpl;
import com.revolut.service.AccountService;
import com.revolut.service.impl.AccountServiceImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AccountServiceTest {
    private static final int THREAD_COUNT = 3;
    private static final AccountStorageDao accountStorageDao = new AccountStorageDaoImpl();
    private static final AccountService accountService = new AccountServiceImpl(accountStorageDao);
    private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

    private Account accountDebit = Account.of(11111L, new BigDecimal(50));
    private Account accountCredit = Account.of(22222L, new BigDecimal(100));

    @Rule
    public ExpectedException exceptionGrabber = ExpectedException.none();

    @Test
    public void testWithdrawnThrowNotEnougthMoneyException() {
        exceptionGrabber.expect(NotEnoughtMoneyException.class);
        accountService.withdrawn(accountDebit, new BigDecimal(150));
    }

    @Test
    public void testWithdrawnAndDepositInFewThreads() throws InterruptedException, ExecutionException {
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            Future<?> future_withdrawn = executorService.submit(() -> accountService.withdrawn(accountCredit, new BigDecimal(THREAD_COUNT)));
            Future<?> future_deposit = executorService.submit(() -> accountService.deposit(accountCredit, new BigDecimal(THREAD_COUNT)));
            futures.add(future_withdrawn);
            futures.add(future_deposit);
        }

        for (Future f : futures) {
            f.get();
        }

        boolean allDone = true;
        for(Future<?> future : futures){
            allDone &= future.isDone();
        }

        assertThat(accountCredit.getBalance(), equalTo(new BigDecimal(100)));
    }

    @Test
    public void testMoneyTransferInFewThreads() throws InterruptedException, ExecutionException {
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            Future<?> future_from_cred_to_deb = executorService.submit(() ->
                    accountService.transferMoney(accountCredit, accountDebit, new BigDecimal(THREAD_COUNT)));
            Future<?> future_from_deb_to_cr = executorService.submit(() ->
                    accountService.transferMoney(accountDebit, accountCredit, new BigDecimal(THREAD_COUNT)));
            futures.add(future_from_cred_to_deb);
            futures.add(future_from_deb_to_cr);
        }

        for (Future f : futures) {
            f.get();
        }

        boolean allDone = true;
        for(Future<?> future : futures){
            allDone &= future.isDone();
        }

        assertThat(accountCredit.getBalance(), equalTo(new BigDecimal(100)));
    }
}
