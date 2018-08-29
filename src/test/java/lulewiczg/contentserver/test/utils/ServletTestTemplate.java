package lulewiczg.contentserver.test.utils;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Matchers;

import lulewiczg.contentserver.permissions.ResourceHelper;
import lulewiczg.contentserver.utils.Constants;

/**
 * Test template for servlet tests.
 * 
 * @author lulewiczg
 */
public abstract class ServletTestTemplate {

    protected static final String TEST = "test";
    protected static final String TEST2 = "test2";
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected HttpSession session;
    protected PrintWriter writer;
    protected ResourceHelper helper;

    /**
     * Sets up tests
     *
     * @throws IOException
     *             the IOException
     * @throws ReflectiveOperationException
     *             the ReflectiveOperationException
     */
    @BeforeEach
    public void before() throws IOException, ReflectiveOperationException {
        helper = TestUtil.mockHelper();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        writer = mock(PrintWriter.class);

        when(request.getSession()).thenReturn(session);
        when(response.getWriter()).thenReturn(writer);
        additionalBefore();
    }

    /**
     * Additional logic to perform in @BeforeEach.
     * 
     * @throws IOException
     *             the IOException
     * @throws ReflectiveOperationException
     *             the ReflectiveOperationException
     */
    protected void additionalBefore() throws IOException, ReflectiveOperationException {
    }

    /**
     * Tests if response is 200 and empty.
     */
    protected void verifyOkEmptyResponse() {
        verifyZeroInteractions(writer);
        verify(response, never()).setContentType(anyString());
        verify(response, never()).setStatus(anyInt());
    }

    /**
     * Tests if response is 200 and empty and set content type to plain text.
     *
     */
    protected void verifyOkPlainTextEmpty() {
        verifyZeroInteractions(writer);
        verify(response).setContentType(Constants.Setting.PLAIN_TEXT);
        verify(response, never()).setStatus(anyInt());
    }

    /**
     * Tests if response is 200 and empty and set content type to plain text and
     * with given response.
     *
     * @param responsetxt
     *            response text
     */
    protected void verifyOk(String responsetxt) {
        verify(writer).write(responsetxt);
        verify(response).setContentType(Constants.Setting.PLAIN_TEXT);
        verify(response, never()).setStatus(anyInt());
    }

    /**
     * Tests if response is 200 and empty and set content type to JSON and with
     * given response.
     *
     * @param responsetxt
     *            response text
     */
    protected void verifyOkJSON(String responsetxt) {
        verify(writer).write(responsetxt);
        verify(response).setContentType(Constants.Setting.APPLICATION_JSON);
        verify(response, never()).setStatus(anyInt());
    }

    /**
     * Verifies if given error code is returned.
     *
     * @param code
     *            error code
     */
    protected void verifyError(int code) {
        verifyZeroInteractions(writer);
        verify(response).setContentType(Constants.Setting.PLAIN_TEXT);
        verify(response, never()).setStatus(Matchers.eq(code));
    }

    /**
     * Verifies if given error code is returned in filter.
     *
     * @param code
     *            error code
     */
    protected void verifyFilterError(int code) {
        verifyZeroInteractions(writer);
        verify(response, never()).setContentType(anyString());
        verify(response, never()).setStatus(Matchers.eq(code));
    }

    /**
     * Verifies if filter did not change anything.
     *
     * @param code
     *            error code
     * @throws UnsupportedEncodingException
     */
    protected void verifyFilterOK() throws UnsupportedEncodingException {
        verifyZeroInteractions(response);
        verify(request, never()).setAttribute(anyString(), any());
        verify(request, never()).setCharacterEncoding(anyString());
    }

}
