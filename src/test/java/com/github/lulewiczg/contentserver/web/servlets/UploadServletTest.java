package com.github.lulewiczg.contentserver.web.servlets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Part;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.github.lulewiczg.contentserver.test.utils.ServletTestTemplate;
import com.github.lulewiczg.contentserver.utils.CommonUtil;
import com.github.lulewiczg.contentserver.utils.Constants;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

/**
 * Tests UploadServlet.
 *
 * @author lulewiczg
 */
public class UploadServletTest extends ServletTestTemplate {

    private static final String TEST123456789 = "test123456789";
    private static final String SOMETEXT = "sometext";
    private static final String SOMETEXT2 = "sometext2";

    private static final String TRASH = "src/main/resources/data/trash/";
    private UploadServlet servlet = spy(UploadServlet.class);

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
    @DisplayName("Upload file with empty path")
    public void testUploadWithEmptyPath() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST);
        when(request.getParameter(Constants.Web.PATH)).thenReturn("");
        servlet.doPost(request, response);

        verifyError(400, Constants.Web.Errors.UPLOAD_DIR_NOT_FOUND);
    }

    @Test
    @DisplayName("Upload file with null path")
    public void testUploadWithNukkPath() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(null);
        servlet.doPost(request, response);

        verifyError(400, Constants.Web.Errors.UPLOAD_DIR_NOT_FOUND);
    }

    @Test
    @DisplayName("Upload 0 files")
    public void testUpload0Files() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(TEST2);
        when(request.getServletContext()).thenReturn(context);
        when(request.getParts()).thenReturn(new ArrayList<>());

        servlet.doPost(request, response);

        verifyOkEmptyResponse();
    }

    @Test
    @DisplayName("Upload 1 file")
    public void testUploadFile() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(TRASH);
        when(request.getServletContext()).thenReturn(context);
        when(settingsUtil.getBufferSize()).thenReturn(100);

        Part part = Mockito.mock(Part.class);
        long file = System.currentTimeMillis();
        when(part.getHeader(Constants.Web.Headers.CONTENT_DISPOSITION)).thenReturn(buildHeader(file));
        when(part.getInputStream()).thenReturn(new MockInputStream(SOMETEXT));
        List<Part> parts = new ArrayList<>();
        parts.add(part);

        when(request.getParts()).thenReturn(parts);

        servlet.doPost(request, response);

        verifyOkEmptyResponse();
        verifyFileExists(file, SOMETEXT);
    }

    @Test
    @DisplayName("Upload 1 file that already exists")
    public void testUploadExistingFile() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(TRASH);
        when(request.getServletContext()).thenReturn(context);
        when(settingsUtil.getBufferSize()).thenReturn(100);

        Part part = Mockito.mock(Part.class);
        String file = "testFile.txt";
        when(part.getHeader(Constants.Web.Headers.CONTENT_DISPOSITION)).thenReturn(buildHeader(file));
        when(part.getInputStream()).thenReturn(new MockInputStream(SOMETEXT));
        List<Part> parts = new ArrayList<>();
        parts.add(part);

        when(request.getParts()).thenReturn(parts);

        servlet.doPost(request, response);

        verifyError(400, String.format(Constants.Web.Errors.FILE_ALREADY_EXIST,
                CommonUtil.normalizePath(new File(".").getCanonicalPath() + Constants.SEP + TRASH + file)));
        verifyFileExists(file, TEST123456789);
    }

    @Test
    @DisplayName("Upload multiple files")
    public void testUploadMultipleFiles() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(TRASH);
        when(request.getServletContext()).thenReturn(context);
        when(settingsUtil.getBufferSize()).thenReturn(100);

        List<Part> parts = new ArrayList<>();
        Part part = Mockito.mock(Part.class);
        long file = System.currentTimeMillis();
        when(part.getHeader(Constants.Web.Headers.CONTENT_DISPOSITION)).thenReturn(buildHeader(file));
        when(part.getInputStream()).thenReturn(new MockInputStream(SOMETEXT));
        parts.add(part);

        Part part2 = Mockito.mock(Part.class);
        long file2 = System.currentTimeMillis() + 1;
        when(part2.getHeader(Constants.Web.Headers.CONTENT_DISPOSITION)).thenReturn(buildHeader(file2));
        when(part2.getInputStream()).thenReturn(new MockInputStream(SOMETEXT2));
        parts.add(part2);

        when(request.getParts()).thenReturn(parts);

        servlet.doPost(request, response);

        verifyOkEmptyResponse();
        verifyFileExists(file, SOMETEXT);
        verifyFileExists(file2, SOMETEXT2);
    }

    /**
     * Builds part header
     *
     * @param name
     *            file name
     * @return header
     */
    private String buildHeader(Object name) {
        return String.format("form-data; name=\"file\"; filename=\"%s\"", name);
    }

    /**
     * Verifies if file was uploaded
     *
     * @param file
     *            file
     * @throws IOException
     *             the IOException
     */
    private void verifyFileExists(Object file, String expected) throws IOException {
        File uploaded = new File(new File(".").getCanonicalPath() + Constants.SEP + TRASH + file);
        assertTrue(uploaded.exists());
        List<String> lines = Files.readLines(uploaded, Charsets.UTF_8);
        assertEquals(1, lines.size());
        assertEquals(expected, lines.get(0));

    }

    /**
     * Mock output stream to get response.
     */
    private class MockInputStream extends ServletInputStream {
        private String txt;
        private int pos = 0;

        @Override
        public int read(byte[] b) throws IOException {
            byte[] txtBytes = txt.getBytes();
            int bytepos = 0;
            if (pos == txt.length()) {
                return -1;
            }
            for (; pos < txt.length(); pos++) {
                b[bytepos++] = txtBytes[pos];
            }
            return bytepos;
        }

        private MockInputStream(String txt) {
            this.txt = txt;
        }

        @Override
        public int read() throws IOException {
            return 0;
        }

    }
}
