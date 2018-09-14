package lulewiczg.contentserver.selenium.pl;

import lulewiczg.contentserver.selenium.ExpectedMsg;
import lulewiczg.contentserver.selenium.ExpectedMsgPL;
import lulewiczg.contentserver.selenium.en.DirectoryNavigationTestSelenium;

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
