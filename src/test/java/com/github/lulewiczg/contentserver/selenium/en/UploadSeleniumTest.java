package com.github.lulewiczg.contentserver.selenium.en;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.github.lulewiczg.contentserver.selenium.ExpectedMsg;
import com.github.lulewiczg.contentserver.selenium.ExpectedMsgEN;
import com.github.lulewiczg.contentserver.selenium.SeleniumTestTemplate;
import com.github.lulewiczg.contentserver.test.utils.TestUtil;
import com.github.lulewiczg.contentserver.utils.CommonUtil;
import com.github.lulewiczg.contentserver.utils.Constants;

/**
 * Tests uploading files.
 * 
 * @author lulewiczg
 */
public class UploadSeleniumTest extends SeleniumTestTemplate {

    private List<String> paths = new ArrayList<>();
    private static String base;

    /**
     * Deletes uploaded files.
     */
    @AfterEach
    public void cleanUp() {
        paths.forEach(i -> new File(i).delete());
    }

    /**
     * Sets up data.
     * 
     * @throws IOException
     *             the IOException
     */
    @BeforeAll
    public static void setUp() throws IOException {
        base = new File(".").getCanonicalPath();
    }

    @Override
    protected ExpectedMsg getMsgs() {
        return new ExpectedMsgEN();
    }

    @Test
    @DisplayName("Tries to upload nothing")
    public void uploadNothing() {
        login(TEST, TEST);
        gotoShortcut(2);
        openUploadWindow();
        WebElement button = driver.findElement(By.id((UPLOAD_MODAL_BUTTON_ID)));
        Assertions.assertFalse(button.isEnabled());
        button.click();

        assertUploadPopup();
        WebElement uploadBox = getElementIfPresent(driver, By.className(UPLOAD_BOX));
        Assertions.assertNotNull(uploadBox);
    }

    @Test
    @DisplayName("Tries to upload 1 file")
    public void uploadUploadFile() throws IOException, InterruptedException {
        login(TEST, TEST);
        gotoShortcut(2);
        clickTableItem(1);
        openUploadWindow();
        WebElement button = driver.findElement(By.id((UPLOAD_MODAL_BUTTON_ID)));
        Assertions.assertFalse(button.isEnabled());
        setInputValue(UPLOAD_ID, CommonUtil.normalizePath((base + Constants.SEP + TestUtil.LOC + "upload/testFile.txt"))
                .replace(Constants.SEP, "\\"));
        new WebDriverWait(driver, 1).until(ExpectedConditions.elementToBeClickable(button));
        Assertions.assertTrue(button.isEnabled());
        paths.add(CommonUtil.normalizePath(getPath() + "/testFile.txt"));
        button.click();
        assertAlert(msg.getUploadSuccess());
        Thread.sleep(1000);
        clickTableItem(1);
        String text = driver.findElement(By.tagName("body")).getText();
        Assertions.assertEquals("test123456789", text);
    }

    @Test
    @DisplayName("Tries to upload multiple files")
    public void uploadUploadMultipleFiles() throws IOException, InterruptedException {
        login(TEST, TEST);
        gotoShortcut(2);
        clickTableItem(1);
        openUploadWindow();
        WebElement button = driver.findElement(By.id((UPLOAD_MODAL_BUTTON_ID)));
        Assertions.assertFalse(button.isEnabled());
        WebElement input = driver.findElement(By.id(UPLOAD_ID));
        input.sendKeys(CommonUtil.normalizePath((base + Constants.SEP + TestUtil.LOC + "upload/testFile.txt"))
                .replace(Constants.SEP, "\\"));
        input.sendKeys(CommonUtil.normalizePath((base + Constants.SEP + TestUtil.LOC + "upload/testFile2.txt"))
                .replace(Constants.SEP, "\\"));
        new WebDriverWait(driver, 1).until(ExpectedConditions.elementToBeClickable(button));
        Assertions.assertTrue(button.isEnabled());
        paths.add(CommonUtil.normalizePath(getPath() + "/testFile.txt"));
        paths.add(CommonUtil.normalizePath(getPath() + "/testFile2.txt"));
        button.click();
        assertAlert(msg.getUploadSuccess());

        Thread.sleep(1000);
        String url = getUrl() + getPath();
        clickTableItem(1);
        String text = driver.findElement(By.tagName("body")).getText();
        Assertions.assertEquals("qwerty", text);

        driver.get(url);
        clickTableItem(2);
        String text2 = driver.findElement(By.tagName("body")).getText();
        Assertions.assertEquals("test123456789", text2);
    }

    @Test
    @DisplayName("Tries to upload the same file")
    public void uploadTheSameFile() throws IOException, InterruptedException {
        login(TEST, TEST);
        gotoShortcut(2);
        openUploadWindow();
        WebElement button = driver.findElement(By.id((UPLOAD_MODAL_BUTTON_ID)));
        Assertions.assertFalse(button.isEnabled());
        setInputValue(UPLOAD_ID, CommonUtil.normalizePath((base + Constants.SEP + TestUtil.LOC + "upload/testFile.txt"))
                .replace(Constants.SEP, "\\"));
        new WebDriverWait(driver, 1).until(ExpectedConditions.elementToBeClickable(button));
        Assertions.assertTrue(button.isEnabled());
        button.click();
        assertAlert(msg.getUploadError());
        WebElement uploadBox = getElementIfPresent(driver, By.className(UPLOAD_BOX));
        Assertions.assertNotNull(uploadBox);
    }
}
