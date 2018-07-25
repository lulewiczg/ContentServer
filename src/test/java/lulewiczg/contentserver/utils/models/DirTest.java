package lulewiczg.contentserver.utils.models;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import lulewiczg.contentserver.permissions.ResourceHelper;
import lulewiczg.contentserver.utils.Constants;

/**
 * Tests directory model.
 * 
 * @author lulewiczg
 */
public class DirTest {

    private static final String LOC = "src/test/resources/";
    private static final String COLON = "\\,";

    @DisplayName("Size formatting")
    @ParameterizedTest(name = "''{0}'' should be formatted to ''{1}''")
    @CsvFileSource(resources = "/file-sizes.csv")
    public void testRead(Long size, String expected) {
        String formatted = new Dir("", size, "", true).getSize();
        Assert.assertEquals(expected, formatted);
    }

    @DisplayName("Listing files")
    @ParameterizedTest(name = "''{1}'' should be listed in ''{0}''")
    @CsvFileSource(resources = "/directory-listing.csv")
    public void testRead(String path, String expected) throws IOException {
        List<String> tmp = Arrays.stream(expected.split(COLON)).map(String::trim)
                .map(i -> LOC + path + Constants.SEP + i).collect(Collectors.toList());
        List<String> exp = new ArrayList<>();
        for (String s : tmp) {
            String absolutePath = ResourceHelper.normalizePath(new File(s).getCanonicalPath());
            exp.add(absolutePath);
        }
        File f = new File(LOC + path);
        Assert.assertTrue("Invalid path", f.exists());
        List<String> files = Dir.getFiles(f).stream().map(i -> i.getPath()).collect(Collectors.toList());
        Assert.assertEquals(exp, files);
    }
}
