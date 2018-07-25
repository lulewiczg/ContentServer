package lulewiczg.contentserver.utils.comparators;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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
    private static final Collator collator = Collator.getInstance(new Locale("pl", "PL"));

    /**
     * Sorts directories in alphabetical order, but folders first.
     *
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Dir dir1, Dir dir2) {
        String s1 = dir1.getName().toLowerCase();
        String s2 = dir2.getName().toLowerCase();

        List<?> split = split(s1);
        List<?> split2 = split(s2);

        if (s1.endsWith(Constants.SEP) && !s2.endsWith(Constants.SEP)) {
            return -1;
        } else if (!s1.endsWith(Constants.SEP) && s2.endsWith(Constants.SEP)) {
            return 1;
        }
        int result = 0;
        int min = Math.min(split.size(), split2.size());
        for (int i = 0; i < min; i++) {
            Object o1 = split.get(i);
            Object o2 = split2.get(i);
            if (o1 instanceof Integer || o2 instanceof Integer) {
                result = new NumberToStringComparator().compare(o1.toString(), o2.toString());
            } else {
                result = collator.compare(s1, s2);
            }
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
    private List<?> split(String s1) {
        String[] split1 = s1.split("[\\d\\/]+");
        String[] split2 = s1.split("[\\D\\/]+");
        System.out.println(s1 + "    " + Arrays.asList(split1) + "     " + Arrays.asList(split2));
        boolean emptyNums = false;
        if (split1.length > 0 && split1[0].isEmpty()) {
            split1 = Arrays.copyOfRange(split1, 1, split1.length);
            emptyNums = false;
        }
        if (split2.length > 0 && split2[0].isEmpty()) {
            split2 = Arrays.copyOfRange(split2, 1, split2.length);
            emptyNums = true;
        }
        List<Object> list = new ArrayList<>();
        int min = Math.min(split1.length, split2.length);
        int max = Math.max(split1.length, split2.length);
        for (int i = 0; i < min; i++) {
            if (emptyNums) {
                list.add(split1[i]);
                list.add(Integer.parseInt(split2[i]));
            } else {
                list.add(Integer.parseInt(split2[i]));
                list.add(split1[i]);
            }
        }
        if (min != max) {
            if (split1.length > split2.length) {
                for (int i = min; i < max; i++) {
                    list.add(split1[i]);
                }
            } else {
                for (int i = min; i < max; i++) {
                    list.add(Integer.parseInt(split2[i]));
                }
            }
        }
        return list;
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
    private List<ArgWrapper<?>> buildStringList(String[] split) {
        List<ArgWrapper<?>> wrappers = new ArrayList<>();
        for (String s : split) {
            if (s.isEmpty()) {
                continue;
            }
            wrappers.add(ArgWrapper.newStringWrapper(s));
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
    private List<ArgWrapper<?>> buildIntList(String[] split) {
        List<ArgWrapper<?>> wrappers = new ArrayList<>();
        for (String s : split) {
            if (s.isEmpty()) {
                continue;
            }
            wrappers.add(ArgWrapper.newIntWrapper(s));
        }
        return wrappers;
    }

}
