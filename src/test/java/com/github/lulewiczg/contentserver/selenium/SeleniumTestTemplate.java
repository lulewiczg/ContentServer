package com.github.lulewiczg.contentserver.selenium;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.github.lulewiczg.contentserver.test.utils.TestMode;
import com.github.lulewiczg.contentserver.test.utils.TestUtil;

/**
 * Template for selenium tests.
 *
 * @author lulewiczg
 */
public abstract class SeleniumTestTemplate {

    private static final String LOGIN_BOX = "login-box";
    private static final String POPUP_TITLE = "popup-title";
    private static final String HREF = "href";
    protected static final String LOGIN_BUTTON_ID = "loginBtn";
    protected static final String LOGOUT_BUTTON_ID = "logoutBtn";
    protected static final String SETTINGS_BUTTON_ID = "settingsBtn";
    protected static final String LOGS_BUTTON_ID = "logsBtn";
    protected static final String WELCOME_LABEL_ID = "userLbl";
    protected static final String CLOSE_BTN_ID = "closeBtn";
    protected static final String SAVE_BTN_ID = "saveBtn";
    protected static final String SHORTCUTS_BUTTON_ID = "shortcutsBtn";
    protected static final String LOGO_ID = "logo";
    protected static final String LOGIN_MODAL_BUTTON_ID = "loginModalBtn";
    protected static final String LOGIN_ID = "login";
    protected static final String PASSWORD_ID = "password";
    protected static final String TEST3 = "test3";
    protected static final String ADMIN = "admin";
    protected static final String TEST = "test";
    protected WebDriver driver;
    protected ExpectedMsg msg;

    /**
     * Goes to main page.
     */
    @BeforeEach
    public void before() {
        Assumptions.assumeTrue(TestUtil.MODE.contains(TestMode.SELENIUM));
        msg = getMsgs();
        FirefoxOptions opt = new FirefoxOptions();
        FirefoxProfile prof = new FirefoxProfile();
        prof.setPreference("intl.accept_languages", msg.getLang());
        opt.setProfile(prof);
        driver = new FirefoxDriver(opt);
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(5, TimeUnit.SECONDS);

        driver.navigate().to(TestUtil.MODE.getLocation().getUrl());
    }

    /**
     * Declare expected messages and lang.
     */
    protected abstract ExpectedMsg getMsgs();

    /**
     * Closes session.
     */
    @AfterEach
    public void after() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Finds button.
     *
     * @param id
     *            button ID
     * @return button
     */
    protected WebElement findBtn(String id) {
        return driver.findElement(By.xpath(String.format("//*[@id='%s']", id)));
    }

    /**
     * Clicks login button and checks if popup appears.
     */
    protected void clickLogin() {
        clickButton(LOGIN_BUTTON_ID);
        assertloginPopup();
    }

    /**
     * Checks if login popup is opened.
     */
    protected void assertloginPopup() {
        WebElement loginBox = getElementIfPresent(driver, By.className(LOGIN_BOX));
        Assertions.assertNotNull(loginBox);
        WebElement title = loginBox.findElement(By.className(POPUP_TITLE));
        Assertions.assertNotNull(title);
        Assertions.assertEquals(msg.getLoginTitle(), title.getText());
        WebElement loginButton = loginBox.findElement(By.id(LOGIN_MODAL_BUTTON_ID));
        Assertions.assertNotNull(loginButton);
        Assertions.assertEquals(msg.getLoginButton(), loginButton.getText());
        WebElement closeButton = loginBox.findElement(By.id(CLOSE_BTN_ID));
        Assertions.assertNotNull(closeButton);
        Assertions.assertEquals(msg.getCloseButton(), closeButton.getText());
    }

    /**
     * Checks if settings popup is opened.
     */
    protected void assertSettingsPopup() {
        WebElement settingsBox = getElementIfPresent(driver, By.className("settings-box"));
        Assertions.assertNotNull(settingsBox);
        WebElement title = settingsBox.findElement(By.className(POPUP_TITLE));
        Assertions.assertNotNull(title);
        Assertions.assertEquals(msg.getSettingsTitle(), title.getText());
        WebElement saveButton = settingsBox.findElement(By.id(SAVE_BTN_ID));
        Assertions.assertNotNull(saveButton);
        Assertions.assertEquals(msg.getSave(), saveButton.getText());
        WebElement closeButton = settingsBox.findElement(By.id(CLOSE_BTN_ID));
        Assertions.assertNotNull(closeButton);
        Assertions.assertEquals(msg.getCloseButton(), closeButton.getText());
    }

    /**
     * Checks if alert with given message is present.
     *
     * @param msg
     *            message
     */
    protected void assertAlert(String msg) {
        Alert alert = driver.switchTo().alert();
        String alertMsg = alert.getText();
        Assertions.assertEquals(msg, alertMsg);
        alert.accept();
    }

    /**
     * Clicks button
     *
     * @param id
     *            button ID
     */
    protected void clickButton(String id) {
        WebElement btn = findBtn(id);
        Assertions.assertNotNull(btn);
        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOf(btn));
        btn.click();
    }

    /**
     * Sets input value
     *
     * @param id
     *            input ID
     * @param value
     *            value
     */
    protected void setInputValue(String id, String value) {
        WebElement input = driver.findElement(By.id(id));
        Assertions.assertNotNull(input);
        input.clear();
        input.sendKeys(value);
    }

    /**
     * Gets input value.
     *
     * @param id
     *            input ID
     */
    protected String getInputValue(String id) {
        WebElement input = driver.findElement(By.id(id));
        Assertions.assertNotNull(input);
        return input.getAttribute("value");
    }

    /**
     * Tests if all required items are present in toolbar and in proper order.
     */
    private void assertToolbarsItems() {
        List<WebElement> toolbarElements = driver.findElements(By.className("toolbar-item"));
        Assertions.assertEquals(7, toolbarElements.size());

        WebElement logo = driver.findElement(By.id(LOGO_ID));
        Assertions.assertNotNull(logo);
        Assertions.assertEquals(msg.getAppTitle(), getText(driver, logo));
        Assertions.assertEquals(toolbarElements.get(0), logo);

        WebElement shortcuts = driver.findElement(By.id(SHORTCUTS_BUTTON_ID));
        Assertions.assertNotNull(shortcuts);
        Assertions.assertEquals(msg.getShortcuts(), getText(driver, shortcuts));
        Assertions.assertNotEquals(0, shortcuts.findElements(By.className("shortcut-dropdown")));
        Assertions.assertEquals(toolbarElements.get(1), shortcuts);

        WebElement login = driver.findElement(By.id(LOGIN_BUTTON_ID));
        Assertions.assertNotNull(login);
        Assertions.assertEquals(msg.getLogin(), getText(driver, login));
        Assertions.assertEquals(toolbarElements.get(2), login);

        WebElement label = driver.findElement(By.id(WELCOME_LABEL_ID));
        Assertions.assertNotNull(label);
        Assertions.assertEquals(toolbarElements.get(3), label);

        WebElement logs = driver.findElement(By.id(LOGS_BUTTON_ID));
        Assertions.assertNotNull(logs);
        Assertions.assertEquals(msg.getLogs(), getText(driver, logs));
        Assertions.assertEquals(toolbarElements.get(4), logs);

        WebElement settings = driver.findElement(By.id(SETTINGS_BUTTON_ID));
        Assertions.assertNotNull(settings);
        Assertions.assertEquals(msg.getSettings(), getText(driver, settings));
        Assertions.assertEquals(toolbarElements.get(5), settings);

        WebElement logout = driver.findElement(By.id(LOGOUT_BUTTON_ID));
        Assertions.assertNotNull(logout);
        Assertions.assertEquals(msg.getLogout(), getText(driver, logout));
        Assertions.assertEquals(toolbarElements.get(6), logout);
    }

    /**
     * Tests if proper data is displayed on toolbar when not logged.
     */
    protected void assertToolbarNotLogged() {
        assertToolbarsItems();
        Assertions.assertTrue(driver.findElement(By.id(LOGIN_BUTTON_ID)).isDisplayed());
        Assertions.assertFalse(driver.findElement(By.id(WELCOME_LABEL_ID)).isDisplayed());
        Assertions.assertFalse(driver.findElement(By.id(SETTINGS_BUTTON_ID)).isDisplayed());
        Assertions.assertFalse(driver.findElement(By.id(LOGS_BUTTON_ID)).isDisplayed());
        Assertions.assertFalse(driver.findElement(By.id(LOGOUT_BUTTON_ID)).isDisplayed());
    }

    /**
     * Tests if proper data is displayed on toolbar when logged.
     */
    protected void assertToolbarLogged(String name) {
        assertToolbarsItems();
        Assertions.assertFalse(driver.findElement(By.id(LOGIN_BUTTON_ID)).isDisplayed());
        WebElement label = driver.findElement(By.id(WELCOME_LABEL_ID));
        Assertions.assertTrue(label.isDisplayed());
        Assertions.assertEquals(String.format(msg.getWelcome(), name), label.getText());
        Assertions.assertFalse(driver.findElement(By.id(SETTINGS_BUTTON_ID)).isDisplayed());
        Assertions.assertFalse(driver.findElement(By.id(LOGS_BUTTON_ID)).isDisplayed());
        Assertions.assertTrue(driver.findElement(By.id(LOGOUT_BUTTON_ID)).isDisplayed());
    }

    /**
     * Tests if proper data is displayed on toolbar when admin logged.
     */
    protected void assertToolbarAdminLogged() {
        assertToolbarsItems();
        Assertions.assertFalse(driver.findElement(By.id(LOGIN_BUTTON_ID)).isDisplayed());
        WebElement label = driver.findElement(By.id(WELCOME_LABEL_ID));
        Assertions.assertTrue(label.isDisplayed());
        Assertions.assertEquals(String.format(msg.getWelcome(), ADMIN), label.getText());
        Assertions.assertTrue(driver.findElement(By.id(SETTINGS_BUTTON_ID)).isDisplayed());
        Assertions.assertTrue(driver.findElement(By.id(LOGS_BUTTON_ID)).isDisplayed());
        Assertions.assertTrue(driver.findElement(By.id(LOGOUT_BUTTON_ID)).isDisplayed());
    }

    /**
     * Gets text from element
     *
     * @param driver
     *            driver
     * @param element
     *            element
     * @return text
     */
    protected String getText(WebDriver driver, WebElement element) {
        WebElement a = getElementIfPresent(element, By.tagName("a"));
        if (a != null) {
            element = a;
        }
        return ((JavascriptExecutor) driver).executeScript("return jQuery(arguments[0]).text();", element).toString().trim();
    }

    /**
     * Finds element if present.
     *
     * @param el
     *            search context
     * @param by
     *            by
     * @return element if present
     */
    protected WebElement getElementIfPresent(SearchContext el, By by) {
        try {
            return el.findElement(by);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /**
     * Logs outs
     */
    protected void logout() {
        clickButton(LOGOUT_BUTTON_ID);
        // WTF Selenium??????
        if (driver.findElement(By.id(LOGOUT_BUTTON_ID)).isDisplayed()) {
            clickButton(LOGOUT_BUTTON_ID);
        }
    }

    /**
     * Logs in.
     *
     * @param name
     *            name
     * @param password
     *            password
     */
    protected void login(String name, String password) {
        loginInternal(name, password);
        new WebDriverWait(driver, 5)
                .until(ExpectedConditions.not(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(LOGIN_MODAL_BUTTON_ID))));
        WebElement loginBox = getElementIfPresent(driver, By.className(LOGIN_BOX));
        Assertions.assertNull(loginBox);
    }

    /**
     * Opens settings box.
     */
    protected void openSettings() {
        clickButton(SETTINGS_BUTTON_ID);
        assertSettingsPopup();
        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(SAVE_BTN_ID)));

    }

    /**
     * Logs in with invalid credentials.
     *
     * @param name
     *            name
     * @param password
     *            password
     */
    protected void invalidLogin(String name, String password) {
        loginInternal(name, password);
        assertAlert(msg.getInvalidCredentialsError());
        WebElement loginBox = getElementIfPresent(driver, By.className(LOGIN_BOX));
        Assertions.assertNotNull(loginBox);
    }

    /**
     * Log in internal logic.
     *
     * @param name
     *            name
     * @param password
     *            password
     */
    private void loginInternal(String name, String password) {
        clickLogin();
        setInputValue(LOGIN_ID, name);
        setInputValue(PASSWORD_ID, password);
        clickButton(LOGIN_MODAL_BUTTON_ID);
    }

    /**
     * Closes popup.
     */
    protected void closePopup() {
        WebElement btn = driver.findElement(By.id(CLOSE_BTN_ID));
        Assertions.assertNotNull(btn);
        btn.click();
    }

    /**
     * Saves settings
     */
    protected void saveSettings() {
        WebElement btn = driver.findElement(By.id(SAVE_BTN_ID));
        Assertions.assertNotNull(btn);
        btn.click();
    }

    /**
     * Opens logs
     */
    protected void openLogs() {
        String handle = driver.getWindowHandle();
        clickButton(LOGS_BUTTON_ID);
        Set<String> handles = driver.getWindowHandles();
        handles.remove(handle);
        Assertions.assertEquals(1, handles.size());
        driver = driver.switchTo().window(new ArrayList<>(handles).get(0));
    }

    /**
     * Obtains shortcuts URLs
     *
     * @return shortcuts
     */
    protected List<String> getShortcuts() {
        driver.findElement(By.id(SHORTCUTS_BUTTON_ID)).click();
        List<WebElement> shortcuts = driver.findElements(By.xpath("//*[contains(@class,'shortcut-dropdown')]/a"));
        List<String> links = shortcuts.stream().map(i -> i.getAttribute(HREF)).collect(Collectors.toList());
        return links;
    }

    /**
     * Goes to shortcut.
     *
     * @param index
     * @return shortcuts
     */
    protected void gotoShortcut(int index) {
        driver.findElement(By.id(SHORTCUTS_BUTTON_ID)).click();
        List<WebElement> shortcuts = driver.findElements(By.xpath("//*[contains(@class,'shortcut-dropdown')]/a"));
        shortcuts.get(index).click();
    }

    /**
     * Clicks at table item
     *
     * @param index
     *            index
     */
    protected void clickTableItem(int index) {
        WebElement el = driver
                .findElement(By.xpath(String.format("(//table[contains(@class,'content-table')]//tr)[%s]/td[1]/a", index + 1)));
        Assertions.assertNotNull(el, "Table item not found");
        el.click();
    }

    /**
     * Gets download link
     *
     * @param index
     *            index
     * @return link
     */
    protected String getDownloadLink(int index) {
        WebElement el = driver
                .findElement(By.xpath(String.format("(//table[contains(@class,'content-table')]//tr)[%s]/td[1]/a", index + 1)));
        Assertions.assertNotNull(el, "Table item not found");
        return el.getAttribute(HREF);
    }

    /**
     * Obtains breadcrumbs.
     *
     * @return breadcrumbs
     */
    protected List<WebElement> getBreadcrumbs() {
        return driver.findElements(By.className("path-breadcrumb"));
    }

    /**
     * Goes 1 directory up if possible.
     */
    protected void goUp() {
        List<WebElement> breadcrumbs = getBreadcrumbs();
        breadcrumbs.get(breadcrumbs.size() - 2).click();
    }
}
