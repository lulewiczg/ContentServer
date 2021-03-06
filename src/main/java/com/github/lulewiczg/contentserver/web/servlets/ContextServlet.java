package com.github.lulewiczg.contentserver.web.servlets;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.lulewiczg.contentserver.utils.CommonUtil;
import com.github.lulewiczg.contentserver.utils.Constants;

/**
 * Servlet for context resolution.
 *
 * @author lulewiczg
 */
public class ContextServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ServletContext context;

    /**
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException {
        context = getServletContext();
    }

    /**
     * Returns web app location.
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(Constants.Setting.PLAIN_TEXT);
        String path = CommonUtil.getContextPath(context);
        resp.getWriter().write(path);
    }
}
