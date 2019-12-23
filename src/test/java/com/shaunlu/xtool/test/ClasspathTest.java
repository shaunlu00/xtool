package com.shaunlu.xtool.test;

import com.shaunlu.xtool.classhack.ClasspathUtil;
import org.junit.Assert;
import org.junit.Test;
import java.net.URL;
import java.util.List;

public class ClasspathTest {

    @Test
    public void testGetURLs() {
//        assertURLs("target/classes/", ClasspathUtil.getResourceURLs("com/shaunlu/xtool/classhack/ClasspathUtil.class", null));
        assertURLs("build/classes/java/main", ClasspathUtil.getPackageURLs("com.shaunlu.xtool.classhack", null));
        assertURL("build/classes/java/main", ClasspathUtil.getClassURL(ClasspathUtil.class, null));
        assertURLs("build/resources/test", ClasspathUtil.getResourceURLs("test-data/img/James.jpeg", null));
        assertURL("build/classes/java/test", ClasspathUtil.getClassURL(ClasspathTest.class, null));

    }

    private void assertURLs(String expectedRet, List<URL> urls) {
        for (URL url : urls) {
            assertURL(expectedRet, url);
        }
    }

    private void assertURL(String expectedRet, URL url) {
        System.out.println(url.toExternalForm());
        Assert.assertTrue(url.toExternalForm().contains(expectedRet));
    }
}
