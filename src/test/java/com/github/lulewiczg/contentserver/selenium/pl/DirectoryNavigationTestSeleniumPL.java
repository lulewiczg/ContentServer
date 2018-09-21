package com.github.lulewiczg.contentserver.selenium.pl;

import com.github.lulewiczg.contentserver.selenium.ExpectedMsg;
import com.github.lulewiczg.contentserver.selenium.ExpectedMsgPL;
import com.github.lulewiczg.contentserver.selenium.en.DirectoryNavigationTestSelenium;

/**
 * Selenium tests for navigation through directories.
 * 
 * @author lulewiczg
 */
public class DirectoryNavigationTestSeleniumPL extends DirectoryNavigationTestSelenium {

    @Override
    protected ExpectedMsg getMsgs() {
        return new ExpectedMsgPL();
    }
}
