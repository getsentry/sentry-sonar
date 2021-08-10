package io.sentry.api.schema;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StackTraceHit {
    @SerializedName("stack.abs_path")
    private List<String> absPath;
    @SerializedName("stack.lineno")
    private List<Long> lineNumber;
    private String issue;
    private long count;

    public List<String> getAbsPath() {
        return absPath;
    }

    public List<Long> getLineNumber() {
        return lineNumber;
    }

    public String getIssue() {
        return issue;
    }

    public long getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "QueryResultEntry{" +
                "absPath=" + absPath +
                ", lineNumber=" + lineNumber +
                ", issue='" + issue + '\'' +
                ", count=" + count +
                '}';
    }
}
