package com.github.lulewiczg.contentserver.web.servlets;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.github.lulewiczg.contentserver.utils.CommonUtil;
import com.github.lulewiczg.contentserver.utils.Constants;
import com.github.lulewiczg.contentserver.utils.ResourceUtil;
import com.github.lulewiczg.contentserver.utils.SettingsUtil;

/**
 * Servlet for file upload
 *
 * @author lulewiczg
 *
 */
public class UploadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String destLocation = req.getParameter(Constants.Web.PATH);
        String user = (String) req.getSession().getAttribute(Constants.Web.USER);
        if (destLocation == null || destLocation.isEmpty()) {
            resp.sendError(400, String.format("todo"));
            return;
        }
        ResourceUtil.get(req.getServletContext()).hasReadAccess(destLocation, user);
        Collection<Part> parts = req.getParts();
        int bufferSize = SettingsUtil.get(req.getServletContext()).getBufferSize();
        for (Part p : parts) {
            String name = getFileName(p);
            if (name == null) {
                continue;
            }
            uploadFile(CommonUtil.normalizePath(destLocation + Constants.SEP + name), bufferSize, p.getInputStream(), resp);
        }
        resp.sendRedirect(req.getServletContext().getContextPath() + "/?path=" + destLocation);
    }

    /**
     * Uploads file
     *
     * @param location
     *            file location
     * @param bufferSize
     *            buffer size
     * @param stream
     *            input stream
     * @param resp
     *            response
     * @throws IOException
     *             the IOException
     */
    private void uploadFile(String location, int bufferSize, InputStream stream, HttpServletResponse resp) throws IOException {
        int bytesRead;
        File f = new File(location);
        if (f.exists()) {
            resp.sendError(400, String.format("todo"));
        }
        try (InputStream input = stream; BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(f))) {
            byte[] buff = new byte[bufferSize];
            while ((bytesRead = input.read(buff)) != -1) {
                output.write(buff, 0, bytesRead);
            }
        }
    }

    /**
     * Obtains submitted file name
     *
     * @param part
     *            part
     * @return file name
     */
    private static String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1);
            }
        }
        return null;
    }
}
