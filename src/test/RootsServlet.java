package test;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import test.utils.PathSettings;

public class RootsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<String> dmz = PathSettings.getInstance(null).getDmz();
		String json = "[";
		boolean first = true;
		for (String s : dmz) {
			if (!first) {
				json += ",";
			}
			json += String.format("\"%s\"", s);
			first = false;
		}
		json += "]";
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");
		resp.getWriter().write(json);
	}
}
