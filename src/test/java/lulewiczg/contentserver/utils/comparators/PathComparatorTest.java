package lulewiczg.contentserver.utils.comparators;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import lulewiczg.contentserver.test.utils.TestUtil;
import lulewiczg.contentserver.utils.models.Dir;

/**
 * Tests directory comparator.
 *
 * @author lulewiczg
 */
public class PathComparatorTest {

    @DisplayName("Paths sorting")
    @ParameterizedTest(name = "''{0}'' should be resolved to ''{1}''")
    @CsvFileSource(resources = "/paths-sort.csv")
    public void testRead(String actual, String expected) {
        List<Dir> act = TestUtil.parsePathsPlain(actual);
        List<Dir> exp = TestUtil.parsePathsPlain(expected);
        Assertions.assertEquals(exp.size(), act.size(), "Invalid data length");
        Collections.sort(act);
        Assertions.assertEquals(exp, act);
    }

}
