package lulewiczg.contentserver.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lulewiczg.contentserver.permissions.ResourceHelper;
import lulewiczg.contentserver.test.utils.TestUtil;

/**
 * Selenium tests for login.
 * 
 * @author lulewiczg
 */
public class LoginTestSelenium extends SeleniumTestTemplate {
    private static final String CONTEXT = TestUtil.LOC + "testContexts/";
    private static final String LOC = "src/test/resources/data/";

    @Test
    @DisplayName("Logs in with invalid credentials")
    public void testLoginInvalidCredentials() {
        ResourceHelper.init(CONTEXT + 1, LOC);
        clickLogin();
        setInputValue(LOGIN_ID, "abc");
        setInputValue(PASSWORD_ID, "bcd");
        clickButton(LOGIN_MODAL_BUTTON_ID);
        assertAlert("Nieprawidłowe dane");
        assertloginPopupPresent();
        assertToolbarNotLogged();
    }

    @Test
    @DisplayName("Logs in")
    public void testLogin() {
        ResourceHelper.init(CONTEXT + 4, LOC);
        clickLogin();
        setInputValue(LOGIN_ID, "test");
        setInputValue(PASSWORD_ID, "test");
        clickButton(LOGIN_MODAL_BUTTON_ID);
        assertloginPopupClosed();
        assertToolbarNotLogged();
    }
}
