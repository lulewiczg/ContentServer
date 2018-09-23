package com.github.lulewiczg.contentserver.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;

import javax.security.sasl.AuthenticationException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import com.github.lulewiczg.contentserver.utils.models.User;

/**
 * Util class for resource management.
 *
 * @author lulewiczg
 */
public class ResourceUtil {
    public static final String NAME = ResourceUtil.class.getName();
    private static final String PERMISSIONS_PATH = "/WEB-INF/settings/permissions.properties";
    private static final String DIR = "{DIR}";
    private Map<String, User> users = new HashMap<>();
    private String contextPath;
    private String testPath;

    public static ResourceUtil get(ServletContext context) {
        return (ResourceUtil) context.getAttribute(NAME);
    }

    public static synchronized ResourceUtil init(ServletContext context, String testPath) {
        ResourceUtil util = new ResourceUtil(context, testPath);
        context.setAttribute(NAME, util);
        return util;
    }

    private ResourceUtil(ServletContext servletContext, String path) {
        String context = CommonUtil.getContextPath(servletContext);
        try {
            path = new File(path).getCanonicalPath();
        } catch (IOException e) {
            Log.getLog().log(e);
        }
        path += Constants.SEP;
        this.contextPath = CommonUtil.normalizePath(context);
        this.testPath = CommonUtil.normalizePath(path);
        Log.getLog().logDebug("Context path: " + contextPath);
        Log.getLog().logDebug("Test path: " + testPath);
        loadPermissions(this.contextPath);
    }

    /**
     * Loads users and their permissions to directories.
     *
     * @param contextPath
     *            servlet context
     */
    private void loadPermissions(String contextPath) {
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
            if (keys.length <= 2) {
                throw new IllegalArgumentException(String.format("Key %s is invalid!", e.getKey()));
            }
            if (keys[2].equals(Constants.Setting.EXTENDS)) {
                toApply.put(user, path);
            } else if (keys[2].equals(Constants.Setting.PASSWORD)) {
                user.setPassword(path);
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
        logUsers();
    }

    /**
     * Logs users.
     */
    private void logUsers() {
        if (Log.isEnabled(Level.FINEST)) {
            Log.getLog().logDebug("Loaded users:");
            for (Map.Entry<String, User> u : users.entrySet()) {
                User user = u.getValue();
                Log.getLog().logDebug("User: " + user.getName());
                Log.getLog().logDebug("   READ:");
                logPaths(user.getRead());
                Log.getLog().logDebug("   WRITE:");
                logPaths(user.getWrite());
                Log.getLog().logDebug("   DELETE:");
                logPaths(user.getDelete());
            }
        }
    }

    /**
     * Logs paths.
     *
     * @param paths
     *            paths
     */
    private void logPaths(List<String> paths) {
        for (String s : paths) {
            Log.getLog().logDebug("       " + s);
        }
        Log.getLog().logDebug("==Paths end==");
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
     *            split property key
     * @param user
     *            user
     * @param value
     *            paths
     */
    private void processUserPermissions(String[] keys, User user, String value) {
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
                }
            }
        }
    }

    /**
     * Loads properties.
     *
     * @param contextPath
     *            servlet context
     * @return properties
     */
    private Properties loadProps(String contextPath) {
        Properties permissionsProperties = new Properties();
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
        path = CommonUtil.normalizePath(path);
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
        return users.get(name);
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
     * @param session
     *            session
     * @throws AuthenticationException
     *             when login or password were incorrect
     */
    public void login(String login, String password, HttpSession session) throws AuthenticationException {
        User user = users.get(login);
        String sha = CommonUtil.sha1(password);
        if (user != null && user.getPassword().equalsIgnoreCase(sha)) {
            Log.getLog().logInfo("Logged: " + login);
            session.setAttribute(Constants.Web.USER, login);
            return;
        }
        Log.getLog().logInfo("Invalid password: " + login);
        throw new AuthenticationException("Invalid login or password");
    }

    /**
     * Normalizes paths to avoid duplicates and to set permissions for the shortest path as possible.
     *
     * @param list
     *            paths to normalize
     */
    public static void normalize(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            String path = list.get(i).replaceAll("\\\\+", Constants.SEP).replaceAll("\\/+", Constants.SEP);
            if (path.endsWith(Constants.SEP)) {
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
        removeEmptyPaths(list);
        Collections.sort(list);
    }

    /**
     * Removes empty paths.
     *
     * @param list
     *            list to clean
     */
    private static void removeEmptyPaths(List<String> list) {
        Iterator<String> i = list.iterator();
        while (i.hasNext()) {
            if (i.next().equals(Constants.EMPTY)) {
                i.remove();
            }
        }
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
        String[] split = path.split(Constants.SEP);
        String[] split2 = path2.split(Constants.SEP);
        int size = split.length < split2.length ? split.length : split2.length;
        for (int i = 0; i < size; i++) {
            if (!split[i].equals(split2[i])) {
                return false;
            }
        }
        return true;
    }

}
