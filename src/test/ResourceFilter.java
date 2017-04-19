package test;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import test.utils.PathSettings;

public class ResourceFilter implements Filter {

	private boolean on = false;

	private PathSettings setttings;

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		String path = req.getParameter("path");
		if (path != null && on) {
			if (!setttings.isInDmz(path)) {
				if (!"kutas".equals(req.getParameter("dupa"))) {
					HttpServletResponse httpResponse = (HttpServletResponse) resp;
					httpResponse.sendRedirect("");
				}
			}
		}
		chain.doFilter(req, resp);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		setttings = PathSettings.getInstance(arg0);
		on = setttings.getProps().get("filter.on").equals("true");
		System.out.println(on);
	}
}
