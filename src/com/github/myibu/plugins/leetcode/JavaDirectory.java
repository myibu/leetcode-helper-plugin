package com.github.myibu.plugins.leetcode;

public class JavaDirectory {
    boolean isPackage;
    String absolutePath;
    String packageName;

    public JavaDirectory(boolean isPackage, String absolutePath, String packageName) {
        this.isPackage = isPackage;
        this.absolutePath = absolutePath;
        this.packageName = packageName;
    }
    public JavaDirectory(String absolutePath) {
        this(false, absolutePath, null);
    }

    public boolean isPackage() {
        return isPackage;
    }

    public String absolutePath() {
        return absolutePath;
    }

    public String packageName() {
        return packageName;
    }
}
