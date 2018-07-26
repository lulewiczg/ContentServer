package lulewiczg.contentserver.web.servlets;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
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

import lulewiczg.contentserver.permissions.ResourceHelper;
import lulewiczg.contentserver.test.utils.ServletTestTemplate;
import lulewiczg.contentserver.utils.Constants;
import lulewiczg.contentserver.utils.json.JSONModel;

/**
 * Tests ShortcutsServlet.
 * 
 * @author lulewiczg
 */
public class ShortcutsServletTest extends ServletTestTemplate {

    private ShortcutsServlet servlet = new ShortcutsServlet();
    private static List<String> guestPaths;
    private static List<String> testPaths;
    private static List<String> adminPaths;
    private static String base;

    /**
     * Prepares data.
     * 
     * @throws IOException
     *             the IOException
     */
    @BeforeAll
    public static void setup() throws IOException {
        base = ResourceHelper.normalizePath(new File("src/test/resources/").getCanonicalPath() + Constants.SEP);
        guestPaths = Arrays.asList(base + "structure/folder1/folder1", base + "structure/folder2/folder1",
                base + "structure/folder1/folder1.txt", base + "structure/folder1");
        testPaths = Arrays.asList(base + "structure/folder1/folder1", base + "structure/folder2",
                base + "testContexts");
        adminPaths = Arrays.asList(base + "structure", base + "context1.csv");
    }

    @Override
    public void additionalBefore() throws IOException, ReflectiveOperationException {
        when(helper.getAvailablePaths(null)).thenReturn(guestPaths);
        when(helper.getAvailablePaths(TEST)).thenReturn(testPaths);
        when(helper.getAvailablePaths(Constants.ADMIN)).thenReturn(adminPaths);
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
        when(helper.getAvailablePaths(TEST2)).thenReturn(paths);

        servlet.doGet(request, response);

        verifyOkJSON(JSONModel.toJSONArray(Arrays.asList(paths.get(0))));
    }

    @Test
    @DisplayName("No paths are returned")
    public void testNoPaths() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST2);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(null);
        when(helper.getAvailablePaths(TEST2)).thenReturn(new ArrayList<>());

        servlet.doGet(request, response);

        verifyOkJSON(JSONModel.EMPTY_ARR);
    }

    @Test
    @DisplayName("Number of parent folders for invalid path")
    public void testNumberOfFoldersInvalidPath() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(null);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(base);
        when(helper.hasReadAccess(base + TEST, Constants.GUEST)).thenReturn(false);

        servlet.doGet(request, response);

        verifyOk("0");
    }

    @Test
    @DisplayName("Number of parent folders for root of allowed path")
    public void testNumberOfFoldersRoot() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(base);
        File file = new File(base).getCanonicalFile();
        when(helper.hasReadAccess(file.getCanonicalPath(), TEST)).thenReturn(true);
        when(helper.hasReadAccess(file.getParentFile().getPath(), TEST)).thenReturn(false);

        servlet.doGet(request, response);

        verifyOk("0");
    }

    @Test
    @DisplayName("Number of parent folders for first level of allowed path")
    public void testNumberOfFoldersFirstLevel() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(base);
        File file = new File(base).getCanonicalFile();
        when(helper.hasReadAccess(file.getCanonicalPath(), TEST)).thenReturn(true);
        when(helper.hasReadAccess(file.getParentFile().getPath(), TEST)).thenReturn(true);
        when(helper.hasReadAccess(file.getParentFile().getParentFile().getPath(), TEST)).thenReturn(false);

        servlet.doGet(request, response);

        verifyOk("1");
    }

    @Test
    @DisplayName("Number of parent folders when given full access to another user")
    public void testNumberOfFoldersFullPathAnotherUser() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST2);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(base);
        when(helper.hasReadAccess(anyString(), eq(TEST))).thenReturn(true);

        servlet.doGet(request, response);

        verifyOk("0");
    }

    @Test
    @DisplayName("Number of parent folders when given full access")
    public void testNumberOfFoldersFullPath() throws IOException, ServletException {
        when(session.getAttribute(Constants.Web.USER)).thenReturn(TEST);
        when(request.getParameter(Constants.Web.PATH)).thenReturn(base);
        File file = new File(base).getCanonicalFile();
        when(helper.hasReadAccess(anyString(), eq(TEST))).thenReturn(true);
        int parents = 0;
        while ((file = file.getParentFile()) != null) {
            parents++;
        }

        servlet.doGet(request, response);

        verifyOk(parents + "");
    }
}
