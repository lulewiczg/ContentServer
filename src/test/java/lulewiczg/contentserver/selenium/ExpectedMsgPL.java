package lulewiczg.contentserver.selenium;

/**
 * PL messages for tests.
 * 
 * @author lulewiczg
 */
public class ExpectedMsgPL extends ExpectedMsg {

    public ExpectedMsgPL() {
        super("pl");
        this.fileName = "Nazwa Pliku";
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
    }
}
