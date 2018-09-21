package com.github.lulewiczg.contentserver.web.servlets;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.lulewiczg.contentserver.test.utils.ServletTestTemplate;
import com.github.lulewiczg.contentserver.utils.Constants;
import com.github.lulewiczg.contentserver.web.servlets.LogoutServlet;

/**
 * Tests LogoutServlet.
 * 
 * @author lulewiczg
 */
public class LogoutServletTest extends ServletTestTemplate {

    private LogoutServlet servlet = spy(LogoutServlet.class);

    /**
     * Sets up tested object.
     * 
     * @see com.github.lulewiczg.contentserver.test.utils.ServletTestTemplate#additionalBefore()
     */
    @Override
    protected void additionalBefore() throws Exception {
        setupServlet(servlet);
    }

    @Test
    @DisplayName("Logs out")
    public void testLogOut() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST);

        servlet.doGet(request, response);

        verifyOkEmptyResponse();
    }

    @Test
    @DisplayName("Logs out when not logged in")
    public void testLogOutNotLogged() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(null);

        servlet.doGet(request, response);

        verifyOkEmptyResponse();
    }
}
