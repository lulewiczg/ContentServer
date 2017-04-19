package test.servlets;

import java.io.IOException;

import javax.security.sasl.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import test.permissions.PermissionsResolver;
import test.utils.Log;

public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		String user = (String) session.getAttribute(PermissionsResolver.USER);
		if (user != null) {
			resp.getWriter().write(user);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		String user = (String) session.getAttribute(PermissionsResolver.USER);
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
				PermissionsResolver.getInstance(null).login(login, password);
				session.setAttribute(PermissionsResolver.USER, login);
				resp.getWriter().write(login);
			} catch (AuthenticationException e) {
				Log.getLog().log(e);
				resp.setStatus(401);
			}
		}
	}
}
