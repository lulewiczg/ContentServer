package test.servlets;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import test.permissions.PermissionsResolver;

public class RootsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String user = (String) req.getSession().getAttribute("user");
		String path = req.getParameter("path");
		resp.setCharacterEncoding("UTF-8");
		if (path == null) {
			List<String> dmz = PermissionsResolver.getInstance(null).getAvailablePaths(user);
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
			resp.setContentType("application/json");
			resp.getWriter().write(json);
		} else {
			File f = new File(path);
			int counter = 0;
			while ((f = f.getParentFile()) != null) {
				if (PermissionsResolver.getInstance(null).hasReadAccess(f.getCanonicalPath(), user)) {
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
