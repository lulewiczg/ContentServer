package lulewiczg.permissions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

public class ResourceHelperTest {

    private static final String DESC = "''{0}'' should {1} have access to ''{2}''";
    private static final String CONTEXT = "src/test/resources/testContexts/";
    private static final String LOC = "src/test/resources/structure/";

    @DisplayName("Tests read permissions with no admin")
    @ParameterizedTest(name = DESC)
    @CsvFileSource(resources = "/context1.csv")
    public void testReadNoAdmin(String name, boolean access, String path) {
        ResourceHelper.init(CONTEXT + 1);
        ResourceHelper instance = ResourceHelper.getInstance();
        assertEquals(access, instance.hasReadAccess(LOC + path, name));
    }

    @DisplayName("Tests read permissions")
    @ParameterizedTest(name = DESC)
    @CsvFileSource(resources = "/context2.csv")
    public void testRead(String name, boolean access, String path) {
        ResourceHelper.init(CONTEXT + 2);
        ResourceHelper instance = ResourceHelper.getInstance();
        assertEquals(access, instance.hasReadAccess(LOC + path, name));
    }

    @DisplayName("Tests read permissions with extended user")
    @ParameterizedTest(name = DESC)
    @CsvFileSource(resources = "/context3.csv")
    public void testReadExtended(String name, boolean access, String path) {
        ResourceHelper.init(CONTEXT + 3);
        ResourceHelper instance = ResourceHelper.getInstance();
        assertEquals(access, instance.hasReadAccess(LOC + path, name));
    }

    @DisplayName("Tests read permissions with extended user and different permissions")
    @ParameterizedTest(name = DESC)
    @CsvFileSource(resources = "/context4.csv")
    public void testReadExtendedWriteDelete(String name, boolean access, String path) {
        ResourceHelper.init(CONTEXT + 4);
        ResourceHelper instance = ResourceHelper.getInstance();
        assertEquals(access, instance.hasReadAccess(LOC + path, name));
    }
}
