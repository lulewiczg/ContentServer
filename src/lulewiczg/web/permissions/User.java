package lulewiczg.web.permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents user.
 * 
 * @author lulewiczg
 */
public class User {

	private static final String EMPTY = "";

	private String name;

	private String password;

	private List<String> read = new ArrayList<>();

	private List<String> write = new ArrayList<>();

	private List<String> delete = new ArrayList<>();

	public User(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public List<String> getRead() {
		return read;
	}

	public List<String> getWrite() {
		return write;
	}

	public List<String> getDelete() {
		return delete;
	}

	/**
	 * Adds permissions of super user.
	 * 
	 * @param u user
	 */
	public void apply(User u) {
		read.addAll(u.read);
		write.addAll(u.write);
		delete.addAll(u.delete);
	}

	/**
	 * Adds READ permissions for user
	 * 
	 * @param paths paths
	 */
	void addRead(String[] paths) {
		read.addAll(Arrays.asList(paths));
	}

	/**
	 * Adds WRITE permissions for user
	 * 
	 * @param paths paths
	 */
	void addWrite(String[] paths) {
		write.addAll(Arrays.asList(paths));
	}

	/**
	 * Adds DELETE permissions for user
	 * 
	 * @param paths paths
	 */
	void addDelete(String[] paths) {
		delete.addAll(Arrays.asList(paths));
	}

	void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("[%s (read: %s, write: %s, delete: %s)]", name, read, write, delete);
	}

	/**
	 * Normalizes permissions for directories.
	 */
	public void normalize() {
		if (password == null) {
			throw new IllegalStateException(String.format("User %s has not set password!", name));
		}
		read.addAll(write);
		read.addAll(delete);
		normalize(read);
		normalize(write);
		normalize(delete);
	}

	/**
	 * Normalizes paths to avoid duplicates and to set permissions for the shortest
	 * path as possible.
	 * 
	 * @param list paths to normalize
	 */
	private void normalize(List<String> list) {
		for (int i = 0; i < list.size(); i++) {
			for (int j = i + 1; j < list.size(); j++) {
				String path = list.get(i);
				String path2 = list.get(j);
				if (EMPTY.equals(path) || EMPTY.equals(path2)) {
					continue;
				}
				if (path.startsWith(path2)) {
					list.set(i, EMPTY);
				} else if (path2.startsWith(path)) {
					list.set(j, EMPTY);
				}
			}
		}
		Iterator<String> i = list.iterator();
		while (i.hasNext()) {
			if (i.next().equals(EMPTY)) {
				i.remove();
			}
		}
		Collections.sort(list);
	}
}
