package com.github.myibu.plugins.leetcode;

/**
 * a leetcode description
 */
public class LeetcodeProblem {
    String problemIndex;
    String problemTitle;
    String problemDescription;
    String problemUrl;
    String codeLines;

    public String problemIndex() {
        return problemIndex;
    }

    public String problemTitle() {
        return problemTitle;
    }

    public String problemDescription() {
        return problemDescription;
    }

    public String problemUrl() {
        return problemUrl;
    }

    public String codeLines() {
        return codeLines;
    }

    public static Builder builder() {
        return new Builder();
    }

    private LeetcodeProblem(Builder builder) {
        this.problemIndex = builder.problemIndex;
        this.problemTitle = builder.problemTitle;
        this.problemDescription = builder.problemDescription;
        this.problemUrl = builder.problemUrl;
        this.codeLines = builder.codeLines;
    }

    public static final class Builder {
        String problemIndex;
        String problemTitle;
        String problemDescription;
        String problemUrl;
        String codeLines;
        public LeetcodeProblem build() {
            return new LeetcodeProblem(this);
        }

        public Builder problemIndex(String problemIndex) {
            this.problemIndex = problemIndex;
            return this;
        }

        public Builder problemTitle(String problemTitle) {
            this.problemTitle = problemTitle;
            return this;
        }

        public Builder problemDescription(String problemDescription) {
            this.problemDescription = problemDescription;
            return this;
        }

        public Builder problemUrl(String problemUrl) {
            this.problemUrl = problemUrl;
            return this;
        }

        public Builder codeLines(String codeLines) {
            this.codeLines = codeLines;
            return this;
        }
    }
}