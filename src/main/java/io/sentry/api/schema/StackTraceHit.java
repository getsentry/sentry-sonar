package io.sentry.api.schema;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StackTraceHit {
    @SerializedName("stack.filename")
    private List<String> fileNames;
    @SerializedName("stack.lineno")
    private List<Integer> lineNumbers;
    @SerializedName("issue")
    private String issueShortId;
    @SerializedName("issue.id")
    private long issueId;
    private long count;

    public List<String> getFileNames() {
        return fileNames;
    }

    public List<Integer> getLineNumbers() {
        return lineNumbers;
    }

    public String getIssueShortId() {
        return issueShortId;
    }

    public long getIssueId() {
        return issueId;
    }

    public long getCount() {
        return count;
    }
}
