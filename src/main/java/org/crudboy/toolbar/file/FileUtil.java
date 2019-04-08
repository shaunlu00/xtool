package org.crudboy.toolbar.file;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is a file management helper class
 */
public class FileUtil {

    private FileUtil(){}

    /**
     * Write byte array into a file
     *
     * @param bytes    Byte array
     * @param filePath The output file path
     * @return
     * @throws IOException
     */
    public static File saveBytesAsFile(byte[] bytes, String filePath) throws IOException {
        File file = new File(filePath);
        return saveBytesAsFile(bytes, file);
    }

    /**
     * Write byte array into a file
     *
     * @param bytes Byte array
     * @param file  The output file
     * @return
     * @throws IOException
     * @throws IllegalArgumentException If the file exists
     */
    public static File saveBytesAsFile(byte[] bytes, File file) throws IOException {
        Preconditions.checkArgument(file.createNewFile(), Strings.lenientFormat("file %s already exists", file.getAbsolutePath()));
        ByteSink byteSink = Files.asByteSink(file);
        byteSink.write(bytes);
        return file;
    }

    /**
     * Read a file and return file data as byte array
     *
     * @param filePath The source file path
     * @return The byte array that contains all file data
     * @throws IOException
     */
    public static byte[] getFileAsBytes(String filePath) throws IOException {
        File file = getFile(filePath);
        ByteSource byteSource = Files.asByteSource(file);
        return byteSource.read();
    }

    /**
     * Read from input stream and write data into a file
     *
     * @param inputStream Input stream
     * @param filePath    Destination file path
     * @return
     * @throws IOException
     */
    public static File saveStreamAsFile(InputStream inputStream, String filePath) throws IOException {
        File file = new File(filePath);
        return saveStreamAsFile(inputStream, file);
    }

    /**
     * Read from input stream and write data into a file
     *
     * @param inputStream Input stream
     * @param file        Destination file
     * @return
     * @throws IOException
     */
    public static File saveStreamAsFile(InputStream inputStream, File file) throws IOException {
        Preconditions.checkArgument(file.createNewFile(), Strings.lenientFormat("file %s already exists", file.getAbsolutePath()));
        ByteSink byteSink = Files.asByteSink(file);
        byteSink.writeFrom(inputStream);
        return file;
    }

    /**
     * Get file instance from file path
     *
     * @param filePath The file path
     * @return File instance
     * @throws IllegalArgumentException If the file does not exist
     */
    public static File getFile(String filePath) {
        File file = new File(filePath);
        Preconditions.checkArgument(file.exists(), Strings.lenientFormat("file %s not exists", filePath));
        return file;
    }

    /**
     * Copy file
     *
     * @param from Source file instance
     * @param to   Destination file instance
     * @throws IOException
     */
    public static void copyFile(File from, File to) throws IOException {
        Files.copy(from, to);
    }
}
