package com.github.lulewiczg.contentserver.selenium.pl;

import com.github.lulewiczg.contentserver.selenium.ExpectedMsg;
import com.github.lulewiczg.contentserver.selenium.ExpectedMsgPL;
import com.github.lulewiczg.contentserver.selenium.en.UploadSeleniumTest;

/**
 * Selenium tests for fiel upload.
 * 
 * @author lulewiczg
 */
public class UploadSeleniumTestPL extends UploadSeleniumTest {

    @Override
    protected ExpectedMsg getMsgs() {
        return new ExpectedMsgPL();
    }

}
