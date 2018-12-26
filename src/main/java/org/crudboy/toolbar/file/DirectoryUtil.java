package org.crudboy.toolbar.file;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.File;

public class DirectoryUtil {

    public static File createDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (dir.isDirectory()) {
            throw new IllegalArgumentException(Strings.lenientFormat("directory %s already exists", dirPath));
        } else {
            dir.mkdir();
        }
        return dir;
    }

    public static File createChildDirectory(String parentDirPath, String dirName) {
        File parentDirectory = getDirectory(parentDirPath);
        File dir = new File(parentDirectory, dirName);
        return createDirectory(dir.getAbsolutePath());
    }

    public static File createChildDirectory(File parentDir, String dirName) {
        Preconditions.checkArgument(parentDir.isDirectory(), Strings.lenientFormat("directory %s not exists", parentDir.getAbsolutePath()));
        File dir = new File(parentDir, dirName);
        return createDirectory(dir.getAbsolutePath());

    }

    public static File getDirectory(String dirPath) {
        File dir = new File(dirPath);
        Preconditions.checkArgument(dir.isDirectory(), Strings.lenientFormat("directory %s not exists", dirPath));
        return dir;
    }

    public static boolean exist(String dirPath) {
        File dir = new File(dirPath);
        return dir.isDirectory();
    }

    public static void deleteDirectory(String dirPath) {
        File dir = getDirectory(dirPath);
        for (File file : dir.listFiles()){
            if (file.isDirectory()) {
                deleteDirectory(file.getAbsolutePath());
            } else {
                file.delete();
            }
        }
        dir.delete();
    }
}
