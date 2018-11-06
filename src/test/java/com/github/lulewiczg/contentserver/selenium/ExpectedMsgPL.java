package com.github.lulewiczg.contentserver.selenium;

/**
 * PL messages for tests.
 * 
 * @author lulewiczg
 */
public class ExpectedMsgPL extends ExpectedMsg {

    public ExpectedMsgPL() {
        super("pl");
        this.fileName = "Nazwa Pliku";
        this.modificationDate = "Data Modyfikacji";
        this.fileSize = "Rozmiar";
        this.options = "Opcje";
        this.invalidCredentialsError = "Użytkownik lub hasło jest niepoprawne!";
        this.appTitle = "Pokazywarka";
        this.shortcuts = "Na Skróty";
        this.login = "Zaloguj";
        this.logout = "Wyloguj";
        this.welcome = "Witaj, %s";
        this.logs = "Logi";
        this.settings = "Ustawienia";
        this.settingsNotSavedError = "Błąd przy zapisie ustawień!";
        this.settingsSaved = "Ustawienia zapisane!";
        this.loginTitle = "Logowanie";
        this.loginButton = "Zaloguj";
        this.closeButton = "Zamknij";
        this.settingsTitle = "Ustawienia";
        this.save = "Zapisz";
    }
}
