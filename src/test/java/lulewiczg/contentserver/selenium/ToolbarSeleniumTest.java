package lulewiczg.contentserver.selenium;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

/**
 * Selenium tests for login.
 * 
 * @author lulewiczg
 */
public class ToolbarSeleniumTest extends SeleniumTestTemplate {

    @Test
    @DisplayName("Logs in with invalid credentials")
    public void testLoginInvalidCredentials() {
        login("abc", "bcd");
        assertAlert("Nieprawidłowe dane");
        assertloginPopupPresent();
        assertToolbarNotLogged();
    }

    @Test
    @DisplayName("Logs in")
    public void testLogin() {
        login(TEST, TEST);
        assertloginPopupClosed();
        assertToolbarLogged(TEST);
        driver.navigate().refresh();
        assertToolbarLogged(TEST);
    }

    @Test
    @DisplayName("Logs in as admin")
    public void testLoginAdmin() {
        login(ADMIN, TEST3);
        assertloginPopupClosed();
        assertToolbarAdminLogged();
        driver.navigate().refresh();
        assertToolbarAdminLogged();
    }

    @Test
    @DisplayName("Logs out")
    public void testLogout() {
        login(TEST, TEST);
        assertloginPopupClosed();
        logout();
        assertToolbarNotLogged();
        driver.navigate().refresh();
        assertToolbarNotLogged();
    }

    @Test
    @DisplayName("Logs out as admin")
    public void testAdminLogout() {
        login(ADMIN, TEST3);
        assertloginPopupClosed();
        logout();
        assertToolbarNotLogged();
        driver.navigate().refresh();
        assertToolbarNotLogged();
    }

    @Test
    @DisplayName("Relogin")
    public void testRelogin() {
        login(ADMIN, TEST3);
        assertloginPopupClosed();
        assertToolbarAdminLogged();
        logout();
        login(TEST, TEST);
        assertloginPopupClosed();
        assertToolbarLogged(TEST);
        driver.navigate().refresh();
        assertToolbarLogged(TEST);
    }

    @Test
    @DisplayName("Opens logs")
    public void testOpenLogs() {
        login(ADMIN, TEST3);
        openLogs();
        String logs = driver.findElement(By.tagName("body")).getText();
        Assertions.assertTrue(logs.contains("Config loaded!"));
        driver.navigate().refresh();
        String logs2 = driver.findElement(By.tagName("body")).getText();
        Assertions.assertTrue(logs2.length() > logs.length());
    }
}
