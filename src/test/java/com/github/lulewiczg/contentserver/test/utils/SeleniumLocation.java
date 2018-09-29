package com.github.lulewiczg.contentserver.test.utils;

/**
 * Test run type;
 *
 * @author lulewiczg
 */
public enum SeleniumLocation {
    LOCAL("http://localhost:8080/ContentServer"), ANDROID("http://192.168.1.11:8090/ContentServer");

    private String url;

    public String getUrl() {
        return url;
    }

    private SeleniumLocation(String url) {
        this.url = url;
    }

}
