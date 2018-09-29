package com.github.lulewiczg.contentserver.utils.models;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.opentest4j.MultipleFailuresError;

/**
 * Tests for Setting model.
 *
 * @author lulewiczg
 */
public class SettingTest {

    @DisplayName("Settings parsing")
    @ParameterizedTest(name = "''{0}'' should be parsed properly")
    @CsvFileSource(resources = "/data/csv/settings.csv")
    public void testRead(String toParse) {
        test(toParse);
    }

    @Test
    @DisplayName("Parse invalid logger level")
    public void testReadInvalidLevel() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> test("logger.level, test"));
        Assertions.assertEquals("Bad level \"test\"", e.getMessage());
    }

    @DisplayName("Parse invalid buffer size")
    @ParameterizedTest(name = "''{0}'' should not be parsed")
    @ValueSource(strings = { "buffer.size, -1", "buffer.size, -100", "buffer.size, 0", "buffer.size, abc",
            "buffer.size, 99999999999999999", })
    public void testReadInvalidBufferSize(String src) {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> test(src));
        Assertions.assertEquals(src.split("\\,")[1].trim() + " is not valid buffer size!", e.getMessage());
    }

    /**
     * Tests loading settings for both single and multiple values sent.
     *
     * @param toParse
     *            toParse
     * @throws MultipleFailuresError
     *             the MultipleFailuresError
     */
    private void test(String toParse) throws MultipleFailuresError {
        String[] split = toParse.split("\\,");
        testParam(split, i -> new String[] { i }, i -> ((String[]) i)[0]);
        testParam(split, i -> i, i -> i.toString());
    }

    /**
     * Tests loading settings.
     *
     * @param split
     *            data to load
     * @param mapper
     *            mapper function
     * @param unmapper
     *            unmapper function
     * @throws MultipleFailuresError
     *             the MultipleFailuresError
     */
    private void testParam(String[] split, Function<String, Object> mapper, Function<Object, String> unmapper)
            throws MultipleFailuresError {
        Map<String, Object> map = IntStream.range(0, split.length - 1).filter(i -> i % 2 == 0).boxed()
                .collect(Collectors.toMap(i -> split[i].trim(), i -> mapper.apply(split[i + 1].trim())));
        List<Setting> settings = Setting.load(map);
        Assertions.assertEquals(map.size(), settings.size());
        Assertions.assertAll(settings.stream().map(i -> () -> {
            Object val = map.get(i.getName());
            Assertions.assertEquals(unmapper.apply(val), i.getValue());
        }));
    }
}
