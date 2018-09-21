package com.github.lulewiczg.contentserver.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import javax.servlet.ServletContext;

/**
 * Class for settings logic.
 * 
 * @author lulewiczg
 */
public class SettingsUtil {

    public static final String NAME = SettingsUtil.class.getName();
    private static final String SETTINGS_PATH = "/WEB-INF/settings/settings.properties";
    private Map<String, String> mimes = new HashMap<>();
    private int bufferSize;
    private Properties settingsProperties;
    private ServletContext servletContext;
    private String contextPath;
    private static boolean encode;
    private Level logLevel = Level.OFF;

    public static SettingsUtil get(ServletContext context) {
        return (SettingsUtil) context.getAttribute(NAME);
    }

    public static synchronized SettingsUtil init(ServletContext context) {
        SettingsUtil util = new SettingsUtil(context);
        Log.setLevel(util.logLevel);
        encode = context.getServerInfo().toLowerCase().contains("tomcat");
        context.setAttribute(NAME, util);
        return util;
    }

    private SettingsUtil(ServletContext servletContext) {
        this.servletContext = servletContext;
        String context = CommonUtil.getContextPath(servletContext);
        this.contextPath = CommonUtil.normalizePath(context);
        Log.getLog().logDebug("Context path: " + contextPath);
        try {
            loadSettings(this.contextPath);
        } catch (IOException e) {
            Log.getLog().log(e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * Loads settings.
     *
     * @param path
     *            servlet context
     * @throws IOException
     *             when could not read settings
     */
    private void loadSettings(String path) throws IOException {
        settingsProperties = new Properties();
        try (InputStream input = new FileInputStream(path + SETTINGS_PATH)) {
            settingsProperties.load(input);
        }
        Set<Entry<Object, Object>> entrySet = settingsProperties.entrySet();
        for (Map.Entry<Object, Object> prop : entrySet) {
            String key = prop.getKey().toString();
            if (key.startsWith(Constants.Setting.MIME)) {
                mimes.put(key.substring(5), prop.getValue().toString());
            }
        }
        bufferSize = Integer.parseInt(settingsProperties.getProperty(Constants.Setting.BUFFER_SIZE)) * 1024;
        logLevel = Level.parse(settingsProperties.getProperty(Constants.Setting.LOGGER_LEVEL));
        logSettings();
    }

    /**
     * Logs settings.
     */
    private void logSettings() {
        if (Log.isEnabled(Level.FINEST)) {
            Log.getLog().logDebug("Buffer size: " + bufferSize);
            Log.getLog().logDebug("Log level: " + logLevel);
            Log.getLog().logDebug("Loaded mimes: ");
            for (Map.Entry<String, String> m : mimes.entrySet()) {
                Log.getLog().logDebug(m);
            }
            Log.getLog().logDebug("==Mimes end==");
        }
    }

    /**
     * Saves settings.
     *
     * @throws IOException
     *             the IOException
     */
    public synchronized void saveSettings() throws IOException {
        try (FileOutputStream os = new FileOutputStream(new File(contextPath + SETTINGS_PATH))) {
            settingsProperties.store(os, null);
        }
        init(servletContext);
        Log.getLog().logDebug("Settings saved!");
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public Properties getSettingsProperties() {
        return settingsProperties;
    }

    /**
     * Obtains MIME type for given file.
     *
     * @param name
     *            file name
     * @return MIME
     */
    public String getMIME(String name) {
        String type = name.substring(name.lastIndexOf('.') + 1).toLowerCase();
        String mime = mimes.get(type);
        if (mime == null) {
            mime = servletContext.getMimeType(name);
        }
        return mime;
    }

    /**
     * Converts parameter to UTF8 if run on Tomcat.
     *
     * @param param
     *            param
     * @return UTF8 param
     */
    public static String decodeParam(String param) {
        if (!encode || param == null) {
            return param;
        }
        byte[] bytes = param.getBytes(StandardCharsets.ISO_8859_1);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
