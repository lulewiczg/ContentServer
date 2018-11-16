package com.github.lulewiczg.contentserver.web.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import com.github.lulewiczg.contentserver.utils.Constants;

/**
 * Filter to set valid encoding for requets and responses.
 *
 * @author Grzegorz
 */
@WebFilter(filterName = "EncodingFilter", urlPatterns = "/rest/*")
public class EncodingFilter implements Filter {

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
        req.setCharacterEncoding(Constants.Setting.UTF8);
        resp.setCharacterEncoding(Constants.Setting.UTF8);
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
