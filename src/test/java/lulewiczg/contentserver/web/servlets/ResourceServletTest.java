package lulewiczg.contentserver.web.servlets;

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
import org.mockito.Mockito;

import lulewiczg.contentserver.permissions.ResourceHelper;
import lulewiczg.contentserver.test.utils.ServletTestTemplate;
import lulewiczg.contentserver.utils.Constants;
import lulewiczg.contentserver.utils.json.JSONModel;
import lulewiczg.contentserver.utils.models.Dir;

/**
 * Tests ResourceServlet.
 *
 * @author lulewiczg
 */
public class ResourceServletTest extends ServletTestTemplate {

    private static final String FILE1 = "folder11.txt";
    private ResourceServlet servlet = new ResourceServlet();
    private static String base;

    /**
     * Prepares data.
     *
     * @throws IOException
     *             the IOException
     */
    @BeforeAll
    public static void setup() throws IOException {
        base = ResourceHelper.normalizePath(new File("src/test/resources/structure").getCanonicalPath() + Constants.SEP);
    }

    @Test
    @DisplayName("Empty path")
    public void testEmptyPath() throws IOException, ServletException {
        when(request.getParameter(Constants.Web.PATH)).thenReturn(null);

        servlet.doGet(request, response);

        Mockito.verifyZeroInteractions(helper);
        verifyError(404);
    }

    @Test
    @DisplayName("Missing path")
    public void testNotExistingPath() throws IOException, ServletException {
        when(request.getParameter(Constants.Web.PATH)).thenReturn(base + "missingFolder");

        servlet.doGet(request, response);

        Mockito.verifyZeroInteractions(helper);
        verifyError(404);
    }

    @Test
    @DisplayName("Directory listing")
    public void testListDir() throws IOException, ServletException {
        when(request.getParameter(Constants.Web.PATH)).thenReturn(base + "folder1");
        List<Dir> dirs = Arrays.asList(new Dir("folder1/", 0, base + "folder1/" + "folder1", false),
                new Dir("folder2/", 0, base + "folder1/" + "folder2", false),
                new Dir("folder1.txt", 0, base + "folder1/" + "folder1.txt", true),
                new Dir(FILE1, 4, base + "folder1/" + FILE1, true));
        servlet.doGet(request, response);

        Mockito.verifyZeroInteractions(helper);
        String json = JSONModel.toJSONArray(dirs);
        verifyOkJSON(json);
    }

    @Test
    @DisplayName("Display small file using small buffer")
    public void testShowSmallFileSmallBuffer() throws IOException, ServletException {
        int buffsize = 1;
        int len = 4;
        when(request.getParameter(Constants.Web.PATH)).thenReturn(base + "folder1/folder11.txt");
        when(request.getParameter(Constants.Web.DOWNLOAD)).thenReturn(null);
        when(request.getHeader(Constants.Web.Headers.RANGE)).thenReturn(null);
        when(helper.getBufferSize()).thenReturn(buffsize);
        when(helper.getMIME(FILE1)).thenReturn(Constants.Setting.PLAIN_TEXT);
        MockOutputStream stream = new MockOutputStream(len);
        when(response.getOutputStream()).thenReturn(stream);

        servlet.doGet(request, response);

        verifyHeaders(FILE1, Constants.Setting.PLAIN_TEXT, buffsize, len, 0, len - 1);
        Assertions.assertEquals(TEST, new String(stream.get()));
    }

    @Test
    @DisplayName("Display small file using big buffer")
    public void testShowSmallFileBigBuffer() throws IOException, ServletException {
        int len = 4;
        int buff = 1024 * 1024 * 1024;
        when(request.getParameter(Constants.Web.PATH)).thenReturn(base + "folder1/folder11.txt");
        when(request.getParameter(Constants.Web.DOWNLOAD)).thenReturn(null);
        when(request.getHeader(Constants.Web.Headers.RANGE)).thenReturn(null);
        when(helper.getBufferSize()).thenReturn(buff);
        MockOutputStream stream = new MockOutputStream(len);
        when(response.getOutputStream()).thenReturn(stream);
        when(helper.getMIME(FILE1)).thenReturn(Constants.Setting.PLAIN_TEXT);

        servlet.doGet(request, response);

        verifyHeaders(FILE1, Constants.Setting.PLAIN_TEXT, buff, len, 0, len - 1);
        Assertions.assertEquals(TEST, stream.get());
    }

    @Test
    @DisplayName("Display big file using small buffer")
    public void testShowBigFileSmallBuffer() throws IOException, ServletException {
        int len = 77280;
        int buff = 1;
        when(request.getParameter(Constants.Web.PATH)).thenReturn(base + "folder3/folder11.txt");
        when(request.getParameter(Constants.Web.DOWNLOAD)).thenReturn(null);
        when(request.getHeader(Constants.Web.Headers.RANGE)).thenReturn(null);
        when(helper.getBufferSize()).thenReturn(buff);
        MockOutputStream stream = new MockOutputStream(len);
        when(response.getOutputStream()).thenReturn(stream);
        when(helper.getMIME(FILE1)).thenReturn(Constants.Setting.PLAIN_TEXT);

        servlet.doGet(request, response);
        verifyHeaders(FILE1, Constants.Setting.PLAIN_TEXT, buff, len, 0, len - 1);
        String expected = Files.readAllLines(Paths.get(base + "folder3/folder11.txt")).get(0);
        Assertions.assertEquals(expected, stream.get());
    }

    @Test
    @DisplayName("Display big file using big buffer")
    public void testShowBigFileBigBuffer() throws IOException, ServletException {
        int len = 77280;
        int buff = 10240;
        when(request.getParameter(Constants.Web.PATH)).thenReturn(base + "folder3/folder11.txt");
        when(request.getParameter(Constants.Web.DOWNLOAD)).thenReturn(null);
        when(request.getHeader(Constants.Web.Headers.RANGE)).thenReturn(null);
        when(helper.getBufferSize()).thenReturn(buff);
        MockOutputStream stream = new MockOutputStream(len);
        when(response.getOutputStream()).thenReturn(stream);
        when(helper.getMIME(FILE1)).thenReturn(Constants.Setting.PLAIN_TEXT);

        servlet.doGet(request, response);
        verifyHeaders(FILE1, Constants.Setting.PLAIN_TEXT, buff, len, 0, len - 1);
        String expected = Files.readAllLines(Paths.get(base + "folder3/folder11.txt")).get(0);
        Assertions.assertEquals(expected, stream.get());
    }

    @Test
    @DisplayName("Force download")
    public void testForceDownload() throws IOException, ServletException {
        int len = 4;
        int buff = 4;
        when(request.getParameter(Constants.Web.PATH)).thenReturn(base + "folder1/folder11.txt");
        when(request.getParameter(Constants.Web.DOWNLOAD)).thenReturn("true");
        when(request.getHeader(Constants.Web.Headers.RANGE)).thenReturn(null);
        when(helper.getBufferSize()).thenReturn(buff);
        MockOutputStream stream = new MockOutputStream(len);
        when(response.getOutputStream()).thenReturn(stream);

        servlet.doGet(request, response);
        Mockito.verify(helper, Mockito.never()).getMIME(Mockito.anyString());
        verifyHeaders(FILE1, Constants.Web.Headers.APPLICATION_FORCE_DOWNLOAD, buff, len, 0, len - 1);
        Assertions.assertEquals(TEST, stream.get());
    }

    @Test
    @DisplayName("Tests content range")
    public void testContentRange() throws IOException, ServletException {
        Assertions.fail();
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
        Mockito.verify(response).setBufferSize(buffsize);
        Mockito.verifyZeroInteractions(writer);
        Mockito.verify(response).setHeader(Constants.Web.Headers.CONTENT_DISPOSITION,
                String.format("inline;filename=\"%s\"", filename));
        Mockito.verify(response).setHeader(Constants.Web.Headers.ACCEPT_RANGES, "bytes");
        Mockito.verify(response).setHeader(Constants.Web.Headers.CONTENT_RANGE, String.format("bytes %s-%s/%s", 0, len - 1, len));
        Mockito.verify(response).setHeader(Constants.Web.Headers.CONTENT_LENGTH, String.format("%s", len));
        Mockito.verify(response).setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        Mockito.verify(response).setContentType(contenttype);
    }

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
