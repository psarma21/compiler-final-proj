import java.io.*;
import java.nio.file.*;
import javax.tools.*;
import java.awt.Desktop;
import java.util.List;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;

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

    public static void execStatement(String filePath) {
        String fileType = getFileType(filePath);

        try {
            switch (fileType) {
                case "java":
                    execJava(filePath);
                    break;
                case "cpp":
                    execCpp(filePath);
                    break;
                case "py":
                    execPython(filePath);
                    break;
                default:
                    System.out.println("Unknown file type");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: Invalid file name");
        }
    }

    private static void execJava(String filePath) {
        boolean compiled = compileJavaFile(filePath);

        if (compiled)
            executeCompiledJavaProgram(filePath);
        else
            System.out.println("Error: unable to execute Java file.");
    }

    private static void execCpp(String filePath) {
        try {
            ProcessBuilder compileProcess = new ProcessBuilder("g++", filePath, "-o", "cppoutput");
            Process compile = compileProcess.start();
            int compileExitCode = compile.waitFor();

            if (compileExitCode == 0) {
                ProcessBuilder executeProcess = new ProcessBuilder("./cppoutput");
                Process execute = executeProcess.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(execute.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                reader.close();
            } else {
                System.out.println("C++ program compilation failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: unable to execute C++ file.");
        }
    }

    private static void execPython(String filePath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", filePath);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();

            int exitCode = process.waitFor();
            if (exitCode != 0)
                System.out.println("Python script execution failed.");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: unable to execute Python file.");
        }
    }

    private static boolean compileJavaFile(String filePath) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int compilationResult = compiler.run(null, null, null, filePath);

        return compilationResult == 0;
    }

    private static void executeCompiledJavaProgram(String filePath) {
        try {
            // classFileDirectory only takes the in directory, not the actual path to the .class
            File f = new File(filePath);
            File classFileDirectory;
            String className;
            URLClassLoader classLoader;

            if (f.getParent() != null) {
                // class file located in a directory somewhere
                classFileDirectory = new File(f.getParent());
                className = getClassNameFromFilePath(filePath);
                classLoader = URLClassLoader.newInstance(new URL[]{classFileDirectory.toURI().toURL()});
            } else {
                // class file located inside the root directory
                className = getClassNameFromFilePath(filePath);
                classLoader = URLClassLoader.newInstance(new URL[]{new File(".").toURI().toURL()});

            }

            Class<?> clazz = Class.forName(className, true, classLoader);
            Method mainMethod = clazz.getMethod("main", String[].class);
            String[] params = {};
            mainMethod.invoke(null, (Object) params);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: unable to execute compiled Java class file.");
        }
    }

    private static String getClassNameFromFilePath(String filePath) {
        File file = new File(filePath);
        String fileName = file.getName();

        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    private static String getFileType(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');

        if (dotIndex != -1 && dotIndex != filePath.length() - 1)
            return filePath.substring(dotIndex + 1);

        return "";
    }

    public static int execsStatement(String filePath) {
        String fileType = getFileType(filePath);
        String output = "";

        try {
            switch (fileType) {
                case "java":
                    output = execsJava(filePath);
                    break;
                case "cpp":
                    output = execsCpp(filePath);
                    break;
                case "py":
                    output = execsPython(filePath);
                    break;
                default:
                    System.out.println("Unknown file type");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: Invalid file name");
        }

        output = output.substring(0,output.length()-1);
        int i = Integer.parseInt(output);
        return i;
    }

    public static String execsJava(String filePath) {
        String javaOutput = "";

        try {
             // Compile the Java file
             Process compileProcess = Runtime.getRuntime().exec("javac " + filePath);
             compileProcess.waitFor();

             if (compileProcess.exitValue() != 0) {
                 System.out.println("Java program execution failed.");
                 return "";
             }

             File javaFile = new File(filePath);
             String directory;

             if (javaFile.getParent() != null)
                directory = javaFile.getParent();
             else
                directory = ".";


             ProcessBuilder processBuilder = new ProcessBuilder("java", getClassNameFromFilePath(filePath));
             processBuilder.directory(new File(directory));
             Process process = processBuilder.start();

             BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             StringBuilder output = new StringBuilder();
             String line;
             while ((line = reader.readLine()) != null) {
                 output.append(line).append("\n");
             }

             javaOutput = output.toString();

         } catch (Exception e) {
             e.printStackTrace();
             System.out.println("Error: unable to store Java output into variable.");
         }

        return javaOutput;
    }

    public static String execsCpp(String filePath) {
        String cppOutput = "";

        try {
            ProcessBuilder compileProcess = new ProcessBuilder("g++", filePath, "-o", "cppoutput");
            Process compile = compileProcess.start();
            int compileExitCode = compile.waitFor();

            if (compileExitCode == 0) {
                // If compilation is successful, execute the compiled program
                ProcessBuilder executeProcess = new ProcessBuilder("./cppoutput");
                Process execute = executeProcess.start();

                // Capture the output of the executed C++ program
                BufferedReader reader = new BufferedReader(new InputStreamReader(execute.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                reader.close();

                int executeExitCode = execute.waitFor();
                if (executeExitCode == 0) {
                    // Print captured output
                    cppOutput = output.toString();
                } else {
                    System.out.println("C++ program execution failed.");
                }
            } else {
                System.out.println("C++ program execution failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: unable to store C++ output into variable.");
        }

        return cppOutput;
    }

    public static String execsPython(String filePath) {
        String pythonOutput = "";

        try {
//            ProcessBuilder processBuilder = new ProcessBuilder("python", filePath);
            ProcessBuilder processBuilder = new ProcessBuilder("/usr/bin/python", filePath);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor(); // Wait for the process to finish
            if (exitCode == 0) {
                pythonOutput = output.toString();
            } else {
                System.out.println("Python script execution failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: unable to store Python output into variable.");
        }

        return pythonOutput;
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

    public static void showStatement(String phrase, String file) {
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

    public static void replaceStatement(String oldPhrase, String filePath, String newPhrase) {
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

            System.out.println("File modified successfully!");
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
//        execStatement("HelloWorld2.java");
//        openStatement("test.txt");
//        moveStatement("test.txt", "test.txt");
//        copyStatement("test.txt", "test.txt");
//        printFileStatement("test.txt");
//        showStatement("hello", "test.txt");
//        replaceStatement("test.txt", "hello", "hola");
        //execStatement("EXECTestFiles/KYLE_DELETELATER.cpp");
        //execStatement("KYLE_DELETELATER.java");
        int a = execsStatement("KYLE_DELETELATER.java");
        System.out.println(a);
//        System.out.println((Object)execsStatement("EXECTestFiles/Demo.java").getClass().getSimpleName());
//        String t = execsStatement("HelloWorld.java"); System.out.println(t);
    }
}
