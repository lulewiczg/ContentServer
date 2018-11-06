package com.github.lulewiczg.contentserver.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
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
    private static final String FORMAT = "[%1$tF %1$tT] [%4$s] - %5$s %n";
    public static final String LOG_LOCATION = "/WEB-INF/logs/log.txt";
    private static final Log DUMMY_LOG = new DummyLog();
    private static Log instance;
    private static final Logger log = Logger.getLogger("Log");
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", FORMAT);
        System.setProperty("jdk.system.logger.format", FORMAT);
    }

    private Log(String path) {
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        log.setLevel(Level.ALL);
        SimpleFormatter formatter = new SimpleFormatter() {

            @Override
            public synchronized String format(LogRecord lr) {
                return String.format(FORMAT, new Date(lr.getMillis()), null, null, lr.getLevel().getLocalizedName(),
                        lr.getMessage());
            }
        };
        addHandlers(file, formatter);
    }

    /**
     * Adds console and file handlers
     *
     * @param file
     *            file to log
     * @param formatter
     *            formatter
     */
    private void addHandlers(File file, SimpleFormatter formatter) {
        StreamHandler consoleHandler = new StreamHandler(System.out, formatter);
        consoleHandler.setFormatter(formatter);
        log.addHandler(consoleHandler);
        try {
            FileHandler handler = new FileHandler(file.getPath(), 0, 1, true);
            handler.setFormatter(formatter);
            log.addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Could not find log for file");
        }
    }

    protected Log() {
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
    private void write(Object s, Level level) {
        log.log(level, s.toString());
    }

    /**
     * Logs exception.
     *
     * @param str
     *            string
     */
    public void log(Throwable ex) {
        // WTF logger is not printing stacktrace?
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String stringEx = sw.toString();
        log.log(Level.SEVERE, stringEx);
    }

    /**
     * Logs info.
     *
     * @param str
     *            string
     */
    public void logInfo(Object str) {
        write(str, Level.INFO);
    }

    /**
     * Logs error.
     *
     * @param str
     *            string
     */
    public void logError(Object str) {
        write(str, Level.SEVERE);
    }

    /**
     * Logs error.
     *
     * @param str
     *            string
     */
    public void logDebug(Object str) {
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

    public static boolean isEnabled(Level l) {
        return log.isLoggable(l);
    }
}
