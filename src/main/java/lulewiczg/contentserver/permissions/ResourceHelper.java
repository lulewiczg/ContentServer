package lulewiczg.contentserver.permissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import javax.security.sasl.AuthenticationException;
import javax.servlet.ServletContext;

import lulewiczg.contentserver.utils.Constants;
import lulewiczg.contentserver.utils.Log;
import lulewiczg.contentserver.utils.models.User;

/**
 * Helper class for resource management.
 *
 * @author lulewiczg
 */
public class ResourceHelper {
    private static final String PERMISSIONS_PATH = "/WEB-INF/settings/permissions.properties";
    private static final String SETTINGS_PATH = "/WEB-INF/settings/settings.properties";
    private static final String DIR = "{DIR}";
    private static ResourceHelper instance;
    private Map<String, User> users = new HashMap<>();
    private Map<String, String> mimes = new HashMap<>();
    private int bufferSize;
    private Level logLevel = Level.OFF;
    private Properties settingsProperties;
    private Properties permissionsProperties;
    private String contextPath;
    private String testPath;
    private static boolean encode;

    public static ResourceHelper getInstance() {
        return instance;
    }

    static synchronized void init(String context, String testPath) {
        instance = new ResourceHelper(context, testPath);
        Log.setLevel(instance.logLevel);
    }

    public static synchronized void init(ServletContext context, String testPath) {
        String path = context.getRealPath(Constants.SEP);
        encode = context.getServerInfo().toLowerCase().contains("tomcat");
        instance = new ResourceHelper(path, testPath);
        Log.setLevel(instance.logLevel);
    }

    private ResourceHelper(String context, String path) {
        try {
            path = new File(path).getCanonicalPath();
        } catch (IOException e) {
            Log.getLog().log(e);
        }
        path += "/";
        this.contextPath = normalizePath(context);
        this.testPath = normalizePath(path);
        try {
            loadPermissions(context);
            loadSettings(context);
        } catch (IOException e) {
            Log.getLog().log(e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * Loads users and their permissions to directories.
     *
     * @param contextPath
     *            servlet context
     * @throws IOException
     *             when could not read permissions
     */
    private void loadPermissions(String contextPath) throws IOException {
        Properties props = loadProps(contextPath);
        Map<User, String> toApply = new HashMap<>();
        for (Entry<Object, Object> e : props.entrySet()) {
            String[] keys = e.getKey().toString().split("\\.");
            String name = keys[1];
            User user = users.get(name);
            if (user == null) {
                user = new User(name);
                users.put(name, user);
            }
            String path = e.getValue().toString();
            if (keys.length > 2) {
                if (keys[2].equals(Constants.Setting.EXTENDS)) {
                    toApply.put(user, path);
                } else if (keys[2].equals(Constants.Setting.PASSWORD)) {
                    user.setPassword(path);
                }
            }

            processUserPermissions(keys, user, path);
        }
        List<Entry<User, String>> applyList = getUsersToApply(toApply);
        for (Map.Entry<User, String> u : applyList) {
            u.getKey().apply(users.get(u.getValue()));
        }
        addGuestPermissions();
        for (Map.Entry<String, User> u : users.entrySet()) {
            u.getValue().normalize();
        }
    }

    /**
     * Adds guest permissions for every user.
     */
    private void addGuestPermissions() {
        User guest = users.get(Constants.GUEST);
        if (guest != null) {
            for (Map.Entry<String, User> u : users.entrySet()) {
                User value = u.getValue();
                if (value != guest) {
                    value.apply(guest);
                }
            }
        }
    }

    /**
     * Returns list of user for further processing.
     *
     * @param toApply
     *            users
     * @return list of users
     */
    private List<Entry<User, String>> getUsersToApply(Map<User, String> toApply) {
        List<Entry<User, String>> applyList = new ArrayList<>(toApply.entrySet());
        Collections.sort(applyList, new Comparator<Entry<User, String>>() {
            @Override
            public int compare(Entry<User, String> o1, Entry<User, String> o2) {
                if (o1.getKey().getName().equals(o2.getValue())) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        return applyList;
    }

    /**
     * Sets permissions for user.
     *
     * @param keys
     *            splitted property key
     * @param user
     *            user
     * @param value
     *            paths
     * @throws IOException
     *             when could not determine current path
     */
    private void processUserPermissions(String[] keys, User user, String value) throws IOException {
        if (keys.length > 3 && keys[2].equals(Constants.Setting.PERMISSION)) {
            if (value.contains(DIR)) {
                value = value.replace(DIR, testPath);
            }
            String[] values = value.split(";");
            if (values.length > 0) {
                Persmission type = Persmission.valueOf(keys[3].toUpperCase());
                switch (type) {
                    case READ:
                        user.addRead(values);
                        break;
                    case WRITE:
                        user.addWrite(values);
                        break;
                    case DELETE:
                        user.addDelete(values);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown permission type: " + type);
                }
            }
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
    }

    /**
     * Loads properties.
     *
     * @param contextPath
     *            servlet context
     * @return properties
     */
    private Properties loadProps(String contextPath) {
        permissionsProperties = new Properties();
        try {
            String path = contextPath + PERMISSIONS_PATH;
            try (InputStream input = new FileInputStream(path)) {
                permissionsProperties.load(input);
            }
        } catch (Exception e) {
            Log.getLog().log(e);
            throw new IllegalArgumentException(e);
        }
        return permissionsProperties;
    }

    /**
     * Checks if user has access to directory
     *
     * @param directory
     *            directory
     * @param name
     *            user name
     * @return true if has access
     */
    public boolean hasReadAccess(String directory, String name) {
        User user = getUserByName(name);
        if (user == null) {
            return false;
        }
        if (user.getName().equals(Constants.ADMIN)) {
            return true;
        }
        File f = new File(directory);
        boolean dir = f.exists() && f.isDirectory();

        String path;
        try {
            path = f.getCanonicalPath();
        } catch (IOException e) {
            Log.getLog().log(e);
            return false;
        }
        if (dir && !path.endsWith(Constants.SEP)) {
            path += Constants.SEP;
        }
        path = normalizePath(path);
        for (String s : user.getRead()) {
            if (startsWith(path, s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets user by name.
     *
     * @param name
     *            user name
     * @return user
     */
    private User getUserByName(String name) {
        if (name == null || name.isEmpty()) {
            name = Constants.GUEST;
        }
        User user = users.get(name);
        return user;
    }

    /**
     * Gets available directories for user
     *
     * @param userName
     *            user name
     * @return list of dirs
     */
    public List<String> getAvailablePaths(String userName) {
        User u = getUserByName(userName);
        return u.getRead();
    }

    /**
     * Logs in user
     *
     * @param login
     *            login
     * @param password
     *            password
     * @throws AuthenticationException
     *             when login or password were incorrect
     */
    public void login(String login, String password) throws AuthenticationException {
        User user = users.get(login);
        String sha = SHA1(password);
        if (user != null && user.getPassword().toUpperCase().equals(sha)) {
            Log.getLog().logInfo("Logged: " + login);
            return;
        }
        Log.getLog().logError("Invalid password: " + login);
        throw new AuthenticationException("Invalid login or password");
    }

    /**
     * Generates SHA for given string.
     *
     * @param text
     *            text to hash
     * @return hashed string
     */
    public static String SHA1(String text) {
        byte[] textBytes;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
            textBytes = text.getBytes("UTF-8");
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        md.update(textBytes, 0, textBytes.length);
        byte[] sha1hash = md.digest();
        return new BigInteger(1, sha1hash).toString(16).toUpperCase();
    }

    /**
     * Normalizes paths to avoid duplicates and to set permissions for the shortest
     * path as possible.
     *
     * @param list
     *            paths to normalize
     */
    public static void normalize(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            String path = list.get(i).replaceAll("\\\\+", Constants.SEP).replaceAll("\\/+", Constants.SEP);
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            list.set(i, path);
        }
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                String path = list.get(i);
                String path2 = list.get(j);
                if (Constants.EMPTY.equals(path) || Constants.EMPTY.equals(path2)) {
                    continue;
                }
                if (startsWith(path, path2)) {
                    list.set(i, Constants.EMPTY);
                } else if (startsWith(path2, path)) {
                    list.set(j, Constants.EMPTY);
                }
            }
        }
        Iterator<String> i = list.iterator();
        while (i.hasNext()) {
            if (i.next().equals(Constants.EMPTY)) {
                i.remove();
            }
        }
        Collections.sort(list);
    }

    /**
     * Checks if one path is subpath of another.
     *
     * @param path
     *            base path
     * @param path2
     *            path to check
     * @return true if subpath
     */
    private static boolean startsWith(String path, String path2) {
        if (path2.length() > path.length()) {
            return false;
        }
        String[] split = path.split("/");
        String[] split2 = path2.split("/");
        int size = split.length < split2.length ? split.length : split2.length;
        for (int i = 0; i < size; i++) {
            if (!split[i].equals(split2[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Obtains MIME type for given file.
     *
     * @param name
     *            file name
     * @return MIME
     */
    public String getMIME(String name) {
        String type = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
        String mime = mimes.get(type);
        if (mime == null) {
            mime = Constants.Setting.APPLICATION_OCTET_STREAM;
        }
        return mime;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public Properties getSettingsProperties() {
        return settingsProperties;
    }

    public Properties getPermissionsProperties() {
        return permissionsProperties;
    }

    /**
     * Saves settings.
     *
     * @throws FileNotFoundException
     *             the FileNotFoundException
     * @throws IOException
     *             the IOException
     */
    public synchronized void saveSettings() throws FileNotFoundException, IOException {
        try (FileOutputStream os = new FileOutputStream(new File(contextPath + SETTINGS_PATH))) {
            settingsProperties.store(os, null);
        }
        init(contextPath, testPath);
    }

    /**
     * Normalizes path.
     *
     * @param path
     *            path
     * @return normalized path
     */
    public static String normalizePath(String path) {
        return path.replaceAll("\\\\+", Constants.SEP).replaceAll(String.format("\\%s+", Constants.SEP), Constants.SEP);
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
