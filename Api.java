import java.io.*;
import java.nio.file.*;
import java.awt.Desktop;
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
        File f = new File(file);

        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(f);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: unable to open file in application");
        }
    }

    public static void showPhraseStatement(String phrase, String file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            int lineNum = 1;

            while ((line = reader.readLine()) != null) {
                int index = line.indexOf(phrase);
                while (index != -1) {
                    System.out.println("Found at line " + lineNum + ", position " + index + " | " + line);
                    index = line.indexOf(phrase, index + 1);
                }
                lineNum++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public static void replaceStatement(String filePath, String oldPhrase, String newPhrase) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
            reader.close();

            String modifiedContent = content.toString().replace(oldPhrase, newPhrase);

            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(modifiedContent);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: unable to replace Strings");
        }
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
//        openStatement("test.txt");
//        showPhraseStatement("hello", "test.txt");
//        replaceStatement("test.txt", "hello", "hola");
    }
}


