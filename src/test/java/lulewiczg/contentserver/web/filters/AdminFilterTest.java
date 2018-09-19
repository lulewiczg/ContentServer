package lulewiczg.contentserver.web.filters;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lulewiczg.contentserver.test.utils.ServletTestTemplate;
import lulewiczg.contentserver.utils.Constants;

/**
 * Tests AdminFilter.
 * 
 * @author lulewiczg
 */
public class AdminFilterTest extends ServletTestTemplate {

    private AdminFilter filter;

    private FilterChain chain = mock(FilterChain.class);

    /**
     * Sets up tested class.
     * 
     * @see lulewiczg.contentserver.test.utils.ServletTestTemplate#additionalBefore()
     */
    @Override
    protected void additionalBefore() throws ServletException {
        filter = initFilter(() -> new AdminFilter());
    }

    @Test
    @DisplayName("Tries to access as guest")
    public void testAccessAsGuest() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(null);
        when(request.getRequestURI()).thenReturn(TEST);
        verifyZeroInteractions(helper);

        filter.doFilter(request, response, chain);

        verifyFilterError(403, String.format(Constants.Web.Errors.ACCESS_DENIED_TO, TEST));
    }

    @Test
    @DisplayName("Tries to access as user")
    public void testAccessAsUser() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST);
        when(request.getRequestURI()).thenReturn(TEST);
        verifyZeroInteractions(helper);

        filter.doFilter(request, response, chain);

        verifyFilterError(403, String.format(Constants.Web.Errors.ACCESS_DENIED_TO, TEST));
    }

    @Test
    @DisplayName("Tries to access as admin")
    public void testAccessAsAdmin() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(Constants.ADMIN);
        when(request.getRequestURI()).thenReturn(TEST);
        verifyZeroInteractions(helper);

        filter.doFilter(request, response, chain);

        verifyFilterOK();
    }
}
