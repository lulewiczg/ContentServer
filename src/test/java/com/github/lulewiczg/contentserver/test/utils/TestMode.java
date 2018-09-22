package com.github.lulewiczg.contentserver.test.utils;

/**
 * Test run type;
 *
 * @author lulewiczg
 */
public enum TestMode {
    UNIT(false, null),
    SELENIUM(true, "http://localhost:8080/ContentServer"),
    ANDROID(true, "http://192.168.1.11:8090/ContentServer");

    private boolean selenium;
    private String url;

    public boolean isSelenium() {
        return selenium;
    }

    public String getUrl() {
        return url;
    }

    private TestMode(boolean selenium, String url) {
        this.selenium = selenium;
        this.url = url;
    }

}
