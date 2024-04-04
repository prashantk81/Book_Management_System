package com.sismics.books.core.service.facebook;

/**
 * Exception raised on a Facebook authentication error.
 *
 * @author jtremeaux 
 */
public class FacebookServiceException extends Exception {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor of FacebookServiceException.
     * 
     * @param message Message
     */
    public FacebookServiceException(String message) {
        super(message);
    }
}