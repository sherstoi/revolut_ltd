package com.revolut.exceptions;

/**
 * This class represent exception which
 * thrown when there is not enought money
 * on account for transfer.
 * @Author Iurii
 * @Version 1.0
 */
public class NotEnoughtMoneyException extends RuntimeException {

    public NotEnoughtMoneyException(String message) {
        super(message);
    }
}
