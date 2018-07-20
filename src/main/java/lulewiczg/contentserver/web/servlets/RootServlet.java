package lulewiczg.contentserver.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lulewiczg.contentserver.utils.Constants;

/**
 * Servlet obtainig app location.
 *
 * @author lulewiczg
 */
public class RootServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Returns currenly logged user.
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(Constants.Setting.PLAIN_TEXT);
        resp.getWriter().write(getServletContext().getRealPath("/"));
    }

}
