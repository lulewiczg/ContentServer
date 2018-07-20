package lulewiczg.contentserver.web.servlets;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lulewiczg.contentserver.permissions.ResourceHelper;
import lulewiczg.contentserver.utils.Constants;

/**
 * Servlet for shortcuts.
 *
 * @author lulewiczg
 */
public class ShortcutsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Returns available shortcuts for user.
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user = (String) req.getSession().getAttribute(Constants.Web.USER);
        String path = req.getParameter(Constants.Web.PATH);
        resp.setCharacterEncoding(Constants.Setting.UTF8);
        if (path == null) {
            List<String> dmz = ResourceHelper.getInstance().getAvailablePaths(user);
            String json = "[";
            boolean first = true;
            for (String s : dmz) {
                if (!new File(s).exists()) {
                    continue;
                }
                if (!first) {
                    json += ",";
                }
                json += String.format("\"%s\"", s);
                first = false;
            }
            json += "]";
            resp.setContentType(Constants.Setting.APPLICATION_JSON);
            resp.getWriter().write(json);
        } else {
            path = ResourceHelper.normalizePath(path);
            File f = new File(path);
            int counter = 0;
            while ((f = f.getParentFile()) != null) {
                if (ResourceHelper.getInstance().hasReadAccess(f.getCanonicalPath(), user)) {
                    counter++;
                } else {
                    break;
                }
            }
            resp.setStatus(200);
            resp.getWriter().write(String.valueOf(counter));
        }
    }
}
