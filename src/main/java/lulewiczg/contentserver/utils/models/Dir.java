package lulewiczg.contentserver.utils.models;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lulewiczg.contentserver.utils.Constants;
import lulewiczg.contentserver.utils.PathComparator;

/**
 * Represents direcory.
 *
 * @author lulewiczg
 */
public class Dir {
    private static final String[] UNITS = { "B", "KB", "MB", "GB", "TB" };

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

    /**
     * Transforms directories to JSON
     *
     * @param dirs
     *            directories
     * @return JSON
     */
    public static String toJSON(List<Dir> dirs) {
        String s = "[";
        boolean first = true;
        for (Dir dir : dirs) {
            if (!first) {
                s += ",";
            }
            String obj = String.format("{\"name\": \"%s\",\"path\": \"%s\", \"size\": \"%s\", \"file\": \"%s\"}", dir.name,
                    dir.path, formatSize(dir), dir.file);
            s += obj;
            first = false;
        }
        s += "]";
        return s;
    }

    /**
     * Formats file size.
     *
     * @param dir
     *            directory
     * @return formatted size
     */
    private static String formatSize(Dir dir) {
        if (!dir.isFile()) {
            return "";
        }
        int unit = 0;
        double len = dir.size;
        while (len > 1024f) {
            len /= 1024f;
            unit++;
        }
        return String.format("%.2f %s", len, UNITS[unit]);
    }

    /**
     * Reads available directories in directory.
     *
     * @param f
     *            directory
     * @return list of directories
     * @throws IOException
     *             when could not read directories
     */
    public static List<Dir> getFiles(File f) throws IOException {
        File[] list = f.listFiles();
        if (list != null) {
            List<Dir> files = new ArrayList<>();
            for (File file : list) {
                String name = file.getName();
                long size = 0;
                boolean isFile = true;
                if (file.isDirectory()) {
                    name += Constants.SEP;
                    isFile = false;
                } else {
                    size = file.length();
                }
                files.add(new Dir(name, size, file.getCanonicalPath().replace("\\", Constants.SEP), isFile));
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
