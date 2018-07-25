package lulewiczg.contentserver.test.utils;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
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
import org.mockito.Matchers;

import lulewiczg.contentserver.permissions.ResourceHelper;
import lulewiczg.contentserver.utils.Constants;

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
        verify(response, times(1)).setContentType(Constants.Setting.PLAIN_TEXT);
        verify(response, never()).setStatus(anyInt());
    }

    /**
     * Tests if response is 200 and empty and set content type to plain text and with given response.
     *
     * @param responsetxt
     *            response text
     */
    protected void verifyOk(String responsetxt) {
        verify(writer, times(1)).write(responsetxt);
        verify(response, times(1)).setContentType(Constants.Setting.PLAIN_TEXT);
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
        verify(response, times(1)).setContentType(Constants.Setting.PLAIN_TEXT);
        verify(response, never()).setStatus(Matchers.eq(code));
    }

}
