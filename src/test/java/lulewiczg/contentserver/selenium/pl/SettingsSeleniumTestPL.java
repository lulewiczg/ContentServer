package lulewiczg.contentserver.selenium.pl;

import lulewiczg.contentserver.selenium.ExpectedMsg;
import lulewiczg.contentserver.selenium.ExpectedMsgPL;
import lulewiczg.contentserver.selenium.en.SettingsSeleniumTest;

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
