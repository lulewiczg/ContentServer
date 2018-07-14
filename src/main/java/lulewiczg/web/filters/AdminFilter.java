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

import lulewiczg.permissions.ResourceHelper;
import lulewiczg.utils.Constants;
import lulewiczg.utils.Log;

/**
 * Filter to validate if user has admin rights.
 *
 * @author lulewiczg
 */
public class AdminFilter implements Filter {

    /**
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
    }

    /**
     * Validates permissions.
     *
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpSession session = ((HttpServletRequest) req).getSession();
        String user = (String) session.getAttribute(Constants.Web.USER);
        String requestURI = ((HttpServletRequest) req).getRequestURI();
        if (user == null || !user.equals(Constants.ADMIN)) {
            if (!ResourceHelper.getInstance().hasReadAccess(requestURI, user)) {
                Log.getLog().logAccessDenied(requestURI, session, req);
                HttpServletResponse httpResponse = (HttpServletResponse) resp;
                httpResponse.setStatus(403);
                return;
            }
        }
        Log.getLog().logAccessGranted(requestURI, session, req);
        chain.doFilter(req, resp);
    }

    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }
}
