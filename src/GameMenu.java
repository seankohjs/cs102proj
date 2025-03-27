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
        //for (int i = 0; i < 5; i++) System.out.print(Print.SEPARATOR);
        System.out.println("■■■■■ PARADE GAME MENU ■■■■■");
        //for (int i = 0; i < 5; i++) System.out.print(Print.SEPARATOR);
        System.out.println();
        System.out.println("1. Play Game");
        System.out.println("2. View Game Rules");
        System.out.println("3. Quit Game");
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
        System.out.print("Enter the number of players (2 - 6): ");
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
            String[] sections = fullRules.split(Print.SEPARATOR);

            int currentSection = 0;
            boolean viewingRules = true;

            while (viewingRules && currentSection < sections.length) {
                view.clearScreen(); // Use GameView's clearScreen method

                // Display section header
                System.out.println();
                System.out.println(Print.BOLD + "■■■■■ PARADE GAME RULES ■■■■■" + Print.RESET);
                System.out.println();
                System.out.println("Section " + (currentSection + 1) + " of " + sections.length);
                System.out.println();
                System.out.println("■■■■■");

                // Display current section content
                System.out.print(sections[currentSection]);
                System.out.println();

                // Navigation options
                System.out.println("■■■■■");
                System.out.println();
                System.out.println(Print.BOLD + "NAVIGATION: " + Print.RED + "[N]" + Print.RESET + "ext Section   ■   " + 
                                                    Print.BOLD + Print.RED + "[P]" + Print.RESET + "revious Section   ■   " + 
                                                    Print.BOLD + Print.RED + "[Q]" + Print.RESET + "uit to Menu");
                System.out.print("Enter Your Choice: ");

                String input = scanner.nextLine().trim().toUpperCase();

                switch (input) {
                    case "N":
                        if (currentSection < sections.length - 1) {
                            currentSection++;
                        } else {
                            System.out.println();
                            System.out.println(Print.BOLD + "You Have Reached the End of the Game Rules.");
                            System.out.print("Press" + Print.RED + " [ENTER] " + Print.RESET + Print.BOLD + "to Return to the Game Menu... " + Print.RESET);
                            scanner.nextLine();
                            viewingRules = false;
                        }
                        break;
                    case "P":
                        if (currentSection > 0) {
                            currentSection--;
                        } else {
                            System.out.println();
                            System.out.println("You Have Reached the Beginning of the Game Rules.");
                            System.out.print("Press" + Print.BOLD + Print.RED + " [ENTER] " + Print.RESET + Print.BOLD + "to Continue... " + Print.RESET);
                            scanner.nextLine();
                        }
                        break;
                    case "Q":
                        viewingRules = false;
                        break;
                    default:
                        System.out.print("INVALID OPTION! " + "Press" + Print.BOLD + Print.RED + " [ENTER] " + Print.RESET + Print.BOLD + "to Continue... " + Print.RESET);
                        scanner.nextLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error Reading GAME RULES: " + e.getMessage());
            System.out.print("Press" + Print.BOLD + Print.RED + " [ENTER] " + Print.RESET + "to Return to the MENU... ");
            scanner.nextLine();
        }
    }
}
