package com.github.lulewiczg.contentserver.utils.models;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.lulewiczg.contentserver.utils.CommonUtil;
import com.github.lulewiczg.contentserver.utils.Constants;
import com.github.lulewiczg.contentserver.utils.comparators.PathComparator;
import com.github.lulewiczg.contentserver.utils.json.JSONModel;
import com.github.lulewiczg.contentserver.utils.json.JSONProperty;

/**
 * Represents direcory.
 *
 * @author lulewiczg
 */
public class Dir extends JSONModel<Dir> implements Comparable<Dir> {
    private static final String[] UNITS = { "B", "KB", "MB", "GB", "TB" };

    private static final SimpleDateFormat DF = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    @JSONProperty(propertyName = "name")
    private String name;

    private long sizeLong;

    @JSONProperty(propertyName = "size")
    private String size;

    @JSONProperty(propertyName = "path")
    private String path;

    @JSONProperty(propertyName = "file")
    private boolean file;

    @JSONProperty(propertyName = "date")
    private String date;

    public String getName() {
        return name;
    }

    public Dir(String name, long size, String path, long date, boolean file) {
        this.name = name;
        this.sizeLong = size;
        this.path = path;
        this.date = DF.format(date);
        this.file = file;
        this.size = formatSize();
    }

    public Dir(File f) throws IOException {
        String fName = f.getName();
        long fSize = 0;
        boolean isFile;
        if (f.isDirectory()) {
            fName += Constants.SEP;
            isFile = false;
        } else {
            fSize = f.length();
            isFile = true;
        }
        this.name = fName;
        this.sizeLong = fSize;
        this.path = CommonUtil.normalizePath(f.getCanonicalPath());
        this.date = DF.format(f.lastModified());
        this.file = isFile;
        this.size = formatSize();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Formats file size.
     *
     * @param dir
     *            directory
     * @return formatted size
     */
    private String formatSize() {
        if (!file) {
            return "";
        }
        int unit = 0;
        double len = sizeLong;
        while (len >= 1024f) {
            len /= 1024f;
            unit++;
        }
        if ((int) len == len || String.format("%.2f", len).endsWith(",00")) {
            return String.format("%d %s", (int) len, UNITS[unit]);
        } else {
            return String.format("%.2f %s", len, UNITS[unit]);
        }
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
                files.add(new Dir(file));
            }
            Collections.sort(files);
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

    public long getSizeLong() {
        return sizeLong;
    }

    /**
     * Sorts directories in alphabetical order, but folders first.
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Dir dir2) {
        return new PathComparator().compare(this, dir2);
    }

    @Override
    public int hashCode() {
        return name.hashCode() + path.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Dir)) {
            return false;
        }
        Dir dir2 = (Dir) obj;
        return name.equals(dir2.name) && path.equals(dir2.path);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
