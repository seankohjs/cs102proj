import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class GameMenu {
    private Scanner scanner;

    public GameMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void display() {
        System.out.println("\n===== MENU =====");
        System.out.println("1. Play Game");
        System.out.println("2. View Game Rules");
        System.out.println("3. Quit Application");
        System.out.print("Please enter your choice: ");
    }

    public void readOption() {
        int choice = 0;
        do {
            display();
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
                continue;
            }
            switch (choice) {
                case 1:
                    startGame();
                    break;
                case 2:
                    viewGameRules();
                    break;
                case 3:
                    System.out.println("Exiting game. Goodbye!");
                    break;
                default:
                    System.out.println("Enter a choice between 1 and 3.");
            }
        } while (choice != 3);
    }

    private void startGame() {
        System.out.print("Enter the number of players (2-6): ");
        int numPlayers = scanner.nextInt();
        scanner.nextLine(); // consume newline
        List<String> playerNames = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            System.out.print("Enter name for Player " + (i + 1) + ": ");
            playerNames.add(scanner.nextLine());
        }
        // Create the game controller and start the game.
        GameController game = new GameController(playerNames, scanner);
        game.startGame();
    }

    private void viewGameRules() {
        // Read and display rules from an external file.
        try {
            String rules = new String(Files.readAllBytes(Paths.get("GameRules.txt")));
            System.out.println("\n" + rules);
        } catch (IOException e) {
            System.out.println("Error reading game rules: " + e.getMessage());
        }
        System.out.print("\nPress Enter to return to the menu...");
        scanner.nextLine();
    }
}
