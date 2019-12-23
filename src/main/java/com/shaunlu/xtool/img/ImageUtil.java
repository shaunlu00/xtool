package com.shaunlu.xtool.img;

import com.shaunlu.xtool.file.FileUtil;

import java.io.IOException;
import java.util.Base64;

public class ImageUtil {

    private static final Base64.Encoder encoder = Base64.getEncoder();

    public static String getImgBase64(String imageFilePath) throws IOException {
        return encoder.encodeToString(FileUtil.getFileAsBytes(imageFilePath));
    }

}
