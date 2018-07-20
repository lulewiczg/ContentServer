package lulewiczg.contentserver.utils.models;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lulewiczg.contentserver.utils.Constants;
import lulewiczg.contentserver.utils.PathComparator;
import lulewiczg.contentserver.utils.json.JSONModel;
import lulewiczg.contentserver.utils.json.JSONProperty;

/**
 * Represents direcory.
 *
 * @author lulewiczg
 */
public class Dir extends JSONModel<Dir> {
    private static final String[] UNITS = { "B", "KB", "MB", "GB", "TB" };

    @JSONProperty(propertyName = "name")
    private String name;

    private long sizeLong;

    @JSONProperty(propertyName = "size")
    private String size;

    @JSONProperty(propertyName = "path")
    private String path;

    @JSONProperty(propertyName = "file", quoted = false)
    private boolean file;

    public String getName() {
        return name;
    }

    public Dir(String name, long size, String path, boolean file) {
        super();
        this.name = name;
        this.sizeLong = size;
        this.path = path;
        this.file = file;
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

    public long getSizeLong() {
        return sizeLong;
    }

}
