package lulewiczg.contentserver.web.servlets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lulewiczg.contentserver.permissions.ResourceHelper;
import lulewiczg.contentserver.utils.Constants;
import lulewiczg.contentserver.utils.json.JSONModel;

/**
 * Servlet for shortcuts.
 *
 * @author lulewiczg
 */
public class ShortcutsServlet extends HttpServlet {

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
     * Returns available shortcuts for user. If path is provided, returns number of
     * previous folders that user has access to.
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user = (String) req.getSession().getAttribute(Constants.Web.USER);
        String path = req.getParameter(Constants.Web.PATH);
        path = ResourceHelper.decodeParam(path);
        if (path == null) {
            processShortcutList(resp, user);
        } else {
            processPathCheck(resp, user, path);
        }
    }

    /**
     * Checks how many folders before actual path can user go back to.
     * 
     * @param resp
     *            response
     * @param user
     *            user
     * @param path
     *            path to check
     * @throws IOException
     *             the IOException
     */
    private void processPathCheck(HttpServletResponse resp, String user, String path) throws IOException {
        path = ResourceHelper.normalizePath(path);
        File f = new File(path);
        int counter = 0;
        while ((f = f.getParentFile()) != null) {
            if (ResourceHelper.get(context).hasReadAccess(f.getCanonicalPath(), user)) {
                counter++;
            } else {
                break;
            }
        }
        resp.setContentType(Constants.Setting.PLAIN_TEXT);
        resp.getWriter().write(String.valueOf(counter));
    }

    /**
     * Lists available paths for given user. If path does not exist in file system,
     * is ignored.
     * 
     * @param resp
     *            response
     * @param user
     *            user
     * @throws IOException
     *             the IOException
     */
    private void processShortcutList(HttpServletResponse resp, String user) throws IOException {
        List<String> dmz = ResourceHelper.get(context).getAvailablePaths(user);
        List<String> dirs = new ArrayList<>();
        for (String s : dmz) {
            if (new File(s).exists()) {
                dirs.add(s);
            }
        }
        resp.setContentType(Constants.Setting.APPLICATION_JSON);
        resp.getWriter().write(JSONModel.toJSONArray(dirs));
    }
}
