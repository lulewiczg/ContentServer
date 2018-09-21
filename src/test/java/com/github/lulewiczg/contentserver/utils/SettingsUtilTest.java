package com.github.lulewiczg.contentserver.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletContext;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import com.github.lulewiczg.contentserver.test.utils.TestUtil;
import com.github.lulewiczg.contentserver.utils.Constants;
import com.github.lulewiczg.contentserver.utils.SettingsUtil;

/**
 * Tests for SettingsUtil.
 * 
 * @author lulewiczg
 */
public class SettingsUtilTest {
    // Grzegorz Brzęczyczykiewicz i Że li Popą
    private static final byte[] TEST_STRING = new byte[] { 71, 114, 122, 101, 103, 111, 114, 122, 32, 66, 114, 122, -60,
            -103, 99, 122, 121, 99, 122, 121, 107, 105, 101, 119, 105, 99, 122, 32, 105, 32, -59, -69, 101, 32, 108,
            105, 32, 80, 111, 112, -60, -123 };
    private ServletContext context = mock(ServletContext.class);

    private static final String CONTEXT = TestUtil.LOC + "testContexts/";

    @DisplayName("MIME type resolution")
    @ParameterizedTest(name = "''{0}'' should be resolved to ''{1}''")
    @CsvFileSource(resources = "/data/csv/mimes.csv")
    public void testResolveMIMEs(String fileName, String mime) {
        SettingsUtil instance = getInstance(1);
        assertEquals(mime, instance.getMIME(fileName));
    }

    @Test
    @DisplayName("Parameter decoding is perfomed on Tomcat")
    public void testDecodeParam() throws IOException {
        getInstance(1);
        String test = new String(TEST_STRING, StandardCharsets.ISO_8859_1);
        Assertions.assertArrayEquals(TEST_STRING, SettingsUtil.decodeParam(test).getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Parameter decoding is not performed on other servlet container")
    public void testDontDecodeParam() throws IOException {
        getInstance(1, "jetty");
        String test = new String(TEST_STRING, StandardCharsets.UTF_8);
        Assertions.assertArrayEquals(TEST_STRING, SettingsUtil.decodeParam(test).getBytes(StandardCharsets.UTF_8));
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
    private SettingsUtil getInstance(int contextIndex, String server) {
        when(context.getRealPath(eq(Constants.SEP))).thenReturn(CONTEXT + contextIndex);
        when(context.getServerInfo()).thenReturn(server);
        SettingsUtil util = SettingsUtil.init(context);
        when(context.getAttribute(eq(SettingsUtil.NAME))).thenReturn(util);
        SettingsUtil instance = SettingsUtil.get(context);
        verify(context, times(1)).getAttribute(eq(SettingsUtil.NAME));
        return instance;
    }

    /**
     * Obtains util instance.
     * 
     * @param contextIndex
     *            context index
     * @return util
     */
    private SettingsUtil getInstance(int contextIndex) {
        return getInstance(contextIndex, "tomcat");
    }
}
