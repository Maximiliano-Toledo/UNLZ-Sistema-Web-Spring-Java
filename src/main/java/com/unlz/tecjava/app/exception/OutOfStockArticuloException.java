package com.unlz.tecjava.app.exception;

public class OutOfStockArticuloException extends Exception {

    public OutOfStockArticuloException() {
        super();
    }


    public OutOfStockArticuloException(String message) {
        super(message);
    }


    public OutOfStockArticuloException(String message, Throwable cause) {
        super(message, cause);
    }
}