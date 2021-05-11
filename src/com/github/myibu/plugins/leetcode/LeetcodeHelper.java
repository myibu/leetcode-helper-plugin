package com.github.myibu.plugins.leetcode;

import com.intellij.ide.util.PropertiesComponent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class LeetcodeHelper {
    public static final String DEFAULT_LEETCODE_URL = "https://leetcode-cn.com/problems/two-sum/";

    public LeetcodeHelper(){}

    private static LeetcodeHelper helper;

    public static LeetcodeHelper getInstance() {
        if (null == helper) {
            helper = new LeetcodeHelper();
        }
        return helper;
    }

    public LeetcodeProblem importProblemFromLeetcodeWebsite(String url, String dir) throws IOException {
        // fetch app properties
        AppProperties appProperties =  new AppProperties(PropertiesComponent.getInstance(), System.getProperties());
        // use chrome browser driver
        if (!appProperties.containsKey(AppProperties.AppParam.BROWSER_DRIVER)) {
            String browserDriver = downloadChromeDriverAndSave(appProperties);
            appProperties.put(AppProperties.AppParam.BROWSER_DRIVER, browserDriver);
            PropertiesComponent.getInstance().setValue(AppProperties.LEETCODE_HELPER_DRIVER, browserDriver);
        }
        if (AppProperties.Platform.LINUX == appProperties.get(AppProperties.AppParam.PLATFORM, AppProperties.Platform.class)) {
            Runtime.getRuntime().exec(String.format("chmod 777 %s", appProperties.get(AppProperties.AppParam.BROWSER_DRIVER, String.class)));
        }
        System.setProperty("webdriver.chrome.driver", appProperties.get(AppProperties.AppParam.BROWSER_DRIVER, String.class));

        // disable notify
        WebDriver chromeDriver = new ChromeDriver();
        chromeDriver.get(url);
        // use jsoup to parse html
        Document doc = Jsoup.parse(chromeDriver.getPageSource());
        // chrome quit
        chromeDriver.quit();
        return convertHtml2Problem(url, doc);
    }

    protected String downloadChromeDriverAndSave(AppProperties appProperties) throws IOException {
        String driverUrl = appProperties.get(AppProperties.AppParam.PLATFORM, AppProperties.Platform.class).driverUrl();
        // download chrome driver
        final HttpURLConnection connection =
                (HttpURLConnection) new URL(driverUrl).openConnection();
        connection.connect();
        InputStream networkInputStream = connection.getInputStream();
        File userDataFile = new File(AppProperties.userDataPath.toString());
        if (!userDataFile.exists()) {
            userDataFile.mkdirs();
        }
        File zipFile = Paths.get(AppProperties.userDataPath.toString(), "chromedriver.zip").toAbsolutePath().toFile();
        FileOutputStream fos = new FileOutputStream(zipFile);
        byte[] buffer = new byte[1024];
        int count = 0;
        while ((count = networkInputStream.read(buffer, 0, 1024)) != -1) {
            fos.write(buffer, 0, count);
        }
        fos.flush();
        fos.close();

        // unzip driver
        File unzipFileDir = Paths.get(userDataFile.getAbsolutePath(), "chromedriver").toAbsolutePath().toFile();
        if (!unzipFileDir.exists()) {
            unzipFileDir.mkdirs();
        }
        String entryFilePath = "";
        ZipFile zip = new ZipFile(zipFile);
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>)zip.entries();
        while(entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            entryFilePath = unzipFileDir.getAbsolutePath() + File.separator + entry.getName();
            File entryFile = new File(entryFilePath);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(entryFile));
            BufferedInputStream bis = new BufferedInputStream(zip.getInputStream(entry));
            buffer = new byte[1024];
            count = 0;
            while ((count = bis.read(buffer, 0, 1024)) != -1) {
                bos.write(buffer, 0, count);
            }
            bos.flush();
            bos.close();
            break;
        }
        return entryFilePath;
    }

    protected LeetcodeProblem convertHtml2Problem(String url, Document doc) {
        LeetcodeProblem.Builder problemBuilder = LeetcodeProblem.builder();
        // problem title
        Elements titleElement = doc.select("h4[data-cypress]");
        if (null != titleElement && titleElement.size() > 0) {
            String title = titleElement.last().text();
            problemBuilder.problemTitle(title);

            if (title.contains(".")) {
                problemBuilder.problemIndex(title.split("[.]")[0]);
            }
        }
        // problem url
        problemBuilder.problemUrl(url);
        // problem description
        Elements problemDescription =  doc.select("div.notranslate");
        if (null != problemDescription && problemDescription.size() > 0) {
            Elements pList = problemDescription.last().children();
            if (null != pList && pList.size() > 0) {
                StringBuilder builder = new StringBuilder();
                for (Element element: pList) {
                    if (element.is("ul")) {
                        for (Element liElement: element.select("li")) {
                            builder.append(liElement.text()).append("\n");
                        }
                        continue;
                    }
                    builder.append(element.text()).append("\n");
                }
                problemBuilder.problemDescription(builder.toString());
            }
        }
        // problem view line
        Elements viewLine =  doc.select("div.view-lines");
        if (null != viewLine && viewLine.size() > 0) {
            Elements viewLines = viewLine.last().select("div.view-line");
            if (null != viewLines && viewLines.size() > 0) {
                StringBuilder builder = new StringBuilder();
                for (Element element: viewLines) {
                    builder.append(element.text()).append("\n");
                }
                problemBuilder.codeLines(builder.toString());
            }
        }
        return problemBuilder.build();
    }

    public void writeProblem2File(LeetcodeProblem problem, String dir, String className, String packageName) {
        className = isEmpty(className) ? "Leetcode_" + problem.problemIndex() : className;
        File javaFile = new File(dir + File.separator + className + ".java");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(javaFile));
            bos.write(LeetcodeProblemTextFormat.format(problem, className, packageName).getBytes(StandardCharsets.UTF_8));
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isEmpty(String text) {
        return null == text || "".equals(text.trim());
    }
}
