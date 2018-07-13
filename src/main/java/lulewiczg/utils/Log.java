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

import lulewiczg.permissions.ResourceHelper;

/**
 * Logs events to file in Jetty directory. If run on desktop, logs to console.
 *
 * @author lulewiczg
 *
 */
public class Log {
    private PrintWriter writer;
    private static Log instance;
    private static final DateFormat format = new SimpleDateFormat("yyyyy-MM-dd HH:mm:ss");

    private Log(String path) {
        File file = new File(path);
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

    Log() {
    }

    /**
     * Obtains logger.
     *
     * @return logger
     */
    public static synchronized Log getLog() {
        if (instance == null) {
            if (ResourceHelper.getInstance().isLogEnabled()) {
                instance = new Log("/storage/emulated/legacy/jetty/tmp/log.txt");
            } else {
                instance = new DummyLog();
            }
        }
        return instance;
    }

    /**
     * Writes log.
     *
     * @param s
     *            string
     */
    private void write(String s) {
        writer.write(s);
        writer.write("\n");
        writer.flush();
    }

    /**
     * Logs exception.
     *
     * @param str
     *            string
     */
    public void log(Exception ex) {
        ex.printStackTrace(writer);
        writer.flush();
    }

    /**
     * Logs string.
     *
     * @param str
     *            string
     */
    public void log(String str) {
        str = String.format("[%s] - %s", format.format(new Date()), str);
        write(str);
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
        String str = String.format("[%s] - [USER: %s, %s] accessed [%s]", format.format(new Date()),
                session.getAttribute(Constants.Setting.USER), req.getRemoteAddr(), path);
        write(str);
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
        String str = String.format("[%s] - [USER: %s, %s] denied for [%s]", format.format(new Date()),
                session.getAttribute(Constants.Setting.USER), req.getRemoteAddr(), path);
        write(str);

    }

    @Override
    protected void finalize() throws Throwable {
        writer.close();
    }

}
