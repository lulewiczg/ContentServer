package lulewiczg.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lulewiczg.permissions.ResourceHelper;

/**
 * Servlet context resolution.
 *
 * @author lulewiczg
 */
public class ContextServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Returns web app location.
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().write(ResourceHelper.normalizePath(getServletContext().getRealPath("/")));
    }
}
