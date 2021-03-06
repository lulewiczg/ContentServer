package com.github.lulewiczg.contentserver.utils.json;

import java.io.IOException;

/**
 * Exception for JSON processing.
 *
 * @author Grzegorz
 */
public class JSONException extends IOException {

    public JSONException(Exception e) {
        super(e);
    }

    public JSONException(String string) {
        super(string);
    }

    private static final long serialVersionUID = 1L;

}
