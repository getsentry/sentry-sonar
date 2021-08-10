package io.sentry.api.schema;

import java.util.List;

public class StackTraceQueryResult {
    List<StackTraceHit> data;

    public List<StackTraceHit> getData() {
        return data;
    }
}
