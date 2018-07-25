package lulewiczg.contentserver.utils.comparators;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import lulewiczg.contentserver.utils.Constants;
import lulewiczg.contentserver.utils.models.Dir;

/**
 * Tests directory comparator.
 * 
 * @author lulewiczg
 */
public class PathComparatorTest {

    private static final String COLON = "\\,";

    @DisplayName("Paths sorting")
    @ParameterizedTest(name = "''{0}'' should be resolved to ''{1}''")
    @CsvFileSource(resources = "/paths-sort.csv")
    public void testRead(String actual, String expected) {
        List<Dir> act = Arrays.stream(actual.split(COLON)).map(String::trim)
                .map(i -> new Dir(i, 0, i, !i.endsWith(Constants.SEP))).collect(Collectors.toList());
        List<Dir> exp = Arrays.stream(expected.split(COLON)).map(String::trim)
                .map(i -> new Dir(i, 0, i, !i.endsWith(Constants.SEP))).collect(Collectors.toList());
        Assert.assertEquals("Invalid data length", exp.size(), act.size());
        Collections.sort(act);
        Assert.assertEquals(exp, act);
    }
}
