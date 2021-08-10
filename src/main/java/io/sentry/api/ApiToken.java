package io.sentry.api;

import java.util.Optional;

public final class ApiToken {
    private final String token;

    public ApiToken(String token) throws InvalidApiToken {
        if (token.length() != 64) {
            throw new InvalidApiToken("Wrong length for Sentry API token");
        }

        for (int i = 0; i < token.length(); i++) {
            if (Character.digit(token.charAt(i), 16) == -1) {
                throw new InvalidApiToken("Invalid characters in Sentry API token");
            }
        }

        this.token = token;
    }

    @Override
    public String toString() {
        return token;
    }
}
