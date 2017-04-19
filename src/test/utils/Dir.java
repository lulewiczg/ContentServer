package test.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Dir {

	private static final String SEP = "/";

	private String name;

	private long size;

	private String path;

	private boolean file;

	public String getName() {
		return name;
	}

	public Dir(String name, long size, String path, boolean file) {
		super();
		this.name = name;
		this.size = size;
		this.path = path;
		this.file = file;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public static String getMIME(String name) {
		String type = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
		switch (type) {
			case "swf":
				return "application/x-shockwave-flash";
			case "mp3":
			case "wav":
			case "aac":
			case "wma":
			case "ape":
			case "flac":
				return "audio/" + type;
			case "jpg":
			case "jpeg":
			case "bmp":
			case "png":
			case "gif":
			case "svg":
				return "image/" + type;
			case "wmv":
			case "mp4":
			case "avi":
			case "webm":
			case "mpg":
			case "mpeg":
			case "rmvb":
			case "vob":
			case "mkv":
				return "video/" + type;
			default:
				return "application/octet-stream";
		}

	}

	public static String toJSON(List<Dir> dirs) {
		String s = "[";
		boolean first = true;
		for (Dir dir : dirs) {
			if (!first) {
				s += ",";
			}
			String obj = String.format("{\"name\": \"%s\",\"path\": \"%s\", \"size\": \"%d\", \"file\": \"%s\"}", dir.name,
					dir.path, dir.size, dir.file);
			s += obj;
			first = false;
		}
		s += "]";
		return s;
	}

	public static List<Dir> getFiles(File f) throws IOException {
		File[] list = f.listFiles();
		if (list != null) {
			List<Dir> files = new ArrayList<>();
			for (File file : list) {
				String name = file.getName();
				long size = 0;
				boolean isFile = true;
				if (file.isDirectory()) {
					name += SEP;
					isFile = false;
				} else {
					size = file.length();
				}
				files.add(new Dir(name, size, file.getCanonicalPath().replace("\\", "/"), isFile));
			}
			Collections.sort(files, new PathComparator());
			return files;
		}
		return new ArrayList<>();
	}

	@Override
	public String toString() {
		return path;
	}

	public boolean isFile() {
		return file;
	}

	public void setFile(boolean file) {
		this.file = file;
	}
}
