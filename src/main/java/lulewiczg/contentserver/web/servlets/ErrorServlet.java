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

    private static final long serialVersionUID = 1L;

    /**
     * Retuns error code.
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(Constants.Setting.PLAIN_TEXT);
        String errorMsg = (String) req.getAttribute("javax.servlet.error.message");
        if (errorMsg == null || errorMsg.isEmpty()) {
            Throwable throwable = (Throwable) req.getAttribute("javax.servlet.error.exception");
            if (throwable != null) {
                errorMsg = throwable.getMessage();
            }
        }
        Integer statusCode = (Integer) req.getAttribute("javax.servlet.error.status_code");
        String servletName = (String) req.getAttribute("javax.servlet.error.servlet_name");
        resp.getWriter().write(String.format("%s error from %s: %s", statusCode, servletName, errorMsg));
    }

}
