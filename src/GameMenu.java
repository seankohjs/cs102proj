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
                System.out.println("INVALID INPUT! Please Enter a Number .");
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
                    System.out.println("Exiting Parade Game. Goodbye!");
                    break;
                default:
                    System.out.println("Enter a Number Between 1 and 3.");
            }
        } while (choice != 3);
    }

    private void startGame() {
        System.out.print("Enter the Number of Human Players (1 - 5): ");
        int numHumans = getValidInput(1, 5);
        
        System.out.print("Enter the Number of Bot Players (0 - " + (6 - numHumans) + "): ");
        int numBots = getValidInput(0, 6 - numHumans);
        
        List<String> playerNames = new ArrayList<>();
        List<Boolean> isBot = new ArrayList<>();
        List<Integer> botDifficulties = new ArrayList<>();
        
        // Get human player names
        for (int i = 0; i < numHumans; i++) {
            System.out.print("Enter Name for Player " + (i + 1) + ": ");
            playerNames.add(scanner.nextLine());
            isBot.add(false);
            botDifficulties.add(0); // Not applicable for human
        }
        
        // Get bot details
        for (int i = 0; i < numBots; i++) {
            System.out.print("Enter Name for Bot " + (i + 1) + " (or press Enter for automatic name): ");
            String botName = scanner.nextLine();
            if (botName.isEmpty()) {
                botName = "Bot " + (i + 1);
            }
            playerNames.add(botName);
            isBot.add(true);
            
            System.out.print("Select Difficulty for " + botName + " (1: Easy, 2: Medium, 3: Hard): ");
            int difficulty = getValidInput(1, 3);
            botDifficulties.add(difficulty);
        }
        
        // Create the game controller with both human and bot players
        GameController game = new GameController(playerNames, isBot, botDifficulties, scanner);
        game.startGame();
    }

    private int getValidInput(int min, int max) {
        int value;
        while (true) {
            if (scanner.hasNextInt()) {
                value = scanner.nextInt();
                scanner.nextLine(); // consume newline
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.print("Please enter a number between " + min + " and " + max + ": ");
                }
            } else {
                System.out.print("Invalid input. Please enter a number: ");
                scanner.nextLine();
            }
        }
    }

    private void viewGameRules() {
        try {
            String fullRules = new String(Files.readAllBytes(Paths.get("GameRules.txt")));
            
            // Split the rules by empty lines (consecutive newlines)
            String[] rawSections = fullRules.split("\n\\s*\n");
            
            // Clean up the sections
            List<String> validSections = new ArrayList<>();
            for (String section : rawSections) {
                if (!section.trim().isEmpty()) {
                    validSections.add(section.trim());
                }
            }
            
            String[] sections = validSections.toArray(new String[0]);
            
            // If no sections were found or very few, fall back to arbitrary chunking
            if (sections.length <= 1) {
                // Split into chunks of approximately 20 lines each
                String[] lines = fullRules.split("\n");
                int linesPerSection = 20;
                int numSections = (int) Math.ceil((double) lines.length / linesPerSection);
                
                sections = new String[numSections];
                for (int i = 0; i < numSections; i++) {
                    StringBuilder sectionBuilder = new StringBuilder();
                    int startLine = i * linesPerSection;
                    int endLine = Math.min(startLine + linesPerSection, lines.length);
                    
                    for (int j = startLine; j < endLine; j++) {
                        sectionBuilder.append(lines[j]).append("\n");
                    }
                    sections[i] = sectionBuilder.toString().trim();
                }
            }
    
            int currentSection = 0;
            boolean viewingRules = true;
    
            while (viewingRules && currentSection < sections.length) {
                view.clearScreen();
    
                // Display section header
                System.out.println();
                System.out.println(Print.BOLD + "■■■■■ PARADE GAME RULES ■■■■■" + Print.RESET);
                System.out.println();
                System.out.println("Section " + (currentSection + 1) + " of " + sections.length);
                System.out.println();
                System.out.println("■■■■■");
    
                // Display current section content
                System.out.println(sections[currentSection]);
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
