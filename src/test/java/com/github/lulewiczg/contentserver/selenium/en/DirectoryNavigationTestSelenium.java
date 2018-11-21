package com.github.lulewiczg.contentserver.selenium.en;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
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
import com.github.lulewiczg.contentserver.test.utils.SeleniumLocation;
import com.github.lulewiczg.contentserver.test.utils.TestUtil;
import com.github.lulewiczg.contentserver.utils.Constants;
import com.github.lulewiczg.contentserver.utils.comparators.PathComparator;
import com.github.lulewiczg.contentserver.utils.models.Dir;

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
        Assumptions.assumeTrue(TestUtil.MODE.getLocation() == SeleniumLocation.LOCAL);
        gotoShortcut(0);
        testNav((i, j) -> false, false, false);
        gotoShortcut(1);
        testNav((i, j) -> false, false, false);
    }

    @Test
    @DisplayName("Navigation is not clickable when not permitted")
    public void testNav() {
        Assumptions.assumeTrue(TestUtil.MODE.getLocation() == SeleniumLocation.LOCAL);
        login(TEST, TEST);
        gotoShortcut(0);
        testNav((i, len) -> false, false, false);
        clickTableItem(1);
        testNav((i, len) -> i == len - 2, false, false);
        goUp();
        clickTableItem(1);
        testNav((i, len) -> i == len - 2, false, false);

        gotoShortcut(1);
        testNav((i, len) -> false, false, false);

        gotoShortcut(2);
        testNav((i, len) -> false, true, false);
        clickTableItem(1);
        testNav((i, len) -> i == len - 2, true, true);
    }

    @Test
    @DisplayName("Every dir is accessible by admin")
    public void testNavAdmin() {
        Assumptions.assumeTrue(TestUtil.MODE.getLocation() == SeleniumLocation.LOCAL);
        login(ADMIN, TEST3);
        while (getBreadcrumbs().size() > 2) {
            testNav((i, len) -> i != len - 1, true, true);
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
    @DisplayName("Opens file with different special chars in name")
    public void testOpenFileSpecialChars() {
        login(ADMIN, TEST3);
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
        HttpClient client = HttpClientBuilder.create().build();

        HttpPost authRequest = new HttpPost(TestUtil.MODE.getUrl() + "/rest/login");
        authRequest.setEntity(new UrlEncodedFormEntity(List.of(new BasicNameValuePair(Constants.Web.LOGIN, TEST),
                new BasicNameValuePair(Constants.Web.PASSWORD, TEST))));
        HttpResponse authResponse = client.execute(authRequest);
        Assertions.assertEquals(200, authResponse.getStatusLine().getStatusCode());

        HttpGet request = new HttpGet(downloadLink);
        HttpResponse response = client.execute(request);
        Assertions.assertEquals(HttpStatus.SC_PARTIAL_CONTENT, response.getStatusLine().getStatusCode());
        Assertions.assertEquals(Constants.Setting.PLAIN_TEXT,
                response.getFirstHeader("Content-Type").getValue().split("\\;")[0]);
        Assertions.assertEquals("4", response.getFirstHeader("Content-Length").getValue());

    }

    /**
     * Tests navigation
     *
     * @param f
     *            function to set enabled setting
     * @param uploadAllowed
     *            upload allowed
     * @param deleteAllowed
     *            delete allowed
     * @throws MultipleFailuresError
     *             the MultipleFailuresError
     */
    private void testNav(BiFunction<Integer, Integer, Boolean> f, boolean uploadAllowed, boolean deleteAllowed)
            throws MultipleFailuresError {
        String baseUrl = getUrl();
        String path = getPath();
        List<NavigationData> navData = buildNavData(baseUrl, path, f);
        NavigationData actualData = navData.get(navData.size() - 1);
        assertBreadcrumbs(navData, uploadAllowed);
        assertListedDirs(path, actualData, deleteAllowed);
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
     * @param delete
     *            delete allowed
     * @throws MultipleFailuresError
     *             the MultipleFailuresError
     */
    private void assertListedDirs(String path, NavigationData expectedData, boolean delete)
            throws MultipleFailuresError {
        WebElement table = driver.findElement(By.xpath("//table[contains(@class,'content-table')]"));
        List<String> headers = table.findElements(By.tagName("th")).stream().map(WebElement::getText)
                .collect(Collectors.toList());
        List<String> expectedHeaders = List.of(msg.getFileName(), msg.getModificationDate(), msg.getFileSize(),
                msg.getOptions());
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

            WebElement modCol = row.findElement(By.xpath("td[2]"));
            String expectedDate = new Dir(null, f.length(), null, f.lastModified(), f.isFile()).getDate();
            Assertions.assertEquals(expectedDate, modCol.getText());

            WebElement sizeCol = row.findElement(By.xpath("td[3]"));
            String expectedSize = new Dir(null, f.length(), null, f.lastModified(), f.isFile()).getSize();
            Assertions.assertEquals(expectedSize, sizeCol.getText());

            WebElement optionsCol = row.findElement(By.xpath("td[4]"));
            String expectedDownloadUrl = expectedUrl + "&download=true";
            WebElement downloadButton = optionsCol.findElement(By.id(DOWNLOAD_BTN_ID));
            Assertions.assertEquals(expectedDownloadUrl, downloadButton.getAttribute("href"));
            String imgSrc = downloadButton.findElement(By.tagName("img")).getAttribute("src");
            Assertions.assertEquals(TestUtil.MODE.getUrl() + "/icons/download.png", imgSrc);

            WebElement deleteButton = optionsCol.findElement(By.id(DELETE_BTN_ID));
            Assertions.assertNotNull(deleteButton);
            if (delete && f.isFile()) {
                Assertions.assertTrue(deleteButton.isDisplayed());
                String delImgSrc = deleteButton.findElement(By.tagName("img")).getAttribute("src");
                Assertions.assertEquals(TestUtil.MODE.getUrl() + "/icons/delete.png", delImgSrc);
            } else {
                Assertions.assertFalse(deleteButton.isDisplayed());
            }
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
        List<Dir> tmpList = tmpMap.entrySet().stream()
                .map(i -> new Dir(getFileName(i.getValue()), 0, "", i.getValue().lastModified(), i.getValue().isFile()))
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
            url = TestUtil.MODE.getUrl() + "/rest/files?path=" + expectedData.getPath() + Constants.SEP + f.getName();
        } else {
            url = TestUtil.MODE.getUrl() + "/?path=" + expectedData.getPath() + Constants.SEP + f.getName();
        }
        return url.replace("%", "%25").replace("&", "%26").replace("'", "%27").replace(" ", "%20");
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
     * Tests breadcrumbs.
     *
     * @param data
     *            nav data
     * @throws MultipleFailuresError
     *             the MultipleFailuresError
     */
    private void assertBreadcrumbs(List<NavigationData> data, boolean uploadAllowed) throws MultipleFailuresError {
        List<WebElement> elements = getBreadcrumbs();
        Assertions.assertEquals(uploadAllowed, driver.findElement(By.id(UPLOAD_BUTTON_ID)).isDisplayed(),
                "Upload button is not valid");
        String actualFolder = driver.findElement(By.id("folderTitle")).getText();
        WebElement lastNavData = elements.get(elements.size() - 1);

        Assertions.assertEquals(data.size(), elements.size());
        Assertions.assertEquals(lastNavData.getText() + " :", actualFolder);
        Assertions.assertTrue(lastNavData.getAttribute("id").equals("actualFolder"), "Last breadcrumb should be last");
        Assertions.assertAll(IntStream.range(0, elements.size()).boxed().map(i -> () -> {
            WebElement el = elements.get(i);
            NavigationData crumb = data.get(i);
            Assertions.assertTrue(el.getAttribute("class").contains("disabled") != crumb.isEnabled(), String
                    .format("Breadcrumb %s should %sbe clickable", crumb.getLabel(), crumb.isEnabled() ? "" : "not "));
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
