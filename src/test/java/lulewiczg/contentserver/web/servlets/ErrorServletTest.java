package lulewiczg.contentserver.web.servlets;

import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lulewiczg.contentserver.test.utils.ServletTestTemplate;

/**
 * Tests ErrorServlet
 * 
 * @author lulewiczg
 */
public class ErrorServletTest extends ServletTestTemplate {

    private ErrorServlet servlet = new ErrorServlet();

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

    /**
     * Builds error message
     */
    private String buildMsg(int code, String servletName, String error) {
        return String.format("%s error from %s: %s", code, servletName, error);
    }
}
