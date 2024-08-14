import java.io.IOException;
import java.util.Scanner;

public class runtime{

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the game launcher!");
        System.out.println("Please enter the game you would like to launch:");
        String gameName = scanner.nextLine();

        // Launch the game based on the user's input
        if (gameName.equals("Game1")) {
            // Code to launch Game1
            System.out.println("Launching Game1...");
        } else if (gameName.equals("Game2")) {
            // Code to launch Game2
            System.out.println("Launching Game2...");
        } else {
            System.out.println("Invalid game name. Please try again.");
        }

        scanner.close();
    }
}