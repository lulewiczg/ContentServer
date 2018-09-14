package lulewiczg.contentserver.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Logs events to file in Jetty directory. If run on desktop, logs to console.
 *
 * @author lulewiczg
 *
 */
public class Log {
    public static final String LOG_LOCATION = "/WEB-INF/logs/log.txt";
    private static final Log DUMMY_LOG = new DummyLog();
    private static Log instance;
    private static final Logger log = Logger.getLogger("Log");
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] - %5$s %n");
    }

    private Log(String path) {
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        log.setLevel(Level.ALL);
        SimpleFormatter formatter = new SimpleFormatter();
        log.addHandler(new StreamHandler(System.out, formatter));
        try {
            FileHandler handler = new FileHandler(file.getPath());
            handler.setFormatter(formatter);
            log.addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Could not find log for file");
        }
    }

    Log() {
    }

    /**
     * Obtains logger.
     *
     * @return logger
     */
    public static synchronized Log getLog() {
        if (instance == null || log.getLevel() == Level.OFF) {
            return DUMMY_LOG;
        }
        return instance;
    }

    /**
     * Inits logger.
     *
     * @return logger
     */
    public static synchronized Log init(String path) {
        instance = new Log(path + LOG_LOCATION);
        return instance;
    }

    /**
     * Writes log.
     *
     * @param s
     *            string
     */
    private void write(String s, Level level) {
        log.log(level, s);
    }

    /**
     * Logs exception.
     *
     * @param str
     *            string
     */
    public void log(Throwable ex) {
        log.log(Level.SEVERE, "Exception - ", ex);
    }

    /**
     * Logs info.
     *
     * @param str
     *            string
     */
    public void logInfo(String str) {
        write(str, Level.INFO);
    }

    /**
     * Logs error.
     *
     * @param str
     *            string
     */
    public void logError(String str) {
        write(str, Level.SEVERE);
    }

    /**
     * Logs error.
     *
     * @param str
     *            string
     */
    public void logDebug(String str) {
        write(str, Level.FINEST);
    }

    /**
     * Logs access granted event.
     *
     * @param path
     *            content path
     * @param session
     *            session
     * @param req
     *            request
     */
    public void logAccessGranted(String path, HttpSession session, ServletRequest req) {
        if (log.isLoggable(Level.FINE)) {
            String str = String.format("[USER: %s, %s] accessed [%s]", session.getAttribute(Constants.Setting.USER),
                    req.getRemoteAddr(), path);
            write(str, Level.FINE);
        }
    }

    /**
     * Logs access denied event.
     *
     * @param path
     *            content path
     * @param session
     *            session
     * @param req
     *            request
     */
    public void logAccessDenied(String path, HttpSession session, ServletRequest req) {
        if (log.isLoggable(Level.FINE)) {
            String str = String.format("[USER: %s, %s] denied for [%s]", session.getAttribute(Constants.Setting.USER),
                    req.getRemoteAddr(), path);
            write(str, Level.FINE);
        }
    }

    public static void setLevel(Level level) {
        log.setLevel(level);
    }
}
