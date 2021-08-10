package io.sentry.api;

import io.sentry.api.schema.SentryIssue;
import junit.framework.TestCase;
import okhttp3.HttpUrl;

import java.util.List;

public class ApiClientTest extends TestCase {
    private ApiClient client;

    public void setUp() throws Exception {
        super.setUp();

        this.client = new ApiClient(new ApiToken("TODO"));
    }

    public void testGetUrl() {
        HttpUrl url = client.getUrl("projects", "foo", "bar");
        assertEquals("https://sentry.io/api/0/projects/foo/bar/", url.toString());
    }

    public void testGetIssues() throws RequestException {
        List<SentryIssue> issues = client.listIssues("sentry", "sentry");
        assertFalse("there should be issues", issues.isEmpty());
    }
}