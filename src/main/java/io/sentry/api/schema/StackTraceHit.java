package io.sentry.api.schema;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class StackTraceHit {
    @SerializedName("stack.abs_path")
    private List<String> absPaths;
    @SerializedName("stack.lineno")
    private List<Integer> lineNumbers;
    @SerializedName("issue")
    private String issueShortId;
    @SerializedName("issue.id")
    private long issueId;
    private long count;

    public List<String> getAbsPaths() {
        return absPaths == null ? Collections.emptyList() : absPaths;
    }

    public List<Integer> getLineNumbers() {
        return lineNumbers == null ? Collections.emptyList() : lineNumbers;
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
