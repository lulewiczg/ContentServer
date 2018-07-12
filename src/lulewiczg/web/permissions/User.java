package lulewiczg.web.permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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

	public void apply(User u) {
		read.addAll(u.read);
		write.addAll(u.write);
		delete.addAll(u.delete);
	}

	void addRead(String[] paths) {
		read.addAll(Arrays.asList(paths));
	}

	void addWrite(String[] paths) {
		write.addAll(Arrays.asList(paths));
	}

	void addDelete(String[] paths) {
		delete.addAll(Arrays.asList(paths));
	}

	void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return String.format("[%s (read: %s, write: %s, delete: %s)]", name, read, write, delete);
	}

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

	private void normalize(List<String> list) {
		for (int i = 0; i < list.size(); i++) {
			for (int j = i + 1; j < list.size(); j++) {
				String path = list.get(i);
				String path2 = list.get(j);
				if (path.equals("") || path2.equals("")) {
					continue;
				}
				if (path.startsWith(path2)) {
					list.set(i, "");
				} else if (path2.startsWith(path)) {
					list.set(j, "");
				}
			}
		}
		Iterator<String> i = list.iterator();
		while (i.hasNext()) {
			if (i.next().equals("")) {
				i.remove();
			}
		}
		Collections.sort(list);
	}
}
