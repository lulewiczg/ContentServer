package com.github.lulewiczg.contentserver.selenium.pl;

import com.github.lulewiczg.contentserver.selenium.ExpectedMsg;
import com.github.lulewiczg.contentserver.selenium.ExpectedMsgPL;
import com.github.lulewiczg.contentserver.selenium.en.SettingsSeleniumTest;

/**
 * Tests settings popup.
 * 
 * @author lulewiczg
 */
public class SettingsSeleniumTestPL extends SettingsSeleniumTest {

    @Override
    protected ExpectedMsg getMsgs() {
        return new ExpectedMsgPL();
    }
}
