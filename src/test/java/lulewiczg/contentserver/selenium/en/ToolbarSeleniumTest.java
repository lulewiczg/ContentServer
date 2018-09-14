package lulewiczg.contentserver.selenium.en;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import lulewiczg.contentserver.selenium.ExpectedMsg;
import lulewiczg.contentserver.selenium.ExpectedMsgEN;
import lulewiczg.contentserver.selenium.SeleniumTestTemplate;

/**
 * Selenium tests for plain toolbar actions.
 * 
 * @author lulewiczg
 */
public class ToolbarSeleniumTest extends SeleniumTestTemplate {

    @Override
    protected ExpectedMsg getMsgs() {
        return new ExpectedMsgEN();
    }

    @Test
    @DisplayName("Logs in with invalid credentials")
    public void testLoginInvalidCredentials() {
        login("abc", "bcd");
        assertAlert(msg.getInvalidCredentialsError());
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

    @Test
    @DisplayName("Checks shortcuts menu as admin")
    public void testShortcutsAdmin() {
        login(ADMIN, TEST3);
        List<String> shortcuts = getShortcuts();
        List<String> expected = List.of("/data/structure");
        Assertions.assertAll(IntStream.range(0, expected.size()).boxed().map(i -> () -> Assertions
                .assertTrue(shortcuts.get(i).endsWith(expected.get(i)), "Shortcut path is invalid")));
    }

    @Test
    @DisplayName("Checks shortcuts menu as user")
    public void testShortcuts() {
        login(TEST, TEST);
        List<String> shortcuts = getShortcuts();
        List<String> expected = List.of("/data/structure/folder1", "/data/structure/folder2/folder2");
        Assertions.assertAll(IntStream.range(0, expected.size()).boxed().map(i -> () -> Assertions
                .assertTrue(shortcuts.get(i).endsWith(expected.get(i)), "Shortcut path is invalid")));
    }

    @Test
    @DisplayName("Checks shortcuts menu as guest")
    public void testShortcutsGuest() {
        List<String> shortcuts = getShortcuts();
        List<String> expected = List.of("/data/structure/folder1/folder1", "/data/structure/folder2/folder2");
        Assertions.assertAll(IntStream.range(0, expected.size()).boxed().map(i -> () -> Assertions
                .assertTrue(shortcuts.get(i).endsWith(expected.get(i)), "Shortcut path is invalid")));
    }

}
