package com.parade.view;

import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.parade.util.Print;

public class GameMenu {

    public static void displayMenu(){
        GameView.clearScreen();
        System.out.print(Print.BOLD);
        System.out.println();
        System.out.println("■■■■■" + Print.RED + " |||   PARADE GAME MENU   ||| " + Print.DEFAULT);
        System.out.println();
        System.out.println("1 .. " + Print.GREEN + "PLAY GAME" + Print.DEFAULT);
        System.out.println("2 .. " + Print.GREEN + "VIEW GAME RULES" + Print.DEFAULT);
        System.out.println("3 .. " + Print.GREEN + "QUIT GAME" + Print.DEFAULT);
        System.out.println();
    }

    public static boolean readOptions(Scanner sc){
        int choice = 0;
        boolean valid = false;
        while (!valid) {
            try {
                System.out.print(Print.YELLOW + "PLEASE ENTER YOUR CHOICE :: " + Print.DEFAULT);
                choice = Integer.parseInt(sc.nextLine());
                if (choice >= 1 && choice <= 3) {
                    valid = true;
                } else { 
                    System.out.print(Print.RED + "PLEASE ENTER [1], [2] OR [3] .. \n" + Print.DEFAULT);
                }
            } catch (NumberFormatException e) {
                System.out.print(Print.RED + "PLEASE ENTER [1], [2] OR [3] .. \n" + Print.DEFAULT);
            }
        }

        GameView.clearScreen();
        switch (choice) {
            case 1:
                return true;
            case 2:
                readRules(sc);
                break;
            case 3:
            System.out.println(Print.RED + "EXITING PARADE GAME .." + Print.DEFAULT);
                System.exit(0);
                break;
            default:
                System.exit(0);
                break;
        }
        return false;
    }

    private static void readRules(Scanner sc){
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
                GameView.clearScreen();
    
                // Display section header
                System.out.println();
                System.out.println("■■■■■" + Print.RED + " PARADE GAME RULES " + Print.DEFAULT);
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
    
                String input = sc.nextLine().trim().toUpperCase();
    
                switch (input) {
                    case "N":
                        if (currentSection < sections.length - 1) {
                            currentSection++;
                        } else {
                            System.out.println(Print.RED + "YOU HAVE REACHED THE END OF THE GAME RULES .." + Print.DEFAULT);
                            System.out.print(Print.YELLOW + "PRESS" + Print.RED + " [ENTER] " + Print.YELLOW + "TO CONTINUE .. " + Print.DEFAULT);
                            sc.nextLine();
                        }
                        break;
                    case "P":
                        if (currentSection > 0) {
                            currentSection--;
                        } else {
                            System.out.println(Print.RED + "YOU HAVE REACHED THE BEGINNING OF THE GAME RULES." + Print.DEFAULT);
                            System.out.print(Print.YELLOW + "PRESS" + Print.RED + " [ENTER] " + Print.YELLOW + "TO CONTINUE .. " + Print.DEFAULT);
                            sc.nextLine();
                        }
                        break;
                    case "Q":
                        viewingRules = false;
                        break;
                    default:
                        System.out.print(Print.RED + "INVALID OPTION! " + Print.DEFAULT);
                        System.out.print(Print.YELLOW + "PRESS" + Print.RED + " [ENTER] " + Print.YELLOW + "TO CONTINUE .. " + Print.DEFAULT);
                        sc.nextLine();
                }
            }
        } catch (IOException e) {
            System.out.println(Print.RED + "ERROR READING GAME RULES: " + e.getMessage() + Print.DEFAULT);
            System.out.print(Print.YELLOW + "PRESS" + Print.RED + " [ENTER] " + Print.YELLOW + "TO RETURN TO THE MAIN MENU .. " + Print.DEFAULT);
            sc.nextLine();
        }
    }
}
