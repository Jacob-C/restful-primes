package com.example.prime.core;

/**
 * Exception to indicate that a calculation has failed
 */
public class CalculationException extends Exception {

    private static final long serialVersionUID = 2452098027949827805L;

    public CalculationException() {}

    public CalculationException(final String message) {
        super(message);
    }

    public CalculationException(final Throwable cause) {
        super(cause);
    }

    public CalculationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CalculationException(
            final String message,
            final Throwable cause,
            final boolean enableSuppression,
            final boolean writableStackTrace) {

        super(message, cause, enableSuppression, writableStackTrace);
    }

}
