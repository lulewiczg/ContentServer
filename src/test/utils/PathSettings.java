package test.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

public class PathSettings {
	private List<String> dmz = new ArrayList<>();

	private Properties props;

	private static PathSettings instance;

	public static synchronized PathSettings getInstance(FilterConfig arg0) throws ServletException {
		if (instance == null) {
			instance = new PathSettings(arg0);
		}
		return instance;
	}

	private PathSettings(FilterConfig arg0) throws ServletException {
		try {
			String path = arg0.getServletContext().getRealPath("/") + "filter.properties";
			props = new Properties();
			try (InputStream input = new FileInputStream(path)) {
				props.load(input);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try (InputStream input = new FileInputStream("/storage/sdcard0/jetty/webapps/Jetty/filter.properties")) {
				props.load(input);
			} catch (Exception e2) {
				throw new ServletException(e2);
			}
		}
		dmz = Arrays.asList(props.getProperty("filter.dmz").split(";"));
		System.out.println(dmz);
	}

	public boolean isInDmz(String source) {
		File f = new File(source);
		String path;
		try {
			path = f.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		if (f.isDirectory() && !source.endsWith(File.separator)) {
			path += File.separator;
		}
		path = path.replace("\\", "/");
		for (String s : dmz) {
			if (path.startsWith(s)) {
				return true;
			}
		}
		return false;
	}

	public List<String> getDmz() {
		return dmz;
	}

	public Properties getProps() {
		return props;
	}
}
