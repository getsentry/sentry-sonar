package io.sentry.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.sentry.api.schema.SentryIssue;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ApiClient {
    public static final String DEFAULT_HOST = "https://sentry.io/";

    private final OkHttpClient client;
    private final Gson gson;

    private final ApiToken token;
    private final String host;

    public ApiClient(ApiToken token) {
        this(token, DEFAULT_HOST);
    }

    public ApiClient(ApiToken token, String host) {
        this.client = new OkHttpClient();
        this.gson = new Gson();

        this.token = token;
        this.host = host;
    }

    protected OkHttpClient getClient() {
        return client;
    }

    protected ApiToken getToken() {
        return token;
    }

    protected String getHost() {
        return host;
    }

    protected String getAuthHeader() {
        return "Bearer " + getToken();
    }

    public HttpUrl getUrl(String... path) {
        HttpUrl.Builder builder = HttpUrl.parse(host)
                .newBuilder()
                .addPathSegments("api/0");

        for (String segment : path) {
            builder.addPathSegment(segment);
        }

        // force a trailing slash
        builder.addPathSegment("");

        return builder.build();
    }

    public <T> T get(Type type, HttpUrl url) throws RequestException {
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", getAuthHeader())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RequestException("Received bad status code " + response.code());
            }

            ResponseBody body = response.body();
            if (body == null) {
                throw new RequestException("Missing response body");
            }

            return gson.fromJson(body.string(), type);
        } catch (IOException error) {
            throw new RequestException("Could not send request to Sentry", error);
        }
    }

    public <T> T get(Type type, String... path) throws RequestException {
        return get(type, getUrl(path));
    }

    public List<SentryIssue> listIssues(String organization, String project) throws RequestException {
        Type type = new TypeToken<ArrayList<SentryIssue>>() {
        }.getType();

        return get(type, "projects", organization, project, "issues");
    }
}
