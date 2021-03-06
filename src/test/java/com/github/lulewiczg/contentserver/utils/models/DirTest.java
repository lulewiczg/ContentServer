package com.github.lulewiczg.contentserver.utils.models;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import com.github.lulewiczg.contentserver.test.utils.TestUtil;
import com.github.lulewiczg.contentserver.utils.CommonUtil;

/**
 * Tests directory model.
 *
 * @author lulewiczg
 */
public class DirTest {

    @DisplayName("Size formatting")
    @ParameterizedTest(name = "''{0}'' should be formatted to ''{1}''")
    @CsvFileSource(resources = "/data/csv/file-sizes.csv")
    public void testRead(Long size, String expected) {
        String formatted = new Dir("", size, "", 0, true).getSize();
        Assertions.assertEquals(expected, formatted);
    }

    @DisplayName("Listing files")
    @ParameterizedTest(name = "''{1}'' should be listed in ''{0}''")
    @CsvFileSource(resources = "/data/csv/directory-listing.csv")
    public void testRead(String path, String expected) throws IOException {
        List<String> tmp = TestUtil.parsePaths(path, expected);
        List<String> exp = new ArrayList<>();
        for (String s : tmp) {
            String absolutePath = CommonUtil.normalizePath(new File(s).getCanonicalPath());
            exp.add(absolutePath);
        }
        File f = new File(TestUtil.LOC + path);
        List<String> files = Dir.getFiles(f).stream().map(i -> i.getPath()).collect(Collectors.toList());
        Assertions.assertEquals(exp, files);
    }

}
