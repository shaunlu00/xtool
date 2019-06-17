package org.crudboy.toolbar.classhack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class ClasspathUtil {

    private ClasspathUtil() { }

    private static final Logger logger = LoggerFactory.getLogger(ClasspathUtil.class);

    public static ClassLoader myThreadContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static ClassLoader myStaticClassLoader() {
        return ClasspathUtil.class.getClassLoader();
    }

    public static ClassLoader[] getDefaultClassLoaders() {
        ClassLoader myThreadContextClassLoader = myThreadContextClassLoader();
        ClassLoader myStaticClassLoader = myStaticClassLoader();
        return myThreadContextClassLoader == myStaticClassLoader ?
                new ClassLoader[]{myThreadContextClassLoader} :
                new ClassLoader[]{myThreadContextClassLoader, myStaticClassLoader};
    }

    /**
     * Get all resource URLs (no resource name in the path)
     *
     * @param resourceName The full resource name, e.g. org/crudboy/toolbar/classhack/ClasspathUtil.class
     * @param classLoaders
     * @return
     */
    public static List<URL> getResourceURLs(String resourceName, ClassLoader[] classLoaders) {
        final List<URL> resourceURLs = new ArrayList<>();
        if (null == classLoaders || 0 == classLoaders.length) {
            classLoaders = getDefaultClassLoaders();
        }
        for (ClassLoader classLoader : classLoaders) {
            try {
                Enumeration<URL> urls = classLoader.getResources(resourceName);
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    int index = url.toExternalForm().lastIndexOf(resourceName);
                    if (index != -1) {
                        // remove resource name from URL
                        resourceURLs.add(new URL(url, url.toExternalForm().substring(0, index)));
                    } else {
                        resourceURLs.add(url);
                    }
                }
            } catch (IOException e) {
                logger.error("Get resource({}) urls error", resourceName, e);
            }
        }
        return distinctUrls(resourceURLs);
    }

    /**
     * Get one resource URL (no resource name in the path)
     *
     * @param resourceName
     * @return
     */
    public static URL getResourceURL(String resourceName) {
        List<URL> urls = getResourceURLs(resourceName, null);
        if (0 != urls.size()) {
            return urls.get(0);
        } else return null;
    }

    /**
     * Get resource absolute path
     * For example: file:/.../file.txt
     * file:/.../example.jar!/file.txt
     *
     * @param resourceName
     * @return
     */
    public static String getResourceAbsolutePath(String resourceName) throws MalformedURLException {
        String ret = null;
        URL url = getResourceURL(resourceName);
        if (null != url) {
            if (url.getPath().endsWith("/")) {
                ret = url.getPath() + resourceName;
            } else {
                ret = url.getPath() + "/" + resourceName;
            }
        }
        return ret;
    }

    /**
     * Get resource as inputstream
     *
     * @param resourceName
     * @return
     * @throws IOException
     */
    public static InputStream getResourceAsStream(String resourceName) throws IOException {
        URL url = getResourceURL(resourceName);
        URL resourceURL = null;
        if (null != url) {
            if (url.toExternalForm().endsWith("/")) {
                resourceURL = new URL(url.toExternalForm() + resourceName);
            } else {
                resourceURL = new URL(url.toExternalForm() + "/" + resourceName);
            }
        }
        if (null != resourceURL) {
            return resourceURL.openConnection().getInputStream();
        } else {
            return null;
        }
    }

    public static List<URL> getPackageURLs(String packageName, ClassLoader[] classLoaders) {
        String resourceName = packageName.replace(".", "/");
        return getResourceURLs(resourceName, classLoaders);
    }

    public static URL getClassURL(Class<?> clss, ClassLoader[] classLoaders) {
        URL url = null;
        String resourceName = clss.getName().replace(".", "/") + ".class";
        List<URL> urls = getResourceURLs(resourceName, classLoaders);
        if (0 != urls.size()) {
            url = urls.get(0);
        }
        return url;
    }

    public static List<URL> getClasspathURLs() {
        List<URL> urls = new ArrayList<URL>();
        String javaClassPath = System.getProperty("file.class.path");
        if (null != javaClassPath) {
            for (String path : javaClassPath.split(File.pathSeparator)) {
                try {
                    urls.add(new File(path).toURI().toURL());
                } catch (Exception e) {
                    logger.error("Get file classpath urls error", e);
                }
            }
        }
        return distinctUrls(urls);
    }

    public static List<URL> getWebInfLibURLs(final ServletContext servletContext) {
        final List<URL> urls = new ArrayList<URL>();
        Set<?> libJars = servletContext.getResourcePaths("/WEB-INF/lib");
        if (null != libJars) {
            for (Object jar : libJars) {
                try {
                    urls.add(servletContext.getResource((String) jar));
                } catch (MalformedURLException e) {
                    logger.error("Get WEB-INF lib jar urls error", e);
                }
            }
        }
        return distinctUrls(urls);
    }

    public static URL getWebInfClassesURL(final ServletContext servletContext) {
        URL webInfClassesURL = null;
        Set<?> classes = servletContext.getResourcePaths("/WEB-INF/classes");
        if (null != classes) {
            for (Object clss : classes) {
                try {
                    URL url = servletContext.getResource((String) clss);
                    String urlString = url.toExternalForm();
                    urlString = urlString.substring(0, urlString.lastIndexOf("/WEB-INF/classes/") + "/WEB-INF/classes/".length());
                    webInfClassesURL = new URL(urlString);
                    break;
                } catch (MalformedURLException e) {
                    logger.error("Get WEB-INF classes url error", e);
                }
            }
        }
        return webInfClassesURL;
    }


    private static List<URL> distinctUrls(Collection<URL> urls) {
        Map<String, URL> distinct = new LinkedHashMap<String, URL>(urls.size());
        for (URL url : urls) {
            distinct.put(url.toExternalForm(), url);
        }
        return new ArrayList<URL>(distinct.values());
    }
}
