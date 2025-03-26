import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class GameMenu {
    private Scanner scanner;
    GameView view = new GameView(scanner);

    public GameMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void display() {
        view.clearScreen();
        System.out.print(Print.BOLD);
        System.out.println();
        for (int i = 0; i < 5; i++) System.out.print("\u2550");
        System.out.print(" MENU ");
        for (int i = 0; i < 5; i++) System.out.print("\u2550");
        System.out.println();
        System.out.println();
        System.out.println("1. Play Game");
        System.out.println("2. View Game Rules");
        System.out.println("3. Quit Application");
        System.out.println();
        System.out.print("PLEASE ENTER YOUR CHOICE: ");
        System.out.print(Print.RESET);
    }

    public void readOption() {
        int choice = 0;
        do {
            display();
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
            } else {
                System.out.println("INVALID INPUT! Please enter a number .");
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
        try {
            // Create a GameView instance to use its clearScreen method

            String fullRules = new String(Files.readAllBytes(Paths.get("GameRules.txt")));

            // Split the rules into logical sections
            String[] sections = fullRules.split("\n\n");

            int currentSection = 0;
            boolean viewingRules = true;

            while (viewingRules && currentSection < sections.length) {
                view.clearScreen(); // Use GameView's clearScreen method

                // Display section header
                System.out.println("\n===== GAME RULES =====");
                System.out.println("Section " + (currentSection + 1) + " of " + sections.length);
                System.out.println("------------------\n");

                // Display current section content
                System.out.println(sections[currentSection]);

                // Navigation options
                System.out.println("\n------------------");
                System.out.println("Navigation: [N]ext section | [P]revious section | [Q]uit to menu");
                System.out.print("Enter your choice: ");

                String input = scanner.nextLine().trim().toUpperCase();

                switch (input) {
                    case "N":
                        if (currentSection < sections.length - 1) {
                            currentSection++;
                        } else {
                            System.out.println("You've reached the end of the rules.");
                            System.out.println("Press Enter to return to the menu...");
                            scanner.nextLine();
                            viewingRules = false;
                        }
                        break;
                    case "P":
                        if (currentSection > 0) {
                            currentSection--;
                        } else {
                            System.out.println("You're already at the beginning of the rules.");
                            System.out.println("Press Enter to continue...");
                            scanner.nextLine();
                        }
                        break;
                    case "Q":
                        viewingRules = false;
                        break;
                    default:
                        System.out.println("Invalid option. Press Enter to continue...");
                        scanner.nextLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading game rules: " + e.getMessage());
            System.out.print("\nPress Enter to return to the menu...");
            scanner.nextLine();
        }
    }
}
