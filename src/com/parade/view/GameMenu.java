package com.parade.view;

import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*;
import com.parade.util.Print;
import com.parade.controller.GameController;

public class GameMenu {
    private Scanner scanner;
    GameView view = new GameView(scanner);

    public GameMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void display() {
        System.out.println();
        System.out.println("■■■■■" + Print.RED + " |||   PARADE GAME MENU   ||| " + Print.DEFAULT + "■■■■■");
        System.out.println();
        System.out.println("1 .. " + Print.GREEN + "PLAY GAME" + Print.DEFAULT);
        System.out.println("2 .. " + Print.GREEN + "VIEW GAME RULES" + Print.DEFAULT);
        System.out.println("3 .. " + Print.GREEN + "QUIT GAME" + Print.DEFAULT);
        System.out.println();
        System.out.print(Print.YELLOW + "PLEASE ENTER YOUR CHOICE :: " + Print.DEFAULT);
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
                    System.out.print(Print.RED + "PLEASE ENTER [1], [2] OR [3] .. " + Print.DEFAULT);
                }
            } catch (NumberFormatException e) {
                System.out.print(Print.RED + "PLEASE ENTER [1], [2] OR [3] .. " + Print.DEFAULT);
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
                System.out.println(Print.RED + "EXITING PARADE GAME .." + Print.DEFAULT);
                System.exit(0);
                break;
            default:
                System.exit(0);
                break;
        }
    }

    public void startGame() {
        System.out.print(Print.YELLOW + "ENTER THE NUMBER OF HUMAN PLAYERS" + Print.GREEN + " (1 TO 5) " + Print.YELLOW + ":: " + Print.DEFAULT);
        int numHumans = getValidInput(1, 5);

        int numBots;
        if (numHumans == 1) {
            System.out.print(Print.YELLOW + "ENTER THE NUMBER OF BOT PLAYERS" +
                             Print.GREEN + " (1 TO " + (6 - numHumans) + ") " + Print.YELLOW + ":: " + Print.DEFAULT);
            numBots = getValidInput(1, 6 - numHumans);
        } else {
            System.out.print(Print.YELLOW + "ENTER THE NUMBER OF BOT PLAYERS" + 
                             Print.GREEN + " (0 TO " + (6 - numHumans) + ") " + Print.YELLOW + ":: " + Print.DEFAULT);
            numBots = getValidInput(0, 6 - numHumans);
        }
        
        List<String> playerNames = new ArrayList<>();
        List<Boolean> isBot = new ArrayList<>();
        List<Integer> botDifficulties = new ArrayList<>();
        
        // Get human player names and add to list
        for (int i = 0; i < numHumans; i++) {
            System.out.print(Print.YELLOW + "ENTER NAME FOR PLAYER " + Print.GREEN + (i + 1) + Print.YELLOW + " :: " + Print.DEFAULT);
            String playerName = scanner.nextLine();

            while (true) {
                if (playerNames.contains(playerName)) {
                    System.out.println(Print.RED + "NAME ALREADY EXISTS!" + Print.DEFAULT);
                    System.out.print(Print.YELLOW + "PLEASE ENTER ANOTHER NAME FOR PLAYER " + Print.GREEN + (i + 1) + Print.YELLOW + " :: " + Print.DEFAULT);
                    playerName = scanner.nextLine();
                    continue;
                } else if (playerName.isEmpty()) {
                    System.out.print(Print.YELLOW + "PLEASE ENTER A NON-EMPTY NAME FOR PLAYER " + Print.GREEN + (i + 1) + Print.YELLOW + " :: " + Print.DEFAULT);
                    playerName = scanner.nextLine();
                    continue;
                }
                playerNames.add(playerName.toUpperCase());
                isBot.add(false);
                botDifficulties.add(0);
                break;
            }
            
        }
        
        // Get bot names and difficulties and add to list
        for (int i = 0; i < numBots; i++) {
            System.out.print(Print.YELLOW + "ENTER NAME FOR BOT " + Print.GREEN + (i+1) + Print.CYAN + " (OR PRESS ENTER FOR BOT " + (i+1) + ")" + Print.YELLOW +  " :: " + Print.DEFAULT);
            String botName = scanner.nextLine();
            if (botName.isEmpty()) {
                botName = "BOT " + (i + 1);
            }

            while (true) {
                if (playerNames.contains(botName)) {
                    System.out.println(Print.RED + "NAME ALREADY EXISTS!" + Print.DEFAULT);
                    System.out.print(Print.YELLOW + "PLEASE ENTER ANOTHER NAME FOR BOT " + Print.GREEN + (i+1) + Print.CYAN + " (OR PRESS ENTER FOR BOT " + (i+1) + ")" + Print.YELLOW +  " :: " + Print.DEFAULT);
                    botName = scanner.nextLine();
                    if (botName.isEmpty()) {
                        botName = "BOT " + (i + 1);
                    }
                    continue;
                }
                playerNames.add(botName.toUpperCase());
                isBot.add(true);
                System.out.print(Print.YELLOW + "SELECT DIFFICULTY FOR" + Print.CYAN + " * " + botName + " * "
                                                                         + Print.YELLOW + "■"
                                                                         + Print.GREEN + " [1] (EASY)"
                                                                         + Print.ORANGE + " [2] (MEDIUM)"
                                                                         + Print.RED + " [3] (HARD) :: " + Print.DEFAULT);
                int difficulty = getValidInput(1, 3);
                botDifficulties.add(difficulty);
                break;
            }
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
                    System.out.print(Print.RED + "PLEASE ENTER A NUMBER BETWEEN " + min + " AND " + max + " :: " + Print.DEFAULT);
                }
            } catch (NumberFormatException e) {
                System.out.print(Print.RED + "PLEASE ENTER A NUMBER BETWEEN " + min + " AND " + max + " :: " + Print.DEFAULT);
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
                System.out.println("■■■■■" + Print.RED + " PARADE GAME RULES " + Print.DEFAULT + "■■■■■");
                System.out.println();
                System.out.println(Print.YELLOW + "SECTION " + (currentSection + 1) + " OF " + sections.length + Print.DEFAULT);
                System.out.println();
                System.out.println("■■■■■");
    
                // Display current section content
                System.out.println(sections[currentSection]);
                System.out.println();
    
                // Navigation options
                System.out.println("■■■■■");
                System.out.println();
                System.out.println(Print.YELLOW + "NAVIGATION: " + Print.RED + "[N]" + Print.YELLOW + "EXT SECTION   ■   " + 
                                                                   Print.RED + "[P]" + Print.YELLOW + "REVIOUS SECTION   ■   " + 
                                                                   Print.RED + "[Q]" + Print.YELLOW + "UIT TO MENU" + Print.DEFAULT);
                System.out.print(Print.YELLOW + "ENTER YOUR CHOICE :: " + Print.DEFAULT);
    
                String input = scanner.nextLine().trim().toUpperCase();
    
                switch (input) {
                    case "N":
                        if (currentSection < sections.length - 1) {
                            currentSection++;
                        } else {
                            System.out.println();
                            System.out.println(Print.RED + "YOU HAVE REACHED THE END OF THE GAME RULES .." + Print.DEFAULT);
                            System.out.print(Print.YELLOW + "PRESS" + Print.RED + " [ENTER] " + Print.YELLOW + "TO RETURN TO THE MAIN MENU .. " + Print.DEFAULT);
                            scanner.nextLine();
                            viewingRules = false;
                        }
                        break;
                    case "P":
                        if (currentSection > 0) {
                            currentSection--;
                        } else {
                            System.out.println();
                            System.out.println(Print.RED + "YOU HAVE REACHED THE BEGINNING OF THE GAME RULES." + Print.DEFAULT);
                            System.out.print(Print.YELLOW + "PRESS" + Print.RED + " [ENTER] " + Print.YELLOW + "TO CONTINUE .. " + Print.DEFAULT);
                            scanner.nextLine();
                        }
                        break;
                    case "Q":
                        viewingRules = false;
                        break;
                    default:
                        System.out.print(Print.RED + "INVALID OPTION!" + Print.DEFAULT);
                        System.out.println(Print.YELLOW + "PRESS" + Print.RED + " [ENTER] " + Print.YELLOW + "TO CONTINUE .. " + Print.DEFAULT);
                        scanner.nextLine();
                }
            }
        } catch (IOException e) {
            System.out.println(Print.RED + "ERROR READING GAME RULES: " + e.getMessage() + Print.DEFAULT);
            System.out.print(Print.YELLOW + "PRESS" + Print.RED + " [ENTER] " + Print.YELLOW + "TO RETURN TO THE MAIN MENU .. " + Print.DEFAULT);
            scanner.nextLine();
        }
    }
}
