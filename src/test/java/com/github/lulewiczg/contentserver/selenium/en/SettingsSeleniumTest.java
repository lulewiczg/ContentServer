package com.github.lulewiczg.contentserver.selenium.en;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.lulewiczg.contentserver.selenium.ExpectedMsg;
import com.github.lulewiczg.contentserver.selenium.ExpectedMsgEN;
import com.github.lulewiczg.contentserver.selenium.SeleniumTestTemplate;
import com.github.lulewiczg.contentserver.utils.Constants;

/**
 * Tests settings popup.
 * 
 * @author lulewiczg
 */
public class SettingsSeleniumTest extends SeleniumTestTemplate {

    private static final String BUFF_SET = "setting-" + Constants.Setting.BUFFER_SIZE;

    @Override
    protected ExpectedMsg getMsgs() {
        return new ExpectedMsgEN();
    }

    @Test
    @DisplayName("Save invalid settings")
    public void testSaveInvalidSettings() {
        login(ADMIN, TEST3);
        clickButton(SETTINGS_BUTTON_ID);
        assertSettingsPopupPresent();
        String oldVal = getInputValue(BUFF_SET);
        setInputValue(BUFF_SET, TEST);
        saveSettings();
        assertAlert(msg.getSettingsNotSavedError());
        clickButton(CLOSE_BTN_ID);
        clickButton(SETTINGS_BUTTON_ID);
        Assertions.assertEquals(oldVal, getInputValue(BUFF_SET), "Value should not be changed");
    }

    @Test
    @DisplayName("Save setting")
    public void test() {
        login(ADMIN, TEST3);
        clickButton(SETTINGS_BUTTON_ID);
        assertSettingsPopupPresent();
        String oldVal = getInputValue(BUFF_SET);
        String newVal = oldVal.equals("10240") ? "1024" : "10240";
        setInputValue(BUFF_SET, newVal);
        saveSettings();
        assertAlert(msg.getSettingsSaved());
        driver.navigate().refresh();
        clickButton(SETTINGS_BUTTON_ID);
        Assertions.assertEquals(newVal, getInputValue(BUFF_SET), "Value should be changed");
    }
}
