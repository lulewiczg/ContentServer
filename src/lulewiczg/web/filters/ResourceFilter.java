package lulewiczg.web.filters;

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

import lulewiczg.utils.Log;
import lulewiczg.web.permissions.ResourceHelper;

/**
 * Filter to validate user permissions to requested resources.
 * 
 * @author lulewiczg
 */
public class ResourceFilter implements Filter {

	private static final String PATH = "path";
	private static final String USER = "user";
	private ResourceHelper settings;

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}

	/**
	 * Validates permissions.
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		String path = req.getParameter(PATH);
		HttpSession session = ((HttpServletRequest) req).getSession();
		String user = (String) session.getAttribute(USER);
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

	/**
	 * Obtains settings.
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		settings = ResourceHelper.getInstance(null);
	}
}
