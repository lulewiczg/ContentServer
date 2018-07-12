package lulewiczg.utils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Classed used when logging is turned off.
 * 
 * @author lulewiczg
 */
public class AbstractLog {

	/**
	 * Logs exception
	 * 
	 * @param ex exception
	 */
	public void log(Exception ex) {
	}

	/**
	 * Logs string
	 * 
	 * @param str string
	 */
	public void log(String str) {
	}

	/**
	 * Logs access granted event.
	 * 
	 * @param path    content path
	 * @param session session
	 * @param req     request
	 */
	public void logAccessGranted(String path, HttpSession session, ServletRequest req) {
	}

	/**
	 * Logs access denied event.
	 * 
	 * @param path    content path
	 * @param session session
	 * @param req     request
	 */
	public void logAccessDenied(String path, HttpSession session, ServletRequest req) {
	}
}
