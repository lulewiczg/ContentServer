package com.github.lulewiczg.contentserver.web.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.github.lulewiczg.contentserver.utils.Constants;
import com.github.lulewiczg.contentserver.utils.Log;
import com.github.lulewiczg.contentserver.utils.ResourceUtil;

/**
 * Filter to validate user permissions to requested resources.
 *
 * @author lulewiczg
 */
@WebFilter(filterName = "ResourceFilter", urlPatterns = {"/rest/files/*", "/rest/upload/*"})
public class ResourceFilter implements Filter {

    private ServletContext context;

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
        String path = req.getParameter(Constants.Web.PATH);
        HttpSession session = ((HttpServletRequest) req).getSession();
        String user = (String) session.getAttribute(Constants.Web.USER);
        if (path != null && !ResourceUtil.get(context).hasReadAccess(path, user)) {
            Log.getLog().logAccessDenied(path, session, req);
            HttpServletResponse httpResponse = (HttpServletResponse) resp;
            httpResponse.sendError(403, String.format(Constants.Web.Errors.ACCESS_DENIED_TO, path));
            return;
        }
        Log.getLog().logAccessGranted(path, session, req);
        chain.doFilter(req, resp);
    }

    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig arg0) throws ServletException {
        this.context = arg0.getServletContext();
    }
}
