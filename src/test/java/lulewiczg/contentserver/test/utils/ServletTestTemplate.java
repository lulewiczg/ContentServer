package lulewiczg.contentserver.test.utils;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;

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
     * 
     * @throws IOException
     *             the IOException
     */
    protected void verifyOkEmptyResponse() throws IOException {
        verifyZeroInteractions(writer);
        requestOk();
        verify(response, never()).setContentType(anyString());
        responseOk();
    }

    /**
     * Tests if response is 200 and empty and set content type to plain text.
     * 
     * @throws IOException
     *             the IOException
     *
     */
    protected void verifyOkPlainTextEmpty() throws IOException {
        verifyZeroInteractions(writer);
        requestOk();
        verify(response).setContentType(Constants.Setting.PLAIN_TEXT);
        responseOk();
    }

    /**
     * Tests if response is 200 and empty and set content type to plain text and
     * with given response.
     *
     * @param responsetxt
     *            response text
     * @throws IOException
     *             the IOException
     */
    protected void verifyOk(String responsetxt) throws IOException {
        verify(writer).write(responsetxt);
        requestOk();
        verify(response).setContentType(Constants.Setting.PLAIN_TEXT);
        responseOk();
    }

    /**
     * Tests if response is 200 and empty and set content type to JSON and with
     * given response.
     *
     * @param responsetxt
     *            response text
     * @throws IOException
     *             the IOException
     */
    protected void verifyOkJSON(String responsetxt) throws IOException {
        verify(writer).write(responsetxt);
        requestOk();
        verify(response).setContentType(Constants.Setting.APPLICATION_JSON);
        responseOk();
    }

    /**
     * Verifies if given error code is returned.
     *
     * @param code
     *            error code
     * @throws IOException
     *             the IOException
     */
    protected void verifyError(int code, String error) throws IOException {
        verifyZeroInteractions(writer);
        requestOk();
        verify(response).setContentType(Constants.Setting.PLAIN_TEXT);
        verify(response, times(1)).sendError(eq(code), eq(error));
        responseError();
    }

    /**
     * Verifies if given error code is returned in filter.
     *
     * @param code
     *            error code
     * @throws IOException
     *             the IOException
     */
    protected void verifyFilterError(int code, String error) throws IOException {
        verifyZeroInteractions(writer);
        requestOk();
        verify(response, never()).setContentType(anyString());
        verify(response, times(1)).sendError(eq(code), eq(String.format(Constants.Web.Errors.ACCESS_DENIED_TO, TEST)));
        responseError();
    }

    /**
     * Verifies if filter did not change anything.
     *
     * @param code
     *            error code
     * @throws IOException
     *             the IOException
     */
    protected void verifyFilterOK() throws IOException {
        verifyZeroInteractions(response);
        requestOk();
        responseOk();
    }

    /**
     * Checks if response was not modified.
     * 
     * @throws IOException
     *             the IOException
     */
    private void responseError() throws IOException {
        verify(response, never()).addCookie(any());
        verify(response, never()).addDateHeader(anyString(), anyLong());
        verify(response, never()).addHeader(anyString(), anyString());
        verify(response, never()).addIntHeader(anyString(), anyInt());
        verify(response, never()).sendRedirect(anyString());
        verify(response, never()).sendError(anyInt());
        verify(response, never()).setBufferSize(anyInt());
        verify(response, never()).setCharacterEncoding(anyString());
        verify(response, never()).setContentLength(anyInt());
        verify(response, never()).setDateHeader(anyString(), anyLong());
        verify(response, never()).setIntHeader(anyString(), anyInt());
        verify(response, never()).setLocale(any());
        verify(response, never()).setStatus(anyInt());
    }

    /**
     * Checks if there was no additional changes to response than error.
     * 
     * @throws IOException
     */
    private void responseOk() throws IOException {
        responseError();
        verify(response, never()).sendError(anyInt(), anyString());

    }

    /**
     * Checks if request was not modified.
     * 
     * @throws IOException
     *             the IOException
     */
    private void requestOk() throws IOException {
        verify(request, never()).setAttribute(anyString(), any());
        verify(request, never()).setCharacterEncoding(anyString());
        verify(request, never()).removeAttribute(anyString());

    }
}
