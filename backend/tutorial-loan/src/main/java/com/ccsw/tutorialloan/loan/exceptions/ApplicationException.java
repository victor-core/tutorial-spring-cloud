package com.ccsw.tutorialloan.loan.exceptions;

public class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ApplicationException(String message) {
        super(message);
    }
}
