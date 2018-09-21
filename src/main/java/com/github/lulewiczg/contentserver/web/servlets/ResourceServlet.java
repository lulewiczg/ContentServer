package com.github.lulewiczg.contentserver.web.servlets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.lulewiczg.contentserver.permissions.ResourceHelper;
import com.github.lulewiczg.contentserver.utils.Constants;
import com.github.lulewiczg.contentserver.utils.Constants.Setting;
import com.github.lulewiczg.contentserver.utils.models.Dir;

/**
 * Servlet for serving resources.
 *
 * @author lulewiczg
 */
public class ResourceServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final long EXPIRE_TIME = 1000L * 60L * 60L * 24L;

    private ServletContext context;

    /**
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException {
        context = getServletContext();
    }

    /**
     * Returns requested file or folder contents.
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getParameter(Constants.Web.PATH);
        path = ResourceHelper.decodeParam(path);
        if (path == null) {
            response.setContentType(Constants.Setting.PLAIN_TEXT);
            response.sendError(404, String.format(Constants.Web.Errors.NOT_FOUND, path));
            return;
        }
        path = ResourceHelper.normalizePath(path);
        File f = new File(path);
        if (!f.exists()) {
            response.setContentType(Constants.Setting.PLAIN_TEXT);
            response.sendError(404, String.format(Constants.Web.Errors.NOT_FOUND, path));
            return;
        }
        if (f.isDirectory()) {
            listDirJSON(response, f);
        } else {
            display(request, response, f);
        }
    }

    /**
     * List available directories in folder
     *
     * @param response
     *            response
     * @param f
     *            folder
     * @throws IOException
     *             when could not read directory.
     */
    private void listDirJSON(HttpServletResponse response, File f) throws IOException {
        List<Dir> files = Dir.getFiles(f);
        response.setContentType(Setting.APPLICATION_JSON);
        String json = Dir.toJSONArray(files);
        response.getWriter().write(json);
    }

    /**
     * Displays requested file
     *
     * @param request
     *            request
     * @param response
     *            response
     * @param f
     *            filter
     * @throws IOException
     *             when could not read file
     */
    private void display(HttpServletRequest request, HttpServletResponse response, File f) throws IOException {
        long length = f.length();
        long start = 0;
        long end = length - 1;
        String range = request.getHeader(Constants.Web.Headers.RANGE);
        String download = request.getParameter(Constants.Web.DOWNLOAD);
        if (range != null) {
            range = range.substring(6);
            String[] split = range.split("-");

            String startStr = null;
            String endStr = null;
            // thx oracle for string.split
            if (split.length != 0) {
                startStr = split[0];
            }
            if (split.length == 2) {
                endStr = split[1];
            } else {
                endStr = null;
            }
            start = parsePosition(start, startStr);
            start = start < 0 ? 0 : start;
            end = parsePosition(end, endStr);
            end = end > length - 1 ? length - 1 : end;
        }

        long contentLength = end - start + 1;
        response.reset();
        int bufferSize = ResourceHelper.get(context).getBufferSize();
        response.setBufferSize(bufferSize);
        setHeaders(response, f, length, start, end, download, contentLength);

        long bytesLeft = contentLength;
        print(response, f, start, bufferSize, bytesLeft);
    }

    /**
     * Parses range position.
     * 
     * @param defaultPos
     *            default position
     * @param parsedPos
     *            parsed position
     * @return parsed position
     */
    private long parsePosition(long defaultPos, String parsedPos) {
        defaultPos = parsedPos == null || parsedPos.isEmpty() ? defaultPos : Long.valueOf(parsedPos);
        return defaultPos;
    }

    /**
     * Prints response to output stream
     * 
     * @param response
     *            response
     * @param f
     *            file
     * @param start
     *            start position
     * @param bufferSize
     *            buffer size
     * @param bytesLeft
     *            bytes left
     * @throws IOException
     *             the IOException
     */
    private void print(HttpServletResponse response, File f, long start, int bufferSize, long bytesLeft)
            throws IOException {
        long bytesRead;
        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(f));
                OutputStream output = response.getOutputStream()) {
            skip(start, stream);
            byte[] buff = new byte[bufferSize];
            while ((bytesRead = stream.read(buff)) != -1 && bytesLeft > 0) {
                output.write(buff, 0, (int) (bytesLeft < bytesRead ? bytesLeft : bytesRead));
                bytesLeft -= bytesRead;
            }
        }
    }

    /**
     * Skips given amount of bytes
     * 
     * @param skip
     *            bytes to skip
     * @param stream
     *            stream
     * @throws IOException
     *             the IOException
     */
    private void skip(long skip, BufferedInputStream stream) throws IOException {
        long skipped = 0;
        while (skipped != skip) {
            skipped += stream.skip(skip);
        }
    }

    /**
     * Sets headers for response.
     *
     * @param response
     *            response
     * @param f
     *            requested file
     * @param length
     *            content range length
     * @param start
     *            content start
     * @param end
     *            content end
     * @param download
     *            download param
     * @param contentLength
     *            content length
     */
    private void setHeaders(HttpServletResponse response, File f, long length, long start, long end, String download,
            long contentLength) {
        response.setHeader(Constants.Web.Headers.CONTENT_DISPOSITION,
                String.format("inline;filename=\"%s\"", f.getName()));
        response.setHeader(Constants.Web.Headers.ACCEPT_RANGES, "bytes");
        response.setDateHeader(Constants.Web.Headers.EXPIRES, System.currentTimeMillis() + EXPIRE_TIME);
        response.setHeader(Constants.Web.Headers.CONTENT_RANGE, String.format("bytes %s-%s/%s", start, end, length));
        response.setHeader(Constants.Web.Headers.CONTENT_LENGTH, String.format("%s", contentLength));
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

        if ("true".equals(download)) {
            response.setContentType(Constants.Web.Headers.APPLICATION_FORCE_DOWNLOAD);
        } else {
            String contentType = ResourceHelper.get(context).getMIME(f.getName());
            response.setContentType(contentType);
        }
    }
}
