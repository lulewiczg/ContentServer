package com.github.lulewiczg.contentserver.web.servlets;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.lulewiczg.contentserver.test.utils.ServletTestTemplate;
import com.github.lulewiczg.contentserver.test.utils.TestUtil;
import com.github.lulewiczg.contentserver.utils.CommonUtil;
import com.github.lulewiczg.contentserver.utils.Constants;
import com.github.lulewiczg.contentserver.utils.json.JSONModel;

/**
 * Tests ShortcutsServlet.
 * 
 * @author lulewiczg
 */
public class ShortcutsServletTest extends ServletTestTemplate {

    private ShortcutsServlet servlet = spy(ShortcutsServlet.class);
    private static List<String> guestPaths;
    private static List<String> testPaths;
    private static List<String> adminPaths;
    private static String base;

    /**
     * Sets up tested object.
     * 
     * @see com.github.lulewiczg.contentserver.test.utils.ServletTestTemplate#additionalBefore()
     */
    @Override
    public void additionalBefore() throws Exception {
        when(resourceUtil.getAvailablePaths(null)).thenReturn(guestPaths);
        when(resourceUtil.getAvailablePaths(TEST)).thenReturn(testPaths);
        when(resourceUtil.getAvailablePaths(Constants.ADMIN)).thenReturn(adminPaths);
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
        base = CommonUtil.normalizePath(new File(TestUtil.LOC).getCanonicalPath() + Constants.SEP);
        guestPaths = Arrays.asList(base + "structure/folder1/folder1", base + "structure/folder2/folder1",
                base + "structure/folder1/folder1.txt", base + "structure/folder1");
        testPaths = Arrays.asList(base + "structure/folder1/folder1", base + "structure/folder2",
                base + "testContexts");
        adminPaths = Arrays.asList(base + "structure", base + "csv/context1.csv");
    }

    @Test
    @DisplayName("Available paths for guest")
    public void testAvailablePathsForGuest() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(null);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(null);

        servlet.doGet(request, response);

        verifyOkJSON(JSONModel.toJSONArray(guestPaths));
    }

    @Test
    @DisplayName("Available paths for user")
    public void testAvailablePathsForUser() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(null);

        servlet.doGet(request, response);

        verifyOkJSON(JSONModel.toJSONArray(testPaths));
    }

    @Test
    @DisplayName("Available paths for admin")
    public void testAvailablePathsForAdmin() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(Constants.ADMIN);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(null);

        servlet.doGet(request, response);

        verifyOkJSON(JSONModel.toJSONArray(adminPaths));
    }

    @Test
    @DisplayName("Not existing paths are not returned")
    public void testMissingPaths() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST2);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(null);
        List<String> paths = Arrays.asList(base + "structure/folder1/folder1", base + "missingFolder");
        when(resourceUtil.getAvailablePaths(TEST2)).thenReturn(paths);

        servlet.doGet(request, response);

        verifyOkJSON(JSONModel.toJSONArray(Arrays.asList(paths.get(0))));
    }

    @Test
    @DisplayName("No paths are returned")
    public void testNoPaths() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST2);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(null);
        when(resourceUtil.getAvailablePaths(TEST2)).thenReturn(new ArrayList<>());

        servlet.doGet(request, response);

        verifyOkJSON(JSONModel.EMPTY_ARR);
    }

    @Test
    @DisplayName("Number of parent folders for invalid path")
    public void testNumberOfFoldersInvalidPath() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(null);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(base);
        when(resourceUtil.hasReadAccess(base + TEST, Constants.GUEST)).thenReturn(false);

        servlet.doGet(request, response);

        verifyOk("0");
    }

    @Test
    @DisplayName("Number of parent folders for root of allowed path")
    public void testNumberOfFoldersRoot() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(base);
        File file = new File(base).getCanonicalFile();
        when(resourceUtil.hasReadAccess(file.getCanonicalPath(), TEST)).thenReturn(true);
        when(resourceUtil.hasReadAccess(file.getParentFile().getPath(), TEST)).thenReturn(false);

        servlet.doGet(request, response);

        verifyOk("0");
    }

    @Test
    @DisplayName("Number of parent folders for first level of allowed path")
    public void testNumberOfFoldersFirstLevel() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(base);
        File file = new File(base).getCanonicalFile();
        when(resourceUtil.hasReadAccess(file.getCanonicalPath(), TEST)).thenReturn(true);
        when(resourceUtil.hasReadAccess(file.getParentFile().getPath(), TEST)).thenReturn(true);
        when(resourceUtil.hasReadAccess(file.getParentFile().getParentFile().getPath(), TEST)).thenReturn(false);

        servlet.doGet(request, response);

        verifyOk("1");
    }

    @Test
    @DisplayName("Number of parent folders when given full access to another user")
    public void testNumberOfFoldersFullPathAnotherUser() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST2);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(base);
        when(resourceUtil.hasReadAccess(anyString(), eq(TEST))).thenReturn(true);

        servlet.doGet(request, response);

        verifyOk("0");
    }

    @Test
    @DisplayName("Number of parent folders when given full access")
    public void testNumberOfFoldersFullPath() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(base);
        File file = new File(base).getCanonicalFile();
        when(resourceUtil.hasReadAccess(anyString(), eq(TEST))).thenReturn(true);
        int parents = 0;
        while ((file = file.getParentFile()) != null) {
            parents++;
        }

        servlet.doGet(request, response);

        verifyOk(parents + "");
    }
}
