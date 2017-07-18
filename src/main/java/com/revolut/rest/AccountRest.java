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
    @Path("/showall")
    public List<Account> findAll() {
        return accountService.findAllAccounts();
    }

    @GET
    @Path("/findacc")
    public Account findAccountById(@QueryParam("accId") Long accountId) {
        return accountService.findAccountById(accountId);
    }

    @POST
    @Path("/withdrawn")
    public void withdrawn(@QueryParam("balance") BigDecimal balance, Account account) {
        accountService.withdrawn(account, balance);
    }

    @POST
    @Path("/deposit")
    public void deposit(@QueryParam("balance") BigDecimal amount, Account account) {
        accountService.deposit(account, amount);
    }

    @POST
    @Path("/create")
    public void createAccount(Account account) {
        accountService.saveAccount(account);
    }

    @POST
    @Path("/transfer")
    public void createAccount(@QueryParam("money") BigDecimal money, Account accountFrom, Account accountTo) {
        accountService.transferMoney(accountFrom, accountTo, money);
    }

    @PUT
    @Path("/deleteall")
    public void deleteAllAccounts() {
        accountService.deleteAllAccounts();
    }
}
