package test.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	private PrintWriter writer;
	private static Log instance;
	private static final DateFormat format = new SimpleDateFormat("yyyyy-MM-dd HH:mm:ss");

	private Log() {
		File file = new File("/storage/emulated/legacy/jetty/tmp/log.txt");
		if (!file.getParentFile().exists()) {
			writer = new PrintWriter(new OutputStreamWriter(System.out));
		} else {
			try {
				writer = new PrintWriter(new FileWriter(file, true));
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalArgumentException("Could not find log for file");
			}
		}
	}

	public static synchronized Log getLog() {
		if (instance == null) {
			instance = new Log();
		}
		return instance;
	}

	public void log(Exception ex) {
		ex.printStackTrace(writer);
		writer.flush();
	}

	public void log(String str) {
		str = String.format("[%s] - %s", format.format(new Date()), str);
		writer.write(str);
		writer.write("\n");
		writer.flush();
	}

	@Override
	protected void finalize() throws Throwable {
		writer.close();
	}
}
