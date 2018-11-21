package com.github.lulewiczg.contentserver.selenium.pl;

import com.github.lulewiczg.contentserver.selenium.ExpectedMsg;
import com.github.lulewiczg.contentserver.selenium.ExpectedMsgPL;
import com.github.lulewiczg.contentserver.selenium.en.DeleteSeleniumTest;

/**
 * Selenium tests for file delete.
 * 
 * @author lulewiczg
 */
public class DeleteSeleniumTestPL extends DeleteSeleniumTest {

    @Override
    protected ExpectedMsg getMsgs() {
        return new ExpectedMsgPL();
    }

}
