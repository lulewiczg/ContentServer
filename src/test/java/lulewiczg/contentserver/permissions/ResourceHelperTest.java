package lulewiczg.contentserver.permissions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import javax.security.sasl.AuthenticationException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mockito;

import lulewiczg.contentserver.test.utils.TestUtil;
import lulewiczg.contentserver.utils.Constants;

public class ResourceHelperTest {

    // Grzegorz Brzęczyczykiewicz i Że li Popą
    private static final byte[] TEST_STRING = new byte[] { 71, 114, 122, 101, 103, 111, 114, 122, 32, 66, 114, 122, -60,
            -103, 99, 122, 121, 99, 122, 121, 107, 105, 101, 119, 105, 99, 122, 32, 105, 32, -59, -69, 101, 32, 108,
            105, 32, 80, 111, 112, -60, -123 };
    private static final String DESC = "''{0}'' should {1} have access to ''{2}''";
    private static final String CONTEXT = TestUtil.LOC + "testContexts/";
    private static final String LOC = "src/test/resources/data/";
    private static final String STRUCTURE = LOC + "structure/";

    @DisplayName("Read permissions with no admin")
    @ParameterizedTest(name = DESC)
    @CsvFileSource(resources = "/data/csv/context1.csv")
    public void testReadNoAdmin(String name, boolean access, String path) {
        ResourceHelper.init(CONTEXT + 1, LOC);
        ResourceHelper instance = ResourceHelper.getInstance();
        assertEquals(access, instance.hasReadAccess(STRUCTURE + path, name));
    }

    @DisplayName("Read permissions")
    @ParameterizedTest(name = DESC)
    @CsvFileSource(resources = "/data/csv/context2.csv")
    public void testRead(String name, boolean access, String path) {
        ResourceHelper.init(CONTEXT + 2, LOC);
        ResourceHelper instance = ResourceHelper.getInstance();
        assertEquals(access, instance.hasReadAccess(STRUCTURE + path, name));
    }

    @DisplayName("Read permissions with extended user")
    @ParameterizedTest(name = DESC)
    @CsvFileSource(resources = "/data/csv/context3.csv")
    public void testReadExtended(String name, boolean access, String path) {
        ResourceHelper.init(CONTEXT + 3, LOC);
        ResourceHelper instance = ResourceHelper.getInstance();
        assertEquals(access, instance.hasReadAccess(STRUCTURE + path, name));
    }

    @DisplayName("Read permissions with extended user and different permissions")
    @ParameterizedTest(name = DESC)
    @CsvFileSource(resources = "/data/csv/context4.csv")
    public void testReadExtendedWriteDelete(String name, boolean access, String path) {
        ResourceHelper.init(CONTEXT + 4, LOC);
        ResourceHelper instance = ResourceHelper.getInstance();
        assertEquals(access, instance.hasReadAccess(STRUCTURE + path, name));
    }

    @DisplayName("Path normalization")
    @ParameterizedTest(name = "''{0}'' should be normalized to ''{1}''")
    @CsvFileSource(resources = "/data/csv/paths.csv")
    public void testReadExtendedWriteDelete(String path, String expectedPath) {
        assertEquals(expectedPath, ResourceHelper.normalizePath(path));
    }

    @DisplayName("MIME type resolution")
    @ParameterizedTest(name = "''{0}'' should be resolved to ''{1}''")
    @CsvFileSource(resources = "/data/csv/mimes.csv")
    public void testResolveMIMEs(String fileName, String mime) {
        ResourceHelper.init(CONTEXT + 1, STRUCTURE);
        assertEquals(mime, ResourceHelper.getInstance().getMIME(fileName));
    }

    @DisplayName("Hash is porperly calculated")
    @ParameterizedTest(name = "''{1}'' should be hash of ''{0}''")
    @CsvFileSource(resources = "/data/csv/sha.csv")
    public void testHash(String text, String sha) throws AuthenticationException {
        assertEquals(sha, ResourceHelper.sha1(text));
    }

    @DisplayName("User can login")
    @ParameterizedTest(name = "User ''{0}'' should log in with password ''{1}''")
    @CsvFileSource(resources = "/data/csv/logins.csv")
    public void testLogin(String user, String password) throws AuthenticationException {
        HttpSession session = mock(HttpSession.class);
        ResourceHelper.init(CONTEXT + 4, STRUCTURE);
        ResourceHelper instance = ResourceHelper.getInstance();
        instance.login(user, password, session);
        verify(session, times(1)).setAttribute(eq(Constants.Web.USER), eq(user));
    }

    @DisplayName("User can not login with invalid password or login")
    @ParameterizedTest(name = "User ''{0}'' should not log in with password ''{1}''")
    @CsvFileSource(resources = "/data/csv/logins-invalid.csv")
    public void testLoginFailed(String user, String password) throws AuthenticationException {
        HttpSession session = mock(HttpSession.class);
        ResourceHelper.init(CONTEXT + 4, STRUCTURE);
        ResourceHelper instance = ResourceHelper.getInstance();
        Assertions.assertThrows(AuthenticationException.class, () -> instance.login(user, password, session),
                "Invalid login or password");
        verify(session, never()).setAttribute(anyString(), anyString());

    }

    @Test
    @DisplayName("Helper initialization using servlet context")
    public void testInitWithServletContext() throws IOException {
        ServletContext mock = Mockito.mock(ServletContext.class);
        when(mock.getRealPath(anyString())).thenReturn(CONTEXT + 1);
        when(mock.getServerInfo()).thenReturn("tomcat");
        ResourceHelper.init(mock, LOC);
        String contextPath = new File(".").getCanonicalPath();
        contextPath = ResourceHelper.normalizePath(contextPath);
        List<String> expected = Arrays.asList(String
                .format("%s/src/test/resources/data/structure/folder1;%s/src/test/resources/data/structure/folder2",
                        contextPath, contextPath)
                .split("\\;"));
        List<String> availablePaths = ResourceHelper.getInstance().getAvailablePaths(Constants.GUEST);
        assertEquals(expected, availablePaths);
    }

    @Test
    @DisplayName("Parameter decoding is perfomed on Tomcat")
    public void testDecodeParam() throws IOException {
        ServletContext mock = Mockito.mock(ServletContext.class);
        when(mock.getRealPath(anyString())).thenReturn(CONTEXT + 1);
        when(mock.getServerInfo()).thenReturn("tomcat");
        ResourceHelper.init(mock, LOC);
        String test = new String(TEST_STRING, StandardCharsets.ISO_8859_1);
        Assertions.assertArrayEquals(TEST_STRING, ResourceHelper.decodeParam(test).getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Parameter decoding is not performed on other servlet container")
    public void testDontDecodeParam() throws IOException {
        ServletContext mock = Mockito.mock(ServletContext.class);
        when(mock.getRealPath(anyString())).thenReturn(CONTEXT + 1);
        when(mock.getServerInfo()).thenReturn("jetty");
        ResourceHelper.init(mock, STRUCTURE);
        String test = new String(TEST_STRING, StandardCharsets.UTF_8);
        Assertions.assertArrayEquals(TEST_STRING, ResourceHelper.decodeParam(test).getBytes(StandardCharsets.UTF_8));
    }
}
