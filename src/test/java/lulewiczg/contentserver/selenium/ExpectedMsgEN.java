package lulewiczg.contentserver.selenium;

/**
 * EN messages for tests.
 * 
 * @author lulewiczg
 */
public class ExpectedMsgEN extends ExpectedMsg {

    public ExpectedMsgEN() {
        super("en");
        this.fileName = "File Name";
        this.fileSize = "Size";
        this.options = "Options";
        this.invalidCredentialsError = "User or password is invalid!";
        this.appTitle = "Content Server";
        this.shortcuts = "Shortcuts";
        this.login = "Login";
        this.logout = "Logout";
        this.welcome = "Hello, %s";
        this.logs = "Logs";
        this.settings = "Settings";
        this.settingsNotSavedError = "Failed to save settings!";
        this.settingsSaved = "Settings saved!";
    }
}
