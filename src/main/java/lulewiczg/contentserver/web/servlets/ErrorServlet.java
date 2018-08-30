package lulewiczg.contentserver.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lulewiczg.contentserver.utils.Constants;

/**
 * Handles errors.
 *
 * @author lulewiczg
 */
public class ErrorServlet extends HttpServlet {

    public static final String SERVLET_NAME = "javax.servlet.error.servlet_name";
    public static final String ERROR_CODE = "javax.servlet.error.status_code";
    public static final String ERROR_EXC = "javax.servlet.error.exception";
    public static final String ERROR_MSG = "javax.servlet.error.message";
    private static final long serialVersionUID = 1L;

    /**
     * Retuns error code.
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(Constants.Setting.PLAIN_TEXT);
        String errorMsg = (String) req.getAttribute(ERROR_MSG);
        if (errorMsg == null || errorMsg.isEmpty()) {
            Throwable throwable = (Throwable) req.getAttribute(ERROR_EXC);
            if (throwable != null) {
                errorMsg = throwable.getMessage() + "\n" + throwable.toString();
            }
        }
        Integer statusCode = (Integer) req.getAttribute(ERROR_CODE);
        String servletName = (String) req.getAttribute(SERVLET_NAME);
        errorMsg = errorMsg == null ? "" : errorMsg;
        servletName = servletName == null ? "" : servletName;
        try {
            resp.getWriter().write(String.format("%s error from %s: %s", statusCode, servletName, errorMsg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
