package com.github.lulewiczg.contentserver.web.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.github.lulewiczg.contentserver.utils.Constants;
import com.github.lulewiczg.contentserver.utils.Log;

/**
 * Filter to validate if user has admin rights.
 *
 * @author lulewiczg
 */
@WebFilter(filterName = "AdminFilter", urlPatterns = { "/rest/settings/*", "/rest/context/*" })
public class AdminFilter implements Filter {

    /**
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        // Do nothing
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
        HttpSession session = ((HttpServletRequest) req).getSession();
        String user = (String) session.getAttribute(Constants.Web.USER);
        String requestURI = ((HttpServletRequest) req).getRequestURI();
        if (user == null || !user.equals(Constants.ADMIN)) {
            Log.getLog().logAccessDenied(requestURI, session, req);
            HttpServletResponse httpResponse = (HttpServletResponse) resp;
            httpResponse.sendError(403, String.format(Constants.Web.Errors.ACCESS_DENIED_TO, requestURI));
            return;
        }
        Log.getLog().logAccessGranted(requestURI, session, req);
        chain.doFilter(req, resp);
    }

    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // Do nothing
    }
}
