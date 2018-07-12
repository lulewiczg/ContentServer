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

import lulewiczg.utils.Dir;
import lulewiczg.web.permissions.ResourceHelper;

public class ResourceServlet extends HttpServlet {

	private static final String PATH = "path";

	private static final long serialVersionUID = 1L;

	private int buffSize;

	private static final long EXPIRE_TIME = 1000 * 60 * 60 * 24;

	private ResourceHelper resolver;

	@Override
	public void init() throws ServletException {
		resolver = ResourceHelper.getInstance(null);
		buffSize = resolver.getBufferSize();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		String path = request.getParameter(PATH);
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

	private void listDirJSON(HttpServletRequest request, HttpServletResponse response, File f) throws IOException {
		List<Dir> files = Dir.getFiles(f);
		response.setContentType("application/json");
		String json = Dir.toJSON(files);
		response.getWriter().write(json);
	}

	private void display(HttpServletRequest request, HttpServletResponse response, File f)
			throws IOException, FileNotFoundException {
		long length = f.length();
		long start = 0;
		long end = length - 1;
		String range = request.getHeader("Range");
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
		response.setBufferSize(buffSize);
		response.setHeader("Content-Disposition", String.format("inline;filename=\"%s\"", f.getName()));
		response.setHeader("Accept-Ranges", "bytes");
		response.setDateHeader("Expires", System.currentTimeMillis() + EXPIRE_TIME);
		String contentType = resolver.getMIME(f.getName());
		response.setContentType(contentType);
		response.setHeader("Content-Range", String.format("bytes %s-%s/%s", start, end, length));
		response.setHeader("Content-Length", String.format("%s", contentLength));
		response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

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
}
