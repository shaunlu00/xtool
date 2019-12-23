package com.shaunlu.xtool.test;

import com.shaunlu.xtool.img.ThumbnailUtil;
import com.shaunlu.xtool.classhack.ClasspathUtil;
import org.junit.Test;

import java.io.IOException;

public class ImageTest {

    @Test
    public void test() throws IOException {
        String testDirPath = ClasspathUtil.getResourceURL("test-data").getPath();
        String originFilePath = ClasspathUtil.getResourceAbsolutePath("test-data/img/James.jpeg");
        String destFilePath1 = testDirPath + "test-data/img/James_200-200.jpeg";
        String destFilePath2 = testDirPath + "test-data/img/James_01.jpeg";
        ThumbnailUtil.createThumbnail(originFilePath, destFilePath1, 200, 200);
        ThumbnailUtil.createThumbnail(originFilePath, destFilePath2, 0.1);
    }
}
