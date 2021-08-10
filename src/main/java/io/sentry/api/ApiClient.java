package io.sentry.api;

import okhttp3.OkHttpClient;

public class ApiClient {
    private final ApiToken token;
    private final OkHttpClient client;

    public ApiClient(ApiToken token) {
        this.token = token;
        this.client = new OkHttpClient();
    }

    protected OkHttpClient getClient() {
        return client;
    }

    protected ApiToken getToken() {
        return token;
    }
}
