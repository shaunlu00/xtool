package org.crudboy.toolbar.img;

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A common util used to create thumbnails
 */
public class ThumbnailUtil {

    private static Logger logger = LoggerFactory.getLogger(ThumbnailUtil.class);

    /**
     * Create thumbnail with given width and height
     *
     * @param originFilePath    Original file path
     * @param destFilePath      Thumbnail file path
     * @param width             Thumbnail width
     * @param height            Thumbnail height
     * @throws IOException
     */
    public static void createThumbnail(String originFilePath, String destFilePath, int width, int height) throws IOException {
        Thumbnails.of(originFilePath).size(width, height).toFile(destFilePath);
    }

    /**
     * Create thumbnail with given scale
     *
     * @param originFilePath    Original file path
     * @param destFilePath      Thumbnail file path
     * @param scale             Image scaled level, for example: 0.1
     * @throws IOException
     */
    public static void createThumbnail(String originFilePath, String destFilePath, double scale) throws IOException {
        Thumbnails.of(originFilePath).scale(scale).toFile(destFilePath);
    }

    /**
     * Create thumbnail and return it as byte array
     *
     * @param originFilePath    Original file path
     * @param format            Thumbnail image format
     * @param width             Thumbnail width
     * @param height            Thumbnail height
     * @return                  The byte array that holds thumbnail data
     */
    public static byte[] getThumbnailAsBytes(String originFilePath, String format, int width, int height) {
        BufferedImage thumbnail = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes = null;
        try {
            thumbnail = Thumbnails.of(originFilePath)
                    .size(width, height)
                    .asBufferedImage();
            ImageIO.write(thumbnail, format, baos);
            baos.flush();
            bytes = baos.toByteArray();
        } catch (IOException e) {
            logger.error("create thumbnail error, origin file is {}", originFilePath, e);
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                logger.error("close output stream error:{}", e);
            }
        }
        return bytes;
    }
}
