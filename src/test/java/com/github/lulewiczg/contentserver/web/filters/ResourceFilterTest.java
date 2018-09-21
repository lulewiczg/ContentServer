package com.github.lulewiczg.contentserver.web.filters;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.lulewiczg.contentserver.test.utils.ServletTestTemplate;
import com.github.lulewiczg.contentserver.utils.Constants;
import com.github.lulewiczg.contentserver.web.filters.ResourceFilter;

/**
 * Tests ResourceFilter.
 * 
 * @author lulewiczg
 */
public class ResourceFilterTest extends ServletTestTemplate {

    private ResourceFilter filter;

    private FilterChain chain = mock(FilterChain.class);

    /**
     * Sets up tested class.
     * 
     * @see com.github.lulewiczg.contentserver.test.utils.ServletTestTemplate#additionalBefore()
     */
    @Override
    protected void additionalBefore() throws ServletException {
        filter = initFilter(() -> new ResourceFilter());
    }

    @Test
    @DisplayName("Tries to access as guest with empty path")
    public void testAccessAsGuestEmptyPath() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(null);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verifyZeroInteractions(resourceUtil);
        verifyFilterOK();
    }

    @Test
    @DisplayName("Tries to access as guest")
    public void testAccessAsGuest() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(null);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(TEST);
        when(resourceUtil.hasReadAccess(eq(TEST), eq(null))).thenReturn(true);

        filter.doFilter(request, response, chain);

        verifyFilterOK();
    }

    @Test
    @DisplayName("Tries to access as guest to denied resource")
    public void testAccessAsGuestPermissionDenied() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(null);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(TEST);
        when(resourceUtil.hasReadAccess(eq(TEST), eq(null))).thenReturn(false);

        filter.doFilter(request, response, chain);

        verifyFilterError(403, String.format(Constants.Web.Errors.ACCESS_DENIED_TO, TEST));
    }

    @Test
    @DisplayName("Tries to access as guest")
    public void testAccessAsUser() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST2);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(TEST);
        when(resourceUtil.hasReadAccess(eq(TEST), eq(TEST2))).thenReturn(true);

        filter.doFilter(request, response, chain);

        verifyFilterOK();
    }

    @Test
    @DisplayName("Tries to access as guest to denied resource")
    public void testAccessAsUserPermissionDenied() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST2);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(TEST);
        when(resourceUtil.hasReadAccess(eq(TEST), eq(TEST2))).thenReturn(false);

        filter.doFilter(request, response, chain);

        verifyFilterError(403, String.format(Constants.Web.Errors.ACCESS_DENIED_TO, TEST));
    }

    @Test
    @DisplayName("Tries to access as admin")
    public void testAccessAsAdmin() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(Constants.ADMIN);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(TEST);
        when(resourceUtil.hasReadAccess(eq(TEST), eq(Constants.ADMIN))).thenReturn(true);

        filter.doFilter(request, response, chain);
        verifyFilterOK();
    }
}
