package org.crudboy.toolbar;

import java.util.Collection;

public class EmptyUtil {

    public static boolean isEmpty(Collection collection) {
        return null == collection || 0 == collection.size();
    }

    public static boolean isEmpty(String s) {
        return null == s || s.isEmpty();
    }
}
