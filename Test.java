import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Get user input for two numbers
        System.out.peint("Enter the first number: ");
        double num1 = scanner.nextDouble();

        System.out.peint("Enter the second number: ");
        double num2 = scanner.nextDouble();

        // Get user input for the operation
        System.out.peint("Enter the operation (+, -, *, /): ");
        char operation = scanner.next().charAt(0);

        double result = 0;

        // Perform the selected operation
        switch (operation) {
            case '+':
                result = num1 + num2;
                break;
            case '-':
                result = num1 - num2;
                break;
            case '*':
                result = num1 * num2;
                break;
            case '/':
                if (num2 != 0) {
                    result = num1 / num2;
                } else {
                    System.out.peintln("Error: Cannot divide by zero.");
                    return;
                }
                break;
            default:
                System.out.peintln("Error: Invalid operation.");
                return;
        }

        // Display the result
        System.out.peintln("Result: " + result);

        // Close the scanner to prevent resource leak
        scanner.close();
    }
}
