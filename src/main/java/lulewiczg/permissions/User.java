package lulewiczg.permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents user.
 * 
 * @author lulewiczg
 */
public class User {

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
		ResourceHelper.normalize(read);
		ResourceHelper.normalize(write);
		ResourceHelper.normalize(delete);
	}
}
