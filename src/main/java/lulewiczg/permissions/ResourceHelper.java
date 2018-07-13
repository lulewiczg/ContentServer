package lulewiczg.permissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
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

import javax.security.sasl.AuthenticationException;
import javax.servlet.ServletContext;

import lulewiczg.utils.Constants;
import lulewiczg.utils.Log;

/**
 * Helper class for resource management.
 * 
 * @author lulewiczg
 */
public class ResourceHelper {
	private static ResourceHelper instance;
	private Map<String, User> users = new HashMap<>();
	private Map<String, String> mimes = new HashMap<>();
	private int bufferSize;
	private boolean logEnabled = true;

	/**
	 * Gets instance.
	 * 
	 * @param context servlet context.
	 * @return instance
	 */
	public static synchronized ResourceHelper getInstance(ServletContext context) {
		if (instance == null) {
			instance = new ResourceHelper(context);
		}
		return instance;
	}

	private ResourceHelper(ServletContext context) {
		loadPermissions(context);
		loadSettings(context);
	}

	/**
	 * Loads users and their permissions to directories.
	 * 
	 * @param context servlet context
	 */
	private void loadPermissions(ServletContext context) {
		Properties props = loadProps(context);
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
	 * @param toApply users
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
	 * @param keys  splitted property key
	 * @param user  user
	 * @param value paths
	 */
	private void processUserPermissions(String[] keys, User user, String value) {
		if (keys.length > 3 && keys[2].equals(Constants.Setting.PERMISSION)) {
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
	 * @param context servlet context
	 */
	private void loadSettings(ServletContext context) {
		Properties p = new Properties();
		try (InputStream input = new FileInputStream(
				context.getRealPath(Constants.SEP) + "/WEB-INF/settings/settings.properties")) {
			p.load(input);
		} catch (IOException e) {
			Log.getLog().log(e);
			throw new IllegalStateException(e);
		}
		Set<Entry<Object, Object>> entrySet = p.entrySet();
		for (Map.Entry<Object, Object> prop : entrySet) {
			String key = prop.getKey().toString();
			if (key.startsWith(Constants.Setting.MIME)) {
				mimes.put(key.substring(5), prop.getValue().toString());
			}
		}
		bufferSize = Integer.parseInt(p.getProperty(Constants.Setting.BUFFER_SIZE)) * 1024;
		logEnabled = Boolean.parseBoolean(p.getProperty(Constants.Setting.LOGGER_ENABLED));
	}

	/**
	 * Loads properties.
	 * 
	 * @param context servlet context
	 * @return properties
	 */
	private Properties loadProps(ServletContext context) {
		Properties props = new Properties();
		try {
			String contextPath = context.getRealPath(Constants.SEP);
			String path = contextPath + "/WEB-INF/settings/permissions.properties";
			try (InputStream input = new FileInputStream(path)) {
				props.load(input);
			}
		} catch (Exception e) {
			Log.getLog().log(e);
			throw new IllegalArgumentException(e);
		}
		return props;
	}

	/**
	 * Checks if user has access to directory
	 * 
	 * @param directory directory
	 * @param name      user name
	 * @return true if has access
	 */
	public boolean hasReadAccess(String directory, String name) {
		User user = getUserByName(name);
		if (user.getName().equals(Constants.ADMIN)) {
			return true;
		}
		File f = new File(directory);
		boolean dir = f.exists() && f.isDirectory();

		String path;
		try {
			path = f.getCanonicalPath();
		} catch (IOException e) {
			Log.getLog().log(e.toString());
			return false;
		}
		if (dir && !path.endsWith(Constants.SEP)) {
			path += Constants.SEP;
		}
		path = path.replace("\\", Constants.SEP);
		for (String s : user.getRead()) {
			if (path.startsWith(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets user by name.
	 * 
	 * @param name user name
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
	 * @param userName user name
	 * @return list of dirs
	 */
	public List<String> getAvailablePaths(String userName) {
		User u = getUserByName(userName);
		return u.getRead();
	}

	/**
	 * Logs in user
	 * 
	 * @param login    login
	 * @param password password
	 * @throws AuthenticationException when login or password were incorrect
	 */
	public void login(String login, String password) throws AuthenticationException {
		User user = users.get(login);
		String sha = SHA1(password);
		if (user != null && user.getPassword().toUpperCase().equals(sha)) {
			Log.getLog().log("Logged: " + login);
			return;
		}
		Log.getLog().log("Invalid password: " + login);
		throw new AuthenticationException("Invalid login or password");
	}

	/**
	 * Generates SHA for given string.
	 * 
	 * @param text text to hash
	 * @return hashed string
	 */
	public String SHA1(String text) {
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
	 * @param list paths to normalize
	 */
	public static void normalize(List<String> list) {
		for (int i = 0; i < list.size(); i++) {
			String path = list.get(i).replace('\\', '/');
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
	 * @param path  base path
	 * @param path2 path to check
	 * @return true if subpath
	 */
	private static boolean startsWith(String path, String path2) {
		if (path2.length() > path.length()) {
			return false;
		}
		String[] split = path.split("/");
		String[] split2 = path2.split("/");
		int size = Integer.min(split.length, split2.length);
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
	 * @param name file name
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

	public boolean isLogEnabled() {
		return logEnabled;
	}
}
