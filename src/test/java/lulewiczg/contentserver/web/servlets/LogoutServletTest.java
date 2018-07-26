package lulewiczg.contentserver.web.servlets;

import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lulewiczg.contentserver.test.utils.ServletTestTemplate;
import lulewiczg.contentserver.utils.Constants;

/**
 * Tests LogoutServlet.
 * 
 * @author lulewiczg
 */
public class LogoutServletTest extends ServletTestTemplate {

    private LogoutServlet servlet = new LogoutServlet();

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
