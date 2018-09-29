package com.github.lulewiczg.contentserver.test.utils;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents test run settings.
 *
 * @author lulewiczg
 */
public final class TestSettings {

    private SeleniumLocation location;
    private EnumSet<TestMode> modes = EnumSet.noneOf(TestMode.class);

    /**
     * Adds selenium target.
     *
     * @param location
     *            location
     * @return this
     */
    public TestSettings with(SeleniumLocation location) {
        this.location = location;
        return this;
    }

    /**
     * Adds test modes
     *
     * @param modes
     *            modes
     * @return this
     */
    public TestSettings with(TestMode... modes) {
        List<TestMode> collect = Arrays.stream(modes).collect(Collectors.toList());
        this.modes = EnumSet.copyOf(collect);
        return this;
    }

    public EnumSet<TestMode> getModes() {
        return modes;
    }

    /**
     * Checks if test mode was set
     *
     * @param o
     *            mode
     * @return true if set
     */
    public boolean contains(TestMode o) {
        return modes.contains(o);
    }

    /**
     * Returns test URL.
     *
     * @return URL
     */
    public String getUrl() {
        return location.getUrl();
    }

    public SeleniumLocation getLocation() {
        return location;
    }
}
