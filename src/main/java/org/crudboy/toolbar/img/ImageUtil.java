package org.crudboy.toolbar.img;

import org.crudboy.toolbar.file.FileUtil;

import java.io.IOException;
import java.util.Base64;

public class ImageUtil {

    private static final Base64.Encoder encoder = Base64.getEncoder();

    public static String getImgBase64(String imageFilePath) throws IOException {
        return encoder.encodeToString(FileUtil.getFileAsBytes(imageFilePath));
    }
}
