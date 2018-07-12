package lulewiczg.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

import lulewiczg.web.permissions.ResourceHelper;

/**
 * Logs events to file in Jetty directory. If run on desktop, logs to console.
 * 
 * @author lulewiczg
 *
 */
public class Log extends AbstractLog {
	private PrintWriter writer;
	private static AbstractLog instance;
	private static final DateFormat format = new SimpleDateFormat("yyyyy-MM-dd HH:mm:ss");

	private Log() {
		File file = new File("/storage/emulated/legacy/jetty/tmp/log.txt");
		if (!file.getParentFile().exists()) {
			writer = new PrintWriter(new OutputStreamWriter(System.out));
		} else {
			try {
				writer = new PrintWriter(new FileWriter(file, true));
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalArgumentException("Could not find log for file");
			}
		}
	}

	/**
	 * Obtains logger.
	 * 
	 * @return logger
	 */
	public static synchronized AbstractLog getLog() {
		if (instance == null) {
			if (ResourceHelper.getInstance(null).isLogEnabled()) {
				instance = new Log();
			} else {
				instance = new AbstractLog();
			}
		}
		return instance;
	}

	/**
	 * Writes log.
	 * 
	 * @param s string
	 */
	private void write(String s) {
		writer.write(s);
		writer.write("\n");
		writer.flush();
	}

	/**
	 * @see lulewiczg.utils.AbstractLog#log(java.lang.Exception)
	 */
	@Override
	public void log(Exception ex) {
		ex.printStackTrace(writer);
		writer.flush();
	}

	/**
	 * @see lulewiczg.utils.AbstractLog#log(java.lang.String)
	 */
	@Override
	public void log(String str) {
		str = String.format("[%s] - %s", format.format(new Date()), str);
		write(str);
	}

	/**
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		writer.close();
	}

	/**
	 * @see lulewiczg.utils.AbstractLog#logAccessGranted(java.lang.String,
	 *      javax.servlet.http.HttpSession, javax.servlet.ServletRequest)
	 */
	@Override
	public void logAccessGranted(String path, HttpSession session, ServletRequest req) {
		String str = String.format("[%s] - [USER: %s, %s] accessed [%s]", format.format(new Date()),
				session.getAttribute(ResourceHelper.USER), req.getRemoteAddr(), path);
		write(str);
	}

	/**
	 * @see lulewiczg.utils.AbstractLog#logAccessDenied(java.lang.String,
	 *      javax.servlet.http.HttpSession, javax.servlet.ServletRequest)
	 */
	@Override
	public void logAccessDenied(String path, HttpSession session, ServletRequest req) {
		String str = String.format("[%s] - [USER: %s, %s] denied for [%s]", format.format(new Date()),
				session.getAttribute(ResourceHelper.USER), req.getRemoteAddr(), path);
		write(str);

	}
}
