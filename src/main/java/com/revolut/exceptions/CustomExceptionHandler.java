package com.revolut.exceptions;

import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by iurii on 7/21/17.
 */
@Provider
@Singleton
public class CustomExceptionHandler implements ExceptionMapper<NotEnoughtMoneyException> {
    @Override
    public Response toResponse(NotEnoughtMoneyException exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage()).build();
    }
}
