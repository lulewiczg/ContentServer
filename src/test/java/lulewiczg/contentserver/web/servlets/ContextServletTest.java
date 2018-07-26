package lulewiczg.contentserver.web.servlets;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lulewiczg.contentserver.test.utils.ServletTestTemplate;
import lulewiczg.contentserver.utils.Constants;

/**
 * Tests ContextServlet.
 * 
 * @author lulewiczg
 */
public class ContextServletTest extends ServletTestTemplate {

    private static final String TEST_PATH = "/a/b c d e f/1 2/3/";
    private ContextServlet servlet = spy(new ContextServlet());

    @Test
    @DisplayName("Gets context path without ending separator")
    public void testGetContextPathWithoutSeparator() throws IOException, ServletException {
        ServletContext context = mock(ServletContext.class);
        doReturn(context).when(servlet).getServletContext();

        when(context.getRealPath(Constants.SEP)).thenReturn(TEST_PATH.substring(0, TEST_PATH.length() - 1));
        servlet.doGet(request, response);

        verifyOk(TEST_PATH);
    }

    @Test
    @DisplayName("Gets context path")
    public void testGetContextPath() throws IOException, ServletException {
        ServletContext context = mock(ServletContext.class);
        doReturn(context).when(servlet).getServletContext();

        when(context.getRealPath(Constants.SEP)).thenReturn(TEST_PATH);
        servlet.doGet(request, response);

        verifyOk(TEST_PATH);
    }
}
