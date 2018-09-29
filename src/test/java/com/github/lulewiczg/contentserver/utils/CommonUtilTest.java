package com.github.lulewiczg.contentserver.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.security.sasl.AuthenticationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

/**
 * Test class for CommonUtil.
 *
 * @author lulewiczg
 */
public class CommonUtilTest {

    @DisplayName("Path normalization")
    @ParameterizedTest(name = "''{0}'' should be normalized to ''{1}''")
    @CsvFileSource(resources = "/data/csv/paths.csv")
    public void testReadExtendedWriteDelete(String path, String expectedPath) {
        assertEquals(expectedPath, CommonUtil.normalizePath(path));
    }

    @DisplayName("Hash is porperly calculated")
    @ParameterizedTest(name = "''{1}'' should be hash of ''{0}''")
    @CsvFileSource(resources = "/data/csv/sha.csv")
    public void testHash(String text, String sha) throws AuthenticationException {
        assertEquals(sha, CommonUtil.sha1(text));
    }
}
