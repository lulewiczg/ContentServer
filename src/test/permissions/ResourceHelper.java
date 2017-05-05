package test.permissions;

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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.security.sasl.AuthenticationException;
import javax.servlet.ServletContext;

import test.utils.Log;

public class ResourceHelper {
	private static final String SEP = "/";
	public static final String USER = "user";
	private static final String DELETE = "delete";
	private static final String WRITE = "write";
	private static final String PASSWORD = "password";
	private static final String GUEST = "guest";
	private static final String READ = "read";
	private static final String PERMISSION = "permission";
	private static final String EXTENDS = "extends";

	private static ResourceHelper instance;
	private Map<String, User> users = new HashMap<>();
	private Map<String, String> mimes = new HashMap<>();
	private int bufferSize;
	private boolean logEnabled = true;

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
			String value = e.getValue().toString();
			if (keys.length > 2) {
				if (keys[2].equals(EXTENDS)) {
					toApply.put(user, value);
				} else if (keys[2].equals(PASSWORD)) {
					user.setPassword(value);
				}
			}

			if (keys.length > 3 && keys[2].equals(PERMISSION)) {
				String[] values = value.split(";");
				if (values.length > 0) {
					String type = keys[3];
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
		ArrayList<Entry<User, String>> applyList = new ArrayList<>(toApply.entrySet());
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
		for (Map.Entry<User, String> u : applyList) {
			u.getKey().apply(users.get(u.getValue()));
		}
		User guest = users.get(GUEST);
		if (guest != null) {
			for (Map.Entry<String, User> u : users.entrySet()) {
				User value = u.getValue();
				if (value != guest) {
					value.apply(guest);
				}
			}
		}
		for (Map.Entry<String, User> u : users.entrySet()) {
			u.getValue().normalize();
		}
	}

	private void loadSettings(ServletContext context) {
		Properties p = new Properties();
		try (InputStream input = new FileInputStream(context.getRealPath(SEP) + "/settings/settings.properties")) {
			p.load(input);
		} catch (IOException e) {
			Log.getLog().log(e);
			throw new IllegalStateException(e);
		}
		Set<Entry<Object, Object>> entrySet = p.entrySet();
		for (Map.Entry<Object, Object> prop : entrySet) {
			String key = prop.getKey().toString();
			if (key.startsWith("mime.")) {
				mimes.put(key.substring(5), prop.getValue().toString());
			}
		}
		bufferSize = Integer.parseInt(p.getProperty("buffer.size")) * 1024;
		logEnabled = Boolean.parseBoolean(p.getProperty("logger.enabled"));
	}

	private Properties loadProps(ServletContext context) {
		Properties props = new Properties();
		try {
			String contextPath = context.getRealPath(SEP);
			String path = contextPath + "/settings/permissions.properties";
			try (InputStream input = new FileInputStream(path)) {
				props.load(input);
			}
		} catch (Exception e) {
			Log.getLog().log(e);
			throw new IllegalArgumentException(e);
		}
		return props;
	}

	public boolean hasReadAccess(String source, String name) {
		User user = getUser(name);
		if (user.getName().equals("admin")) {
			return true;
		}
		File f = new File(source);
		boolean dir = f.exists() && f.isDirectory();

		String path;
		try {
			path = f.getCanonicalPath();
		} catch (IOException e) {
			Log.getLog().log(e.toString());
			return false;
		}
		if (dir && !path.endsWith(SEP)) {
			path += SEP;
		}
		path = path.replace("\\", SEP);
		for (String s : user.getRead()) {
			if (path.startsWith(s)) {
				return true;
			}
		}
		return false;
	}

	private User getUser(String name) {
		if (name == null || name.isEmpty()) {
			name = GUEST;
		}
		User user = users.get(name);
		return user;
	}

	public List<String> getAvailablePaths(String name) {
		User u = getUser(name);
		return u.getRead();
	}

	public void login(String login, String password) throws AuthenticationException {
		User user = users.get(login);
		String sha = SHA1(password);
		if (user != null && user.getPassword().equals(sha)) {
			Log.getLog().log("Logged: " + login);
			return;
		}
		Log.getLog().log("Invalid password: " + login);
		throw new AuthenticationException("Invalid login or password");
	}

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

	public String getMIME(String name) {
		String type = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
		String mime = mimes.get(type);
		if (mime == null) {
			mime = "application/octet-stream";
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
