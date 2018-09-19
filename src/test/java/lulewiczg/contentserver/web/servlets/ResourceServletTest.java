package lulewiczg.contentserver.web.servlets;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lulewiczg.contentserver.permissions.ResourceHelper;
import lulewiczg.contentserver.test.utils.ServletTestTemplate;
import lulewiczg.contentserver.test.utils.TestUtil;
import lulewiczg.contentserver.utils.Constants;
import lulewiczg.contentserver.utils.json.JSONModel;
import lulewiczg.contentserver.utils.models.Dir;

/**
 * Tests ResourceServlet.
 *
 * @author lulewiczg
 */
public class ResourceServletTest extends ServletTestTemplate {

    private static final String FOLDER1_11 = "folder1/folder11.txt";
    private static final String FOLDER3_11 = "folder3/folder11.txt";
    private static final String FILE11 = "folder11.txt";
    private ResourceServlet servlet = spy(ResourceServlet.class);
    private static String base;

    /**
     * Sets up tested object.
     * 
     * @see lulewiczg.contentserver.test.utils.ServletTestTemplate#additionalBefore()
     */
    @Override
    protected void additionalBefore() throws Exception {
        setupServlet(servlet);
    }

    /**
     * Prepares data.
     *
     * @throws IOException
     *             the IOException
     */
    @BeforeAll
    public static void setup() throws IOException {
        base = ResourceHelper.normalizePath(new File(TestUtil.LOC + "structure").getCanonicalPath() + Constants.SEP);
    }

    @Test
    @DisplayName("Empty path")
    public void testEmptyPath() throws IOException, ServletException {
        when(request.getParameter(Constants.Web.PATH)).thenReturn(null);

        servlet.doGet(request, response);

        verifyZeroInteractions(helper);
        verifyError(404, String.format(Constants.Web.Errors.NOT_FOUND, "null"));
    }

    @Test
    @DisplayName("Missing path")
    public void testNotExistingPath() throws IOException, ServletException {
        when(request.getParameter(Constants.Web.PATH)).thenReturn(base + "missingFolder");

        servlet.doGet(request, response);

        verifyZeroInteractions(helper);
        verifyError(404, String.format(Constants.Web.Errors.NOT_FOUND, base + "missingFolder"));
    }

    @Test
    @DisplayName("Directory listing")
    public void testListDir() throws IOException, ServletException {
        when(request.getParameter(Constants.Web.PATH)).thenReturn(base + "folder1");
        List<Dir> dirs = Arrays.asList(new Dir("folder1/", 0, base + "folder1/" + "folder1", false),
                new Dir("folder2/", 0, base + "folder1/" + "folder2", false),
                new Dir("folder1.txt", 0, base + "folder1/" + "folder1.txt", true),
                new Dir(FILE11, 4, base + "folder1/" + FILE11, true));
        servlet.doGet(request, response);

        verifyZeroInteractions(helper);
        String json = JSONModel.toJSONArray(dirs);
        verifyOkJSON(json);
    }

    @Test
    @DisplayName("Display small file using small buffer")
    public void testShowSmallFileSmallBuffer() throws IOException, ServletException {
        int buff = 1;
        int len = 4;
        MockOutputStream stream = setupRequest(buff, len, FOLDER1_11);

        servlet.doGet(request, response);

        verifyHeaders(FILE11, Constants.Setting.PLAIN_TEXT, buff, len, 0, len - 1);
        Assertions.assertEquals(TEST, new String(stream.get()));
    }

    @Test
    @DisplayName("Display small file using big buffer")
    public void testShowSmallFileBigBuffer() throws IOException, ServletException {
        int len = 4;
        int buff = 1024 * 1024 * 1024;
        MockOutputStream stream = setupRequest(buff, len, FOLDER1_11);

        servlet.doGet(request, response);

        verifyHeaders(FILE11, Constants.Setting.PLAIN_TEXT, buff, len, 0, len - 1);
        Assertions.assertEquals(TEST, stream.get());
    }

    @Test
    @DisplayName("Display big file using small buffer")
    public void testShowBigFileSmallBuffer() throws IOException, ServletException {
        int len = 77280;
        int buff = 1;
        MockOutputStream stream = setupRequest(buff, len, FOLDER3_11);

        servlet.doGet(request, response);

        verifyHeaders(FILE11, Constants.Setting.PLAIN_TEXT, buff, len, 0, len - 1);
        String expected = Files.readAllLines(Paths.get(base + FOLDER3_11)).get(0);
        Assertions.assertEquals(expected, stream.get());
    }

    @Test
    @DisplayName("Display big file using big buffer")
    public void testShowBigFileBigBuffer() throws IOException, ServletException {
        int len = 77280;
        int buff = 10240;
        MockOutputStream stream = setupRequest(buff, len, FOLDER3_11);

        servlet.doGet(request, response);

        verifyHeaders(FILE11, Constants.Setting.PLAIN_TEXT, buff, len, 0, len - 1);
        String expected = Files.readAllLines(Paths.get(base + FOLDER3_11)).get(0);
        Assertions.assertEquals(expected, stream.get());
    }

    @Test
    @DisplayName("Force download")
    public void testForceDownload() throws IOException, ServletException {
        int len = 4;
        int buff = 4;
        MockOutputStream stream = setupRequest(buff, len, FOLDER1_11, true);

        servlet.doGet(request, response);

        verify(helper, never()).getMIME(anyString());
        verifyHeaders(FILE11, Constants.Web.Headers.APPLICATION_FORCE_DOWNLOAD, buff, len, 0, len - 1);
        Assertions.assertEquals(TEST, stream.get());
    }

    @Test
    @DisplayName("Content range")
    public void testContentRange() throws IOException, ServletException {
        int len = 77280;
        int buff = 1024;
        int start = 0;
        int end = 19;
        MockOutputStream stream = setupRangeRequest(len, buff, start, end);

        servlet.doGet(request, response);

        verifyHeaders(FILE11, Constants.Setting.PLAIN_TEXT, buff, len, start, end);
        Assertions.assertEquals("testtesttesttesttest", stream.get());
    }

    @Test
    @DisplayName("Content range in the middle")
    public void testContentRangeMiddle() throws IOException, ServletException {
        int len = 77280;
        int buff = 1024;
        int start = 1000;
        int end = 1010;
        MockOutputStream stream = setupRangeRequest(len, buff, start, end);

        servlet.doGet(request, response);

        verifyHeaders(FILE11, Constants.Setting.PLAIN_TEXT, buff, len, start, end);
        Assertions.assertEquals("testtesttes", stream.get());
    }

    @Test
    @DisplayName("Content range single byte")
    public void testContentRangeSingleByte() throws IOException, ServletException {
        int len = 77280;
        int buff = 1024;
        int start = 1000;
        int end = 1000;
        MockOutputStream stream = setupRangeRequest(len, buff, start, end);

        servlet.doGet(request, response);

        verifyHeaders(FILE11, Constants.Setting.PLAIN_TEXT, buff, len, start, end);
        Assertions.assertEquals("t", stream.get());
    }

    @Test
    @DisplayName("Content range in the end")
    public void testContentRangeEnd() throws IOException, ServletException {
        int len = 77280;
        int buff = 1024;
        int start = 77260;
        int end = 77279;
        MockOutputStream stream = setupRangeRequest(len, buff, start, end);

        servlet.doGet(request, response);

        verifyHeaders(FILE11, Constants.Setting.PLAIN_TEXT, buff, len, start, end);
        Assertions.assertEquals("testtesttesttesttest", stream.get());
    }

    @Test
    @DisplayName("Opened content range")
    public void testContentRangeOpen() throws IOException, ServletException {
        int len = 77280;
        int buff = 1024;
        int end = 15;
        MockOutputStream stream = setupRangeRequest(len, buff, null, end);

        servlet.doGet(request, response);

        verifyHeaders(FILE11, Constants.Setting.PLAIN_TEXT, buff, len, 0, end);
        Assertions.assertEquals("testtesttesttest", stream.get());
    }

    @Test
    @DisplayName("Opened end content range")
    public void testContentRangeOpenEnd() throws IOException, ServletException {
        int len = 77280;
        int buff = 1024;
        int start = 77275;
        MockOutputStream stream = setupRangeRequest(len, buff, start, null);

        servlet.doGet(request, response);

        verifyHeaders(FILE11, Constants.Setting.PLAIN_TEXT, buff, len, start, len - 1);
        Assertions.assertEquals("ttest", stream.get());
    }

    @Test
    @DisplayName("Opened content range single byte")
    public void testContentRangeOpenSingleByte() throws IOException, ServletException {
        int len = 77280;
        int buff = 1024;
        int end = 0;
        MockOutputStream stream = setupRangeRequest(len, buff, null, end);

        servlet.doGet(request, response);

        verifyHeaders(FILE11, Constants.Setting.PLAIN_TEXT, buff, len, 0, end);
        Assertions.assertEquals("t", stream.get());
    }

    @Test
    @DisplayName("Opened end content range single byte")
    public void testContentRangeOpenEndSingleByte() throws IOException, ServletException {
        int len = 77280;
        int buff = 1024;
        int start = 77279;
        MockOutputStream stream = setupRangeRequest(len, buff, start, null);

        servlet.doGet(request, response);

        verifyHeaders(FILE11, Constants.Setting.PLAIN_TEXT, buff, len, start, len - 1);
        Assertions.assertEquals("t", stream.get());
    }

    @Test
    @DisplayName("Opened end content range single byte")
    public void testContentRangeFullOpen() throws IOException, ServletException {
        int len = 77280;
        int buff = 1024;
        MockOutputStream stream = setupRangeRequest(len, buff, null, null);

        servlet.doGet(request, response);

        verifyHeaders(FILE11, Constants.Setting.PLAIN_TEXT, buff, len, 0, len - 1);
        String expected = Files.readAllLines(Paths.get(base + FOLDER3_11)).get(0);
        Assertions.assertEquals(expected, stream.get());
    }

    @Test
    @DisplayName("Opened end content range single byte")
    public void testOversizedRange() throws IOException, ServletException {
        int len = 77280;
        int buff = 1024;
        int start = 77270;
        int end = 77281;
        MockOutputStream stream = setupRangeRequest(len, buff, start, end);

        servlet.doGet(request, response);

        verifyHeaders(FILE11, Constants.Setting.PLAIN_TEXT, buff, len, start, len - 1);
        Assertions.assertEquals("sttesttest", stream.get());
    }

    /**
     * Verify standard headers
     *
     * @param filename
     *            filename
     * @param len
     *            file length
     * @param start
     *            start
     * @param end
     *            end
     */
    private void verifyHeaders(String filename, String contenttype, int buffsize, int len, int start, int end) {
        verify(response).setBufferSize(buffsize);
        verifyZeroInteractions(writer);
        verify(response).setHeader(Constants.Web.Headers.CONTENT_DISPOSITION,
                String.format("inline;filename=\"%s\"", filename));
        verify(response).setHeader(Constants.Web.Headers.ACCEPT_RANGES, "bytes");
        verify(response).setHeader(Constants.Web.Headers.CONTENT_RANGE,
                String.format("bytes %s-%s/%s", start, end, len));
        verify(response).setHeader(Constants.Web.Headers.CONTENT_LENGTH, String.format("%s", end - start + 1));
        verify(response).setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        verify(response).setContentType(contenttype);
    }

    /**
     * Builds range header.
     * 
     * @param start
     *            range start
     * @param end
     *            range end
     * @return range header
     */
    private String buildRange(Integer start, Integer end) {
        String str1 = start != null ? start.toString() : "";
        String str2 = end != null ? end.toString() : "";
        return String.format("bytes=%s-%s", str1, str2);
    }

    /**
     * Builds range request and returns mocked output stream.
     * 
     * @param len
     *            file length
     * @param buff
     *            buffer size
     * @param start
     *            range start
     * @param end
     *            range end
     * @return mocked output stream
     * @throws IOException
     *             the IOException
     */
    private MockOutputStream setupRangeRequest(int len, int buff, Integer start, Integer end) throws IOException {
        when(request.getParameter(Constants.Web.PATH)).thenReturn(base + FOLDER3_11);
        when(request.getParameter(Constants.Web.DOWNLOAD)).thenReturn(null);
        when(request.getHeader(Constants.Web.Headers.RANGE)).thenReturn(buildRange(start, end));
        when(helper.getMIME(FILE11)).thenReturn(Constants.Setting.PLAIN_TEXT);
        when(helper.getBufferSize()).thenReturn(buff);
        MockOutputStream stream = new MockOutputStream(len);
        when(response.getOutputStream()).thenReturn(stream);
        return stream;
    }

    /**
     * Builds standard request and returns mocked output stream.
     * 
     * @param buffsize
     *            buffer size
     * @param len
     *            file length
     * @param path
     *            file path
     * @param download
     *            download
     * @return mocked output stream
     * @throws IOException
     *             the IOException
     */
    private MockOutputStream setupRequest(int buffsize, int len, String path, Boolean download) throws IOException {
        when(request.getParameter(Constants.Web.PATH)).thenReturn(base + path);
        when(request.getParameter(Constants.Web.DOWNLOAD)).thenReturn(download != null ? download + "" : null);
        when(request.getHeader(Constants.Web.Headers.RANGE)).thenReturn(null);
        when(helper.getBufferSize()).thenReturn(buffsize);
        when(helper.getMIME(FILE11)).thenReturn(Constants.Setting.PLAIN_TEXT);
        MockOutputStream stream = new MockOutputStream(len);
        when(response.getOutputStream()).thenReturn(stream);
        return stream;
    }

    /**
     * Builds standard request and returns mocked output stream.
     * 
     * @param buffsize
     *            buffer size
     * @param len
     *            file length
     * @param path
     *            file path
     * @return mocked output stream
     * @throws IOException
     *             the IOException
     */
    private MockOutputStream setupRequest(int buffsize, int len, String path) throws IOException {
        return setupRequest(buffsize, len, path, null);
    }

    /**
     * Mock output stream to get response.
     */
    private class MockOutputStream extends ServletOutputStream {
        private ByteBuffer buffer;

        @Override
        public void write(int b) throws IOException {
            buffer.put((byte) b);
        }

        private String get() {
            return new String(buffer.array(), 0, buffer.position());
        }

        private MockOutputStream(int size) {
            buffer = ByteBuffer.allocate(size);
        }
    }
}
