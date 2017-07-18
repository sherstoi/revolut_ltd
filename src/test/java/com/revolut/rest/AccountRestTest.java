package com.revolut.rest;

import com.revolut.model.Account;
import com.revolut.repository.AccountStorageDao;
import com.revolut.repository.impl.AccountStorageDaoImpl;
import com.revolut.service.AccountService;
import com.revolut.service.impl.AccountServiceImpl;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.math.BigDecimal;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class AccountRestTest {
    private static final AccountStorageDao acountDao = new AccountStorageDaoImpl();
    private static final AccountService accountService = new AccountServiceImpl(acountDao);

    private Account accountCredit = Account.of(22222L, new BigDecimal(100));

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new AccountRest(accountService))
            .build();

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
}
