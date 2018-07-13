package lulewiczg.utils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Classed used when logging is turned off.
 * 
 * @author lulewiczg
 */
public class DummyLog extends Log {

	public void log(Exception ex) {
	}

	public void log(String str) {
	}

	public void logAccessGranted(String path, HttpSession session, ServletRequest req) {
	}

	public void logAccessDenied(String path, HttpSession session, ServletRequest req) {
	}
}
