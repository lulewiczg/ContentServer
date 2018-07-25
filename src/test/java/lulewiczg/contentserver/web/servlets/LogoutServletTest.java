package lulewiczg.contentserver.web.servlets;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lulewiczg.contentserver.permissions.ResourceHelper;
import lulewiczg.contentserver.test.utils.TestUtil;
import lulewiczg.contentserver.utils.Constants;

public class LogoutServletTest {

    private static final String TEST = "test";
    private LogoutServlet servlet = new LogoutServlet();
    private static ResourceHelper helper;

    @BeforeEach
    public void setup() throws ReflectiveOperationException {
        helper = mock(ResourceHelper.class);
        TestUtil.swapHelper(helper);
    }

    @Test
    @DisplayName("Logs out")
    public void testLogOut() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST);
        when(response.getWriter()).thenReturn(writer);
        servlet.doGet(request, response);

        verifyZeroInteractions(writer);
        verify(response, never()).setContentType(anyString());
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    @DisplayName("Logs out when not logged in")
    public void testLogOutNotLogged() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(Constants.Web.USER)).thenReturn(null);
        when(response.getWriter()).thenReturn(writer);
        servlet.doGet(request, response);

        verifyZeroInteractions(writer);
        verify(response, never()).setContentType(anyString());
        verify(response, never()).setStatus(anyInt());
    }
}
