package com.github.lulewiczg.contentserver.web.filters;

import java.io.IOException;
import java.util.Base64;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.github.lulewiczg.contentserver.utils.Constants;
import com.github.lulewiczg.contentserver.utils.ResourceUtil;

/**
 * Performs basic HTTP login. Needs Android API >24.
 *
 * @author lulewiczg
 */
public class UrlLoginFilter implements Filter {
    private static final String BASIC = "Basic ";
    private ServletContext context;

    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        context = filterConfig.getServletContext();
    }

    /**
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        String auth = ((HttpServletRequest) req).getHeader(Constants.Web.Headers.AUTHORIZATION);
        if (auth != null && auth.startsWith(BASIC)) {
            HttpSession session = ((HttpServletRequest) req).getSession();
            auth = auth.replace(BASIC, "");
            String decode = new String(Base64.getDecoder().decode(auth));
            String[] split = decode.split("\\:");
            ResourceUtil.get(context).login(split[0], split[1], session);
        }
        chain.doFilter(req, resp);
    }

    /**
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        // Do nothing
    }

}
