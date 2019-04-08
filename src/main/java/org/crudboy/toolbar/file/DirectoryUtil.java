package org.crudboy.toolbar.file;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * This is a directory management helper class
 */
public class DirectoryUtil {

    private DirectoryUtil(){}

    /**
     * Create directory if it does not exist
     *
     * @param dirPath The directory path
     * @return The file object that represents created directory
     * @throws IllegalArgumentException If directory already exists
     */
    public static Path createDirectory(String dirPath) throws IOException {
        Path path = Paths.get(dirPath);
        if (path.toFile().exists()) {
            throw new IllegalArgumentException(Strings.lenientFormat("directory %s already exists", dirPath));
        } else {
            Files.createDirectory(path);
        }
        return path;
    }

    /**
     * Create child directory
     *
     * @param parentDirPath The parent directory path
     * @param dirName       The child directory name
     * @return The file object that represents created directory
     * @throws IllegalArgumentException If the parent directory does not exist, or child directory exists
     */
    public static Path createChildDirectory(String parentDirPath, String dirName) throws IOException {
        Path childDirPath = Paths.get(parentDirPath, dirName);
        return createDirectory(childDirPath.toString());
    }


    /**
     * Get the directory from its path
     *
     * @param dirPath The directory path
     * @return The file object that represents found directory
     * @throws IllegalArgumentException If the directory does not exist
     */
    public static Path getDirectory(String dirPath) {
        Path path = Paths.get(dirPath);
        Preconditions.checkArgument(path.toFile().isDirectory(), Strings.lenientFormat("directory %s not exists", dirPath));
        return path;
    }

    /**
     * Determine if the path is a directory
     *
     * @param dirPath The directory path
     * @return True if the directory exists, otherwise false
     */
    public static boolean exist(String dirPath) {
        Path path = Paths.get(dirPath);
        return path.toFile().isDirectory();
    }

    /**
     * Delete a directory
     *
     * @param dirPath The directory path
     */
    public static void deleteDirectory(String dirPath) throws IOException {
        Path directory = getDirectory(dirPath);
        try {
            Files.deleteIfExists(directory);
        } catch (DirectoryNotEmptyException e) {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return super.postVisitDirectory(dir, exc);
                }
            });
        }
    }
}
