package com.shaunlu.xtool.test;

import com.shaunlu.xtool.classhack.ClasspathUtil;
import com.shaunlu.xtool.file.DirectoryUtil;
import com.shaunlu.xtool.file.FileUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class FileTest {


    @Test
    public void test() throws IOException {
        String testClassPath = ClasspathUtil.getResourceURL("test-data").getPath();
        String testDirPath = testClassPath + "test-data" + File.separator + "file";
        if (DirectoryUtil.exist(testDirPath)) {
            DirectoryUtil.deleteDirectory(testDirPath);
        }
        Path testDir = DirectoryUtil.createDirectory(testDirPath);
        String originFilePath = ClasspathUtil.getResourceAbsolutePath("test-data/img/James.jpeg");
        FileUtil.copyFile(new File(originFilePath), new File(testDir.toString(), "Lebron.jpeg"));
    }
}
