package com.github.lulewiczg.contentserver.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.lulewiczg.contentserver.utils.Constants;
import com.github.lulewiczg.contentserver.utils.Log;

/**
 * Handles errors.
 *
 * @author lulewiczg
 */
@WebServlet(name = "ErrorServlet", urlPatterns = "/error")
public class ErrorServlet extends HttpServlet {

    public static final String SERVLET_NAME = "javax.servlet.error.servlet_name";
    public static final String ERROR_CODE = "javax.servlet.error.status_code";
    public static final String ERROR_EXC = "javax.servlet.error.exception";
    public static final String ERROR_MSG = "javax.servlet.error.message";
    private static final long serialVersionUID = 1L;

    /**
     * Retuns error code.
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(Constants.Setting.PLAIN_TEXT);
        String errorMsg = (String) req.getAttribute(ERROR_MSG);
        Throwable throwable = (Throwable) req.getAttribute(ERROR_EXC);
        if ((errorMsg == null || errorMsg.isEmpty()) && throwable != null) {
            errorMsg = throwable.getMessage();
        }
        Integer statusCode = (Integer) req.getAttribute(ERROR_CODE);
        String servletName = (String) req.getAttribute(SERVLET_NAME);
        errorMsg = errorMsg == null ? "" : errorMsg;
        servletName = servletName == null ? "" : servletName;
        try {
            String error = String.format("%s error from %s: %s", statusCode, servletName, errorMsg);
            resp.getWriter().write(error);
            Log.getLog().logError(error);
            if (throwable != null) {
                Log.getLog().log(throwable);
            }
        } catch (IOException e) {
            Log.getLog().log(e);
        }
    }

}
