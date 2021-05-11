package com.github.myibu.plugins.leetcode;


import java.lang.reflect.Field;

import static com.github.myibu.plugins.leetcode.LeetcodeHelper.isEmpty;

/**
 * format a problem in java file
 * {problemUrl}
 * {problemTitle}
 * {problemDescription}
 */
public class LeetcodeProblemTextFormat {

    public static final String DEFAULT_NOTE_FORMAT = "/**\n" +
            " * <h1>{problemTitle}</h1>\n * \n" +
            " * <a href=\"{problemUrl}\">{problemUrl}</a>\n" +
            " * <pre>\n" +
            "{problemDescription}" +
            " * </pre>\n" +
            " */\n";

    public static final String DEFAULT_CODE_FORMAT = "public class {className} {\n" +
            "\tpublic static class Solution {\n" +
            "\n" +
            "\t}\n\n" +
            "\tpublic static void main(String[] args) {\n" +
            "\n" +
            "\t}\n" +
            "}\n";


    LeetcodeProblem problem;

    protected LeetcodeProblemTextFormat(LeetcodeProblem problem) {
        this.problem = problem;
    }

    public static String formatCode(LeetcodeProblem problem, String pattern, String className, String packageName) {
        LeetcodeProblemTextFormat tmp = new LeetcodeProblemTextFormat(problem);
        return tmp.formatCode(pattern, className, packageName);
    }

    public static String formatNote(LeetcodeProblem problem, String pattern) {
        LeetcodeProblemTextFormat tmp = new LeetcodeProblemTextFormat(problem);
        return tmp.formatNote(pattern);
    }

    public String formatNote(String pattern) {
        Field[] fields = problem.getClass().getDeclaredFields();
        String result = pattern;
        for (Field field: fields) {
            try {
                if (field.getName().equals("problemDescription") && DEFAULT_NOTE_FORMAT.equals(pattern)) {
                    String problemMultiLine = field.get(problem).toString();
                    StringBuilder builder = new StringBuilder();
                    for (String line: problemMultiLine.split("\n")) {
                        builder.append(" * ").append(line).append("\n");
                    }
                    result = result.replaceAll("\\{" + field.getName() + "\\}", builder.toString());
                    continue;
                }
                result = result.replaceAll("\\{" + field.getName() + "\\}", field.get(problem).toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public String formatCode(String pattern, String className, String packageName) {
        StringBuilder builder = new StringBuilder();
        if (!isEmpty(packageName)) {
            builder.append(String.format("package %s;\n", packageName));
        }
        className = isEmpty(className) ? "Leetcode_" + problem.problemIndex() : className;
        builder.append(pattern.replaceAll("\\{className\\}", className));
        return builder.toString();
    }

    public static String format(LeetcodeProblem problem, String className, String packageName) {
        StringBuilder builder = new StringBuilder();
        builder.append(formatNote(problem, DEFAULT_NOTE_FORMAT));
        builder.append(formatCode(problem, DEFAULT_CODE_FORMAT, className, packageName));
        return builder.toString();
    }
}
