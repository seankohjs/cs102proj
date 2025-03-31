import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*;

public class GameMenu {
    private Scanner scanner;
    GameView view = new GameView(scanner);

    public GameMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void display() {
        System.out.print(Print.BOLD);
        System.out.println();
        System.out.println("■■■■■ PARADE GAME MENU ■■■■■");
        System.out.println();
        System.out.println("1.. Play Game");
        System.out.println("2.. View Game Rules");
        System.out.println("3.. Quit Game");
        System.out.println();
        System.out.print("PLEASE ENTER YOUR CHOICE: ");
        System.out.print(Print.RESET);
    }

    public void readOption() {
        view.clearScreen();
        display();
        int choice = 0;
        boolean valid = false;
        while (!valid) {
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= 3) {
                    valid = true;
                } else { 
                    System.out.println("Please Enter '1', '2' or '3'..");
                }
            } catch (NumberFormatException e) {
                System.out.println(Print.BOLD + "Please Enter '1', '2' or '3'.." + Print.RESET);
            }
        }
        switch (choice) {
            case 1:
                startGame();
                break;
            case 2:
                viewGameRules();
                break;
            case 3:
                System.out.println("EXITING Parade Game..");
                System.exit(0);
                break;
            default:
                System.exit(0);
                break;
        }
    }

    public void startGame() {
        System.out.print("Enter the Number of HUMAN Players (1 to 5) :: ");
        int numHumans = getValidInput(1, 5);
        
        System.out.print("Enter the Number of BOT Players (0 to " + (6 - numHumans) + ") :: ");
        int numBots = getValidInput(0, 6 - numHumans);
        
        List<String> playerNames = new ArrayList<>();
        List<Boolean> isBot = new ArrayList<>();
        List<Integer> botDifficulties = new ArrayList<>();
        
        // Get human player names and add to list
        for (int i = 0; i < numHumans; i++) {
            System.out.print("Enter Name for Player " + (i + 1) + " :: ");
            playerNames.add(scanner.nextLine());
            isBot.add(false);
            botDifficulties.add(0);
        }
        
        // Get bot names and difficulties and add to list
        for (int i = 0; i < numBots; i++) {
            System.out.print(Print.BOLD + "Enter Name for BOT " + (i+1) + " (OR Press Enter for BOT " + (i+1) + ") :: " + Print.RESET);
            String botName = scanner.nextLine();
            if (botName.isEmpty()) {
                botName = "BOT " + (i + 1);
            }
            playerNames.add(botName);
            isBot.add(true);
            
            System.out.print(Print.BOLD + "Select Difficulty for " + Print.PURPLE + botName + Print.GREY + " [1] (Easy)  [2] (Medium)  [3] (Hard) :: ");
            int difficulty = getValidInput(1, 3);
            botDifficulties.add(difficulty);
        }
        
        // Create the game controller with both human and bot players
        GameController game = new GameController(playerNames, isBot, botDifficulties, scanner);
        game.startGame();
    }

    private int getValidInput(int min, int max) {
        int value = 0;
        while (true) {
            try {
                value = Integer.parseInt(scanner.nextLine());
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.print("Please Enter a NUMBER Between " + min + " and " + max + ": ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Please Enter a NUMBER Between " + min + " and " + max + ": ");
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
