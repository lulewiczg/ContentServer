package lulewiczg.contentserver.selenium;

/**
 * Class to hold expected messages for selenium tests.
 * 
 * @author lulewiczg
 *
 */
public abstract class ExpectedMsg {

    protected String fileName;
    protected String fileSize;
    protected String options;
    protected String invalidCredentialsError;
    protected String lang;
    protected String appTitle;
    protected String shortcuts;
    protected String login;
    protected String logout;
    protected String welcome;
    protected String logs;
    protected String settings;

    public String getLang() {
        return lang;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public String getOptions() {
        return options;
    }

    public String getInvalidCredentialsError() {
        return invalidCredentialsError;
    }

    public String getAppTitle() {
        return appTitle;
    }

    public String getShortcuts() {
        return shortcuts;
    }

    public String getLogin() {
        return login;
    }

    public String getLogout() {
        return logout;
    }

    public String getWelcome() {
        return welcome;
    }

    public String getLogs() {
        return logs;
    }

    public String getSettings() {
        return settings;
    }

    public ExpectedMsg(String lang) {
        this.lang = lang;
    }
}
