package com.github.lulewiczg.contentserver.test.utils;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;

import com.github.lulewiczg.contentserver.utils.Constants;
import com.github.lulewiczg.contentserver.utils.ResourceUtil;
import com.github.lulewiczg.contentserver.utils.SettingsUtil;
import com.github.lulewiczg.contentserver.utils.models.Dir;

/**
 * Test utility.
 *
 * @author lulewiczg
 */
public final class TestUtil {
    public static final String LOC = "src/main/resources/data/";
    private static final String SPLIT_REGEX = "\\, ";
    public static final TestSettings MODE = new TestSettings().with(SeleniumLocation.LOCAL);

    /**
     * Mocks ResourceUtil for tests.
     *
     * @param util
     *            mocked util
     * @return
     * @throws ReflectiveOperationException
     *             the ReflectiveOperationException
     */
    public static ResourceUtil mockResourceUtil(ServletContext context) throws ReflectiveOperationException {
        ResourceUtil util = mock(ResourceUtil.class);
        when(context.getAttribute(eq(ResourceUtil.NAME))).thenReturn(util);
        return ResourceUtil.get(context);
    }

    /**
     * Mocks SettingsUtil for tests.
     *
     * @param util
     *            mocked util
     * @return
     * @throws ReflectiveOperationException
     *             the ReflectiveOperationException
     */
    public static SettingsUtil mockSettingsUtil(ServletContext context) throws ReflectiveOperationException {
        SettingsUtil util = mock(SettingsUtil.class);
        when(context.getAttribute(eq(SettingsUtil.NAME))).thenReturn(util);
        return SettingsUtil.get(context);
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
        return Arrays.stream(expected.split(SPLIT_REGEX)).map(String::trim).filter(i -> !i.isEmpty())
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
        return Arrays.stream(paths.split(SPLIT_REGEX)).map(String::trim).filter(i -> !i.isEmpty())
                .map(i -> new Dir(i, 0, i, 0, !i.endsWith(Constants.SEP))).collect(Collectors.toList());
    }
}
