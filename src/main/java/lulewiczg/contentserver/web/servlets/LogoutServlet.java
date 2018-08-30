package lulewiczg.contentserver.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lulewiczg.contentserver.utils.Constants;
import lulewiczg.contentserver.utils.Log;

/**
 * Servlet for logging out.
 *
 * @author lulewiczg
 */
public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Logs out.
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user = (String) req.getSession().getAttribute(Constants.Web.USER);
        req.getSession().removeAttribute(Constants.Web.USER);
        Log.getLog().logInfo(String.format("User \"%s\" logged out", user));
    }
}
