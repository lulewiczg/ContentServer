package lulewiczg.contentserver.utils;

import java.util.Comparator;

import lulewiczg.contentserver.utils.models.Dir;

/**
 * Comparator for direcotires.
 *
 * @author lulewiczg
 *
 */
public class PathComparator implements Comparator<Dir> {

    /**
     * Sorts directories in alphabetical order, but folders first.
     *
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Dir dir1, Dir dir2) {
        String s1 = dir1.getName().toLowerCase();
        String s2 = dir2.getName().toLowerCase();
        if (s1.endsWith(Constants.SEP) && !s2.endsWith(Constants.SEP)) {
            return -0xFFF + s1.compareTo(s2);
        } else if (!s1.endsWith(Constants.SEP) && s2.endsWith(Constants.SEP)) {
            return 0xFFF + s1.compareTo(s2);
        }
        return s1.compareTo(s2);
    }

}
