package com.shaunlu.xtool;

import java.util.Collection;
import java.util.Map;

public class EmptyUtil {

    public static boolean isEmpty(Collection collection) {
        return null == collection || 0 == collection.size();
    }

    public static boolean isEmpty(Map map) {
        return null == map || 0 == map.size();
    }

    public static boolean isEmpty(String s) {
        return null == s || s.isEmpty();
    }
}
