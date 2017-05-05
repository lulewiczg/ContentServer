package test.servlets;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import test.permissions.ResourceHelper;
import test.utils.Log;

public class ResourceFilter implements Filter {

	private ResourceHelper settings;

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		String path = req.getParameter("path");
		HttpSession session = ((HttpServletRequest) req).getSession();
		String user = (String) session.getAttribute("user");
		if (path != null) {
			if (!settings.hasReadAccess(path, user)) {
				Log.getLog().logAccessDenied(path, session, req);
				HttpServletResponse httpResponse = (HttpServletResponse) resp;
				httpResponse.setStatus(403);
				return;
			}
		}
		Log.getLog().logAccessGranted(path, session, req);
		chain.doFilter(req, resp);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		settings = ResourceHelper.getInstance(null);
	}
}
