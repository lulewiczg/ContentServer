package lulewiczg.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lulewiczg.utils.Log;
import lulewiczg.web.permissions.ResourceHelper;

public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		String user = (String) session.getAttribute(ResourceHelper.USER);
		if (user != null) {
			resp.getWriter().write(user);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		String user = (String) session.getAttribute(ResourceHelper.USER);
		if (user != null) {
			resp.setStatus(403);
		} else {
			String login = req.getParameter("login");
			String password = req.getParameter("password");
			if (login == null || password == null) {
				resp.setStatus(401);
				return;
			}
			try {
				ResourceHelper.getInstance(null).login(login, password);
				session.setAttribute(ResourceHelper.USER, login);
				resp.getWriter().write(login);
			} catch (Exception e) {
				Log.getLog().log(e);
				resp.setStatus(401);
			}
		}
	}
}
