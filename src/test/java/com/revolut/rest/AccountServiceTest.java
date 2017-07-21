package com.revolut.rest;

import com.revolut.exceptions.NotEnoughtMoneyException;
import com.revolut.exceptions.UnexpectedServerError;
import com.revolut.model.Account;
import com.revolut.repository.AccountStorageDao;
import com.revolut.repository.impl.AccountStorageDaoImpl;
import com.revolut.service.AccountService;
import com.revolut.service.impl.AccountServiceImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.*;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AccountServiceImpl.class)
public class AccountServiceTest {
    private static final AccountStorageDao accountStorageDao = new AccountStorageDaoImpl();
    private static final AccountService accountService = new AccountServiceImpl(accountStorageDao);

    private static ExecutorService threadPool = Executors.newFixedThreadPool(3);
    private Account accountDebit = Account.of(11111L, new BigDecimal(50));
    private Account accountCredit = Account.of(22222L, new BigDecimal(100));

    @Rule
    public ExpectedException exceptionGrabber = ExpectedException.none();

    @Before
    public void cleanUp() {
        accountService.deleteAllAccounts();
    }

    @Test
    public void testMoneyTransferThrowExceptionNotEnoughtMoney() {
        accountService.saveAccounts(Arrays.asList(accountDebit, accountCredit));
        exceptionGrabber.expect(NotEnoughtMoneyException.class);
        Boolean wasTrans = accountService.moneyTransfer(accountDebit.getAccountId(), accountCredit.getAccountId(),
                new BigDecimal(100));
        assertThat(wasTrans, nullValue());
    }

    @Test(expected = UnexpectedServerError.class)
    public void testMoneyTransferAtomicity() throws Exception {
        BigDecimal accCred = accountCredit.getBalance();
        BigDecimal accDeb = accountDebit.getBalance();
        accountService.saveAccounts(Arrays.asList(accountDebit, accountCredit));
        AccountServiceImpl spy = PowerMockito.spy(new AccountServiceImpl(accountStorageDao));
        when(spy, method(AccountServiceImpl.class, "deposit", Account.class, BigDecimal.class)).
                withArguments(notNull(), notNull()).
                thenThrow(UnexpectedServerError.class);
        spy.moneyTransfer(accountCredit.getAccountId(), accountDebit.getAccountId(), new BigDecimal(5));
        assertThat(accCred, equalTo(accountCredit.getBalance()));
        assertThat(accDeb, equalTo(accountDebit.getBalance()));
    }

    @Test
    public void testMoneyTransferInDifferentOrderInParallel() throws InterruptedException, ExecutionException{
        List<Future<Boolean>> futures = new ArrayList<>();
        accountService.saveAccounts(Arrays.asList(accountDebit, accountCredit));
        Future<Boolean> future_withdrawn = threadPool.submit(() -> accountService.moneyTransfer
                (accountDebit.getAccountId(), accountCredit.getAccountId(), new BigDecimal(10)));
        Future<Boolean> future_depos = threadPool.submit(() -> accountService.moneyTransfer
                (accountCredit.getAccountId(), accountDebit.getAccountId(), new BigDecimal(10)));
        futures.add(future_withdrawn);
        futures.add(future_depos);

        for (Future f : futures) {
            f.get();
        }

        boolean allDone = true;
        for(Future<?> future : futures){
            allDone &= future.isDone();
        }
        assertThat(allDone, is(true));
    }
}
