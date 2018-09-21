package com.github.lulewiczg.contentserver.web.servlets;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.lulewiczg.contentserver.test.utils.ServletTestTemplate;
import com.github.lulewiczg.contentserver.utils.Constants;
import com.github.lulewiczg.contentserver.web.servlets.ContextServlet;

/**
 * Tests ContextServlet.
 * 
 * @author lulewiczg
 */
public class ContextServletTest extends ServletTestTemplate {

    private static final String TEST_PATH = "/a/b c d e f/1 2/3/";
    public ContextServlet servlet = spy(new ContextServlet());

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
    @DisplayName("Gets context path without ending separator")
    public void testGetContextPathWithoutSeparator() throws IOException, ServletException {
        doReturn(context).when(servlet).getServletContext();

        when(context.getRealPath(Constants.SEP)).thenReturn(TEST_PATH.substring(0, TEST_PATH.length() - 1));
        servlet.doGet(request, response);

        verifyOk(TEST_PATH);
    }

    @Test
    @DisplayName("Gets context path")
    public void testGetContextPath() throws IOException, ServletException {
        doReturn(context).when(servlet).getServletContext();

        when(context.getRealPath(Constants.SEP)).thenReturn(TEST_PATH);
        servlet.doGet(request, response);

        verifyOk(TEST_PATH);
    }
}
