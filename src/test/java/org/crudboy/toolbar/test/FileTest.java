package org.crudboy.toolbar.test;

import org.crudboy.toolbar.classhack.ClasspathUtil;
import org.crudboy.toolbar.file.DirectoryUtil;
import org.crudboy.toolbar.file.FileUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class FileTest {


    @Test
    public void test() throws IOException {
        String testClassPath = ClasspathUtil.getClassURL(FileTest.class, null).getPath();
        String testDirPath = testClassPath + "test-data" + File.separator + "file";
        if (DirectoryUtil.exist(testDirPath)) {
            DirectoryUtil.deleteDirectory(testDirPath);
        }
        Path testDir = DirectoryUtil.createDirectory(testDirPath);
        String originFilePath = ClasspathUtil.getResourceAbsolutePath("test-data/img/James.jpeg");
        FileUtil.copyFile(new File(originFilePath), new File(testDir.toString(), "Lebron.jpeg"));
    }
}
