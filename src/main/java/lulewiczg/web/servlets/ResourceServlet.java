package lulewiczg.web.servlets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lulewiczg.permissions.ResourceHelper;
import lulewiczg.utils.Constants;
import lulewiczg.utils.Constants.Setting;
import lulewiczg.utils.models.Dir;

/**
 * Servlet for serving resources.
 *
 * @author lulewiczg
 */
public class ResourceServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final long EXPIRE_TIME = 1000 * 60 * 60 * 24;

    /**
     * Returns requested file or folder contents.
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding(Constants.Setting.UTF8);
        String path = request.getParameter(Constants.Web.PATH);
        if (path == null) {
            response.setStatus(404);
            return;
        }
        File f = new File(path);
        if (!f.exists()) {
            response.setStatus(404);
            return;
        }
        if (f.isDirectory()) {
            listDirJSON(request, response, f);
        } else {
            display(request, response, f);
        }
    }

    /**
     * List available directories in folder
     *
     * @param request
     *            request
     * @param response
     *            response
     * @param f
     *            folder
     * @throws IOException
     *             when could not read directory.
     */
    private void listDirJSON(HttpServletRequest request, HttpServletResponse response, File f) throws IOException {
        List<Dir> files = Dir.getFiles(f);
        response.setContentType(Setting.APPLICATION_JSON);
        String json = Dir.toJSON(files);
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
     * @throws FileNotFoundException
     *             when file not found
     */
    private void display(HttpServletRequest request, HttpServletResponse response, File f)
            throws IOException, FileNotFoundException {
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
            if (split.length == 2) {
                startStr = split[0];
                endStr = split[1];
            } else if (split.length == 1) {
                if (range.startsWith("-")) {
                    endStr = split[0];
                } else {
                    startStr = split[0];
                }
            }
            start = startStr == null ? start : Long.valueOf(startStr);
            start = start < 0 ? 0 : start;
            end = endStr == null ? end : Long.valueOf(endStr);
            end = end > length - 1 ? length - 1 : end;
        }

        long contentLength = end - start + 1;
        response.reset();
        response.setBufferSize(ResourceHelper.getInstance().getBufferSize());
        setHeaders(response, f, length, start, end, download, contentLength);

        long bytesLeft = contentLength;
        long bytesRead;
        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(f));
                OutputStream output = response.getOutputStream()) {
            stream.skip(start);
            byte[] buff = new byte[20480];
            while ((bytesRead = stream.read(buff)) != -1 && bytesLeft > 0) {
                output.write(buff, 0, (int) (bytesLeft < bytesRead ? bytesLeft : bytesRead));
                bytesLeft -= bytesRead;
            }
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
        response.setHeader(Constants.Web.Headers.CONTENT_DISPOSITION, String.format("inline;filename=\"%s\"", f.getName()));
        response.setHeader(Constants.Web.Headers.ACCEPT_RANGES, "bytes");
        response.setDateHeader(Constants.Web.Headers.EXPIRES, System.currentTimeMillis() + EXPIRE_TIME);
        response.setHeader(Constants.Web.Headers.CONTENT_RANGE, String.format("bytes %s-%s/%s", start, end, length));
        response.setHeader(Constants.Web.Headers.CONTENT_LENGTH, String.format("%s", contentLength));
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

        if ("true".equals(download)) {
            response.setContentType(Constants.Web.Headers.APPLICATION_FORCE_DOWNLOAD);
        } else {
            String contentType = ResourceHelper.getInstance().getMIME(f.getName());
            response.setContentType(contentType);
        }
    }
}
