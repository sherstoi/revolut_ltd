package com.revolut.rest;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.revolut.model.Account;
import com.revolut.repository.AccountStorageDao;
import com.revolut.repository.impl.AccountStorageDaoImpl;
import com.revolut.service.AccountService;
import com.revolut.service.impl.AccountServiceImpl;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.math.BigDecimal;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;

public class AccountRestTest {
    private static final AccountStorageDao acountDao = new AccountStorageDaoImpl();
    private static final AccountService accountService = new AccountServiceImpl(acountDao);

    private Account accountCredit = Account.of(22222L, new BigDecimal(100));
    private Account accountDebit = Account.of(11111L, new BigDecimal(50));

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new AccountRest(accountService))
            .build();

    @Rule
    public ExpectedException exceptionGrabber = ExpectedException.none();

    @Before
    public void cleanUp() {
        accountService.deleteAllAccounts();
        resources.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Test
    public void testSaveAndFindAccount() throws IOException {
        Entity<?> entity = Entity.entity(accountCredit, MediaType.APPLICATION_JSON_TYPE);
        resources.target("/create").request().post(entity);
        assertThat(acountDao.count(), is(1));
        String response = resources.target("/findacc").
                queryParam("accId", accountCredit.getAccountId()).
                request().get(String.class);
        ObjectReader objectReader = resources.getObjectMapper().readerFor(Account.class);
        Account accountResponse = objectReader.readValue(response);
        assertThat(accountResponse.getAccountId(), equalTo(accountCredit.getAccountId()));
    }

    @Test
    public void testDeleteAccount() throws IOException {
        Entity<?> entity = Entity.entity(accountCredit, MediaType.APPLICATION_JSON_TYPE);
        resources.target("/create").request().post(entity);
        assertThat(acountDao.count(), is(1));
        resources.target("/delete").queryParam("accountId", accountCredit.getAccountId()).request().delete();
        String response = resources.target("/findacc").
                queryParam("accId", accountCredit.getAccountId()).
                request().get(String.class);
        assertThat(response, isEmptyString());
    }

    @Test
    public void testMoneyTransferSuccess() throws IOException {
        Entity<?> entityCredit = Entity.entity(accountCredit, MediaType.APPLICATION_JSON_TYPE);
        resources.target("/create").request().post(entityCredit);
        Entity<?> entityDebit = Entity.entity(accountDebit, MediaType.APPLICATION_JSON_TYPE);
        resources.target("/create").request().post(entityDebit);
        assertThat(acountDao.count(), is(2));
        BigDecimal minusMoney = new BigDecimal(5);
        resources.target("/transfer").queryParam("fromAccId", accountCredit.getAccountId())
                                     .queryParam("toAccId", accountDebit.getAccountId())
                                     .queryParam("balance", minusMoney).request().post(null);
        String response = resources.target("/findacc").
                queryParam("accId", accountCredit.getAccountId()).
                request().get(String.class);
        ObjectReader objectReader = resources.getObjectMapper().readerFor(Account.class);
        Account accountResponse = objectReader.readValue(response);
        assertThat(accountResponse.getBalance(), is(new BigDecimal(95)));
    }

    @Test
    public void testMoneyTransferFail() {
        Entity<?> entityCredit = Entity.entity(accountCredit, MediaType.APPLICATION_JSON_TYPE);
        resources.target("/create").request().post(entityCredit);
        Entity<?> entityDebit = Entity.entity(accountDebit, MediaType.APPLICATION_JSON_TYPE);
        resources.target("/create").request().post(entityDebit);
        BigDecimal oldBalAccCred = accountCredit.getBalance();
        BigDecimal oldBalAccDeb = accountDebit.getBalance();
        assertThat(acountDao.count(), is(2));
        BigDecimal minusMoney = new BigDecimal(150);
        Response response = resources.target("/transfer").queryParam("fromAccId", accountCredit.getAccountId())
                .queryParam("toAccId", accountDebit.getAccountId())
                .queryParam("balance", minusMoney).request().post(null);
        assertThat(response.getStatus(), is(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));
        assertThat(acountDao.findAccountById(accountCredit.getAccountId()).getBalance(), is(oldBalAccCred));
        assertThat(acountDao.findAccountById(accountDebit.getAccountId()).getBalance(), is(oldBalAccDeb));
    }
}
