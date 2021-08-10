package io.sentry.api.schema;

public class SentryIssue {
    public static final String STATUS_UNRESOLVED = "unresolved";

    String id;
    String shortId;
    String title;
    String culprit;
    String permalink;
    String status;

    public String getId() {
        return id;
    }

    public String getShortId() {
        return shortId;
    }

    public String getTitle() {
        return title;
    }

    public String getCulprit() {
        return culprit;
    }

    public String getPermalink() {
        return permalink;
    }

    public String getStatus() {
        return status;
    }

    public boolean isUnresolved() {
        return getStatus().equals(STATUS_UNRESOLVED);
    }
}
