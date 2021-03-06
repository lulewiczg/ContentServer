package com.github.lulewiczg.contentserver.test.utils;

import com.github.lulewiczg.contentserver.utils.Constants;

/**
 * Information for current breadcrumb.
 */
public class NavigationData {
    private String label;
    private String url;
    private String path;
    private boolean enabled;

    public String getLabel() {
        return label;
    }

    public String getUrl() {
        if (!enabled) {
            return null;
        }
        return url + path;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getPath() {
        return path;
    }

    private NavigationData(String label, String url, String path, boolean enabled) {
        this.label = label + Constants.SEP;
        this.url = url;
        this.path = path;
        this.enabled = enabled;
    }

    public static NavigationData create(String url, String label, boolean enabled) {
        return new NavigationData(label, url, label, enabled);
    }

    public static NavigationData create(NavigationData data, String label, boolean enabled) {
        return new NavigationData(label, data.url, data.path + Constants.SEP + label, enabled);
    }

    @Override
    public String toString() {
        return String.format("[Label: %s\nURL: %s\nEnabled: %b]", label, url, enabled);
    }
}