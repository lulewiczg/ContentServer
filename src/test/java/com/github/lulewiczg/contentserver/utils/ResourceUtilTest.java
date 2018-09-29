package com.github.lulewiczg.contentserver.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.security.sasl.AuthenticationException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import com.github.lulewiczg.contentserver.test.utils.TestUtil;

/**
 * Tests for ResourceUtil.
 *
 * @author lulewiczg
 */
public class ResourceUtilTest {

    private static final String DESC = "''{0}'' should {1} have access to ''{2}''";
    private static final String CONTEXT = TestUtil.LOC + "testContexts/";
    private static final String LOC = "src/test/resources/data/";
    private static final String STRUCTURE = LOC + "structure/";

    private ServletContext context = mock(ServletContext.class);

    @DisplayName("Read permissions with no admin")
    @ParameterizedTest(name = DESC)
    @CsvFileSource(resources = "/data/csv/context1.csv")
    public void testReadNoAdmin(String name, boolean access, String path) {
        ResourceUtil instance = getInstance(1);

        assertEquals(access, instance.hasReadAccess(STRUCTURE + path, name));
        verify(context, times(1)).getAttribute(eq(ResourceUtil.NAME));
    }

    @DisplayName("Read permissions")
    @ParameterizedTest(name = DESC)
    @CsvFileSource(resources = "/data/csv/context2.csv")
    public void testRead(String name, boolean access, String path) {
        ResourceUtil instance = getInstance(2);

        assertEquals(access, instance.hasReadAccess(STRUCTURE + path, name));
    }

    @DisplayName("Read permissions with extended user")
    @ParameterizedTest(name = DESC)
    @CsvFileSource(resources = "/data/csv/context3.csv")
    public void testReadExtended(String name, boolean access, String path) {
        ResourceUtil instance = getInstance(3);

        assertEquals(access, instance.hasReadAccess(STRUCTURE + path, name));
    }

    @DisplayName("Read permissions with extended user and different permissions")
    @ParameterizedTest(name = DESC)
    @CsvFileSource(resources = "/data/csv/context4.csv")
    public void testReadExtendedWriteDelete(String name, boolean access, String path) {
        ResourceUtil instance = getInstance(4);

        assertEquals(access, instance.hasReadAccess(STRUCTURE + path, name));
    }

    @Test
    @DisplayName("Read permissions with invalid key")
    public void testReadInvalidKey() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> getInstance(5));
        assertEquals("Key user.test is invalid!", e.getMessage());
    }

    @Test
    @DisplayName("Read permissions with invalid permission type")
    public void testReadInvalidPermissionType() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> getInstance(6));
        assertEquals("No enum constant " + Persmission.class.getName() + ".IKSDE", e.getMessage());

    }

    @DisplayName("User can login")
    @ParameterizedTest(name = "User ''{0}'' should log in with password ''{1}''")
    @CsvFileSource(resources = "/data/csv/logins.csv")
    public void testLogin(String user, String password) throws AuthenticationException {
        HttpSession session = mock(HttpSession.class);
        ResourceUtil instance = getInstance(4);

        instance.login(user, password, session);
        verify(session, times(1)).setAttribute(eq(Constants.Web.USER), eq(user));
    }

    @DisplayName("User can not login with invalid password or login")
    @ParameterizedTest(name = "User ''{0}'' should not log in with password ''{1}''")
    @CsvFileSource(resources = "/data/csv/logins-invalid.csv")
    public void testLoginFailed(String user, String password) throws AuthenticationException {
        HttpSession session = mock(HttpSession.class);
        ResourceUtil instance = getInstance(4);

        Exception e = Assertions.assertThrows(AuthenticationException.class, () -> instance.login(user, password, session));
        assertEquals("Invalid login or password", e.getMessage());
        verify(session, never()).setAttribute(anyString(), anyString());

    }

    /**
     * Obtains util instance.
     *
     * @param contextIndex
     *            context index
     * @param server
     *            server name
     * @return util
     */
    private ResourceUtil getInstance(int contextIndex) {
        when(context.getRealPath(eq(Constants.SEP))).thenReturn(CONTEXT + contextIndex);
        ResourceUtil util = ResourceUtil.init(context, LOC);
        when(context.getAttribute(eq(ResourceUtil.NAME))).thenReturn(util);
        ResourceUtil instance = ResourceUtil.get(context);
        verify(context, times(1)).getAttribute(eq(ResourceUtil.NAME));
        return instance;
    }
}
