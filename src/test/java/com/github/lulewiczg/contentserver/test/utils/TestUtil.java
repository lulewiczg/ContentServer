package com.github.lulewiczg.contentserver.test.utils;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;

import com.github.lulewiczg.contentserver.permissions.ResourceHelper;
import com.github.lulewiczg.contentserver.utils.Constants;
import com.github.lulewiczg.contentserver.utils.models.Dir;

/**
 * Test utility.
 * 
 * @author lulewiczg
 */
public final class TestUtil {
    public static final String LOC = "src/main/resources/data/";
    private static final String COLON = "\\,";
    public static final String URL = "http://localhost:8080/ContentServer";

    /**
     * Swaps helper for tests
     *
     * @param helper
     *            mocked helper
     * @return
     * @throws ReflectiveOperationException
     *             the ReflectiveOperationException
     */
    public static ResourceHelper mockHelper(ServletContext context) throws ReflectiveOperationException {
        ResourceHelper helper = mock(ResourceHelper.class);
        when(context.getAttribute(eq(ResourceHelper.HELPER))).thenReturn(helper);
        return ResourceHelper.get(context);
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
