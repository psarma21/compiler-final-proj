import java.io.File;
import java.nio.file.*;
import java.util.List;

public class Api {

    public Api() {}

    public static void runListStatement(String directoryPath) {
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    System.out.println(file.getName());
                }
            }
        } else {
            System.out.println("Directory doesn't exist or is not a directory");
        }
    }

    public static void runListStatement() {
        String directoryPath = "."; // Current directory

        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    System.out.println(file.getName());
                }
            }
        } else {
            System.out.println("Directory doesn't exist or is not a directory.");
        }
    }

    public static void runPwdStatement() {
        File directory = new File("."); // current directory
        try {
            System.out.println(directory.getCanonicalPath());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getting current directory");
        }
    }


    public static void createFileStatement(String filePath) {
        File file = new File(filePath);
        try {
            file.createNewFile();
        } catch (Exception e) {
            if (file.exists()) {
                removeFileOrDirStatement(filePath);
                createFileStatement(filePath);
            }
            else {
                e.printStackTrace();
                System.out.println("Error creating new file");
            }

        }
    }

    public static void createDirStatement(String dirPath) {
        File directory = new File(dirPath);
        try {
            directory.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error creating new folder");
        }
    }

    public static void removeFileOrDirStatement(String path) {
        File fileOrDirectory = new File(path);
        try {
            fileOrDirectory.delete();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error deleting file or folder");
        }
    }

    public static void moveStatement(String oldFile, String newFile) {
        copyStatement(oldFile, newFile);
        removeFileOrDirStatement(oldFile);
    }

    public static void copyStatement(String oldFile, String newFile) {
        createFileStatement(newFile);
        Path oldPath = Paths.get(oldFile);
        Path newPath = Paths.get(newFile);
        try {
            Files.copy(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error copying file");
        }
    }

    public static void printFileStatement(String file) {
        Path path = Paths.get(file);
        try {
            List<String> fileContents = Files.readAllLines(path);
            for (String s : fileContents) {
                System.out.println(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error printing file");
        }
    }

    public static void openStatement(String file) {
        // TODO
    }

    public static void showPhraseStatement(String phrase, String file) {

    }

    public static void replaceStatement(String filePath, String oldPhrase, String newPhrase) {
        // TODO - Ethan
    }
    
    public static void main(String[] args) {
//        runListStatement();
//        runPwdStatement();
//        createFileStatement("test.txt");
//        createDirStatement("test");
//        removeFileOrDirStatement("test.txt");
//        moveStatement("test.txt", "test.txt");
//        copyStatement("test.txt", "test.txt");
//        printFileStatement("test.txt");
    }
}


