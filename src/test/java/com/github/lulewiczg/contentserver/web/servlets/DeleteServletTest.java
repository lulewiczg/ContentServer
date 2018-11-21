package com.github.lulewiczg.contentserver.web.servlets;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import javax.servlet.ServletException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.lulewiczg.contentserver.test.utils.ServletTestTemplate;
import com.github.lulewiczg.contentserver.test.utils.TestUtil;
import com.github.lulewiczg.contentserver.utils.Constants;

/**
 * Tests DeleteServlet.
 *
 * @author lulewiczg
 */
public class DeleteServletTest extends ServletTestTemplate {

    private DeleteServlet servlet = spy(DeleteServlet.class);

    private static Stack<String> files;

    /**
     * Cleans up upload directory.
     * 
     * @throws IOException
     *             the IOException
     */
    @AfterAll
    public static void afterAll() throws IOException {
        TestUtil.clearTrash();
    }

    /**
     * Creates test files.
     * 
     * @throws IOException
     *             the IOException
     */
    @BeforeAll
    public static void beforeAll() throws IOException {
        files = TestUtil.createFiles(2);
    }

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
    @DisplayName("Delete file with empty path")
    public void testDeleteWithEmptyPath() throws IOException, ServletException {
        when(request.getParameter(Constants.Web.PATH)).thenReturn("");
        servlet.doPost(request, response);

        verifyError(400, Constants.Web.Errors.NOT_FOUND);
    }

    @Test
    @DisplayName("Delete file with null path")
    public void testDeleteWithNullPath() throws IOException, ServletException {
        when(request.getParameter(Constants.Web.PATH)).thenReturn(null);
        servlet.doPost(request, response);

        verifyError(400, Constants.Web.Errors.NOT_FOUND);
    }

    @Test
    @DisplayName("Delete file")
    public void testDeleteFile() throws IOException, ServletException {
        String pop = files.pop();
        when(request.getParameter(Constants.Web.PATH)).thenReturn(new File(TestUtil.TRASH + pop).getCanonicalPath());
        servlet.doPost(request, response);

        verifyOkEmptyResponse();
        Assertions.assertFalse(new File(TestUtil.TRASH + pop).exists());
    }

    @Test
    @DisplayName("Delete directory")
    public void testDeleteDir() throws IOException, ServletException {
        when(request.getParameter(Constants.Web.PATH)).thenReturn(new File(TestUtil.TRASH).getCanonicalPath());
        servlet.doPost(request, response);

        verifyError(400, Constants.Web.Errors.DIRECTORY_DELETE);
        Assertions.assertTrue(new File(TestUtil.TRASH).exists());
    }

    @Test
    @DisplayName("Delete not existing file")
    public void testDeleteNotExistingFile() throws IOException, ServletException {
        when(request.getParameter(Constants.Web.PATH)).thenReturn(new File(TestUtil.TRASH + "abc").getCanonicalPath());
        servlet.doPost(request, response);

        verifyError(400, Constants.Web.Errors.DELETE_FAILED);
    }
}
