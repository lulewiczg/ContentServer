package lulewiczg.contentserver.test.utils;

import java.lang.reflect.Field;

import lulewiczg.contentserver.permissions.ResourceHelper;

public final class TestUtil {

    /**
     * Swaps helper for tests
     * 
     * @param helper
     *            mocked helper
     * @throws ReflectiveOperationException
     *             the ReflectiveOperationException
     */
    public static void swapHelper(ResourceHelper helper) throws ReflectiveOperationException {
        Field field = ResourceHelper.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(null, helper);
    }
}
