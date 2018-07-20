package lulewiczg.contentserver.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lulewiczg.contentserver.permissions.ResourceHelper;
import lulewiczg.contentserver.utils.Constants;
import lulewiczg.contentserver.utils.Log;

/**
 * Servlet for handling logging in.
 *
 * @author lulewiczg
 */
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Returns currenly logged user.
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String user = (String) session.getAttribute(Constants.Web.USER);
        if (user != null) {
            resp.getWriter().write(user);
        }
    }

    /**
     * Logs in.
     *
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String user = (String) session.getAttribute(Constants.Web.USER);
        if (user != null) {
            resp.setStatus(403);
        } else {
            String login = req.getParameter(Constants.Web.LOGIN);
            String password = req.getParameter(Constants.Web.PASSWORD);
            if (login == null || password == null) {
                resp.setStatus(401);
                return;
            }
            try {
                ResourceHelper.getInstance().login(login, password);
                session.setAttribute(Constants.Web.USER, login);
                resp.getWriter().write(login);
            } catch (Exception e) {
                Log.getLog().log(e);
                resp.setStatus(401);
            }
        }
    }
}
