package lulewiczg.contentserver.test.utils;

import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lulewiczg.contentserver.permissions.ResourceHelper;
import lulewiczg.contentserver.utils.Constants;
import lulewiczg.contentserver.utils.models.Dir;

public final class TestUtil {
    public static final String LOC = "src/test/resources/data/";
    private static final String COLON = "\\,";

    /**
     * Swaps helper for tests
     *
     * @param helper
     *            mocked helper
     * @return
     * @throws ReflectiveOperationException
     *             the ReflectiveOperationException
     */
    public static ResourceHelper mockHelper() throws ReflectiveOperationException {
        ResourceHelper helper = mock(ResourceHelper.class);
        Field field = ResourceHelper.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(null, helper);
        return helper;
    }

    /**
     * Parses expected dirs in given directory.
     *
     * @param basePath
     *            base path
     * @param expected
     *            expected paths
     * @return absolute expected paths
     */
    public static List<String> parsePaths(String basePath, String expected) {
        return Arrays.stream(expected.split(COLON)).map(String::trim).filter(i -> !i.isEmpty())
                .map(i -> LOC + basePath + Constants.SEP + i).collect(Collectors.toList());
    }

    /**
     * Parses plains paths
     *
     * @param paths
     *            paths
     * @return parsed paths
     */
    public static List<Dir> parsePathsPlain(String paths) {
        return Arrays.stream(paths.split(COLON)).map(String::trim).filter(i -> !i.isEmpty())
                .map(i -> new Dir(i, 0, i, !i.endsWith(Constants.SEP))).collect(Collectors.toList());
    }
}
