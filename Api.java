import java.io.File;

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
            System.out.println("Directory doesn't exist or is not a directory.");
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
            e.printStackTrace();
            System.out.println("Error creating new file");
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

    public static void main(String[] args) {
//        runListStatement();
//        runPwdStatement();
//        createFileStatement("test.txt");
//        createDirStatement("test");
        removeFileOrDirStatement("test.txtasdfadsf");

    }
}


