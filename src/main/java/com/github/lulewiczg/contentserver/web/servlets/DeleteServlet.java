package com.github.lulewiczg.contentserver.web.servlets;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.lulewiczg.contentserver.utils.CommonUtil;
import com.github.lulewiczg.contentserver.utils.Constants;
import com.github.lulewiczg.contentserver.utils.Log;
import com.github.lulewiczg.contentserver.utils.SettingsUtil;

/**
 * Servlet for deleting files.
 *
 * @author lulewiczg
 *
 */
public class DeleteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getParameter(Constants.Web.PATH);
        path = SettingsUtil.decodeParam(path);
        if (path == null || path.isEmpty()) {
            resp.sendError(400, Constants.Web.Errors.NOT_FOUND);
            return;
        }
        path = CommonUtil.normalizePath(path);
        processDelete(req, resp, path);
    }

    /**
     * Process delete. DELETE method not working WTF?!
     *
     * @param request
     *            request
     * @param response
     *            response
     * @param path
     *            path
     * @throws IOException
     *             the IOException
     */
    private void processDelete(HttpServletRequest request, HttpServletResponse response, String path)
            throws IOException {
        File file = new File(path);
        Log.getLog().logInfo(String.format("Processing %s for delete", path));
        if (file.isDirectory()) {
            Log.getLog().logError("Tried to delete directory!");
            response.sendError(400, Constants.Web.Errors.DIRECTORY_DELETE);
            return;
        }
        if (!file.exists() || !file.delete()) {
            Log.getLog().logError("Delete failed!");
            response.sendError(400, Constants.Web.Errors.DELETE_FAILED);
            return;
        }
        Log.getLog().logError("File deleted!");
    }
}
