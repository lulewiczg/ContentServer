package lulewiczg.contentserver.utils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Dummy logger.
 *
 * @author lulewiczg
 *
 */
public class DummyLog extends Log {

	public DummyLog() {
		super();
	}

	/**
	 * Does nothing.
	 * 
	 * @see lulewiczg.contentserver.utils.Log#log(java.lang.Exception)
	 */
	@Override
	public void log(Exception ex) {
	}

	/**
	 * Does nothing.
	 * 
	 * @see lulewiczg.contentserver.utils.Log#logInfo(java.lang.String)
	 */
	@Override
	public void logInfo(String str) {
	}

	/**
	 * Does nothin
	 * 
	 * @see lulewiczg.contentserver.utils.Log#logError(java.lang.String)
	 */
	@Override
	public void logError(String str) {
	}

	/**
	 * @see lulewiczg.contentserver.utils.Log#logDebug(java.lang.String)
	 */
	@Override
	public void logDebug(String str) {
	}

	/**
	 * @see lulewiczg.contentserver.utils.Log#logAccessGranted(java.lang.String,
	 *      javax.servlet.http.HttpSession, javax.servlet.ServletRequest)
	 */
	@Override
	public void logAccessGranted(String path, HttpSession session, ServletRequest req) {
	}

	/**
	 * @see lulewiczg.contentserver.utils.Log#logAccessDenied(java.lang.String,
	 *      javax.servlet.http.HttpSession, javax.servlet.ServletRequest)
	 */
	@Override
	public void logAccessDenied(String path, HttpSession session, ServletRequest req) {
	}

}
