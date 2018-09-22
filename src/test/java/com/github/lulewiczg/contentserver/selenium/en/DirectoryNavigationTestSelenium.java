package com.github.lulewiczg.contentserver.selenium.en;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.opentest4j.MultipleFailuresError;

import com.github.lulewiczg.contentserver.selenium.ExpectedMsg;
import com.github.lulewiczg.contentserver.selenium.ExpectedMsgEN;
import com.github.lulewiczg.contentserver.selenium.SeleniumTestTemplate;
import com.github.lulewiczg.contentserver.test.utils.NavigationData;
import com.github.lulewiczg.contentserver.test.utils.TestMode;
import com.github.lulewiczg.contentserver.utils.Constants;
import com.github.lulewiczg.contentserver.utils.comparators.PathComparator;
import com.github.lulewiczg.contentserver.utils.models.Dir;
import com.google.common.base.Charsets;

/**
 * Selenium tests for navigation through directories.
 *
 * @author lulewiczg
 */
public class DirectoryNavigationTestSelenium extends SeleniumTestTemplate {

    @Override
    protected ExpectedMsg getMsgs() {
        return new ExpectedMsgEN();
    }

    @Test
    @DisplayName("Navigation is not clickable when it is nowhere to go")
    public void testNavNowhereToGo() {
        gotoShortcut(0);
        testNav((i, j) -> false);
        gotoShortcut(1);
        testNav((i, j) -> false);
    }

    @Test
    @DisplayName("Navigation is not clickable when not permitted")
    public void testNav() {
        login(TEST, TEST);
        gotoShortcut(0);
        testNav((i, len) -> false);
        clickTableItem(1);
        testNav((i, len) -> i == len - 2);
        goUp();
        clickTableItem(1);
        testNav((i, len) -> i == len - 2);

        gotoShortcut(1);
        testNav((i, len) -> false);
    }

    @Test
    @DisplayName("Every dir is accessible by admin")
    public void testNavAdmin() {
        Assumptions.assumeTrue(MODE != TestMode.ANDROID);
        login(ADMIN, TEST3);
        while (getBreadcrumbs().size() > 2) {
            testNav((i, len) -> i != len - 1);
            goUp();
        }
    }

    @Test
    @DisplayName("Opens file")
    public void testOpenFile() {
        login(TEST, TEST);
        gotoShortcut(0);
        clickTableItem(4);
        String text = driver.findElement(By.tagName("body")).getText();
        Assertions.assertEquals(TEST, text);
    }

    @Test
    @DisplayName("Downloads file")
    public void testDownloadFile() throws ClientProtocolException, IOException {
        login(TEST, TEST);
        gotoShortcut(0);
        String downloadLink = getDownloadLink(4);
        HttpGet request = new HttpGet(downloadLink);
        String auth = TEST + ":" + TEST;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth);
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(request);
        Assertions.assertEquals(HttpStatus.SC_PARTIAL_CONTENT, response.getStatusLine().getStatusCode());
        Assertions.assertEquals(Constants.Setting.PLAIN_TEXT, response.getFirstHeader("Content-Type").getValue());
        Assertions.assertEquals("4", response.getFirstHeader("Content-Length").getValue());

    }

    /**
     * Tests navigation
     *
     * @param f
     *            function to set enabled setting
     * @throws MultipleFailuresError
     *             the MultipleFailuresError
     */
    private void testNav(BiFunction<Integer, Integer, Boolean> f) throws MultipleFailuresError {
        String baseUrl = getUrl();
        String path = getPath();
        List<NavigationData> navData = buildNavData(baseUrl, path, f);
        NavigationData actualData = navData.get(navData.size() - 1);
        assertBreadcrumbs(navData);
        assertListedDirs(path, actualData);
    }

    /**
     * Builds navigation data.
     *
     * @param baseUrl
     *            base URL
     * @param path
     *            file path
     * @param f
     *            function to set enabled setting
     * @return navigation data
     */
    private List<NavigationData> buildNavData(String baseUrl, String path, BiFunction<Integer, Integer, Boolean> f) {
        NavigationData actualData = null;
        List<NavigationData> navData = new ArrayList<>();
        String[] split = path.split("\\/");
        int index = 0;
        for (String s : split) {
            if (actualData == null) {
                actualData = NavigationData.create(baseUrl, s, f.apply(index++, split.length));
            } else {
                actualData = NavigationData.create(actualData, s, f.apply(index++, split.length));
            }
            navData.add(actualData);
        }
        return navData;
    }

    /**
     * Tests if proper directory info is displayed.
     *
     * @param path
     *            current file path
     * @param expectedData
     *            expected navigation data
     * @throws MultipleFailuresError
     *             the MultipleFailuresError
     */
    private void assertListedDirs(String path, NavigationData expectedData) throws MultipleFailuresError {
        WebElement table = driver.findElement(By.xpath("//table[contains(@class,'content-table')]"));
        List<String> headers = table.findElements(By.tagName("th")).stream().map(WebElement::getText)
                .collect(Collectors.toList());
        List<String> expectedHeaders = List.of(msg.getFileName(), msg.getFileSize(), msg.getOptions());
        Assertions.assertEquals(expectedHeaders, headers);

        File[] files = getSortedFiles(path);

        List<WebElement> rows = table.findElements(By.tagName("tr"));
        rows.remove(0);
        Assertions.assertEquals(files.length, rows.size());

        Assertions.assertAll(IntStream.range(0, files.length).boxed().map(i -> () -> {
            File f = files[i];
            WebElement row = rows.get(i);
            WebElement nameCol = row.findElement(By.xpath("td[1]"));
            Assertions.assertEquals(getFileName(f), nameCol.getText());
            String expectedUrl = getExpectedURL(expectedData, f);
            Assertions.assertEquals(expectedUrl, nameCol.findElement(By.tagName("a")).getAttribute("href"));

            WebElement sizeCol = row.findElement(By.xpath("td[2]"));
            String expectedSize = new Dir(null, f.length(), null, f.isFile()).getSize();
            Assertions.assertEquals(expectedSize, sizeCol.getText());

            WebElement downloadCol = row.findElement(By.xpath("td[3]"));
            String expectedDownloadUrl = expectedUrl + "&download=true";
            WebElement downloadButton = downloadCol.findElement(By.tagName("a"));
            Assertions.assertEquals(expectedDownloadUrl, downloadButton.getAttribute("href"));
            String imgSrc = downloadButton.findElement(By.tagName("img")).getAttribute("src");
            Assertions.assertEquals(MODE.getUrl() + "/icons/download.png", imgSrc);
        }));
    }

    /**
     * Gets files in given directory and returns them in sorted order.
     *
     * @param path
     *            path
     * @return sorted files
     */
    private File[] getSortedFiles(String path) {
        File[] files = Paths.get(path).toFile().listFiles();

        Map<String, File> tmpMap = Arrays.stream(files).collect(Collectors.toMap(i -> getFileName(i), i -> i));
        List<Dir> tmpList = tmpMap.entrySet().stream().map(i -> new Dir(getFileName(i.getValue()), 0, "", i.getValue().isFile()))
                .collect(Collectors.toList());
        Collections.sort(tmpList, new PathComparator());
        files = tmpList.stream().map(i -> tmpMap.get(i.getName())).collect(Collectors.toList()).toArray(new File[] {});
        return files;
    }

    /**
     * Returns expected URL based on file type.
     *
     * @param expectedData
     *            expected nava data
     * @param f
     *            file
     * @return URL
     */
    private String getExpectedURL(NavigationData expectedData, File f) {
        String url;
        if (f.isFile()) {
            url = MODE.getUrl() + "/rest/files?path=" + expectedData.getPath() + Constants.SEP + f.getName();
        } else {
            url = MODE.getUrl() + "/?path=" + expectedData.getPath() + Constants.SEP + f.getName();
        }
        return url.replace(" ", "%20");
    }

    /**
     * Returns file name depending on file type
     *
     * @param f
     *            file
     * @return name
     */
    private String getFileName(File f) {
        if (f.isFile()) {
            return f.getName();
        }
        return f.getName() + Constants.SEP;
    }

    /**
     * Returns base URL.
     *
     * @return URL
     */
    private String getUrl() {
        List<NameValuePair> args = parseUrl();
        return args.stream().filter(i -> i.getValue() == null).map(NameValuePair::getName).findFirst().get() + "?path=";
    }

    /**
     * Returns path to file
     *
     * @return path
     */
    private String getPath() {
        List<NameValuePair> args = parseUrl();
        String path = args.stream().filter(i -> i.getName().equals("path")).map(NameValuePair::getValue).findFirst().get();
        return path;
    }

    /**
     * Parses URL
     *
     * @return name-value pairs
     */
    private List<NameValuePair> parseUrl() {
        List<NameValuePair> args = URLEncodedUtils.parse(driver.getCurrentUrl(), Charsets.US_ASCII, '?', '&');
        return args;
    }

    /**
     * Tests breadcrumbs.
     *
     * @param data
     *            nav data
     * @throws MultipleFailuresError
     *             the MultipleFailuresError
     */
    private void assertBreadcrumbs(List<NavigationData> data) throws MultipleFailuresError {
        List<WebElement> elements = getBreadcrumbs();
        String actualFolder = driver.findElement(By.id("folderTitle")).getText();
        WebElement lastNavData = elements.get(elements.size() - 1);

        Assertions.assertEquals(data.size(), elements.size());
        Assertions.assertEquals(lastNavData.getText() + " :", actualFolder);
        Assertions.assertTrue(lastNavData.getAttribute("id").equals("actualFolder"), "Last breadcrumb should be last");
        Assertions.assertAll(IntStream.range(0, elements.size()).boxed().map(i -> () -> {
            WebElement el = elements.get(i);
            NavigationData crumb = data.get(i);
            Assertions.assertTrue(el.getAttribute("class").contains("disabled") != crumb.isEnabled(),
                    String.format("Breadcrumb %s should %sbe clickable", crumb.getLabel(), crumb.isEnabled() ? "" : "not "));
            Assertions.assertEquals(crumb.getLabel(), el.getText(), "Labels shoud be the same");
            String attribute = el.findElement(By.tagName("a")).getAttribute("href");
            Assertions.assertEquals(getSlashUrl(crumb), attribute, "URLs shoud be the same");
        }));
    }

    /**
     * Gets URL with slash at end
     *
     * @param crumb
     *            crumb
     * @return URL
     */
    private String getSlashUrl(NavigationData crumb) {
        if (crumb.isEnabled()) {
            return crumb.getUrl() + Constants.SEP;
        }
        return null;
    }
}
