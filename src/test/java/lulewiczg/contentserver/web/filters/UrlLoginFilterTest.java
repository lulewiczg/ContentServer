package lulewiczg.contentserver.web.filters;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Base64;

import javax.security.sasl.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lulewiczg.contentserver.test.utils.ServletTestTemplate;
import lulewiczg.contentserver.utils.Constants;
import lulewiczg.contentserver.utils.models.User;

/**
 * Tests UrlLoginFilter.
 * 
 * @author lulewiczg
 */
public class UrlLoginFilterTest extends ServletTestTemplate {

    private static final String CREDS = "test:test2";

    private static final String AUTH = "Basic " + Base64.getEncoder().encodeToString(CREDS.getBytes());

    private UrlLoginFilter filter;

    private FilterChain chain = mock(FilterChain.class);

    /**
     * Sets up tested class.
     * 
     * @see lulewiczg.contentserver.test.utils.ServletTestTemplate#additionalBefore()
     */
    @Override
    protected void additionalBefore() throws ServletException {
        filter = initFilter(() -> new UrlLoginFilter());
    }

    @Test
    @DisplayName("Tries to login")
    public void testLogin() throws IOException, ServletException {
        when(request.getHeader(Constants.Web.Headers.AUTHORIZATION)).thenReturn(AUTH);
        when(session.getAttribute(Constants.Web.USER)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(helper, times(1)).login(eq(TEST), eq(TEST2), eq(session));
        verifyFilterOK();
    }

    @Test
    @DisplayName("Tries to login with invalid credentials")
    public void testLoginInvalidCredentials() throws IOException, ServletException {
        when(request.getHeader(Constants.Web.Headers.AUTHORIZATION)).thenReturn(AUTH);
        when(session.getAttribute(Constants.Web.USER)).thenReturn(null);
        doThrow(new AuthenticationException("Invalid login or password")).when(helper).login(eq(TEST), eq(TEST2),
                eq(session));

        Assertions.assertThrows(AuthenticationException.class, () -> filter.doFilter(request, response, chain));

        verify(helper, times(1)).login(eq(TEST), eq(TEST2), eq(session));
        verifyFilterOK();
    }

    @Test
    @DisplayName("Do not login")
    public void testDoNotLogin() throws IOException, ServletException {
        when(request.getHeader(Constants.Web.Headers.AUTHORIZATION)).thenReturn(null);
        when(session.getAttribute(Constants.Web.USER)).thenReturn(new User(null));

        filter.doFilter(request, response, chain);

        verifyZeroInteractions(helper);
        verifyFilterOK();
    }

    @Test
    @DisplayName("Relogin")
    public void testRelogin() throws IOException, ServletException {
        when(request.getHeader(Constants.Web.Headers.AUTHORIZATION)).thenReturn(AUTH);
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST);

        filter.doFilter(request, response, chain);

        verify(helper, times(1)).login(eq(TEST), eq(TEST2), eq(session));
        verifyFilterOK();
    }

    @Test
    @DisplayName("Login with unknown auth")
    public void testUnknownAuth() throws IOException, ServletException {
        when(request.getHeader(Constants.Web.Headers.AUTHORIZATION)).thenReturn(TEST);
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST);

        filter.doFilter(request, response, chain);

        verifyZeroInteractions(helper);
        verifyFilterOK();
    }
}
