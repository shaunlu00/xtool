package org.crudboy.toolbar.test;

import org.crudboy.toolbar.classhack.ClasspathUtil;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.util.List;

public class ClasspathTest {

    @Test
    public void testGetURLs() {
        assertURLs("target/classes/", ClasspathUtil.getResourceURLs("org/crudboy/toolbar/classhack/ClasspathUtil.class", null));
        assertURLs("target/classes/", ClasspathUtil.getPackageURLs("org.crudboy.toolbar.classhack", null));
        assertURL("target/classes/", ClasspathUtil.getClassURL(ClasspathUtil.class, null));
        assertURLs("target/test-classes/", ClasspathUtil.getResourceURLs("test-data/img/James.jpeg", null));
        assertURL("target/test-classes/", ClasspathUtil.getClassURL(ClasspathTest.class, null));

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
