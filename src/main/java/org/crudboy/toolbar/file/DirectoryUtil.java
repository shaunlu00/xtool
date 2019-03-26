package org.crudboy.toolbar.file;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.File;

/**
 * This is a directory management helper class
 */
public class DirectoryUtil {

    /**
     * Create directory if it does not exist
     *
     * @param dirPath The directory path
     * @return The file object that represents created directory
     * @throws IllegalArgumentException If directory already exists
     */
    public static File createDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (dir.isDirectory()) {
            throw new IllegalArgumentException(Strings.lenientFormat("directory %s already exists", dirPath));
        } else {
            dir.mkdir();
        }
        return dir;
    }

    /**
     * Create child directory
     *
     * @param parentDirPath The parent directory path
     * @param dirName       The child directory name
     * @return The file object that represents created directory
     * @throws IllegalArgumentException If the parent directory does not exist, or child directory exists
     */
    public static File createChildDirectory(String parentDirPath, String dirName) {
        File parentDirectory = getDirectory(parentDirPath);
        File dir = new File(parentDirectory, dirName);
        return createDirectory(dir.getAbsolutePath());
    }

    /**
     * Create child directory
     *
     * @param parentDir The parent directory file object
     * @param dirName   The child directory name
     * @return The file object that represents created directory
     * @throws IllegalArgumentException If the parent directory does not exist, or child directory exists
     */
    public static File createChildDirectory(File parentDir, String dirName) {
        Preconditions.checkArgument(parentDir.isDirectory(), Strings.lenientFormat("directory %s not exists", parentDir.getAbsolutePath()));
        File dir = new File(parentDir, dirName);
        return createDirectory(dir.getAbsolutePath());

    }

    /**
     * Get the directory from its path
     *
     * @param dirPath The directory path
     * @return The file object that represents found directory
     * @throws IllegalArgumentException If the directory does not exist
     */
    public static File getDirectory(String dirPath) {
        File dir = new File(dirPath);
        Preconditions.checkArgument(dir.isDirectory(), Strings.lenientFormat("directory %s not exists", dirPath));
        return dir;
    }

    /**
     * Determine if the path is a directory
     *
     * @param dirPath The directory path
     * @return True if the directory exists, otherwise false
     */
    public static boolean exist(String dirPath) {
        File dir = new File(dirPath);
        return dir.isDirectory();
    }

    /**
     * Delete a directory
     *
     * @param dirPath The directory path
     */
    public static void deleteDirectory(String dirPath) {
        File dir = getDirectory(dirPath);
        if (null != dir.listFiles()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    deleteDirectory(file.getAbsolutePath());
                } else {
                    file.delete();
                }
            }
        }
        dir.delete();
    }
}
