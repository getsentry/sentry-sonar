package io.sentry.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.sentry.api.schema.ApiError;
import io.sentry.api.schema.StackTraceQueryResult;
import io.sentry.api.schema.StackTraceHit;
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

    public static class QueryParam {
        private final String name;
        private final String value;

        public QueryParam(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

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

    public HttpUrl getUrl(String[] path) {
        return getUrl(path, new QueryParam[]{});
    }

    public HttpUrl getUrl(String[] path, QueryParam[] query) {
        HttpUrl.Builder builder = HttpUrl.parse(host)
                .newBuilder()
                .addPathSegments("api/0");

        for (String segment : path) {
            builder.addPathSegment(segment);
        }

        // force a trailing slash
        builder.addPathSegment("");

        for (QueryParam param : query) {
            builder.addQueryParameter(param.getName(), param.getValue());
        }

        return builder.build();
    }

    public <T> T get(Type type, HttpUrl url) throws RequestException {
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", getAuthHeader())
                .build();

        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();

            if (!response.isSuccessful()) {
                if (body != null) {
                    ApiError apiError = gson.fromJson(body.string(), ApiError.class);
                    String message = String.format("Received bad status code %d (%s)", response.code(), apiError.getDetail());
                    throw new RequestException(message);
                }

                throw new RequestException("Received bad status code " + response.code());
            }

            if (body == null) {
                throw new RequestException("Missing response body");
            }

            return gson.fromJson(body.string(), type);
        } catch (IOException error) {
            throw new RequestException("Could not send request to Sentry", error);
        }
    }

    public <T> T get(Type type, String[] path, QueryParam[] query) throws RequestException {
        return get(type, getUrl(path, query));
    }

    public List<SentryIssue> listIssues(String organization, String project) throws RequestException {
        Type type = new TypeToken<ArrayList<SentryIssue>>() {
        }.getType();

        return get(type, new String[]{"projects", organization, project, "issues"}, new QueryParam[]{});
    }

    public List<StackTraceHit> countStackTraces(String organization, int projectId) throws RequestException {
        QueryParam[] query = {
                new QueryParam("project", String.valueOf(projectId)),
                new QueryParam("query", "event.type:error"),
                new QueryParam("sort", "-count"),
                new QueryParam("statsPeriod", "14d"), // TODO: Parameterize

                new QueryParam("field", "issue"),
                new QueryParam("field", "stack.abs_path"),
                new QueryParam("field", "stack.lineno"),
                new QueryParam("field", "count()"),
                new QueryParam("field", "count_unique(user.display)"),
        };

        StackTraceQueryResult result = get(StackTraceQueryResult.class, new String[]{"organizations", organization, "eventsv2"}, query);
        return result.getData();
    }
}
