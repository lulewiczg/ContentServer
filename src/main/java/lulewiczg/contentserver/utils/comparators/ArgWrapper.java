package lulewiczg.contentserver.utils.comparators;

import java.text.Collator;
import java.util.Locale;

/**
 * Wraps path element for comparator.
 * 
 * @author lulewiczg
 *
 * @param <T>
 *            argument type
 */
class ArgWrapper<T extends Comparable<T>> implements Comparable<ArgWrapper<? extends Comparable<?>>> {

    protected T arg;

    public T getArg() {
        return arg;
    }

    private ArgWrapper(T arg) {
        this.arg = arg;
    }

    /**
     * Creates wrapper for String value.
     * 
     * @param arg
     *            arg
     * @return wrapper
     */
    public static StringWrapper newStringWrapper(String arg) {
        return new StringWrapper(arg);
    }

    /**
     * Creates wrapper for int value.
     * 
     * @param arg
     *            arg
     * @return wrapper
     */
    public static IntWrapper newIntWrapper(String arg) {
        Integer val = Integer.parseInt(arg);
        return new IntWrapper(val);
    }

    @Override
    public int compareTo(ArgWrapper<? extends Comparable<?>> o) {
        if (getClass() == o.getClass()) {
            return compareArg(o);
        } else if (o instanceof IntWrapper) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * Default argument comparison.
     * 
     * @param o
     *            argument to compare
     * @return compare result
     */
    @SuppressWarnings("unchecked")
    protected int compareArg(ArgWrapper<? extends Comparable<?>> o) {
        return arg.compareTo((T) o.arg);
    }

    @Override
    public String toString() {
        return arg.toString();
    }

    /**
     * Wrapper for ints.
     * 
     * @author lulewiczg
     *
     */
    static class IntWrapper extends ArgWrapper<Integer> {
        public IntWrapper(int arg) {
            super(arg);
        }

    }

    /**
     * Wrappers for Strings
     * 
     * @author lulewiczg
     *
     */
    static class StringWrapper extends ArgWrapper<String> {
        protected static final Collator collator = Collator.getInstance(new Locale("pl", "PL"));

        public StringWrapper(String arg) {
            super(arg);
        }

        /**
         * Compares strings using collator.
         * 
         * @see lulewiczg.contentserver.utils.comparators.ArgWrapper#compareArg(lulewiczg.contentserver.utils.comparators.ArgWrapper)
         */
        @Override
        protected int compareArg(ArgWrapper<? extends Comparable<?>> o) {
            return collator.compare(arg, o.arg.toString());
        }
    }

}
