import java.io.File;

public class Api {

    public Api() {}

    public static void runListStatement(String directoryPath) {

        // Create a File object representing the directory
        File directory = new File(directoryPath);

        // Check if the provided path exists and is a directory
        if (directory.exists() && directory.isDirectory()) {
            // Get a list of files and directories in the specified directory
            File[] files = directory.listFiles();

            // Display the list of files and directories
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

        // Create a File object representing the directory
        File directory = new File(directoryPath);

        // Check if the provided path exists and is a directory
        if (directory.exists() && directory.isDirectory()) {
            // Get a list of files and directories in the specified directory
            File[] files = directory.listFiles();

            // Display the list of files and directories
            if (files != null) {
                for (File file : files) {
                    System.out.println(file.getName());
                }
            }
        } else {
            System.out.println("Directory doesn't exist or is not a directory.");
        }
    }

    // TODO - add all functions here!

    public static void main(String[] args) {
        runListStatement();
    }
}


