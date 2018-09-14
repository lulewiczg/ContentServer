package lulewiczg.contentserver.selenium.pl;

import lulewiczg.contentserver.selenium.ExpectedMsg;
import lulewiczg.contentserver.selenium.ExpectedMsgPL;
import lulewiczg.contentserver.selenium.en.ToolbarSeleniumTest;

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
