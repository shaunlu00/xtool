package org.crudboy.toolbar.file;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {

    public static File saveBytesAsFile(byte[] bytes, String filePath) throws IOException {
        File file = new File(filePath);
        return saveBytesAsFile(bytes, file);
    }

    public static File saveBytesAsFile(byte[] bytes, File file) throws IOException {
        Preconditions.checkArgument(file.createNewFile(), Strings.lenientFormat("file %s already exists", file.getAbsolutePath()));
        ByteSink byteSink = Files.asByteSink(file);
        byteSink.write(bytes);
        return file;
    }

    public static byte[] getFileAsBytes(String filePath) throws IOException {
        File file = getFile(filePath);
        ByteSource byteSource = Files.asByteSource(file);
        return byteSource.read();
    }

    public static File saveStreamAsFile(InputStream inputStream, String filePath) throws IOException {
        File file = new File(filePath);
        return saveStreamAsFile(inputStream, file);
    }

    public static File saveStreamAsFile(InputStream inputStream, File file) throws IOException {
        Preconditions.checkArgument(file.createNewFile(), Strings.lenientFormat("file %s already exists", file.getAbsolutePath()));
        ByteSink byteSink = Files.asByteSink(file);
        byteSink.writeFrom(inputStream);
        return file;
    }

    public static File getFile(String filePath) {
        File file = new File(filePath);
        Preconditions.checkArgument(file.exists(), Strings.lenientFormat("file %s not exists", filePath));
        return file;
    }

    public static void copyFile(File from, File to) throws IOException {
        Files.copy(from, to);
    }
}
