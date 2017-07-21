package com.revolut;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.revolut.config.ServerConfig;
import com.revolut.exceptions.CustomExceptionHandler;
import com.revolut.repository.AccountStorageDao;
import com.revolut.repository.impl.AccountStorageDaoImpl;
import com.revolut.rest.AccountRest;
import com.revolut.service.AccountService;
import com.revolut.service.impl.AccountServiceImpl;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * It's the main class of this application.
 * You should use run method to start dropwizard
 * context.
 * @Author Iurii
 * @Version 1.0
 */
public class AppStarter extends Application<ServerConfig> {
    private static final String SERVER = "server";

    @Override
    public void initialize(Bootstrap<ServerConfig> bootstrap) {}

    @Override
    public void run(ServerConfig serverConfig, Environment environment) throws Exception {
        Injector injector = createInjector(serverConfig);

        environment.jersey().register(injector.getInstance(AccountRest.class));
        environment.jersey().register(new CustomExceptionHandler());
        environment.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static void main(String[] args) throws Exception {
        new AppStarter().run(SERVER);
    }

    private Injector createInjector(final ServerConfig conf) {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(ServerConfig.class).toInstance(conf);
                bind(AccountStorageDao.class).to(AccountStorageDaoImpl.class);
                bind(AccountService.class).to(AccountServiceImpl.class);
            }
        });
    }
}
