package lulewiczg.contentserver.utils.comparators;

import java.util.Comparator;

/**
 * Compares numbers and strings in natural order.
 * 
 * @author lulewiczg
 */
public class NumberToStringComparator implements Comparator<String> {

    private static final String NON_NUMBERS_REGEX = "\\D";
    private static final String REPLACEMENT = "";

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(String s1, String s2) {
        return compareNums(s1, s2);
    }

    /**
     * Compares numbers in string names.
     * 
     * @param s1
     *            string 1
     * @param s2
     *            string 2
     * 
     * @return compare result
     */
    private int compareNums(String s1, String s2) {
        String numS1 = s1.replaceAll(NON_NUMBERS_REGEX, REPLACEMENT);
        String numS2 = s2.replaceAll(NON_NUMBERS_REGEX, REPLACEMENT);
        int numS1Num = Integer.MAX_VALUE;
        int numS2Num = Integer.MAX_VALUE;
        if (!numS1.isEmpty()) {
            numS1Num = Integer.parseInt(numS1);
        }
        if (!numS2.isEmpty()) {
            numS2Num = Integer.parseInt(numS2);
        }
        return Integer.compare(numS1Num, numS2Num);
    }
}
