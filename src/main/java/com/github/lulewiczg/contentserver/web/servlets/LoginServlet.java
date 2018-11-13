package com.github.lulewiczg.contentserver.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.github.lulewiczg.contentserver.utils.Constants;
import com.github.lulewiczg.contentserver.utils.Log;
import com.github.lulewiczg.contentserver.utils.ResourceUtil;
import com.github.lulewiczg.contentserver.utils.models.UserPermissions;

/**
 * Servlet for handling logging in.
 *
 * @author lulewiczg
 */
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Returns currently logged user.
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String path = req.getParameter(Constants.Web.PATH);
        String user = (String) session.getAttribute(Constants.Web.USER);
        boolean upload = false;
        if (path != null) {
            upload = ResourceUtil.get(req.getServletContext()).hasWriteAccess(path, user);
        }
        resp.setContentType(Constants.Setting.APPLICATION_JSON);
        resp.getWriter().write(new UserPermissions(user, upload).toJSON());
    }

    /**
     * Logs in.
     *
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        resp.setContentType(Constants.Setting.PLAIN_TEXT);
        String user = (String) session.getAttribute(Constants.Web.USER);
        if (user != null) {
            resp.sendError(403, Constants.Web.Errors.USER_ALREADY_LOGGED);
        } else {
            login(req, resp, session);
        }
    }

    /**
     * Performs login.
     *
     * @param req
     *            request
     * @param resp
     *            response
     * @param session
     *            session
     * @throws IOException
     *             the IOException
     */
    private void login(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        String login = req.getParameter(Constants.Web.LOGIN);
        String password = req.getParameter(Constants.Web.PASSWORD);
        if (login == null || password == null) {
            resp.sendError(401, Constants.Web.Errors.INVALID_CREDENTIALS);
            return;
        }
        try {
            ResourceUtil.get(getServletContext()).login(login, password, session);
            resp.getWriter().write(login);
        } catch (Exception e) {
            Log.getLog().log(e);
            resp.sendError(401, Constants.Web.Errors.INVALID_CREDENTIALS);
        }
    }
}
