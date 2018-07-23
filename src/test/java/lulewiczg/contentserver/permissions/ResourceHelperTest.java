package lulewiczg.contentserver.permissions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import javax.security.sasl.AuthenticationException;
import javax.servlet.ServletContext;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mockito;

import lulewiczg.contentserver.utils.Constants;

public class ResourceHelperTest {

    private static final String DESC = "''{0}'' should {1} have access to ''{2}''";
    private static final String CONTEXT = "src/test/resources/testContexts/";
    private static final String LOC = "src/test/resources/structure/";

    @DisplayName("Read permissions with no admin")
    @ParameterizedTest(name = DESC)
    @CsvFileSource(resources = "/context1.csv")
    public void testReadNoAdmin(String name, boolean access, String path) {
        ResourceHelper.init(CONTEXT + 1);
        ResourceHelper instance = ResourceHelper.getInstance();
        assertEquals(access, instance.hasReadAccess(LOC + path, name));
    }

    @DisplayName("Read permissions")
    @ParameterizedTest(name = DESC)
    @CsvFileSource(resources = "/context2.csv")
    public void testRead(String name, boolean access, String path) {
        ResourceHelper.init(CONTEXT + 2);
        ResourceHelper instance = ResourceHelper.getInstance();
        assertEquals(access, instance.hasReadAccess(LOC + path, name));
    }

    @DisplayName("Read permissions with extended user")
    @ParameterizedTest(name = DESC)
    @CsvFileSource(resources = "/context3.csv")
    public void testReadExtended(String name, boolean access, String path) {
        ResourceHelper.init(CONTEXT + 3);
        ResourceHelper instance = ResourceHelper.getInstance();
        assertEquals(access, instance.hasReadAccess(LOC + path, name));
    }

    @DisplayName("Read permissions with extended user and different permissions")
    @ParameterizedTest(name = DESC)
    @CsvFileSource(resources = "/context4.csv")
    public void testReadExtendedWriteDelete(String name, boolean access, String path) {
        ResourceHelper.init(CONTEXT + 4);
        ResourceHelper instance = ResourceHelper.getInstance();
        assertEquals(access, instance.hasReadAccess(LOC + path, name));
    }

    @DisplayName("Path normalization")
    @ParameterizedTest(name = "''{0}'' should be normalized to ''{1}''")
    @CsvFileSource(resources = "/paths.csv")
    public void testReadExtendedWriteDelete(String path, String expectedPath) {
        assertEquals(expectedPath, ResourceHelper.normalizePath(path));
    }

    @DisplayName("MIME type resolution")
    @ParameterizedTest(name = "''{0}'' should be resolved to ''{1}''")
    @CsvFileSource(resources = "/mimes.csv")
    public void testResolveMIMEs(String fileName, String mime) {
        ResourceHelper.init(CONTEXT + 1);
        assertEquals(mime, ResourceHelper.getInstance().getMIME(fileName));
    }

    @DisplayName("Hash is porperly calculated")
    @ParameterizedTest(name = "''{1}'' should be hash of ''{0}''")
    @CsvFileSource(resources = "/sha.csv")
    public void testHash(String text, String sha) {
        assertEquals(sha, ResourceHelper.SHA1(text));
    }

    @DisplayName("User can login")
    @ParameterizedTest(name = "User ''{0}'' should log in with password ''{1}''")
    @CsvFileSource(resources = "/logins.csv")
    public void testLogin(String user, String password) throws AuthenticationException {
        ResourceHelper.init(CONTEXT + 4);
        ResourceHelper instance = ResourceHelper.getInstance();
        instance.login(user, password);
    }

    @DisplayName("User can not login with invalid password or login")
    @ParameterizedTest(name = "User ''{0}'' should not log in with password ''{1}''")
    @CsvFileSource(resources = "/logins-invalid.csv")
    public void testLoginFailed(String user, String password) throws AuthenticationException {
        ResourceHelper.init(CONTEXT + 4);
        ResourceHelper instance = ResourceHelper.getInstance();
        Assertions.assertThrows(AuthenticationException.class, () -> instance.login(user, password),
                "Invalid login or password");
    }

    @Test
    @DisplayName("Helper initialization using servlet context")
    public void testInitWithServletContext() throws IOException {
        ServletContext mock = Mockito.mock(ServletContext.class);
        when(mock.getRealPath(Mockito.anyString())).thenReturn(CONTEXT + 1);
        when(mock.getServerInfo()).thenReturn("tomcat");
        ResourceHelper.init(mock);
        String contextPath = new File(".").getCanonicalPath();
        contextPath = ResourceHelper.normalizePath(contextPath);
        List<String> expected = Arrays
                .asList(String.format("%s/src/test/resources/structure/folder1;%s/src/test/resources/structure/folder2",
                        contextPath, contextPath).split("\\;"));
        List<String> availablePaths = ResourceHelper.getInstance().getAvailablePaths(Constants.GUEST);
        assertEquals(expected, availablePaths);
    }

    @Test
    @DisplayName("Parameter decoding is perfomed on Tomcat")
    public void testDecodeParam() throws IOException {
        ServletContext mock = Mockito.mock(ServletContext.class);
        when(mock.getRealPath(Mockito.anyString())).thenReturn(CONTEXT + 1);
        when(mock.getServerInfo()).thenReturn("tomcat");
        ResourceHelper.init(mock);
        String expected = "Grzegorz Brzęczyczykiewicz i Że li Popą";
        String test = new String(expected.getBytes(), StandardCharsets.ISO_8859_1);
        assertEquals(expected, ResourceHelper.decodeParam(test));
    }

    @Test
    @DisplayName("Parameter decoding is not performed on other servlet container")
    public void testDontDecodeParam() throws IOException {
        ServletContext mock = Mockito.mock(ServletContext.class);
        when(mock.getRealPath(Mockito.anyString())).thenReturn(CONTEXT + 1);
        when(mock.getServerInfo()).thenReturn("jetty");
        ResourceHelper.init(mock);
        String expected = "Grzegorz Brzęczyczykiewicz i Żyli Popą";
        assertEquals(expected, ResourceHelper.decodeParam(expected));
    }
}
