package com.github.lulewiczg.contentserver.selenium;

/**
 * Class to hold expected messages for selenium tests.
 * 
 * @author lulewiczg
 *
 */
public abstract class ExpectedMsg {

    protected String fileName;
    protected String modificationDate;
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
    protected String settingsNotSavedError;
    protected String settingsSaved;
    protected String loginTitle;
    protected String loginButton;
    protected String closeButton;
    protected String settingsTitle;
    protected String save;
    protected String uploadTitle;
    protected String uploadLabel;
    protected String upload;
    protected String uploadSuccess;
    protected String uploadError;
    protected String deleteConfirm;
    protected String deleteSuccess;
    protected String deleteFailed;

    public String getSettingsTitle() {
        return settingsTitle;
    }

    public String getSave() {
        return save;
    }

    public String getCloseButton() {
        return closeButton;
    }

    public String getLoginButton() {
        return loginButton;
    }

    public String getLoginTitle() {
        return loginTitle;
    }

    public String getSettingsNotSavedError() {
        return settingsNotSavedError;
    }

    public String getSettingsSaved() {
        return settingsSaved;
    }

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

    public String getModificationDate() {
        return modificationDate;
    }

    public String getUploadTitle() {
        return uploadTitle;
    }

    public String getUploadLabel() {
        return uploadLabel;
    }

    public String getUpload() {
        return upload;
    }

    public String getUploadSuccess() {
        return uploadSuccess;
    }

    public String getUploadError() {
        return uploadError;
    }

    public String getDeleteConfirm() {
        return deleteConfirm;
    }

    public String getDeleteSuccess() {
        return deleteSuccess;
    }

    public String getDeleteFailed() {
        return deleteFailed;
    }

}
