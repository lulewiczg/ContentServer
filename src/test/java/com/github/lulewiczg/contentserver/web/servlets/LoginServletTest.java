package com.github.lulewiczg.contentserver.web.servlets;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.naming.AuthenticationException;
import javax.servlet.ServletException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.lulewiczg.contentserver.test.utils.ServletTestTemplate;
import com.github.lulewiczg.contentserver.utils.Constants;
import com.github.lulewiczg.contentserver.web.servlets.LoginServlet;

/**
 * Tests LoginServlet.
 * 
 * @author lulewiczg
 */
public class LoginServletTest extends ServletTestTemplate {

    private LoginServlet servlet = spy(LoginServlet.class);

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
    @DisplayName("Gets current user when not logged")
    public void testGetUserNotLogged() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(null);

        servlet.doGet(request, response);

        verifyOkPlainTextEmpty();
    }

    @Test
    @DisplayName("Gets current user")
    public void testGetUser() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST);

        servlet.doGet(request, response);

        verifyOk(TEST);
    }

    @Test
    @DisplayName("Logs in without login")
    public void testLoginwithoutLogin() throws IOException, ServletException {
        when(request.getParameter(Constants.Web.LOGIN)).thenReturn(null);
        when(request.getParameter(Constants.Web.PASSWORD)).thenReturn(TEST);
        when(session.getAttribute(Constants.Web.USER)).thenReturn(null);

        servlet.doPost(request, response);

        verifyError(401, Constants.Web.Errors.INVALID_CREDENTIALS);
        verify(session, never()).setAttribute(anyString(), anyString());
    }

    @Test
    @DisplayName("Logs in without password")
    public void testLoginwithoutPassword() throws IOException, ServletException {
        when(request.getParameter(Constants.Web.LOGIN)).thenReturn(TEST);
        when(request.getParameter(Constants.Web.PASSWORD)).thenReturn(null);
        when(session.getAttribute(Constants.Web.USER)).thenReturn(null);

        servlet.doPost(request, response);

        verifyError(401, Constants.Web.Errors.INVALID_CREDENTIALS);
        verify(session, never()).setAttribute(anyString(), anyString());
    }

    @Test
    @DisplayName("Logs in when already logged")
    public void testLoginWhenLogged() throws IOException, ServletException {
        when(request.getParameter(Constants.Web.LOGIN)).thenReturn(TEST);
        when(request.getParameter(Constants.Web.PASSWORD)).thenReturn(TEST);
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST);

        servlet.doPost(request, response);

        verifyError(403, Constants.Web.Errors.USER_ALREADY_LOGGED);
        verify(session, never()).setAttribute(anyString(), anyString());
    }

    @Test
    @DisplayName("Logs in when already logged as another user")
    public void testLoginWhenLoggedAsAnother() throws IOException, ServletException {
        when(request.getParameter(Constants.Web.LOGIN)).thenReturn(TEST);
        when(request.getParameter(Constants.Web.PASSWORD)).thenReturn(TEST);
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST2);

        servlet.doPost(request, response);

        verifyError(403, Constants.Web.Errors.USER_ALREADY_LOGGED);
        verify(session, never()).setAttribute(anyString(), anyString());
    }

    @Test
    @DisplayName("Logs in with invalid credentials")
    public void testLoginWithInvalidCredenitals() throws IOException, ServletException {
        when(request.getParameter(Constants.Web.LOGIN)).thenReturn(TEST);
        when(request.getParameter(Constants.Web.PASSWORD)).thenReturn(TEST);
        when(session.getAttribute(Constants.Web.USER)).thenReturn(null);
        doThrow(AuthenticationException.class).when(resourceUtil).login(eq(TEST), eq(TEST), eq(session));

        servlet.doPost(request, response);

        verifyError(401, Constants.Web.Errors.INVALID_CREDENTIALS);
        verify(session, never()).setAttribute(anyString(), anyString());
    }

    @Test
    @DisplayName("Logs in")
    public void testLogin() throws IOException, ServletException {
        when(request.getParameter(Constants.Web.LOGIN)).thenReturn(TEST);
        when(request.getParameter(Constants.Web.PASSWORD)).thenReturn(TEST);
        when(session.getAttribute(Constants.Web.USER)).thenReturn(null);

        servlet.doPost(request, response);

        verifyOk(TEST);
    }

}
