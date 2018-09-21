package com.github.lulewiczg.contentserver.web.servlets;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.lulewiczg.contentserver.test.utils.ServletTestTemplate;
import com.github.lulewiczg.contentserver.web.servlets.ErrorServlet;

/**
 * Tests ErrorServlet
 * 
 * @author lulewiczg
 */
public class ErrorServletTest extends ServletTestTemplate {

    private ErrorServlet servlet = spy(ErrorServlet.class);

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
    @DisplayName("Get error message")
    public void testErrorMsg() throws IOException, ServletException {
        when(request.getAttribute(ErrorServlet.ERROR_CODE)).thenReturn(404);
        when(request.getAttribute(ErrorServlet.SERVLET_NAME)).thenReturn(TEST);
        when(request.getAttribute(ErrorServlet.ERROR_MSG)).thenReturn(TEST2);

        servlet.doGet(request, response);

        verifyOk(buildMsg(404, TEST, TEST2));
    }

    @Test
    @DisplayName("Get error message when exception thrown")
    public void testErrorMsgExc() throws IOException, ServletException {
        when(request.getAttribute(ErrorServlet.ERROR_CODE)).thenReturn(404);
        when(request.getAttribute(ErrorServlet.SERVLET_NAME)).thenReturn(TEST);
        when(request.getAttribute(ErrorServlet.ERROR_EXC)).thenReturn(new Exception(TEST2));

        servlet.doGet(request, response);

        verifyOk(buildMsg(404, TEST, TEST2 + "\n" + new Exception(TEST2).toString()));
    }

    @Test
    @DisplayName("Get error message when no message")
    public void testErrorMsgNoMsg() throws IOException, ServletException {
        when(request.getAttribute(ErrorServlet.ERROR_CODE)).thenReturn(404);
        when(request.getAttribute(ErrorServlet.SERVLET_NAME)).thenReturn(TEST);

        servlet.doGet(request, response);

        verifyOk(buildMsg(404, TEST, ""));
    }

    @Test
    @DisplayName("Get error message when no serlet name")
    public void testErrorMsgNoServletName() throws IOException, ServletException {
        when(request.getAttribute(ErrorServlet.ERROR_CODE)).thenReturn(404);
        when(request.getAttribute(ErrorServlet.ERROR_MSG)).thenReturn(TEST2);

        servlet.doGet(request, response);

        verifyOk(buildMsg(404, "", TEST2));
    }

    @Test
    @DisplayName("Get error message when exception thrown")
    public void testErrorException() throws IOException, ServletException {
        when(request.getAttribute(ErrorServlet.ERROR_CODE)).thenReturn(500);
        when(request.getAttribute(ErrorServlet.ERROR_MSG)).thenReturn("");
        NullPointerException ex = new NullPointerException("test");
        when(request.getAttribute(ErrorServlet.ERROR_EXC)).thenReturn(ex);

        servlet.doGet(request, response);

        verifyOk(buildMsg(500, "", ex.getMessage() + "\n" + ex.toString()));
    }

    /**
     * Builds error message
     */
    private String buildMsg(int code, String servletName, String error) {
        return String.format("%s error from %s: %s", code, servletName, error);
    }
}
