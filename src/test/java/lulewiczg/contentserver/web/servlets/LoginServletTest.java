package lulewiczg.contentserver.web.servlets;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.AuthenticationException;
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

public class LoginServletTest {

    private static final String TEST = "test";
    private static final Object TEST2 = "test2";
    private LoginServlet servlet = new LoginServlet();
    private static ResourceHelper helper;

    @BeforeEach
    public void setup() throws ReflectiveOperationException {
        helper = mock(ResourceHelper.class);
        TestUtil.swapHelper(helper);
    }

    @Test
    @DisplayName("Gets current user when not logged")
    public void testGetUserNotLogged() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(Constants.Web.USER)).thenReturn(null);
        when(response.getWriter()).thenReturn(writer);
        servlet.doGet(request, response);

        verifyZeroInteractions(writer);
        verify(response, times(1)).setContentType(Constants.Setting.PLAIN_TEXT);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    @DisplayName("Gets current user")
    public void testGetUser() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        verify(writer, times(1)).write(TEST);
        verify(response, times(1)).setContentType(Constants.Setting.PLAIN_TEXT);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    @DisplayName("Logs in without login")
    public void testLoginwithoutLogin() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(request.getSession()).thenReturn(session);
        when(request.getParameter(Constants.Web.LOGIN)).thenReturn(null);
        when(request.getParameter(Constants.Web.PASSWORD)).thenReturn(TEST);
        when(session.getAttribute(Constants.Web.USER)).thenReturn(null);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);

        verifyZeroInteractions(writer);
        verify(response, times(1)).setContentType(Constants.Setting.PLAIN_TEXT);
        verify(response, times(1)).sendError(eq(401), anyString());
    }

    @Test
    @DisplayName("Logs in without password")
    public void testLoginwithoutPassword() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(request.getSession()).thenReturn(session);
        when(request.getParameter(Constants.Web.LOGIN)).thenReturn(TEST);
        when(request.getParameter(Constants.Web.PASSWORD)).thenReturn(null);
        when(session.getAttribute(Constants.Web.USER)).thenReturn(null);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);

        verifyZeroInteractions(writer);
        verify(response, times(1)).setContentType(Constants.Setting.PLAIN_TEXT);
        verify(response, times(1)).sendError(eq(401), anyString());
    }

    @Test
    @DisplayName("Logs in when already logged")
    public void testLoginWhenLogged() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(request.getSession()).thenReturn(session);
        when(request.getParameter(Constants.Web.LOGIN)).thenReturn(TEST);
        when(request.getParameter(Constants.Web.PASSWORD)).thenReturn(TEST);
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);

        verifyZeroInteractions(writer);
        verify(response, times(1)).setContentType(Constants.Setting.PLAIN_TEXT);
        verify(response, times(1)).sendError(eq(403), anyString());
    }

    @Test
    @DisplayName("Logs in when already logged as another user")
    public void testLoginWhenLoggedAsAnother() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(request.getSession()).thenReturn(session);
        when(request.getParameter(Constants.Web.LOGIN)).thenReturn(TEST);
        when(request.getParameter(Constants.Web.PASSWORD)).thenReturn(TEST);
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST2);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);

        verifyZeroInteractions(writer);
        verify(response, times(1)).setContentType(Constants.Setting.PLAIN_TEXT);
        verify(response, times(1)).sendError(eq(403), anyString());
    }

    @Test
    @DisplayName("Logs in with invalid credentials")
    public void testLoginWithInvalidCredenitals() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(request.getSession()).thenReturn(session);
        when(request.getParameter(Constants.Web.LOGIN)).thenReturn(TEST);
        when(request.getParameter(Constants.Web.PASSWORD)).thenReturn(TEST);
        when(session.getAttribute(Constants.Web.USER)).thenReturn(null);
        when(response.getWriter()).thenReturn(writer);
        doThrow(AuthenticationException.class).when(helper).login(eq(TEST), eq(TEST));

        servlet.doPost(request, response);

        verifyZeroInteractions(writer);
        verify(response, times(1)).setContentType(Constants.Setting.PLAIN_TEXT);
        verify(response, times(1)).sendError(eq(401), anyString());
    }

    @Test
    @DisplayName("Logs in")
    public void testLogin() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(request.getSession()).thenReturn(session);
        when(request.getParameter(Constants.Web.LOGIN)).thenReturn(TEST);
        when(request.getParameter(Constants.Web.PASSWORD)).thenReturn(TEST);
        when(session.getAttribute(Constants.Web.USER)).thenReturn(null);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);

        verify(response, never()).setStatus(anyInt());
        verify(writer, times(1)).write(TEST);
    }

}
