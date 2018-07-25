package lulewiczg.contentserver.permissions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lulewiczg.contentserver.utils.models.User;

/**
 * Represents user.
 *
 * @author lulewiczg
 */
public class UserTest {

    private static final List<String> EMPTY = new ArrayList<>();
    private static final String TEST = "test";
    private static final String[] PATHS = new String[] { "C:/a/a/a/b/a", "C:/a/b/a/a/a", "C:/a/1/2/3", "C:/a/1/2/4",
            "C:/a/b", "C:/a/2", "C:/a/3", "C:/b", "C:/b/1", "C:/b/2/3", "C:/b/1/2", "D:", "/a/b/a/a", "/a/a/b/a",
            "/a/b/a/b", "/a/b/a/b", "/a/b/a/c", "/a/b/a.c", "/a/b/a.d", "/a/b.a.d", "b", "a/b.a.d", "a/b/a.d",
            "a/c/a.d", "a/c" };

    private static final String[] PATHS2 = new String[] { "C:/a/b/a", "D:/a/b", "C:/a/a/a/b/aa", "C:/a/a/a/b/a.a/c/f/e",
            "C:/c", "/a/b" };

    private static final List<String> EXPECTED = Arrays.asList("C:/b", "D:", "C:/a/1/2/3", "C:/a/1/2/4", "C:/a/2",
            "C:/a/3", "C:/a/a/a/b/a", "C:/a/b", "/a/a/b/a", "/a/b/a/a", "/a/b/a/b", "/a/b/a/c", "/a/b/a.c", "/a/b/a.d",
            "/a/b.a.d", "b", "a/b.a.d", "a/b/a.d", "a/c");
    private static final List<String> EXPECTED2 = Arrays.asList("C:/b", "D:", "C:/a/1/2/3", "C:/a/1/2/4", "C:/a/2",
            "C:/a/3", "C:/a/a/a/b/a", "C:/a/a/a/b/aa", "C:/a/a/a/b/a.a/c/f/e", "C:/c", "C:/a/b", "/a/a/b/a", "/a/b",
            "/a/b.a.d", "b", "a/b.a.d", "a/b/a.d", "a/c");

    private User user;

    /**
     * Prepares data.
     *
     */
    @BeforeEach
    public void before() {
        user = new User(TEST);
        user.setPassword(TEST);
    }

    /**
     * Tests if permissions are properly normalized.
     */
    @Test
    @DisplayName("Tests read permissions")
    public void testReadPermissions() {
        user.addRead(PATHS);
        user.normalize();
        List<String> expectedRead = EXPECTED;
        compare(expectedRead, user.getRead());
    }

    /**
     * Tests if permissions are properly normalized when ended with slash.
     */
    @Test
    @DisplayName("Tests read permissions with paths ended with slashes")
    public void testReadPermissionsSlashEnd() {
        List<String> collect = Arrays.asList(PATHS).stream().map(i -> i + "/").collect(Collectors.toList());
        user.addRead(collect.toArray(new String[collect.size()]));
        user.normalize();
        compare(EXPECTED, user.getRead());
        compare(EMPTY, user.getDelete());
        compare(EMPTY, user.getWrite());
    }

    /**
     * Tests if permissions are properly normalized when used backslashes.
     */
    @Test
    @DisplayName("Tests read permissions with paths with backslashes")
    public void testReadPermissionsBackslash() {
        List<String> collect = Arrays.stream(PATHS).map(i -> i.replace('/', '\\')).collect(Collectors.toList());
        user.addRead(collect.toArray(new String[collect.size()]));
        user.normalize();
        compare(EXPECTED, user.getRead());
        compare(EMPTY, user.getDelete());
        compare(EMPTY, user.getWrite());
    }

    /**
     * Tests if permissions are properly normalized when ended with backslash.
     */
    @Test
    @DisplayName("Tests read permissions with paths ended with backslashes")
    public void testReadPermissionBackslashEnd() {
        List<String> collect = Arrays.stream(PATHS).map(i -> i.replace('/', '\\') + "\\").collect(Collectors.toList());
        user.addRead(collect.toArray(new String[collect.size()]));
        user.normalize();
        compare(EXPECTED, user.getRead());
        compare(EMPTY, user.getDelete());
        compare(EMPTY, user.getWrite());
    }

    /**
     * Tests if permissions are properly normalized when paths contain slashes and
     * backslashes.
     */
    @Test
    @DisplayName("Tests read permissions with paths with mixed slashes")
    public void testReadPermissionMixedSlashes() {
        List<String> collect = Arrays.stream(PATHS).map(i -> i.replace('/', '\\')).collect(Collectors.toList());
        collect.addAll(Arrays.asList(PATHS));
        user.addRead(collect.toArray(new String[collect.size()]));
        user.normalize();
        compare(EXPECTED, user.getRead());
        compare(EMPTY, user.getDelete());
        compare(EMPTY, user.getWrite());
    }

    /**
     * Tests if permissions are properly normalized when the same paths are set for
     * READ and DELETE permissions.
     */
    @Test
    @DisplayName("Tests cumulative the same permissions for read and delete")
    public void testCumulativePersmissonsTheSameDelete() {
        user.addRead(PATHS);
        user.addDelete(PATHS);
        user.normalize();
        compare(EXPECTED, user.getRead());
        compare(EXPECTED, user.getDelete());
        compare(EMPTY, user.getWrite());
    }

    /**
     * Tests if permissions are properly normalized when the same paths are set for
     * READ and WRITE permissions.
     */
    @Test
    @DisplayName("Tests cumulative the same permissions for read and write")
    public void testCumulativePersmissonsTheSameWrite() {
        user.addRead(PATHS);
        user.addWrite(PATHS);
        user.normalize();
        compare(EXPECTED, user.getRead());
        compare(EMPTY, user.getDelete());
        compare(EXPECTED, user.getWrite());
    }

    /**
     * Tests if permissions are properly normalized when the same paths are set for
     * all permissions.
     */
    @Test
    @DisplayName("Tests cumulative the same permissions for all permissions")
    public void testCumulativePersmissonsTheSameAll() {
        user.addRead(PATHS);
        user.addDelete(PATHS);
        user.addWrite(PATHS);
        user.normalize();
        compare(EXPECTED, user.getRead());
        compare(EXPECTED, user.getDelete());
        compare(EXPECTED, user.getWrite());
    }

    /**
     * Tests if permissions are properly normalized when the READ permissions are
     * not set, but WRITE are.
     */
    @Test
    @DisplayName("Tests if read permissions are the same as write")
    public void testCumulativePersmissonsWriteAndNoRead() {
        user.addWrite(PATHS);
        user.normalize();
        compare(EXPECTED, user.getRead());
        compare(EMPTY, user.getDelete());
        compare(EXPECTED, user.getWrite());
    }

    /**
     * Tests if permissions are properly normalized when the READ permissions are
     * not set, but DELETE are.
     */
    @Test
    @DisplayName("Tests if read permissions are the same as delete")
    public void testCumulativePersmissonsDeleteAndNoRead() {
        user.addDelete(PATHS);
        user.normalize();
        compare(EXPECTED, user.getRead());
        compare(EXPECTED, user.getDelete());
        compare(EMPTY, user.getWrite());
    }

    /**
     * Tests if permissions are properly normalized when different READ and WRITE
     * permissions are set.
     */
    @Test
    @DisplayName("Tests cumulative permissions with different read and write")
    public void testDifferentReadAndWritePermissions() {
        user.addRead(PATHS);
        user.addWrite(PATHS2);
        user.normalize();
        compare(EXPECTED2, user.getRead());
        compare(EMPTY, user.getDelete());
        compare(Arrays.asList(PATHS2), user.getWrite());
    }

    /**
     * Tests if permissions are properly normalized when different READ and DELETE
     * permissions are set.
     */
    @Test
    @DisplayName("Tests cumulative permissions with different read and delete")
    public void testDifferentReadAndDeletePermissions() {
        user.addRead(PATHS);
        user.addDelete(PATHS2);
        user.normalize();
        compare(EXPECTED2, user.getRead());
        compare(EMPTY, user.getWrite());
        compare(Arrays.asList(PATHS2), user.getDelete());
    }

    /**
     * Tests if permissions are properly normalized when different WRITE and DELETE
     * permissions are set.
     */
    @Test
    @DisplayName("Tests cumulative permissions with different delete and write")
    public void testDifferentWriteAndDeletePermissions() {
        user.addWrite(PATHS);
        user.addDelete(PATHS2);
        user.normalize();
        compare(EXPECTED2, user.getRead());
        compare(EXPECTED, user.getWrite());
        compare(Arrays.asList(PATHS2), user.getDelete());
    }

    /**
     * Tests if user can be created without password
     */
    @Test
    @DisplayName("Tests cumulative permissions with different delete and write")
    public void testUserNoPassword() {
        Assertions.assertThrows(IllegalStateException.class, () -> new User("test").normalize());
    }

    /**
     * Compares is paths are as expected.
     *
     * @param expected
     *            expected
     * @param actual
     *            actual
     */
    private void compare(List<String> expected, List<String> actual) {
        Collections.sort(expected);
        Collections.sort(actual);
        assertEquals(expected, actual);
    }
}
