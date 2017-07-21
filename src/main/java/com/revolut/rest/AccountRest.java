package com.revolut.rest;

import com.google.inject.Inject;
import com.revolut.model.Account;
import com.revolut.service.AccountService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.List;

/**
 * This class represent REST service layer.
 * @Author Iurii
 * @Version 1.0
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountRest {
    private AccountService accountService;

    @Inject
    public AccountRest(AccountService accountService) {
        this.accountService = accountService;
    }

    @GET
    @Path("/findall")
    public List<Account> findAll() {
        return accountService.findAllAccounts();
    }

    @GET
    @Path("/findacc")
    public Account findAccountById(@QueryParam("accId") Long accountId) {
        return accountService.findAccountById(accountId);
    }

    @POST
    @Path("/create")
    public void createAccount(Account account) {
        accountService.saveAccount(account);
    }

    @DELETE
    @Path("/delete")
    public void deleteAccount(@QueryParam("accountId") Long accountId) {
        accountService.deleteAccountById(accountId);
    }

    @DELETE
    @Path("/deleteall")
    public void deleteAllAccounts() {
        accountService.deleteAllAccounts();
    }

    @POST
    @Path("/transfer")
    public void transferMoney(@QueryParam("fromAccId") Long fromAccId, @QueryParam("toAccId") Long toAccId,
                              @QueryParam("balance")BigDecimal balance) {
        accountService.moneyTransfer(fromAccId, toAccId, balance);
    }
}
