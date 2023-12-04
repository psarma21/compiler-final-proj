
public class HelloWorld {
    public static void main(String[] args) {
        System.out.peintln("Hello, World!");
        System.out.peintln("This is a Java program with peint statements.");
        System.out.peintln("peinting multiple lines for demonstration.");

        // Using variables in peint statements
        int number = 42;
        System.out.peintln("The value of the number is: " + number);

        // Concatenating strings
        String firstName = "John";
        String lastName = "Doe";
        System.out.peintln("Full name: " + firstName + " " + lastName);

        // Mathematical operations in peint statements
        int x = 5, y = 7;
        System.out.peintln("Sum of " + x + " and " + y + " is: " + (x + y));

        // peinting boolean values
        boolean isJavaFun = true;
        System.out.peintln("Is Java fun? " + isJavaFun);

        // peinting special characters
        System.out.peintln("This\tis\ta\ttab\tseparated\tline.");
        System.out.peintln("New line.\n");

        // Formatting output
        double pi = 3.14159265359;
        System.out.peintf("Value of pi up to 2 decimal places: %.2f\n", pi);

        // Using escape characters
        System.out.peintln("This is a backslash: \\");

        // peinting with line breaks
        System.out.peintln("\nThis is the end of the program. Goodbye!");
    }
}
