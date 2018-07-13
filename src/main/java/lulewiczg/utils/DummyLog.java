package lulewiczg.utils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Classed used when logging is turned off.
 *
 * @author lulewiczg
 */
public class DummyLog extends Log {

    @Override
    public void log(Exception ex) {
    }

    @Override
    public void log(String str) {
    }

    @Override
    public void logAccessGranted(String path, HttpSession session, ServletRequest req) {
    }

    @Override
    public void logAccessDenied(String path, HttpSession session, ServletRequest req) {
    }
}
