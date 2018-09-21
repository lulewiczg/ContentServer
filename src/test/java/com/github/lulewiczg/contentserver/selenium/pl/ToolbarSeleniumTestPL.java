package com.github.lulewiczg.contentserver.selenium.pl;

import com.github.lulewiczg.contentserver.selenium.ExpectedMsg;
import com.github.lulewiczg.contentserver.selenium.ExpectedMsgPL;
import com.github.lulewiczg.contentserver.selenium.en.ToolbarSeleniumTest;

/**
 * Selenium tests for plain toolbar actions.
 * 
 * @author lulewiczg
 */
public class ToolbarSeleniumTestPL extends ToolbarSeleniumTest {

    @Override
    protected ExpectedMsg getMsgs() {
        return new ExpectedMsgPL();
    }

}
