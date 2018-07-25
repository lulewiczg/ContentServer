package lulewiczg.contentserver.utils.comparators;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lulewiczg.contentserver.utils.Constants;
import lulewiczg.contentserver.utils.models.Dir;

/**
 * Comparator for directories.
 *
 * @author lulewiczg
 *
 */
public class PathComparator implements Comparator<Dir> {

    private static final String NUMS_REGEX = "[\\D\\/]+";
    private static final String STRS_REGEX = "[\\d\\/]+";

    /**
     * Sorts directories in alphabetical order, but folders first.
     *
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Dir dir1, Dir dir2) {
        String s1 = dir1.getName().toLowerCase();
        String s2 = dir2.getName().toLowerCase();

        List<ArgWrapper<? extends Comparable<?>>> split = split(s1);
        List<ArgWrapper<? extends Comparable<?>>> split2 = split(s2);

        if (s1.endsWith(Constants.SEP) && !s2.endsWith(Constants.SEP)) {
            return -1;
        } else if (!s1.endsWith(Constants.SEP) && s2.endsWith(Constants.SEP)) {
            return 1;
        }
        int result = 0;
        int min = Math.min(split.size(), split2.size());
        for (int i = 0; i < min; i++) {
            ArgWrapper<? extends Comparable<?>> o1 = split.get(i);
            ArgWrapper<? extends Comparable<?>> o2 = split2.get(i);
            result = o1.compareTo(o2);
            if (result != 0) {
                break;
            }
        }
        return result;
    }

    /**
     * Splits numbers and string separately
     * 
     * @param s1
     *            string
     * @return splitted string
     */
    private List<ArgWrapper<? extends Comparable<?>>> split(String s1) {
        String[] split1 = s1.split(STRS_REGEX);
        String[] split2 = s1.split(NUMS_REGEX);
        boolean numsFirst = numsFirst(split1, split2);
        List<ArgWrapper<? extends Comparable<?>>> stringList = buildStringList(split1);
        List<ArgWrapper<? extends Comparable<?>>> intList = buildIntList(split2);

        List<ArgWrapper<? extends Comparable<?>>> merged = new ArrayList<>();
        int min = Math.min(stringList.size(), intList.size());
        int max = Math.max(stringList.size(), intList.size());

        List<ArgWrapper<? extends Comparable<?>>> list1;
        List<ArgWrapper<? extends Comparable<?>>> list2;
        if (numsFirst) {
            list1 = stringList;
            list2 = intList;
        } else {
            list1 = intList;
            list2 = stringList;
        }
        for (int i = 0; i < min; i++) {
            merged.add(list1.get(i));
            merged.add(list2.get(i));
        }
        if (list1.size() > list2.size()) {
            for (int i = min; i < max; i++) {
                merged.add(list1.get(i));
            }
        } else {
            for (int i = min; i < max; i++) {
                merged.add(list2.get(i));
            }
        }
        return merged;

    }

    /**
     * Checks if nums should be merged first.
     * 
     * @param strings
     *            splitted strings
     * @param nums
     *            splitted nums
     * @return true if should
     */
    private boolean numsFirst(String[] strings, String[] nums) {
        if (strings.length > 0 && strings[0].isEmpty()) {
            return false;
        } else if (nums.length > 0 && nums[0].isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Builds list of string wrappers
     * 
     * @param split
     *            splitted String
     * @return wrappers
     */
    private List<ArgWrapper<? extends Comparable<?>>> buildStringList(String[] split) {
        List<ArgWrapper<? extends Comparable<?>>> wrappers = new ArrayList<>();
        for (String s : split) {
            if (s.isEmpty()) {
                continue;
            }
            wrappers.add(ArgWrapper.newStringWrapper(s.trim()));
        }
        return wrappers;
    }

    /**
     * Builds list of int wrappers
     * 
     * @param split
     *            splitted String
     * @return wrappers
     */
    private List<ArgWrapper<? extends Comparable<?>>> buildIntList(String[] split) {
        List<ArgWrapper<? extends Comparable<?>>> wrappers = new ArrayList<>();
        for (String s : split) {
            if (s.isEmpty()) {
                continue;
            }
            wrappers.add(ArgWrapper.newIntWrapper(s.trim()));
        }
        return wrappers;
    }

}
