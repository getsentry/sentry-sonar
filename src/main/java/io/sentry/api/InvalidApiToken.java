package io.sentry.api;

public class InvalidApiToken extends Exception {
    public InvalidApiToken(String message) {
        super(message);
    }
}
