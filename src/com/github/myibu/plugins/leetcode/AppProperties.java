package com.github.myibu.plugins.leetcode;

import com.intellij.ide.util.PropertiesComponent;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;

public class AppProperties  extends HashMap<AppProperties.AppParam, Object> {
    public static final Path userDataPath = Paths.get(System.getProperty("user.home"), ".leetcode-helper").toAbsolutePath();
    public static final String LEETCODE_HELPER_DRIVER = "leetcode.helper.driver";

    public AppProperties(PropertiesComponent appProperties,
                         Properties systemProperties) {
        super();
        this.put(AppParam.PLATFORM, Platform.of(systemProperties.getProperty("os.name")));
        this.put(AppParam.USER_HOME, systemProperties.getProperty("user.name"));
        if (null != appProperties.getValue(LEETCODE_HELPER_DRIVER)) {
            this.put(AppParam.BROWSER_DRIVER, appProperties.getValue(LEETCODE_HELPER_DRIVER));
        }
    }

    public <T> T get(AppParam param, Class<T> type) {
        Object value = super.get(param);
        return (T)value;
    }

    public enum Platform {
        /**window*/
        WINDOW("http://chromedriver.storage.googleapis.com/90.0.4430.24/chromedriver_win32.zip"),
        /**linux*/
        LINUX("http://chromedriver.storage.googleapis.com/90.0.4430.24/chromedriver_linux64.zip"),
        /**mac*/
        MAC("http://chromedriver.storage.googleapis.com/90.0.4430.24/chromedriver_mac64.zip");

        private String driverUrl;
        Platform(String driverUrl) {
            this.driverUrl = driverUrl;
        }

        public String driverUrl() {
            return this.driverUrl;
        }

        public static Platform of(String name) {
            if (name.startsWith("Linux") || name.startsWith("linux")) {
                return LINUX;
            }
            if (name.startsWith("Mac") || name.startsWith("mac")) {
                return MAC;
            }
            return WINDOW;
        }
    }

    public enum AppParam {
        BROWSER_DRIVER,
        PLATFORM,
        USER_HOME;
    }
}
